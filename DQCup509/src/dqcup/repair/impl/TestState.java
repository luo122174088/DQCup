package dqcup.repair.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import dqcup.repair.comp.DQTuple;

public class TestState {
	public static void main(String[] args) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader("input/DB-normal-comb.txt"));
		String line = null;

		Map<String, String> zips = new HashMap<String, String>();

		boolean column = true;
		System.out.println("start");
		while ((line = reader.readLine()) != null) {
			if (column) {
				column = false;
				continue;
			}
			String[] tuple = line.split(":");

			String zip = tuple[DQTuple.ZIP_INDEX + DQTuple.Offset];
			String city = tuple[DQTuple.CITY_INDEX + DQTuple.Offset];

			if (zips.get(zip) == null) {
				zips.put(zip, city);
			} else {
				if (!city.equals(zips.get(zip))) {
					System.err.println(zip + " " + city);
				}
			}
		}
		reader.close();
	}
}
