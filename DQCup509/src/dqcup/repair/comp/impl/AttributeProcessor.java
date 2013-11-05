package dqcup.repair.comp.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dqcup.repair.ColumnNames;
import dqcup.repair.RepairedCell;
import dqcup.repair.attr.AttributeValidator;
import dqcup.repair.attr.impl.AgeValidator;
import dqcup.repair.attr.impl.ApmtValidator;
import dqcup.repair.attr.impl.BirthValidator;
import dqcup.repair.attr.impl.CityValidator;
import dqcup.repair.attr.impl.FNameValidator;
import dqcup.repair.attr.impl.MinitValidator;
import dqcup.repair.attr.impl.SSNValidator;
import dqcup.repair.attr.impl.SalaryValidator;
import dqcup.repair.attr.impl.StAddValidator;
import dqcup.repair.attr.impl.StNumValidator;
import dqcup.repair.attr.impl.StateValidator;
import dqcup.repair.attr.impl.TaxValidator;
import dqcup.repair.attr.impl.ZipValidator;
import dqcup.repair.comp.DQCupContext;
import dqcup.repair.comp.DQCupProcessor;
import dqcup.repair.comp.DQTuple;

public class AttributeProcessor implements DQCupProcessor {

	private static Logger logger = LoggerFactory.getLogger(AttributeProcessor.class);

	private static Map<Integer, AttributeValidator> attrValidators;

	private static final boolean[] autoRepair = new boolean[DQTuple.AttrCount];

	static {
		attrValidators = new HashMap<Integer, AttributeValidator>();
		attrValidators.put(DQTuple.SSN_INDEX, new SSNValidator());
		attrValidators.put(DQTuple.FNAME_INDEX, new FNameValidator());
		attrValidators.put(DQTuple.MINIT_INDEX, new MinitValidator());
		attrValidators.put(DQTuple.LNAME_INDEX, new FNameValidator());
		attrValidators.put(DQTuple.STNUM_INDEX, new StNumValidator());
		attrValidators.put(DQTuple.STADD_INDEX, new StAddValidator());

		attrValidators.put(DQTuple.APMT_INDEX, new ApmtValidator());
		attrValidators.put(DQTuple.CITY_INDEX, new CityValidator());
		attrValidators.put(DQTuple.STATE_INDEX, new StateValidator());
		attrValidators.put(DQTuple.ZIP_INDEX, new ZipValidator());
		attrValidators.put(DQTuple.BIRTH_INDEX, new BirthValidator());
		attrValidators.put(DQTuple.AGE_INDEX, new AgeValidator());
		attrValidators.put(DQTuple.SALARY_INDEX, new SalaryValidator());
		attrValidators.put(DQTuple.TAX_INDEX, new TaxValidator());

		Arrays.fill(autoRepair, true);
		autoRepair[DQTuple.SSN_INDEX] = false;

		autoRepair[DQTuple.BIRTH_INDEX] = false;
		autoRepair[DQTuple.AGE_INDEX] = false;

		autoRepair[DQTuple.STADD_INDEX] = false;
		autoRepair[DQTuple.STNUM_INDEX] = false;
		autoRepair[DQTuple.APMT_INDEX] = false;
	}
	private ColumnNames columnNames;
	private HashSet<RepairedCell> repairs;

	private Map<String, DQTuple> dqTuples;

	private List<String> invalidCuids;
	private List<BitSet> invalidAttrs;

	private List<DQTuple> invalidDqTuples;
	private DQTuple prevTuple;
	private String[] prevValues;
	private BitSet prevInvalidAttr;
	private SSNRepairer ssnRepairer;
	private BirthAgeRepairer birthAgeRepairer;
	private StAddNumApmtRepairer stAddNumApmtRepairer;

