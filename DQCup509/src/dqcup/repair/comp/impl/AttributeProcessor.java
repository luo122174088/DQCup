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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dqcup.repair.ColumnNames;
import dqcup.repair.RepairedCell;
import dqcup.repair.attr.AttributeValidator;
import dqcup.repair.attr.composite.impl.BirthAgeValidator;
import dqcup.repair.attr.composite.impl.StAddNumApmtValidator;
import dqcup.repair.attr.impl.CityValidator;
import dqcup.repair.attr.impl.FNameValidator;
import dqcup.repair.attr.impl.MinitValidator;
import dqcup.repair.attr.impl.SSNValidator;
import dqcup.repair.attr.impl.SalaryValidator;
import dqcup.repair.attr.impl.StateValidator;
import dqcup.repair.attr.impl.TaxValidator;
import dqcup.repair.attr.impl.ZipValidator;
import dqcup.repair.comp.AttributeRepairer;
import dqcup.repair.comp.DQCupContext;
import dqcup.repair.comp.DQCupProcessor;
import dqcup.repair.comp.DQTuple;

public class AttributeProcessor implements DQCupProcessor {

	private static Logger logger = LoggerFactory.getLogger(AttributeProcessor.class);

	private static Map<Integer, AttributeValidator> attrValidators;

	private static final boolean[] autoRepair = new boolean[DQTuple.AttrCount];

	static {
		attrValidators = new HashMap<Integer, AttributeValidator>();
		attrValidators.put(DQTuple.FNAME_INDEX, new FNameValidator());
		attrValidators.put(DQTuple.MINIT_INDEX, new MinitValidator());
		attrValidators.put(DQTuple.LNAME_INDEX, new FNameValidator());

		// attrValidators.put(DQTuple.STNUM_INDEX, new StNumValidator());
		// attrValidators.put(DQTuple.STADD_INDEX, new StAddValidator());
		// attrValidators.put(DQTuple.APMT_INDEX, new ApmtValidator());

		attrValidators.put(DQTuple.CITY_INDEX, new CityValidator());
		attrValidators.put(DQTuple.STATE_INDEX, new StateValidator());
		attrValidators.put(DQTuple.ZIP_INDEX, new ZipValidator());

		// attrValidators.put(DQTuple.BIRTH_INDEX, new BirthValidator());
		// attrValidators.put(DQTuple.AGE_INDEX, new AgeValidator());

		attrValidators.put(DQTuple.SSN_INDEX, new SSNValidator());
		attrValidators.put(DQTuple.SALARY_INDEX, new SalaryValidator());
		attrValidators.put(DQTuple.TAX_INDEX, new TaxValidator());

		Arrays.fill(autoRepair, true);

		autoRepair[DQTuple.SSN_INDEX] = false;

		// autoRepair[DQTuple.BIRTH_INDEX] = false;
		// autoRepair[DQTuple.AGE_INDEX] = false;

		// autoRepair[DQTuple.STADD_INDEX] = false;
		// autoRepair[DQTuple.STNUM_INDEX] = false;
		// autoRepair[DQTuple.APMT_INDEX] = false;
	}
	private ColumnNames columnNames;
	private HashSet<RepairedCell> repairs;

	private Map<String, DQTuple> dqTuples;

	private Map<String, BitSet> invalidTuples;

	private AttributeRepairer ssnRepairer;
	// private AttributeRepairer birthAgeRepairer;
	// private AttributeRepairer stAddNumApmtRepairer;

	private BirthAgeValidator birthAgeValidator;
	private StAddNumApmtValidator stAddNumApmtValidator;

	// private SalaryTaxValidator salaryTaxValidator;

	private void init(DQCupContext context) {
		repairs = context.getRepairs();
		invalidTuples = new HashMap<String, BitSet>();
		dqTuples = new HashMap<String, DQTuple>();
		ssnRepairer = new SSNRepairer();
		// birthAgeRepairer = new BirthAgeRepairer();
		// stAddNumApmtRepairer = new StAddNumApmtRepairer();

		birthAgeValidator = new BirthAgeValidator();
		stAddNumApmtValidator = new StAddNumApmtValidator();
		// salaryTaxValidator = new SalaryTaxValidator();
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
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		repair();
		context.set("dqTuples", dqTuples);
		context.set("columnNames", columnNames);
	}

