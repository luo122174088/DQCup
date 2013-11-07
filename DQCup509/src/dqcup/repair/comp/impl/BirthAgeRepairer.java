package dqcup.repair.comp.impl;

import java.util.BitSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import dqcup.repair.RepairedCell;
import dqcup.repair.attr.AttributeContainer;
import dqcup.repair.attr.AttributeContainer.AttributeEntry;
import dqcup.repair.attr.composite.impl.BirthAgeValidator;
import dqcup.repair.comp.AttributeRepairer;
import dqcup.repair.comp.DQTuple;

public class BirthAgeRepairer implements AttributeRepairer {

	private class BirthAge implements Comparable<BirthAge> {
		public int birth;
		public int age;
		public int count;

		public BirthAge(int birth, int age, int count) {
			super();
			this.birth = birth;
			this.age = age;
			this.count = count;
		}

		public int compareTo(BirthAge o) {
			if (count < o.count) {
				return 1;
			} else if (count == o.count) {
				return 0;
			} else {
				return -1;
			}
		}
	}

	private PriorityQueue<BirthAge> queue = new PriorityQueue<BirthAgeRepairer.BirthAge>();

	private BirthAgeValidator validator = new BirthAgeValidator();

	public void repair(DQTuple tuple, Set<RepairedCell> repairs, BitSet invalidAttrs) {
		AttributeContainer birthContainer = tuple.getAttributeContainer(DQTuple.BIRTH_INDEX);
		AttributeContainer ageContainer = tuple.getAttributeContainer(DQTuple.AGE_INDEX);
		List<AttributeEntry> birthList = birthContainer.getOrderValues();
		List<AttributeEntry> ageList = ageContainer.getOrderValues();
		List<Integer> ruids = tuple.getRuids();
		if (birthList.size() == 1 && ageList.size() == 1) {
			birthContainer.autoRepair(repairs, DQTuple.BIRTH, ruids);
			ageContainer.autoRepair(repairs, DQTuple.AGE, ruids);
			return;
		}
		queue.clear();
		if (birthList.size() > 0 && ageList.size() > 0) {
			boolean[][] visited = new boolean[birthList.size()][ageList.size()];
			AttributeEntry birthEntry = birthList.get(0);
			AttributeEntry ageEntry = ageList.get(0);
			visited[0][0] = true;
			queue.add(new BirthAge(0, 0, birthEntry.count * ageEntry.count));
			while (!queue.isEmpty()) {
				BirthAge birthAge = queue.poll();
				birthEntry = birthList.get(birthAge.birth);
				ageEntry = ageList.get(birthAge.age);
				if (validator.validate(birthEntry.value, ageEntry.value)) {
					birthContainer.superviseRepair(repairs, DQTuple.BIRTH, birthEntry.value, ruids);
					ageContainer.superviseRepair(repairs, DQTuple.AGE, ageEntry.value, ruids);
					break;
				}
				if (birthAge.birth + 1 < birthList.size()
						&& !visited[birthAge.birth + 1][birthAge.age]) {
					visited[birthAge.birth + 1][birthAge.age] = true;
					queue.add(new BirthAge(birthAge.birth + 1, birthAge.age, birthList
							.get(birthAge.birth + 1).count * ageEntry.count));
				}
				if (birthAge.age + 1 < ageList.size() && !visited[birthAge.birth][birthAge.age + 1]) {
					visited[birthAge.birth][birthAge.age + 1] = true;
					queue.add(new BirthAge(birthAge.birth, birthAge.age + 1, birthEntry.count
							* ageList.get(birthAge.age + 1).count));
				}
			}
		} else {
			birthContainer.autoRepair(repairs, DQTuple.BIRTH, ruids);
			ageContainer.autoRepair(repairs, DQTuple.AGE, ruids);
		}

	}
}
