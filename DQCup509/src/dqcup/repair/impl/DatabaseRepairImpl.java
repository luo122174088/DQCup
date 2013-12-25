package dqcup.repair.impl;

import java.util.HashSet;
import java.util.Set;

import dqcup.repair.DatabaseRepair;
import dqcup.repair.RepairedCell;
import dqcup.repair.comp.DQCupChain;
import dqcup.repair.comp.impl.AttributeProcessor;

public class DatabaseRepairImpl implements DatabaseRepair {

	private DQCupChain chain = null;

	@Override
	public Set<RepairedCell> repair(String fileRoute) {
		// Please implement your own repairing methods here.
		// LinkedList<Tuple> tuples = DbFileReader.readFile(fileRoute);

		HashSet<RepairedCell> result = new HashSet<RepairedCell>();

		chain = new DQCupChain(fileRoute, result);
		chain.add(new AttributeProcessor());
		chain.execute();

		return result;
	}

	public static void main(String[] args) {
		int x = 10;
		int y = 2;
		x = x - y;
		y = x + y;
		x = y - x;
		System.out.println(x);
		System.out.println(y);

	}

}
