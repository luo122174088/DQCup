package dqcup.repair.comp.impl;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dqcup.repair.RepairedCell;
import dqcup.repair.Tuple;
import dqcup.repair.attr.AttributeValidator;
import dqcup.repair.attr.impl.BirthAgeValidator;
import dqcup.repair.attr.impl.CityValidator;
import dqcup.repair.attr.impl.FNameValidator;
import dqcup.repair.attr.impl.MinitValidator;
import dqcup.repair.attr.impl.SSNSalaryTaxValidator;
import dqcup.repair.attr.impl.StAddNumApmtValidator;
import dqcup.repair.attr.impl.StateValidator;
import dqcup.repair.attr.impl.ZipValidator;
import dqcup.repair.comp.DQCupContext;
import dqcup.repair.comp.DQCupProcessor;
import dqcup.repair.comp.DQTuple;

public class AttributeProcessor implements DQCupProcessor {

	private static Logger logger = LoggerFactory.getLogger(AttributeProcessor.class);

	private static Map<String, AttributeValidator> attrValidators;
	private static BirthAgeValidator birthAgeValidator = new BirthAgeValidator();
	private static SSNSalaryTaxValidator ssnSalaryTaxValidator = new SSNSalaryTaxValidator();
	private static StAddNumApmtValidator stAddNumApmtValidator = new StAddNumApmtValidator();
	static {
		attrValidators = new HashMap<String, AttributeValidator>();
		attrValidators.put(DQTuple.FNAME, new FNameValidator());
		attrValidators.put(DQTuple.MINIT, new MinitValidator());
		attrValidators.put(DQTuple.LNAME, new FNameValidator());
		attrValidators.put(DQTuple.CITY, new CityValidator());
		attrValidators.put(DQTuple.STATE, new StateValidator());
		attrValidators.put(DQTuple.ZIP, new ZipValidator());
	}

	private LinkedList<Tuple> origTuples;
	private HashSet<RepairedCell> repairs;
	private List<Tuple> invalidTuples;
	private List<BitSet> invalidAttrs;

	/**
	 * result tuple, the inner list is used for storing the conflicting tuples
	 * with same cuid
	 */
	private SortedMap<String, List<DQTuple>> dqTuples;
	private List<DQTuple> invalidDqTuples;
	private List<DQTuple> prevList;

	@Override
	public void process(DQCupContext context) {
		init(context);
		for (Tuple tuple : origTuples) {
			processTuple(tuple);
		}
		repair();

		context.set("dqTuples", dqTuples);
		context.set("invalidDqTuples", invalidDqTuples);

	}

	private void init(DQCupContext context) {
		origTuples = context.getTuples();
		repairs = context.getRepairs();

		invalidTuples = new LinkedList<Tuple>();
		invalidDqTuples = new LinkedList<DQTuple>();
		invalidAttrs = new LinkedList<BitSet>();
		dqTuples = new TreeMap<String, List<DQTuple>>();
		prevList = null;

	}

