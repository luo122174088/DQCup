package dqcup.repair.attr.repair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ApmtRepairer implements AttributeRepairer {

	@Override
	public String repair(List<String> data) {
		Map<String, Integer> apmtMap = new HashMap<String, Integer>();
		for (String apmt : data) {
			StringBuilder sb = new StringBuilder();
			if (apmt.length() < 3) {
				continue;
			}
			boolean nextNum = true;
			int len = 0;
			apmt = apmt.toLowerCase();
			for (int i = 0; i < apmt.length(); i++) {
				char c = apmt.charAt(i);
				if (nextNum && Character.isDigit(c)) {
					sb.append(c);
					nextNum = false;
					len++;
				} else if (!nextNum && Character.isLetter(c)) {
					sb.append(c);
					nextNum = true;
					len++;
				}
				if (len == 3) {
					break;
				}
			}
			if (len < 3) {
				continue;
			}
			String Ampt = sb.toString();
			Integer count = apmtMap.get(Ampt);
			if (count == null) {
				apmtMap.put(Ampt, 1);
			} else {
				apmtMap.put(Ampt, count + 1);
			}
		}
		int count = 0;
		String candidate = "";
		for (Entry<String, Integer> e : apmtMap.entrySet()) {
			if (e.getValue() > count) {
				candidate = e.getKey();
				count = e.getValue();
			}
		}
		return candidate;

	}

}
