package dqcup.repair.attr.repair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import dqcup.repair.attr.impl.StateValidator;

public class StateRepairer implements AttributeRepairer {
	private static StateValidator validator = new StateValidator();

	@Override
	public String repair(List<String> data) {
		Map<String, Integer> stateMap = new HashMap<String, Integer>();
		for (String state : data) {
			if (state.length() < 2) {
				continue;
			}
			StringBuilder sb = new StringBuilder(2);
			int len = 0;
			for (int i = 0; i < state.length(); i++) {
				if (Character.isLetter(state.charAt(i))) {
					len++;
					sb.append(state.charAt(i));
					if (len == 2) {
						break;
					}
				}
			}
			if (len < 2) {
				continue;
			}
			String State = sb.toString().toUpperCase();
			if (validator.validate(State)) {
				Integer count = stateMap.get(State);
				if (count == null) {
					stateMap.put(State, 1);
				} else {
					stateMap.put(State, count + 1);
				}
			}
		}
		int count = 0;
		String candidate = "";
		for (Entry<String, Integer> e : stateMap.entrySet()) {
			if (e.getValue() > count) {
				candidate = e.getKey();
				count = e.getValue();
			}
		}
		return candidate;
	}

}