	private void processTuple(Tuple tuple) {
		HashMap<String, String> cell = tuple.getCells();
		boolean valid = true;
		BitSet invalidAttrs = new BitSet(DQTuple.AttrCount);
		int ruid = Integer.valueOf(cell.get(DQTuple.RUID));
		// validate the single attribute
		for (Entry<String, AttributeValidator> entry : attrValidators.entrySet()) {
			String column = entry.getKey();
			AttributeValidator repair = entry.getValue();
			String value = cell.get(column);
			if (repair != null && !repair.valid(value)) {
				invalidAttrs.set(DQTuple.AttrIndex.get(column));
				valid = false;
				logger.error("Ruid:{}\tInvalid {}:{}", ruid, column, value);
			}
		}
		// validate the compositional attributes
		String birth = cell.get(DQTuple.BIRTH);
		String age = cell.get(DQTuple.AGE);
		if (!birthAgeValidator.validate(birth, age)) {
			invalidAttrs.set(DQTuple.BIRTH_INDEX);
			invalidAttrs.set(DQTuple.AGE_INDEX);
			logger.error("Ruid:{} \t Invalid {}:{}\t{}:{}", ruid, DQTuple.BIRTH, birth,
					DQTuple.AGE, age);
		}

		String stAdd = cell.get(DQTuple.STADD);
		String stNum = cell.get(DQTuple.STNUM);
		String apmt = cell.get(DQTuple.APMT);
		if (!stAddNumApmtValidator.validate(stAdd, stNum, apmt)) {
			invalidAttrs.set(DQTuple.STADD_INDEX);
			invalidAttrs.set(DQTuple.STNUM_INDEX);
			invalidAttrs.set(DQTuple.APMT_INDEX);
			logger.error("Ruid:{} \t Invalid {}:{}\t{}:{}\t{}:{}", ruid, DQTuple.STADD, stAdd,
					DQTuple.STNUM, stNum, DQTuple.APMT, apmt);
		}

		String ssn = cell.get(DQTuple.SSN);
		String salary = cell.get(DQTuple.SALARY);
		String tax = cell.get(DQTuple.TAX);
		if (!ssnSalaryTaxValidator.validate(ssn, salary, tax)) {
			invalidAttrs.set(DQTuple.SSN_INDEX);
			invalidAttrs.set(DQTuple.SALARY_INDEX);
			invalidAttrs.set(DQTuple.TAX_INDEX);
			logger.error("Ruid:{} \t Invalid {}:{}\t{}:{}\t{}:{}", ruid, DQTuple.SSN, ssn,
					DQTuple.SALARY, salary, DQTuple.TAX, tax);
		}

		if (!valid) {
			invalidTuples.add(tuple);
			this.invalidAttrs.add(invalidAttrs);
		} else {
			if (prevList == null) {
				addNewTuple(tuple);
				return;
			}
			DQTuple prevTuple = prevList.get(0);
			if (!prevTuple.getCuid().equals(tuple.getCells().get(DQTuple.CUID))) {
				addNewTuple(tuple);
				return;
			}
			// merge
			for (DQTuple dqTuple : prevList) {
				if (dqTuple.equalsTuple(tuple)) {
					// no conflict, just merge
					dqTuple.getRuids().add(Integer.valueOf(cell.get(DQTuple.RUID)));
					return;
				}
			}
			// conflict occurs
			prevList.add(new DQTuple(tuple));
		}
	}

	private void addNewTuple(Tuple tuple) {
		DQTuple dq = new DQTuple(tuple);
		prevList = new LinkedList<DQTuple>();
		prevList.add(dq);
		dqTuples.put(dq.getCuid(), prevList);
	}

	private void repair() {
		if (invalidTuples.size() == 0) {
			return;
		}
		Iterator<Tuple> tupleIt = invalidTuples.iterator();
		Iterator<BitSet> attrIt = invalidAttrs.iterator();
		while (tupleIt.hasNext()) {
			Tuple tuple = tupleIt.next();
			BitSet invalidAttr = attrIt.next();
			String cuid = tuple.getCells().get(DQTuple.CUID);

			List<DQTuple> dqList = dqTuples.get(cuid);
			if (dqList == null) {
				// no correct tuple with same cuid
				dqList = new LinkedList<DQTuple>();
				DQTuple dq = new DQTuple(tuple);
				dq.setInvalidAttrs(invalidAttr);
				dqList.add(dq);
				dqTuples.put(cuid, dqList);

				invalidDqTuples.add(dq);
			} else {
				// find dq tuples with same cuid
				boolean corrected = false;
				for (DQTuple dq : dqList) {
					if (dq.getInvalidAttrs() == null && dq.partialEquals(tuple, invalidAttr)) {
						// we can correct the invalid tuple now
						HashMap<String, String> cell = tuple.getCells();
						int ruid = Integer.valueOf(cell.get(DQTuple.RUID));
						for (int i = 0; i < DQTuple.AttrCount; i++) {
							if (invalidAttr.get(i)
									&& !dq.getData(i).equals(cell.get(DQTuple.Attrs[i]))) {
								// find an error
								repairs.add(new RepairedCell(ruid, DQTuple.Attrs[i], dq.getData(i)));
							}
						}
						dq.getRuids().add(Integer.valueOf(ruid));
						corrected = true;
						break;
					}
				}
				if (!corrected) {
					// no partially equal dq tuple, we cannot correct the
					// invalid tuple right now
					DQTuple dq = new DQTuple(tuple);
					dq.setInvalidAttrs(invalidAttr);
					invalidDqTuples.add(dq);
				}
			}

		}
	}
}
