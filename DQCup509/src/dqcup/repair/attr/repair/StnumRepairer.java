package dqcup.repair.attr.repair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class StnumRepairer implements AttributeRepairer {

	@Override
	public String repair(List<String> data) {
		Map<String, Integer> stnumMap = new HashMap<String, Integer>();
		for (String stnum : data) {
			StringBuilder sb = new StringBuilder();
			int len = 0;
			for (int i = 0; i < stnum.length(); i++) {
				char c = stnum.charAt(i);
				if (Character.isDigit(c)) {
					sb.append(c);
					len++;
					if (len == 4) {
						break;
					}
				}
			}
			String Stnum = sb.toString();
			Integer count = stnumMap.get(Stnum);
			if (count == null) {
				stnumMap.put(Stnum, 1);
			} else {
				stnumMap.put(Stnum, count + 1);
			}
		}
		int count = 0;
		String candidate = "";
		for (Entry<String, Integer> e : stnumMap.entrySet()) {
			if (e.getValue() > count) {
				candidate = e.getKey();
				count = e.getValue();
			}
		}
		return candidate;
	}

}
