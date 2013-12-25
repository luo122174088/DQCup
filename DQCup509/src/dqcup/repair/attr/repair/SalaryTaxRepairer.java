package dqcup.repair.attr.repair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SalaryTaxRepairer implements AttributeRepairer {

	@Override
	public String repair(List<String> data) {
		Map<String, Integer> stMap = new HashMap<String, Integer>();
		for (String st : data) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < st.length(); i++) {
				char c = st.charAt(i);
				if (Character.isDigit(c)) {
					sb.append(c);
				}
			}
			if (sb.length() == 0) {
				continue;
			}
			String St = sb.toString();
			Integer count = stMap.get(St);
			if (count == null) {
				stMap.put(St, 1);
			} else {
				stMap.put(St, count + 1);
			}
		}
		int count = 0;
		String candidate = "";
		for (Entry<String, Integer> e : stMap.entrySet()) {
			if (e.getValue() > count) {
				candidate = e.getKey();
				count = e.getValue();
			}
		}
		return candidate;
	}

}
