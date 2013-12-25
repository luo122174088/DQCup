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
import dqcup.repair.comp.DQTuple;
import dqcup.util.CounterSet;

public class SSNRepairer {
	/**
	 * SSN->(CUID->Count)
	 */
	private Map<String, CounterSet<String>> ssnIndex = new HashMap<String, CounterSet<String>>();

	public static String SSN_Null = "000000000";

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

	public void repair(DQTuple tuple, Set<RepairedCell> repairs, BitSet invalidAttrs,
			SalaryTaxRepairer stRepairer) {
		AttributeContainer ssnContainer = tuple.getAttributeContainer(DQTuple.SSN_INDEX);
		AttributeContainer salaryContainer = tuple.getAttributeContainer(DQTuple.SALARY_INDEX);
		AttributeContainer taxContainer = tuple.getAttributeContainer(DQTuple.TAX_INDEX);
		String ssn = null;
		List<Integer> ruids = tuple.getRuids();
		String cuid = tuple.getCuid();
		if (invalidAttrs.get(DQTuple.SSN_INDEX)) {

			boolean nullable = true;
			if (!"0".equals(salaryContainer.getValue(0))) {
				nullable = false;
			}
			ssn = repairSSN(ssnContainer, ruids, repairs, nullable);
		} else {
			ssn = ssnContainer.getValue(0);
		}
		if (SSN_Null.equals(ssn)) {
			if (invalidAttrs.get(DQTuple.SALARY_INDEX) || !"0".equals(salaryContainer.getValue(0))) {
				salaryContainer.superviseRepair(repairs, DQTuple.SALARY, "0", ruids);
			}

			if (invalidAttrs.get(DQTuple.TAX_INDEX) || !"0".equals(taxContainer.getValue(0))) {
				taxContainer.superviseRepair(repairs, DQTuple.TAX, "0", ruids);
				stRepairer.removeCuid(cuid);
			}
		} else {
			if (invalidAttrs.get(DQTuple.SALARY_INDEX) || invalidAttrs.get(DQTuple.TAX_INDEX)) {
				stRepairer.repair(tuple, repairs, invalidAttrs);
			}
		}
	}

	private String repairSSN(AttributeContainer ssnContainer, List<Integer> ruids,
			Set<RepairedCell> repairs, boolean nullable) {
		String candidate = null;
		boolean nullExist = false;
		List<AttributeEntry> values = ssnContainer.getOrderValues();
		for (AttributeEntry e : values) {
			candidate = e.value;
			boolean valid = true;
			if (!candidate.equals(SSN_Null)) {
				CounterSet<String> cuids = ssnIndex.get(candidate);
				for (Entry<String, Integer> ee : cuids) {
					if (ee.getValue() > e.count) {
						valid = false;
						break;
					}
				}
			} else {
				valid = nullable;
				nullExist = true;
			}
			if (valid) {
				break;
			}
		}
		if (candidate == null && nullExist) {
			candidate = SSN_Null;
		}
		return ssnContainer.superviseRepair(repairs, DQTuple.SSN, candidate, ruids);
	}
}
