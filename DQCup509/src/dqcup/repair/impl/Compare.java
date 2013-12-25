package dqcup.repair.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

public class Compare {
	public static void main(String[] args) throws Exception {
		Set<String> wangyu = new HashSet<String>();
		Set<String> luochen = new HashSet<String>();
		String line = null;
		BufferedReader reader = new BufferedReader(new FileReader("/Users/luochen/Desktop/wang"));
		while ((line = reader.readLine()) != null) {
			wangyu.add(line);
		}
		reader.close();
		reader = new BufferedReader(new FileReader("/Users/luochen/Desktop/luo"));
		while ((line = reader.readLine()) != null) {
			luochen.add(line);
		}
		reader.close();
		System.err.println("wangyu has while luochen not:");
		for (String s : wangyu) {
			if (!luochen.contains(s) && !s.contains("FNAME") && !s.contains("LNAME")) {
				System.out.println(s);
			}
		}
		System.err.println();
		System.err.println();
		System.err.println();
		System.err.println();
		System.err.println();

		System.err.println("luochen has while wangyu not:");
		for (String s : luochen) {
			if (!wangyu.contains(s) && !s.contains("FNAME") && !s.contains("LNAME")) {
				System.out.println(s);
			}
		}
	}
}
