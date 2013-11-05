package dqcup.repair.impl;

import java.util.HashSet;

import dqcup.repair.RepairedCell;
import dqcup.repair.comp.DQCupContext;
import dqcup.repair.comp.impl.AttributeProcessor;

public class TestNormal {

	public static void main(String[] args) {
		AttributeProcessor processor = new AttributeProcessor();
		HashSet<RepairedCell> repairs = new HashSet<RepairedCell>();

		DQCupContext context = new DQCupContext("input/DB-normal.txt", repairs);
		processor.process(context);

		for (RepairedCell cell : repairs) {
			System.out.println(cell);
		}
		System.out.println();
		System.out.println(repairs.size());

	}
}
