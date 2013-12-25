package dqcup.repair.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import dqcup.repair.ColumnNames;
import dqcup.repair.RepairedCell;
import dqcup.repair.comp.DQTuple;
import dqcup.repair.test.TestUtil;

public class Combine {

	public static void main(String[] args) throws IOException {
		String dbFile = "input/DB-normal.txt";
		String resultFile = "input/Truth-normal.txt";
		String outputFile = "input/DB-easy-normal.txt";
		List<String[]> tuples = new ArrayList<String[]>();

		File file = new File(dbFile);
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			boolean columnNameLine = true;
			ColumnNames columnNames = null;
			while (null != (line = br.readLine())) {
				if (columnNameLine) {
					columnNames = new ColumnNames(line);
					columnNameLine = false;
				} else {
					String[] tuple = line.split(":");
					tuples.add(tuple);
				}
			}

			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Set<RepairedCell> repairs = TestUtil.readTruth(resultFile);
		for (RepairedCell cell : repairs) {
			String[] tuple = tuples.get(cell.getRowId());
			int column = 0;
			for (int i = 0; i < DQTuple.Attrs.length; i++) {
				if (DQTuple.Attrs[i].equals(cell.getColumnId())) {
					column = i;
					break;
				}
			}
			tuple[column] = cell.getValue();
		}

		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		for (String[] tuple : tuples) {
			for (int i = 0; i < tuple.length; i++) {
				if (i != 0) {
					writer.write(':');
				}
				writer.write(tuple[i]);
			}
			writer.write('\n');
		}
		writer.close();

	}

}
