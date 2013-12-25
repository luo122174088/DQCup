package dqcup.repair.attr.repair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FNameRepairer implements AttributeRepairer {

	@Override
	public String repair(List<String> data) {
		Map<String, Integer> nameMap = new HashMap<String, Integer>();
		for (String name : data) {
			StringBuilder sb = new StringBuilder();
			if (name.length() == 0) {
				continue;
			}
			name = name.toLowerCase();
			for (int i = 0; i < name.length(); i++) {
				char c = name.charAt(i);
				if (c == ',' || c == '.' || Character.isLetter(c)) {
					sb.append(c);
				}
			}
			if (sb.length() == 0) {
				continue;
			}

			sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
			String Name = sb.toString();
			Integer count = nameMap.get(Name);
			if (count == null) {
				nameMap.put(Name, 1);
			} else {
				nameMap.put(Name, count + 1);
			}
		}
		int count = 0;
		String candidate = "";
		for (Entry<String, Integer> e : nameMap.entrySet()) {
			if (e.getValue() > count) {
				candidate = e.getKey();
				count = e.getValue();
			}
		}
		return candidate;
	}

}
