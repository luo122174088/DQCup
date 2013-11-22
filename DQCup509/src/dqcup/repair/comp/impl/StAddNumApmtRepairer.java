package dqcup.repair.comp.impl;

import java.util.BitSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import dqcup.repair.RepairedCell;
import dqcup.repair.attr.AttributeContainer;
import dqcup.repair.attr.AttributeContainer.AttributeEntry;
import dqcup.repair.attr.composite.impl.StAddNumApmtValidator;
import dqcup.repair.comp.AttributeRepairer;
import dqcup.repair.comp.DQTuple;

public class StAddNumApmtRepairer implements AttributeRepairer {

	private class StAddNumApmt implements Comparable<StAddNumApmt> {
		public int stAdd;
		public int stNum;
		public int apmt;
		public int count;

		public StAddNumApmt(int stAdd, int stNum, int apmt, int count) {
			super();
			this.stAdd = stAdd;
			this.stNum = stNum;
			this.apmt = apmt;
			this.count = count;
		}

		public int compareTo(StAddNumApmt o) {
			if (count < o.count) {
				return 1;
			} else if (count == o.count) {
				return 0;
			} else {
				return -1;
			}
		}

	}

	private PriorityQueue<StAddNumApmt> queue = new PriorityQueue<StAddNumApmt>();

	private StAddNumApmtValidator validator = new StAddNumApmtValidator();

	public void repair(DQTuple tuple, Set<RepairedCell> repairs, BitSet invalidAttrs) {
		AttributeContainer stAddContainer = tuple.getAttributeContainer(DQTuple.STADD_INDEX);
		AttributeContainer stNumContainer = tuple.getAttributeContainer(DQTuple.STNUM_INDEX);
		AttributeContainer apmtContainer = tuple.getAttributeContainer(DQTuple.APMT_INDEX);
		List<AttributeEntry> stAddList = stAddContainer.getOrderValues();
		List<AttributeEntry> stNumList = stNumContainer.getOrderValues();
		List<AttributeEntry> apmtList = apmtContainer.getOrderValues();
		List<Integer> ruids = tuple.getRuids();
		if (stAddList.size() <= 1 && stNumList.size() <= 1 && apmtList.size() <= 1) {
			stAddContainer.autoRepair(repairs, DQTuple.STADD, ruids);
			stNumContainer.autoRepair(repairs, DQTuple.STNUM, ruids);
			apmtContainer.autoRepair(repairs, DQTuple.APMT, ruids);
			return;
		}
		queue.clear();
		if (stAddList.size() > 0 && stNumList.size() > 0 && apmtList.size() > 0) {
			boolean[][][] visited = new boolean[stAddList.size()][stNumList.size()][apmtList.size()];
			AttributeEntry stAddEntry = stAddList.get(0);
			AttributeEntry stNumEntry = stNumList.get(0);
			AttributeEntry apmtEntry = apmtList.get(0);
			visited[0][0][0] = true;
			queue.add(new StAddNumApmt(0, 0, 0, stAddEntry.count + stNumEntry.count
					+ apmtEntry.count));
			while (!queue.isEmpty()) {
				StAddNumApmt s = queue.poll();
				stAddEntry = stAddList.get(s.stAdd);
				stNumEntry = stNumList.get(s.stNum);
				apmtEntry = apmtList.get(s.apmt);
				if (validator.validate(stAddEntry.value, stNumEntry.value, apmtEntry.value)) {
					stAddContainer.superviseRepair(repairs, DQTuple.STADD, stAddEntry.value, ruids);
					stNumContainer.superviseRepair(repairs, DQTuple.STNUM, stNumEntry.value, ruids);
					apmtContainer.superviseRepair(repairs, DQTuple.APMT, apmtEntry.value, ruids);
					break;
				}
				if (s.stAdd + 1 < stAddList.size() && !visited[s.stAdd + 1][s.stNum][s.apmt]) {
					queue.add(new StAddNumApmt(s.stAdd + 1, s.stNum, s.apmt, stAddList
							.get(s.stAdd + 1).count + stNumEntry.count + apmtEntry.count));
					visited[s.stAdd + 1][s.stNum][s.apmt] = true;
				}

				if (s.stNum + 1 < stNumList.size() && !visited[s.stAdd][s.stNum + 1][s.apmt]) {
					queue.add(new StAddNumApmt(s.stAdd, s.stNum + 1, s.apmt, stAddEntry.count
							+ stNumList.get(s.stNum + 1).count + apmtEntry.count));
					visited[s.stAdd][s.stNum + 1][s.apmt] = true;
				}

				if (s.apmt + 1 < apmtList.size() && !visited[s.stAdd][s.stNum][s.apmt + 1]) {
					queue.add(new StAddNumApmt(s.stAdd, s.stNum, s.apmt + 1, stAddEntry.count
							+ stNumEntry.count + apmtList.get(s.apmt + 1).count));
					visited[s.stAdd][s.stNum][s.apmt + 1] = true;
				}

			}
		} else {
			stAddContainer.autoRepair(repairs, DQTuple.STADD, ruids);
			stNumContainer.autoRepair(repairs, DQTuple.STNUM, ruids);
			apmtContainer.autoRepair(repairs, DQTuple.APMT, ruids);
		}

	}
}
