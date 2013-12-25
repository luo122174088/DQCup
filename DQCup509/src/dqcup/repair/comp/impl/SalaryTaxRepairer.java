package dqcup.repair.comp.impl;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import dqcup.repair.RepairedCell;
import dqcup.repair.attr.AttributeContainer;
import dqcup.repair.comp.DQTuple;

public class SalaryTaxRepairer {

	// state -> salary -> tax -> cuids
	private Map<String, SortedMap<Integer, Map<Integer, Set<String>>>> index;

	// state -> salary -> tax
	private Map<String, Map<String, String>> repair;

	private Set<String> invalidCuids;

	public SalaryTaxRepairer() {
		this.index = new HashMap<String, SortedMap<Integer, Map<Integer, Set<String>>>>();
		this.repair = new HashMap<String, Map<String, String>>();
		this.invalidCuids = new HashSet<String>();
	}

	public void addIndex(String state, String salary, String tax, String cuid) {
		Integer iSalary = Integer.valueOf(salary);
		Integer iTax = Integer.valueOf(tax);

		SortedMap<Integer, Map<Integer, Set<String>>> salaries = index.get(state);
		if (salaries == null) {
			salaries = new TreeMap<Integer, Map<Integer, Set<String>>>();
			index.put(state, salaries);
		}
		Map<Integer, Set<String>> taxes = salaries.get(iSalary);
		if (taxes == null) {
			taxes = new HashMap<Integer, Set<String>>();
			salaries.put(iSalary, taxes);
		}
		Set<String> cuids = taxes.get(iTax);
		if (cuids == null) {
			cuids = new HashSet<String>();
			taxes.put(iTax, cuids);
		}
		cuids.add(cuid);
	}

	public void buildRepair() {
		for (Entry<String, SortedMap<Integer, Map<Integer, Set<String>>>> e : index.entrySet()) {
			SortedMap<Integer, Map<Integer, Set<String>>> salariesMap = e.getValue();
			List<Entry<Integer, Map<Integer, Set<String>>>> salaries = new ArrayList<Map.Entry<Integer, Map<Integer, Set<String>>>>(
					salariesMap.entrySet());
			int length = salaries.size();
			for (int i = 0; i < length; i++) {
				Entry<Integer, Map<Integer, Set<String>>> entry = salaries.get(i);
				if (entry.getValue().size() <= 1) {
					continue;
				} else {
					int prevTax = Integer.MAX_VALUE;
					int nextTax = Integer.MIN_VALUE;
					int prev = i - 1;
					while (prev >= 0) {
						Entry<Integer, Map<Integer, Set<String>>> e1 = salaries.get(prev);
						if (e1.getValue().size() == 1) {
							prevTax = e1.getValue().keySet().iterator().next();
							break;
						}
						prev--;
					}
					int next = i + 1;
					while (next < length) {
						Entry<Integer, Map<Integer, Set<String>>> e1 = salaries.get(next);
						if (e1.getValue().size() == 1) {
							nextTax = e1.getValue().keySet().iterator().next();
							break;
						}
						next++;
					}
					Integer candidate = null;
					int count = 0;
					Map<Integer, Set<String>> taxes = entry.getValue();
					Iterator<Entry<Integer, Set<String>>> taxIt = taxes.entrySet().iterator();
					while (taxIt.hasNext()) {
						int t = taxIt.next().getKey();
						if (prevTax < t && t < nextTax) {
							candidate = t;
							count++;
						}
					}
					if (count == 1) {
						addRepair(e.getKey(), String.valueOf(entry.getKey()),
								String.valueOf(candidate));
						taxIt = entry.getValue().entrySet().iterator();
						Set<String> set = null;
						while (taxIt.hasNext()) {
							Entry<Integer, Set<String>> taxEntry = taxIt.next();
							if (!taxEntry.getKey().equals(candidate)) {
								invalidCuids.addAll(taxEntry.getValue());
							} else {
								set = taxEntry.getValue();
							}
						}
						taxes.clear();
						taxes.put(candidate, set);
					}
				}
			}
			int prevTax = -1;
			int prevSalary = -1;
			for (int i = 0; i < length - 1; i++) {
				Entry<Integer, Map<Integer, Set<String>>> entry = salaries.get(i);
				if (entry.getValue().size() > 1) {
					continue;
				}
				if (entry.getKey() < 1500) {
					continue;
				}
				Map<Integer, Set<String>> taxes = entry.getValue();
				if (prevTax == -1) {
					prevSalary = entry.getKey();
					prevTax = taxes.keySet().iterator().next();
					continue;
				}
				int currentSalary = entry.getKey();
				int currentTax = entry.getValue().keySet().iterator().next();
				Entry<Integer, Map<Integer, Set<String>>> nextEntry = salaries.get(i + 1);
				if (nextEntry.getValue().size() > 1) {
					continue;
				}
				int nextSalary = nextEntry.getKey();
				int nextTax = nextEntry.getValue().keySet().iterator().next();

				if ((currentTax < prevTax || currentTax > nextTax) && (prevTax < nextTax)) {
					//error
					Set<String> cuids = taxes.get(currentTax);
					invalidCuids.addAll(cuids);
					int rate = (int) Math.round((double) (nextTax - prevTax) * 100
							/ (nextSalary - prevSalary));//per salary 100
					currentTax = (int) (prevTax + (double) (currentSalary - prevSalary) * rate
							/ 100);
					if ((currentSalary - prevSalary) % 100 != 0) {
						int ppTax = 0;
						int ppSalary = 0;
						int max = Math.max(0, i - 7);
						for (int j = i - 2; j >= max; j--) {
							Entry<Integer, Map<Integer, Set<String>>> ppEntry = salaries.get(j);
							if (ppEntry.getValue().size() > 1) {
								break;
							}
							ppSalary = ppEntry.getKey();
							ppTax = ppEntry.getValue().keySet().iterator().next();
							if (ppTax > nextTax) {
								break;
							}
							if ((currentSalary - ppSalary) % 100 == 0) {
								currentTax = (int) Math.round(ppTax + (currentSalary - ppSalary)
										* rate / 100);
								break;
							}
						}
					}
					taxes.clear();
					taxes.put(currentTax, cuids);
					addRepair(e.getKey(), String.valueOf(currentSalary), String.valueOf(currentTax));
				}
				prevTax = currentTax;
				prevSalary = currentSalary;
			}
		}
	}

