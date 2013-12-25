package dqcup.repair.attr.repair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MinitRepairer implements AttributeRepairer {

	@Override
	public String repair(List<String> data) {
		Map<String, Integer> countMinit = new HashMap<String, Integer>();
		for (String minit : data) {
			String Minit = null;
			for (int i = 0; i < minit.length(); i++) {
				char c = minit.charAt(i);
				if (Character.isLetter(c)) {
					Minit = String.valueOf(Character.toUpperCase(c));
					break;
				}
			}
			if (Minit == null) {
				continue;
			}
			if (countMinit.get(Minit) == null) {
				countMinit.put(Minit, 1);
			} else {
				int count = countMinit.get(Minit);
				countMinit.put(Minit, count + 1);
			}
		}
		int max = 0;
		String right = "";
		for (Map.Entry<String, Integer> entry : countMinit.entrySet()) {
			int count = entry.getValue();
			String minit = entry.getKey();
			if (count > max) {
				max = count;
				right = minit;
			}
		}

		return right;
	}

}
