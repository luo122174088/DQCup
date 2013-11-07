package dqcup.repair.comp;

import java.util.BitSet;
import java.util.Set;

import dqcup.repair.RepairedCell;

public interface AttributeRepairer {
	public void repair(DQTuple tuple, Set<RepairedCell> repairs, BitSet invalidAttrs);
}
