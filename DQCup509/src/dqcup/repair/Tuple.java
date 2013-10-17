package dqcup.repair;

import java.util.HashMap;

public class Tuple {
	private HashMap<String, String> cells;

	public Tuple(ColumnNames columnNames, String tupleLine) {
		cells = new HashMap<String, String>();
		String[] cellValues = tupleLine.split(":");
		for (int i = 0; i < cellValues.length; i++) {
			cells.put(columnNames.get(i), cellValues[i]);
		}
	}

	public String toString() {
		return cells.toString();
	}

	// added by luochen, make the data in tuple accessible.
	public HashMap<String, String> getCells() {
		return cells;
	}

}
