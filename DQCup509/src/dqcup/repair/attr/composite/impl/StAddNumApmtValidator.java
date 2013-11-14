package dqcup.repair.attr.composite.impl;

/**
 * 
 * @author luochen
 * 
 */
public class StAddNumApmtValidator {

	private static final String POBox_Prefix = "PO Box";

	public boolean strictValidate(String stAdd, String stNum, String apmt) {
		try {
			if (stAdd.startsWith(POBox_Prefix)) {
				// PO BOX xxxx
				String[] addrs = stAdd.split("\\s");
				if (addrs.length != 3) {
					return false;
				}
				Integer.parseInt(addrs[2]);
				return stNum.isEmpty() && apmt.isEmpty();
			} else {
				int len = stAdd.length();
				// verify STADD
				boolean upper = true;
				for (int i = 0; i < len; i++) {
					char c = stAdd.charAt(i);
					if (Character.isLetter(c)) {
						if (upper && Character.isLowerCase(c)) {
							return false;
						}
						if (!upper && Character.isUpperCase(c)) {
							return false;
						}
						upper = false;
					} else if (c == ' ') {
						upper = true;
					} else if (c != ',' && c != '.') {
						return false;
					}
				}

				if (stNum.isEmpty()) {
					return true;
				}
				len = stNum.length();
				if (len > 4) {
					return false;
				}
				for (int i = 0; i < len; i++) {
					char c = stNum.charAt(i);
					if (!Character.isDigit(c)) {
						return false;
					}
				}
				if (apmt.isEmpty()) {
					return true;
				}
				len = apmt.length();
				if (len != 3) {
					return false;
				}
				return Character.isDigit(apmt.charAt(0)) && Character.isLowerCase(apmt.charAt(1))
						&& Character.isDigit(apmt.charAt(2));
			}
		} catch (Exception e) {
			return false;
		}

	}

	public boolean validate(String stAdd, String stNum, String apmt) {
		if (stAdd.startsWith(POBox_Prefix)) {
			return stNum.isEmpty() && apmt.isEmpty();
		} else if (stNum.isEmpty() || apmt.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}
}