	private void processTuple(String[] tuple) {
		int ruid = Integer.valueOf(tuple[0]);
		String cuid = tuple[1];
		DQTuple dqTuple = dqTuples.get(cuid);
		boolean created = false;
		boolean valid = true;
		boolean validSSN = true;
		if (dqTuple == null) {
			dqTuple = new DQTuple(cuid, ruid);
			dqTuples.put(cuid, dqTuple);
			created = true;
		} else {
			dqTuple.addRuid(ruid);
		}
		BitSet invalidAttr = invalidTuples.get(cuid);
		if (invalidAttr == null) {
			invalidAttr = new BitSet(DQTuple.AttrCount);
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
				valid = false;
				invalidAttr.set(index);
				value = null;
				logger.info("Ruid:{}\tInvalid {}:{}", ruid, DQTuple.Attrs[index], value);
			}
			dqTuple.addSingleValue(index, value, tuple[index + DQTuple.Offset]);
		}

		String stAdd = tuple[DQTuple.STADD_INDEX + DQTuple.Offset];
		String stNum = tuple[DQTuple.STNUM_INDEX + DQTuple.Offset];
		String apmt = tuple[DQTuple.APMT_INDEX + DQTuple.Offset];
		String validStAdd = stAdd;
		String validStNum = stNum;
		String validApmt = apmt;
		int result = stAddNumApmtValidator.strictValidate(stAdd, stNum, apmt);
		if ((result & StAddNumApmtValidator.Invalid_Conflict) != 0) {
			validStAdd = validStNum = validApmt = null;
		} else {
			if ((result & StAddNumApmtValidator.Invalid_StAdd) != 0) {
				validStAdd = null;
			}
			if ((result & StAddNumApmtValidator.Invalid_StNum) != 0) {
				validStNum = null;
			}
			if ((result & StAddNumApmtValidator.Invalid_Apmt) != 0) {
				validApmt = null;
			}
		}
		if (validStAdd == null || validStNum == null || validApmt == null) {
			valid = false;
		}
		dqTuple.addSingleValue(DQTuple.STADD_INDEX, validStAdd, stAdd);
		dqTuple.addSingleValue(DQTuple.STNUM_INDEX, validStNum, stNum);
		dqTuple.addSingleValue(DQTuple.APMT_INDEX, validApmt, apmt);

		String birth = tuple[DQTuple.BIRTH_INDEX + DQTuple.Offset];
		String age = tuple[DQTuple.AGE_INDEX + DQTuple.Offset];
		String validBirth = birth;
		String validAge = age;
		result = birthAgeValidator.strictValidate(birth, age);
		if ((result & BirthAgeValidator.Invalid_Conflit) != 0) {
			validBirth = validAge = null;
		} else {
			if ((result & BirthAgeValidator.Invalid_Birth) != 0) {
				validBirth = null;
			}
			if ((result & BirthAgeValidator.Invalid_Age) != 0) {
				validAge = null;
			}
		}
		if (validBirth == null || validAge == null) {
			valid = false;
		}
		dqTuple.addSingleValue(DQTuple.BIRTH_INDEX, validBirth, birth);
		dqTuple.addSingleValue(DQTuple.AGE_INDEX, validAge, age);

		if (!created) {
			for (int i = 0; i < DQTuple.AttrCount; i++) {
				if (!invalidAttr.get(i)
						&& !tuple[i + DQTuple.Offset].equals(dqTuple.getAttributeContainer(i)
								.getValue(0))) {
					invalidAttr.set(i);
					valid = false;
				}
			}
		}
		if (!valid) {
			invalidTuples.put(cuid, invalidAttr);
		}
		if (validSSN) {
			String ssn = tuple[DQTuple.SSN_INDEX + DQTuple.Offset];
			((SSNRepairer) ssnRepairer).addSSNIndex(ssn, cuid);
		}
	}

	private void repair() {
		Iterator<Entry<String, BitSet>> it = invalidTuples.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, BitSet> e = it.next();
			String cuid = e.getKey();
			BitSet invalidAttr = e.getValue();
			DQTuple tuple = dqTuples.get(cuid);
			List<Integer> ruids = tuple.getRuids();
			if (invalidAttr.get(DQTuple.SSN_INDEX)) {
				ssnRepairer.repair(tuple, repairs, invalidAttr);
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
