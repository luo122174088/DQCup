package dqcup.repair.attr.repair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CityRepairer implements AttributeRepairer {

	@Override
	public String repair(List<String> data) {
		Map<String, Integer> cityMap = new HashMap<String, Integer>();
		for (String city : data) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < city.length(); i++) {
				char c = city.charAt(i);
				if ((c == '/') || (c == '.') || (c == '-') || (c == ' ') || (c == '\'')
						|| (Character.isLetter(c))) {
					sb.append(c);
				}
			}
			String City = sb.toString();
			Integer count = cityMap.get(City);
			if (count == null) {
				cityMap.put(City, 1);
			} else {
				cityMap.put(City, count + 1);
			}
		}
		int count = 0;
		String candidate = "";
		for (Entry<String, Integer> e : cityMap.entrySet()) {
			if (e.getValue() > count) {
				candidate = e.getKey();
				count = e.getValue();
			}
		}
		return candidate;
	}

}
