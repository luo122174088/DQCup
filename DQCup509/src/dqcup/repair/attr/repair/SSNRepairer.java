package dqcup.repair.attr.repair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SSNRepairer implements AttributeRepairer {

	@Override
	public String repair(List<String> data) {
		Map<String, Integer> ssnMap = new HashMap<String, Integer>();
		for (String ssn : data) {
			if (ssn.length() < 9) {
				continue;
			}
			StringBuilder sb = new StringBuilder();
			int len = 0;
			for (int i = 0; i < ssn.length(); i++) {
				char c = ssn.charAt(i);
				if (Character.isDigit(c)) {
					sb.append(c);
					len++;
					if (len == 9) {
						break;
					}
				}
			}
			if (len < 9) {
				continue;
			}
			String SSN = sb.toString();
			Integer count = ssnMap.get(SSN);
			if (count == null) {
				ssnMap.put(SSN, 1);
			} else {
				ssnMap.put(SSN, count + 1);
			}
		}
		int count = 0;
		String candidate = "";
		for (Entry<String, Integer> e : ssnMap.entrySet()) {
			if (e.getValue() > count) {
				candidate = e.getKey();
				count = e.getValue();
			}
		}
		return candidate;
	}

}
