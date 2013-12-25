package dqcup.repair.comp.impl;

import java.util.BitSet;
import java.util.List;
import java.util.Set;

import dqcup.repair.RepairedCell;
import dqcup.repair.attr.AttributeContainer;
import dqcup.repair.attr.composite.impl.StAddNumApmtValidator;
import dqcup.repair.comp.DQTuple;

public class StAddNumApmtRepairer {

	public void repair(DQTuple tuple, Set<RepairedCell> repairs, BitSet invalidAttrs) {
		String stadd = null;
		List<Integer> ruids = tuple.getRuids();
		AttributeContainer staddCon = tuple.getAttributeContainer(DQTuple.STADD_INDEX);
		AttributeContainer apmtCon = tuple.getAttributeContainer(DQTuple.APMT_INDEX);
		AttributeContainer stnumCon = tuple.getAttributeContainer(DQTuple.STNUM_INDEX);

		if (invalidAttrs.get(DQTuple.STADD_INDEX)) {
			stadd = staddCon.autoRepair(repairs, DQTuple.STADD, ruids);
		} else {
			stadd = staddCon.getValue(0);
		}

		if (stadd != null && stadd.startsWith(StAddNumApmtValidator.POBox_Prefix)) {
			if (invalidAttrs.get(DQTuple.APMT_INDEX) || !"".equals(apmtCon.getValue(0))) {
				apmtCon.superviseRepair(repairs, DQTuple.APMT, "", ruids);
			}

			if (invalidAttrs.get(DQTuple.STNUM_INDEX) || !"".equals(stnumCon.getValue(0))) {
				stnumCon.superviseRepair(repairs, DQTuple.STNUM, "", ruids);
			}
		} else {
			if (invalidAttrs.get(DQTuple.APMT_INDEX)) {
				apmtCon.autoRepair(repairs, DQTuple.APMT, ruids);
			}
			if (invalidAttrs.get(DQTuple.STNUM_INDEX)) {
				stnumCon.autoRepair(repairs, DQTuple.STNUM, ruids);
			}
		}

	}
}
