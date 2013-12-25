package dqcup.repair.comp.impl;

import java.util.BitSet;
import java.util.List;
import java.util.Set;

import dqcup.repair.RepairedCell;
import dqcup.repair.attr.AttributeContainer;
import dqcup.repair.comp.DQTuple;

public class BirthAgeRepairer {

	public void repair(DQTuple tuple, Set<RepairedCell> repairs, BitSet invalidAttrs) {
		AttributeContainer birthContainer = tuple.getAttributeContainer(DQTuple.BIRTH_INDEX);
		AttributeContainer ageContainer = tuple.getAttributeContainer(DQTuple.AGE_INDEX);
		List<Integer> ruids = tuple.getRuids();
		String birth = null;
		if (invalidAttrs.get(DQTuple.BIRTH_INDEX)) {
			birth = birthContainer.autoRepair(repairs, DQTuple.BIRTH, ruids);
		} else {
			birth = birthContainer.getValue(0);
		}
		try {
			int year = Integer.valueOf(birth.substring(birth.length() - 4));
			String age = String.valueOf(2013 - year);
			if (invalidAttrs.get(DQTuple.AGE_INDEX) || !age.equals(ageContainer.getValue(0))) {
				ageContainer.superviseRepair(repairs, DQTuple.AGE, age, ruids);
			}
			return;
		} catch (Exception e) {

		}
		if (invalidAttrs.get(DQTuple.AGE_INDEX)) {
			ageContainer.autoRepair(repairs, DQTuple.AGE, ruids);
		}

	}
}
