package dqcup.repair.attr.repair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class StaddRepairer implements AttributeRepairer {

	@Override
	public String repair(List<String> data) {
		Map<String, Integer> staddMap = new HashMap<String, Integer>();
		for (String stadd : data) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < stadd.length(); i++) {
				char c = stadd.charAt(i);
				if (c == ' ' || c == ',' || c == '.' || Character.isLetter(c)) {
					sb.append(c);
				}
			}
			String Stadd = sb.toString();
			Integer count = staddMap.get(Stadd);
			if (count == null) {
				staddMap.put(Stadd, 1);
			} else {
				staddMap.put(Stadd, count + 1);
			}
		}
		int count = 0;
		String candidate = "";
		for (Entry<String, Integer> e : staddMap.entrySet()) {
			if (e.getValue() > count) {
				candidate = e.getKey();
				count = e.getValue();
			}
		}
		return candidate;
	}

}