	private void init(DQCupContext context) {
		repairs = context.getRepairs();

		invalidDqTuples = new LinkedList<DQTuple>();
		invalidAttrs = new LinkedList<BitSet>();

		invalidCuids = new LinkedList<String>();

		dqTuples = new TreeMap<String, DQTuple>();
		prevTuple = null;
		prevValues = null;
		prevInvalidAttr = null;
		ssnRepairer = new SSNRepairer();
		birthAgeRepairer = new BirthAgeRepairer();
		stAddNumApmtRepairer = new StAddNumApmtRepairer();
	}

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
			if (!invalidCuids.get(invalidCuids.size() - 1).equals(prevTuple.getCuid())
					&& prevInvalidAttr.cardinality() > 0) {
				invalidAttrs.add(prevInvalidAttr);
				invalidCuids.add(prevTuple.getCuid());
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

	private void processTuple(String[] tuple) {
		boolean validSSN = true;
		int ruid = Integer.valueOf(tuple[0]);
		String cuid = tuple[1];
		if (prevTuple == null || !prevTuple.getCuid().equals(cuid)) {
			if (prevTuple != null) {
				if (prevInvalidAttr.cardinality() > 0) {
					invalidAttrs.add(prevInvalidAttr);
					invalidCuids.add(prevTuple.getCuid());
				}
			}

			prevTuple = new DQTuple(cuid, ruid);
			dqTuples.put(cuid, prevTuple);
			prevValues = tuple;
			prevInvalidAttr = new BitSet(DQTuple.AttrCount);

		} else {
			prevTuple.addRuid(ruid);
		}
		// validate the single attribute
		for (Entry<Integer, AttributeValidator> entry : attrValidators.entrySet()) {
			AttributeValidator validator = entry.getValue();
			int index = entry.getKey();
			String value = tuple[index + DQTuple.Offset];
			if (!validator.validate(value)) {
				if (index == DQTuple.SSN_INDEX) {
					validSSN = false;
				}
				prevInvalidAttr.set(index);
				prevTuple.addSingleValue(index, null);
				logger.info("Ruid:{}\tInvalid {}:{}", ruid, DQTuple.Attrs[index], value);
			} else {
				prevTuple.addSingleValue(index, value);
			}
		}

		if (prevValues != tuple) {
			for (int i = 0; i < DQTuple.AttrCount; i++) {
				if (!tuple[i + DQTuple.Offset].equals(prevValues[i + DQTuple.Offset])) {
					prevInvalidAttr.set(i);
				}
			}
		}
		if (validSSN) {
			String ssn = tuple[DQTuple.SSN_INDEX + DQTuple.Offset];
			ssnRepairer.addSSNIndex(ssn, cuid);
		}
	}

	private void repair() {
		Iterator<BitSet> attrIt = invalidAttrs.iterator();
		Iterator<String> cuidIt = invalidCuids.iterator();

		while (cuidIt.hasNext()) {
			String cuid = cuidIt.next();
			BitSet invalidAttr = attrIt.next();
			DQTuple tuple = dqTuples.get(cuid);
			List<Integer> ruids = tuple.getRuids();
			if(cuid.equals("81233")){
				System.out.println();
			}
			if (invalidAttr.get(DQTuple.SSN_INDEX)) {
				ssnRepairer.repair(tuple, repairs, invalidAttr);
			}
			if (invalidAttr.get(DQTuple.BIRTH_INDEX) || invalidAttr.get(DQTuple.AGE_INDEX)) {
				birthAgeRepairer.repair(tuple, repairs, invalidAttr);
			}
			if (invalidAttr.get(DQTuple.STADD_INDEX) || invalidAttr.get(DQTuple.STNUM_INDEX)
					|| invalidAttr.get(DQTuple.APMT_INDEX)) {
				stAddNumApmtRepairer.repair(tuple, repairs, invalidAttr);
			}
			for (int i = 0; i < DQTuple.AttrCount; i++) {
				if (invalidAttr.get(i) && autoRepair[i]) {
					tuple.getAttributeContainer(i).autoRepair(repairs,
							DQTuple.Attrs[i + DQTuple.Offset], ruids);
				}
			}

		}
	}
}
