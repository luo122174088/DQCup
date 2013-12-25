package dqcup.repair.attr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dqcup.repair.RepairedCell;
import dqcup.repair.attr.repair.AgeRepairer;
import dqcup.repair.attr.repair.ApmtRepairer;
import dqcup.repair.attr.repair.AttributeRepairer;
import dqcup.repair.attr.repair.CityRepairer;
import dqcup.repair.attr.repair.FNameRepairer;
import dqcup.repair.attr.repair.MinitRepairer;
import dqcup.repair.attr.repair.SSNRepairer;
import dqcup.repair.attr.repair.SalaryTaxRepairer;
import dqcup.repair.attr.repair.StaddRepairer;
import dqcup.repair.attr.repair.StateRepairer;
import dqcup.repair.attr.repair.StnumRepairer;
import dqcup.repair.attr.repair.ZipRepairer;
import dqcup.repair.comp.DQTuple;

public class AttributeContainer {

	private static Map<String, AttributeRepairer> repairers = null;
	static {
		repairers = new HashMap<String, AttributeRepairer>();
		repairers.put(DQTuple.AGE, new AgeRepairer());
		repairers.put(DQTuple.APMT, new ApmtRepairer());
		repairers.put(DQTuple.CITY, new CityRepairer());
		repairers.put(DQTuple.FNAME, new FNameRepairer());
		repairers.put(DQTuple.LNAME, new FNameRepairer());
		repairers.put(DQTuple.MINIT, new MinitRepairer());
		repairers.put(DQTuple.SALARY, new SalaryTaxRepairer());
		repairers.put(DQTuple.TAX, new SalaryTaxRepairer());
		repairers.put(DQTuple.STADD, new StaddRepairer());
		repairers.put(DQTuple.STNUM, new StnumRepairer());
		repairers.put(DQTuple.ZIP, new ZipRepairer());
		repairers.put(DQTuple.SSN, new SSNRepairer());
		repairers.put(DQTuple.STATE, new StateRepairer());

	}

	public static class AttributeEntry implements Comparable<AttributeEntry> {
		public int count;
		public String value;

		public AttributeEntry(String value, int count) {
			this.value = value;
			this.count = count;
		}

		@Override
		public int compareTo(AttributeEntry ae) {
			if (count < ae.count) {
				return 1;
			} else if (count == ae.count) {
				return 0;
			} else {
				return -1;
			}
		}
	}

	private List<String> values = new ArrayList<String>(5);

	private List<String> originalValues = new ArrayList<String>(5);

	private List<AttributeEntry> orderedValues = null;

	public List<AttributeEntry> getOrderValues() {
		if (orderedValues != null) {
			return orderedValues;
		}
		orderedValues = buildOrderValues(values);
		return orderedValues;
	}

	private List<AttributeEntry> buildOrderValues(List<String> list) {
		// build order values
		List<AttributeEntry> result = new ArrayList<AttributeEntry>();
		boolean[] visited = new boolean[list.size()];
		int size = list.size();
		for (int i = 0; i < size; i++) {
			if (visited[i]) {
				continue;
			}
			String value = list.get(i);
			if (value == null) {
				visited[i] = true;
				continue;
			}
			AttributeEntry e = new AttributeEntry(value, 1);
			for (int j = i + 1; j < size; j++) {
				if (e.value.equals(list.get(j))) {
					visited[j] = true;
					e.count++;
				}
			}
			result.add(e);
		}
		if (result.size() > 1) {
			Collections.sort(result);
		}
		return result;
	}

	public String maximalValue() {
		if (orderedValues != null) {
			if (orderedValues.size() > 0) {
				return orderedValues.get(0).value;
			} else {
				return null;
			}
		}
		getOrderValues();
		return maximalValue();
	}

	/**
	 * 
	 * @param repairs
	 * @param column
	 */
	public String autoRepair(Set<RepairedCell> repairs, String column, List<Integer> ruids) {
		String candidate = maximalValue();
		if (candidate == null) {
			AttributeRepairer repairer = repairers.get(column);
			if (repairer != null) {
				candidate = repairer.repair(originalValues);
			}
		}
		int size = originalValues.size();
		for (int i = 0; i < size; i++) {
			if (!originalValues.get(i).equals(candidate)) {
				repairs.add(new RepairedCell(ruids.get(i), column, candidate));
				values.set(i, candidate);
				originalValues.set(i, candidate);
			}
		}
		return candidate;
	}

	public String superviseRepair(Set<RepairedCell> repairs, String column, String candidate,
			List<Integer> ruids) {
		if (candidate == null) {
			AttributeRepairer repairer = repairers.get(column);
			if (repairer != null) {
				candidate = repairer.repair(originalValues);
			}
		}
		int size = originalValues.size();
		for (int i = 0; i < size; i++) {
			if (!originalValues.get(i).equals(candidate)) {
				repairs.add(new RepairedCell(ruids.get(i), column, candidate));
				values.set(i, candidate);
				originalValues.set(i, candidate);
			}
		}
		return candidate;
	}

	public void add(String value, String origin) {
		values.add(value);
		originalValues.add(origin);
	}

	public List<String> getValues() {
		return values;
	}

	public String getValue(int i) {
		return values.get(i);
	}
}