	private void addRepair(String state, String salary, String tax) {
		Map<String, String> salaries = repair.get(state);
		if (salaries == null) {
			salaries = new HashMap<String, String>();
			repair.put(state, salaries);
		}
		salaries.put(salary, tax);
	}

	public void repair(DQTuple tuple, Set<RepairedCell> repairs, BitSet invalidAttrs) {
		AttributeContainer salaryContainer = tuple.getAttributeContainer(DQTuple.SALARY_INDEX);
		AttributeContainer stateContainer = tuple.getAttributeContainer(DQTuple.STATE_INDEX);
		AttributeContainer taxContainer = tuple.getAttributeContainer(DQTuple.TAX_INDEX);
		List<Integer> ruids = tuple.getRuids();
		if (invalidAttrs.get(DQTuple.SALARY_INDEX)) {
			salaryContainer.autoRepair(repairs, DQTuple.SALARY, ruids);
		}
		String tax = getRepair(stateContainer.getValue(0), salaryContainer.getValue(0));
		if (tax == null) {
			if (invalidAttrs.get(DQTuple.TAX_INDEX)) {
				taxContainer.autoRepair(repairs, DQTuple.TAX, ruids);
			}
		} else {
			taxContainer.superviseRepair(repairs, DQTuple.TAX, tax, ruids);
			invalidCuids.remove(tuple.getCuid());
		}
	}

	public void autoRepair(Map<String, DQTuple> tuples, Set<RepairedCell> repairs) {
		for (String cuid : invalidCuids) {
			DQTuple tuple = tuples.get(cuid);
			List<Integer> ruids = tuple.getRuids();
			AttributeContainer salaryContainer = tuple.getAttributeContainer(DQTuple.SALARY_INDEX);
			AttributeContainer stateContainer = tuple.getAttributeContainer(DQTuple.STATE_INDEX);
			AttributeContainer taxContainer = tuple.getAttributeContainer(DQTuple.TAX_INDEX);
			String tax = getRepair(stateContainer.getValue(0), salaryContainer.getValue(0));
			taxContainer.superviseRepair(repairs, DQTuple.TAX, tax, ruids);
		}
	}

	private String getRepair(String state, String salary) {
		if (state == null || salary == null) {
			return null;
		}
		Map<String, String> salaries = repair.get(state);
		if (salaries == null) {
			return null;
		}
		return salaries.get(salary);
	}

	public void print() {
		for (String state : index.keySet()) {
			SortedMap<Integer, Map<Integer, Set<String>>> salaries = index.get(state);

			Integer prevTax = null;

			for (Entry<Integer, Map<Integer, Set<String>>> e : salaries.entrySet()) {
				if (prevTax == null) {
					prevTax = e.getValue().keySet().iterator().next();
					continue;
				}
				Integer tax = e.getValue().keySet().iterator().next();
				if (tax < prevTax) {
					System.err.println(state + " " + e.getKey() + " " + tax + " "
							+ e.getValue().get(tax));
				}
				prevTax = tax;
			}
		}
	}

	public void removeCuid(String cuid) {
		invalidCuids.remove(cuid);
	}

}
