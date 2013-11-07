package dqcup.repair.comp.impl;

import java.util.BitSet;
import java.util.List;
import java.util.Set;

import dqcup.repair.RepairedCell;
import dqcup.repair.attr.AttributeContainer;
import dqcup.repair.attr.AttributeContainer.AttributeEntry;
import dqcup.repair.attr.impl.CityValidator;
import dqcup.repair.comp.AttributeRepairer;
import dqcup.repair.comp.DQTuple;

public class CityRepairer implements AttributeRepairer {

	private CityValidator validator = new CityValidator();

	@Override
	public void repair(DQTuple tuple, Set<RepairedCell> repairs, BitSet invalidAttrs) {
		AttributeContainer container = tuple.getAttributeContainer(DQTuple.CITY_INDEX);
		List<AttributeEntry> values = container.getOrderValues();
		List<Integer> ruids = tuple.getRuids();
		if (values.size() > 0) {
			for (AttributeEntry e : values) {
				if (validator.strictValidate(e.value)) {
					container.superviseRepair(repairs, DQTuple.CITY, e.value, ruids);
					return;
				}
			}
		}
		container.autoRepair(repairs, DQTuple.CITY, ruids);
	}
}
