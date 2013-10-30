package dqcup.repair.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

import dqcup.repair.RepairedCell;
import dqcup.repair.comp.DQCupContext;
import dqcup.repair.comp.DQTuple;
import dqcup.repair.comp.impl.AttributeProcessor;

public class TestNormal {

	public static void main(String[] args) {
		AttributeProcessor processor = new AttributeProcessor();
		HashSet<RepairedCell> repairs = new HashSet<RepairedCell>();

		DQCupContext context = new DQCupContext("input/DB-normal.txt", repairs);
		processor.process(context);

		@SuppressWarnings("unchecked")
		SortedMap<String, List<DQTuple>> tuples = (SortedMap<String, List<DQTuple>>) context
				.get("dqTuples");
		for (Entry<String, List<DQTuple>> entry : tuples.entrySet()) {
			if (entry.getValue().size() == 1) {
				continue;
			}
			for (DQTuple tuple : entry.getValue()) {
				StringBuilder sb = new StringBuilder();
				sb.append(tuple.getRuids().size());
				sb.append(",");
				sb.append(entry.getKey());
				for (int i = 0; i < DQTuple.AttrCount; i++) {
					sb.append(",");
					sb.append(tuple.getData(i));
				}
				System.out.println(sb.toString());
			}
			System.out.println();
		}

		for (RepairedCell cell : repairs) {
			System.out.println(cell);
		}
		System.out.println();
		System.out.println(repairs.size());

	}
}
