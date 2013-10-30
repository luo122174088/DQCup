package dqcup.repair.comp.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

import dqcup.repair.ColumnNames;
import dqcup.repair.RepairedCell;
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

	private static Map<Integer, AttributeValidator> attrValidators;
	private static BirthAgeValidator birthAgeValidator = new BirthAgeValidator();
	private static SSNSalaryTaxValidator ssnSalaryTaxValidator = new SSNSalaryTaxValidator();
	private static StAddNumApmtValidator stAddNumApmtValidator = new StAddNumApmtValidator();

	static {
		attrValidators = new HashMap<Integer, AttributeValidator>();
		attrValidators.put(DQTuple.FNAME_INDEX, new FNameValidator());
		attrValidators.put(DQTuple.MINIT_INDEX, new MinitValidator());
		attrValidators.put(DQTuple.LNAME_INDEX, new FNameValidator());
		attrValidators.put(DQTuple.CITY_INDEX, new CityValidator());
		attrValidators.put(DQTuple.STATE_INDEX, new StateValidator());
		attrValidators.put(DQTuple.ZIP_INDEX, new ZipValidator());
	}
	private ColumnNames columnNames;
	private HashSet<RepairedCell> repairs;
	private List<String[]> invalidTuples;
	private List<BitSet> invalidAttrs;
	/**
	 * CUID->(RUID->SSN)
	 */
	private Map<String, Map<Integer, String>> ssnCandidates;
	/**
	 * SSN->(CUID->Count)
	 */
	private Map<String, Map<String, Integer>> ssnIndex;
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
		String path = context.getFilePath();
		File file = new File(path);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			boolean columnNameLine = true;
			while (null != (line = reader.readLine())) {
				if (columnNameLine) {
					columnNames = new ColumnNames(line);
					columnNameLine = false;
				} else {
					String[] tuple = line.split(":");
					processTuple(tuple);
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		repair();

		context.set("dqTuples", dqTuples);
		context.set("invalidDqTuples", invalidDqTuples);
		context.set("columnNames", columnNames);
	}

	private void init(DQCupContext context) {
		repairs = context.getRepairs();

		invalidTuples = new LinkedList<String[]>();
		invalidDqTuples = new LinkedList<DQTuple>();
		invalidAttrs = new LinkedList<BitSet>();

		dqTuples = new TreeMap<String, List<DQTuple>>();
		ssnCandidates = new HashMap<String, Map<Integer, String>>();
		ssnIndex = new HashMap<String, Map<String, Integer>>();
		prevList = null;
	}

	private void processTuple(String[] tuple) {
		boolean valid = true;
		BitSet invalidAttrs = new BitSet(DQTuple.AttrCount);
		int ruid = Integer.valueOf(tuple[0]);
		String cuid = tuple[1];
		// validate the single attribute
		for (Entry<Integer, AttributeValidator> entry : attrValidators.entrySet()) {
			AttributeValidator validator = entry.getValue();
			int index = entry.getKey() + DQTuple.Offset;
			String value = tuple[index];
			if (!validator.valid(value)) {
				invalidAttrs.set(entry.getKey());
				valid = false;
				logger.info("Ruid:{}\tInvalid {}:{}", ruid, DQTuple.Attrs[entry.getKey()], value);
			}
		}
		// validate the compositional attributes
		String birth = tuple[DQTuple.BIRTH_INDEX + DQTuple.Offset];
		String age = tuple[DQTuple.AGE_INDEX + DQTuple.Offset];
		if (!birthAgeValidator.validate(birth, age)) {
			invalidAttrs.set(DQTuple.BIRTH_INDEX);
			invalidAttrs.set(DQTuple.AGE_INDEX);
			valid = false;
			logger.info("Ruid:{} \t Invalid {}:{}\t{}:{}", ruid, DQTuple.BIRTH, birth, DQTuple.AGE,
					age);
		}

		String stAdd = tuple[DQTuple.STADD_INDEX + DQTuple.Offset];
		String stNum = tuple[DQTuple.STNUM_INDEX + DQTuple.Offset];
		String apmt = tuple[DQTuple.APMT_INDEX + DQTuple.Offset];
		if (!stAddNumApmtValidator.validate(stAdd, stNum, apmt)) {
			invalidAttrs.set(DQTuple.STADD_INDEX);
			invalidAttrs.set(DQTuple.STNUM_INDEX);
			invalidAttrs.set(DQTuple.APMT_INDEX);
			valid = false;
			logger.info("Ruid:{} \t Invalid {}:{}\t{}:{}\t{}:{}", ruid, DQTuple.STADD, stAdd,
					DQTuple.STNUM, stNum, DQTuple.APMT, apmt);
		}

		String ssn = tuple[DQTuple.SSN_INDEX + DQTuple.Offset];
		String salary = tuple[DQTuple.SALARY_INDEX + DQTuple.Offset];
		String tax = tuple[DQTuple.TAX_INDEX + DQTuple.Offset];
		if (!ssnSalaryTaxValidator.validate(ssn, salary, tax)) {
			invalidAttrs.set(DQTuple.SSN_INDEX);
			invalidAttrs.set(DQTuple.SALARY_INDEX);
			invalidAttrs.set(DQTuple.TAX_INDEX);
			valid = false;
			logger.info("Ruid:{} \t Invalid {}:{}\t{}:{}\t{}:{}", ruid, DQTuple.SSN, ssn,
					DQTuple.SALARY, salary, DQTuple.TAX, tax);
		}
		if (!valid) {
			invalidTuples.add(tuple);
			this.invalidAttrs.add(invalidAttrs);
		}
		addSSNIndex(ssn, cuid);
		if (prevList == null) {
			addNewTuple(tuple, invalidAttrs);
			return;
		}
		DQTuple prevTuple = prevList.get(0);
		if (!prevTuple.getCuid().equals(tuple[1])) {
			addNewTuple(tuple, invalidAttrs);
			return;
		}
		// merge
		for (DQTuple dqTuple : prevList) {
			if (dqTuple.getRuids().size() == 0) {
				// a partial dq tuple
				updateTuple(dqTuple, tuple, invalidAttrs);
				return;
			} else if (dqTuple.equalsTuple(tuple)) {
				// no conflict, just merge
				dqTuple.getRuids().add(Integer.valueOf(tuple[0]));
				return;
			} else if (dqTuple.equalsWithoutSSN(tuple)) {
				addSSNCandidate(dqTuple, tuple);
				return;
			}
		}
		// conflict occurs
		if (valid) {
			prevList.add(new DQTuple(tuple));
		}
	}

	private void addSSNCandidate(DQTuple dqTuple, String[] tuple) {
		String cuid = dqTuple.getCuid();
		Map<Integer, String> ssns = ssnCandidates.get(cuid);
		if (ssns == null) {
			ssns = new HashMap<Integer, String>();
			String dqSSN = dqTuple.getData(DQTuple.SSN_INDEX);
			for (Integer i : dqTuple.getRuids()) {
				ssns.put(i, dqSSN);
			}
			ssnCandidates.put(cuid, ssns);
		}
		int ruid = Integer.valueOf(tuple[0]);
		String ssn = tuple[DQTuple.SSN_INDEX + DQTuple.Offset];
		ssns.put(ruid, ssn);
	}

	private void addSSNIndex(String ssn, String cuid) {
		Map<String, Integer> cuids = ssnIndex.get(ssn);
		if (cuids == null) {
			cuids = new HashMap<String, Integer>();
			ssnIndex.put(ssn, cuids);
		}
		Integer count = cuids.get(cuid);
		if (count == null) {
			cuids.put(cuid, 1);
		} else {
			cuids.put(cuid, count + 1);
		}
	}

	private void updateTuple(DQTuple dqTuple, String[] tuple, BitSet invalidAttrs) {
		if (invalidAttrs.cardinality() == 0) {
			dqTuple.setDatas(tuple);
		} else {
			for (int i = 0; i < DQTuple.AttrCount; i++) {
				if (!invalidAttrs.get(i) && dqTuple.getData(i) == null) {
					dqTuple.setData(i, tuple[i + DQTuple.Offset]);
				}
			}
		}
	}

	private void addNewTuple(String[] tuple, BitSet invalidAttrs) {
		DQTuple dq = null;
		if (invalidAttrs.cardinality() == 0) {
			dq = new DQTuple(tuple);
		} else {
			dq = new DQTuple(); // only save the correct attributes
			dq.setCuid(tuple[1]);
			for (int i = 0; i < DQTuple.AttrCount; i++) {
				if (!invalidAttrs.get(i)) {
					dq.setData(i, tuple[i + DQTuple.Offset]);
				}
			}
		}
		prevList = new LinkedList<DQTuple>();
		prevList.add(dq);
		dqTuples.put(dq.getCuid(), prevList);

	}

	private void repair() {
		if (invalidTuples.size() == 0) {
			return;
		}
		Iterator<String[]> tupleIt = invalidTuples.iterator();
		Iterator<BitSet> attrIt = invalidAttrs.iterator();
		while (tupleIt.hasNext()) {
			String[] tuple = tupleIt.next();
			BitSet invalidAttr = attrIt.next();
			int ruid = Integer.valueOf(tuple[0]);
			String cuid = tuple[1];
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
						for (int i = 0; i < DQTuple.AttrCount; i++) {
							if (invalidAttr.get(i)) {
								String data = dq.getData(i);
								if (data != null && !data.equals(tuple[i + DQTuple.Offset])) {
									// find an error
									repairs.add(new RepairedCell(ruid, DQTuple.Attrs[i], data));
								}
							}
						}
						dq.getRuids().add(ruid);
						corrected = true;
						break;
					}
				}
				if (corrected) {
					continue;
				}
				DQTuple majority = dqList.get(0);
				for (int i = 1; i < dqList.size(); i++) {
					DQTuple dqTuple = dqList.get(i);
					if (dqTuple.getRuids().size() > majority.getRuids().size()) {
						majority = dqTuple;
					}
				}
				for (int i = 0; i < DQTuple.AttrCount; i++) {
					if (i != DQTuple.SSN_INDEX && invalidAttr.get(i)) {
						String data = majority.getData(i);
						if (data != null && !tuple[i + DQTuple.Offset].equals(data)) {
							repairs.add(new RepairedCell(ruid, DQTuple.Attrs[i], majority
									.getData(i)));
							invalidAttr.set(i, false);
							tuple[i + DQTuple.Offset] = data;
						}
					}
				}
				if (majority.equalsTuple(tuple)) {
					majority.getRuids().add(ruid);
				} else if (majority.equalsWithoutSSN(tuple)) {
					addSSNCandidate(majority, tuple);
				} else if (!majority.equalsTuple(tuple)) {
					// still have errors
					DQTuple dq = new DQTuple(tuple);
					dq.setInvalidAttrs(invalidAttr);
					invalidDqTuples.add(dq);
					dqList.add(dq);
					logger.info("Unable to repair:{}", dq);
				}
			}
		}
		// merge ssn
		for (Entry<String, Map<Integer, String>> e : ssnCandidates.entrySet()) {
			Map<String, Integer> validCandidates = new HashMap<String, Integer>();
			Map<Integer, String> ssns = e.getValue();
			String cuid = e.getKey();
			for (Entry<Integer, String> ee : ssns.entrySet()) {
				String ssn = ee.getValue();
				Map<String, Integer> cuids = ssnIndex.get(ssn);
				int count = cuids.get(cuid);
				boolean valid = true;
				for (Entry<String, Integer> eee : cuids.entrySet()) {
					if (eee.getValue() > count) {
						valid = false;
						break;
					}
				}
				if (valid) {
					Integer tupleCount = validCandidates.get(ssn);
					if (tupleCount == null) {
						validCandidates.put(ssn, 1);
					} else {
						validCandidates.put(ssn, tupleCount + 1);
					}
				}
			}
			String ssn = null;
			int count = 0;
			for (Entry<String, Integer> ee : validCandidates.entrySet()) {
				if (ssn == null || ee.getValue() > count) {
					ssn = ee.getKey();
					count = ee.getValue();
				}
			}
			List<DQTuple> tuples = dqTuples.get(cuid);
			DQTuple dqTuple = tuples.get(0);
			for (DQTuple dq : tuples) {
				if (!dq.getData(DQTuple.SSN_INDEX).equals(ssn)) {
					dq.setData(DQTuple.SSN_INDEX, ssn);
					for (int r : dq.getRuids()) {
						repairs.add(new RepairedCell(r, DQTuple.SSN, ssn));
					}
				}
			}
			for (Entry<Integer, String> ee : ssns.entrySet()) {
				if (!ee.getValue().equals(ssn)) {
					repairs.add(new RepairedCell(ee.getKey(), DQTuple.SSN, ssn));
				}
				dqTuple.getRuids().add(ee.getKey());
			}

		}
	}
}
