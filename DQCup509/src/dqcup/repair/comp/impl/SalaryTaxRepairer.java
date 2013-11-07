package dqcup.repair.comp.impl;

import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import dqcup.repair.RepairedCell;
import dqcup.repair.comp.AttributeRepairer;
import dqcup.repair.comp.DQTuple;
import dqcup.util.MultiMap;

public class SalaryTaxRepairer implements AttributeRepairer {

	private class SalaryTax implements Comparable<SalaryTax> {
		public int salary;
		public int tax;

		@Override
		public int compareTo(SalaryTax o) {
			if (salary < o.salary) {
				return -1;
			} else if (salary == o.salary) {
				if (tax < o.tax) {
					return -1;
				} else if (tax == o.tax) {
					return 0;
				} else {
					return 1;
				}
			} else {
				return 1;
			}
		}

		public SalaryTax(String salary, String tax) {
			this.salary = Integer.valueOf(salary);
			this.tax = Integer.valueOf(tax);
		}

		@Override
		public String toString() {
			return "SalaryTax [salary=" + salary + ", tax=" + tax + "]";
		}

	}

	private MultiMap<String, SalaryTax> map = new MultiMap<String, SalaryTaxRepairer.SalaryTax>();

	public void addSalaryTaxIndex(String state, String salary, String tax) {
		map.add(state, new SalaryTax(salary, tax));
	}

	@Override
	public void repair(DQTuple tuple, Set<RepairedCell> repairs, BitSet invalidAttrs) {
		// TODO Auto-generated method stub

	}

	public void print() {
		for (Entry<String, List<SalaryTax>> e : map) {
			Collections.sort(e.getValue());
			System.out.println(e.getKey());
			SalaryTax prev = null;
			for (SalaryTax st : e.getValue()) {
				if (prev == null) {
					prev = st;
					continue;
				}
				if (st.salary > prev.salary && st.tax <= prev.tax
						&& !(st.salary <= 1500 && st.tax == 0)) {
					System.out.println(prev + "\t" + st);
				}
				if (st.salary == prev.salary && st.tax != prev.tax) {
					System.out.println(prev + "\t" + st);
				}
			}
			System.out.println();
		}
	}
}
