package dqcup.repair.attr.repair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class AgeRepairer implements AttributeRepairer {

	@Override
	public String repair(List<String> data) {
		Map<String, Integer> ageMap = new HashMap<String, Integer>();
		for (String age : data) {
			if (age.length() < 2) {
				continue;
			}
			StringBuilder sb = new StringBuilder();
			int len = 0;
			for (int i = 0; i < age.length(); i++) {
				char c = age.charAt(i);
				if (Character.isDigit(c)) {
					sb.append(c);
					len++;
					if (len == 2) {
						break;
					}
				}
			}
			if (len < 2) {
				continue;
			}
			String Age = sb.toString();
			Integer iAge = Integer.valueOf(Age);
			if (iAge > 83 || iAge < 24) {
				continue;
			}
			Integer count = ageMap.get(Age);
			if (count == null) {
				ageMap.put(Age, 1);
			} else {
				ageMap.put(Age, count + 1);
			}
		}
		int count = 0;
		String candidate = "";
		for (Entry<String, Integer> e : ageMap.entrySet()) {
			if (e.getValue() > count) {
				candidate = e.getKey();
				count = e.getValue();
			}
		}
		return candidate;
	}

}
