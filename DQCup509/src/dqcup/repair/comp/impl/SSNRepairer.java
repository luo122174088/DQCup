package dqcup.repair.comp.impl;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import dqcup.repair.RepairedCell;
import dqcup.repair.attr.AttributeContainer;
import dqcup.repair.attr.AttributeContainer.AttributeEntry;
import dqcup.repair.comp.AttributeRepairer;
import dqcup.repair.comp.DQTuple;
import dqcup.util.CounterSet;

public class SSNRepairer implements AttributeRepairer {
	/**
	 * SSN->(CUID->Count)
	 */
	private Map<String, CounterSet<String>> ssnIndex = new HashMap<String, CounterSet<String>>();

	private static String SSN_Null = "000000000";

	public void addSSNIndex(String ssn, String cuid) {
		if (SSN_Null.equals(ssn)) {
			return;
		}
		CounterSet<String> cuids = ssnIndex.get(ssn);
		if (cuids == null) {
			cuids = new CounterSet<String>();
			ssnIndex.put(ssn, cuids);
		}
		cuids.add(cuid);
	}

	public void repair(DQTuple tuple, Set<RepairedCell> repairs, BitSet invalidAttrs) {
		AttributeContainer ssnContainer = tuple.getAttributeContainer(DQTuple.SSN_INDEX);
		AttributeContainer salaryContainer = tuple.getAttributeContainer(DQTuple.SALARY_INDEX);
		AttributeContainer taxContainer = tuple.getAttributeContainer(DQTuple.TAX_INDEX);

		String possibleCandidate = null;
		String candidate = null;
		int maximal = 0;

		List<AttributeEntry> values = ssnContainer.getOrderValues();
		List<Integer> ruids = tuple.getRuids();

		List<String> salaries = salaryContainer.getValues();
		List<String> taxes = taxContainer.getValues();
		if (tuple.getCuid().equals("72836")) {
			System.out.println();
		}
		for (AttributeEntry e : values) {
			possibleCandidate = e.value;
			boolean valid = true;
			if (!possibleCandidate.equals(SSN_Null)) {
				CounterSet<String> cuids = ssnIndex.get(possibleCandidate);
				for (Entry<String, Integer> ee : cuids) {
					if (ee.getValue() > e.count) {
						valid = false;
						break;
					}
				}
			}
			if (valid) {
				int count = e.count;
				for (int i = 0; i < salaries.size(); i++) {
					String salary = salaries.get(i);
					String tax = taxes.get(i);
					if (salary != null) {
						if (possibleCandidate.equals(SSN_Null) && salary.equals("0")) {
							count++;
						} else if (!possibleCandidate.equals(SSN_Null) && !salary.equals("0")) {
							count++;
						}
					}
					if (tax != null) {
						if (salary != null) {
							int iSalary = Integer.valueOf(salary);
							if (!possibleCandidate.equals(SSN_Null)) {
								if ((iSalary <= 1500 && tax.equals("0"))
										|| (iSalary > 1500 && !tax.equals("0"))) {
									count++;
								}
							} else if (tax.equals("0")) {
								count++;
							}
						} else {
							if (possibleCandidate.equals(SSN_Null) && tax.equals("0")) {
								count++;
							} else if (!possibleCandidate.equals(SSN_Null) && !tax.equals("0")) {
								count++;
							}
						}
					}
				}
				if (count > maximal) {
					candidate = possibleCandidate;
					maximal = count;
				}

			}
		}
		ssnContainer.superviseRepair(repairs, DQTuple.SSN, candidate, ruids);
		if (SSN_Null.equals(candidate)) {
			tuple.getAttributeContainer(DQTuple.SALARY_INDEX).superviseRepair(repairs,
					DQTuple.SALARY, "0", ruids);
			tuple.getAttributeContainer(DQTuple.TAX_INDEX).superviseRepair(repairs, DQTuple.TAX,
					"0", ruids);
			invalidAttrs.set(DQTuple.SALARY_INDEX, false);
			invalidAttrs.set(DQTuple.TAX_INDEX, false);
		}
	}
}
