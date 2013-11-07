package dqcup.repair.attr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import dqcup.repair.RepairedCell;

public class AttributeContainer {

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

	private List<AttributeEntry> orderedValues = null;

	public List<AttributeEntry> getOrderValues() {
		if (orderedValues != null) {
			return orderedValues;
		}
		// build order values
		orderedValues = new ArrayList<AttributeEntry>();
		boolean[] visited = new boolean[values.size()];
		int size = values.size();
		for (int i = 0; i < size; i++) {
			if (visited[i]) {
				continue;
			}
			String value = values.get(i);
			if (value == null) {
				visited[i] = true;
				continue;
			}
			AttributeEntry e = new AttributeEntry(value, 1);
			for (int j = i + 1; j < size; j++) {
				if (e.value.equals(values.get(j))) {
					visited[j] = true;
					e.count++;
				}
			}
			orderedValues.add(e);
		}
		Collections.sort(orderedValues);
		return orderedValues;
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
	 * Assume the size > 1
	 * 
	 * @param repairs
	 * @param column
	 */
	public void autoRepair(Set<RepairedCell> repairs, String column, List<Integer> ruids) {
		String candidate = maximalValue();
		if (candidate == null) {
			System.err.println("Invalid " + column + " ruid:" + ruids.get(0));
		}
		for (int i = 0; i < values.size(); i++) {
			if (candidate == null || !candidate.equals(values.get(i))) {
				repairs.add(new RepairedCell(ruids.get(i), column, candidate));
				values.set(i, candidate);
			}
		}
	}

	public void superviseRepair(Set<RepairedCell> repairs, String column, String candidate,
			List<Integer> ruids) {
		for (int i = 0; i < values.size(); i++) {
			if (candidate == null || !candidate.equals(values.get(i))) {
				repairs.add(new RepairedCell(ruids.get(i), column, candidate));
				values.set(i, candidate);
			}
		}
	}

	public void add(String value) {
		values.add(value);
	}

	public List<String> getValues() {
		return values;
	}

	public String getValue(int i) {
		return values.get(i);
	}
}
