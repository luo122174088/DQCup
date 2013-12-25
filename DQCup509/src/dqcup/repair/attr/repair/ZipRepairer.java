package dqcup.repair.attr.repair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ZipRepairer implements AttributeRepairer {

	@Override
	public String repair(List<String> data) {
		Map<String, Integer> zipMap = new HashMap<String, Integer>();
		for (String zip : data) {
			if (zip.length() < 5) {
				continue;
			}
			StringBuilder sb = new StringBuilder();
			int len = 0;
			for (int i = 0; i < zip.length(); i++) {
				char c = zip.charAt(i);
				if (Character.isDigit(c)) {
					sb.append(c);
					len++;
					if (len == 5) {
						break;
					}
				}
			}
			if (len < 5) {
				continue;
			}
			String Zip = sb.toString();
			Integer count = zipMap.get(Zip);
			if (count == null) {
				zipMap.put(Zip, 1);
			} else {
				zipMap.put(Zip, count + 1);
			}
		}
		int count = 0;
		String candidate = "";
		for (Entry<String, Integer> e : zipMap.entrySet()) {
			if (e.getValue() > count) {
				candidate = e.getKey();
				count = e.getValue();
			}
		}
		return candidate;
	}

}
