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
		String candidate = null;
		List<AttributeEntry> values = ssnContainer.getOrderValues();
		List<Integer> ruids = tuple.getRuids();
		for (AttributeEntry e : values) {
			candidate = e.value;
			boolean valid = true;
			if (!candidate.equals(SSN_Null)) {
				CounterSet<String> cuids = ssnIndex.get(candidate);
				for (Entry<String, Integer> ee : cuids) {
					if (ee.getValue() > e.count) {
						valid = false;
					}
				}
			}
			if (valid) {
				break;
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
