package dqcup.repair.attr.impl;

import dqcup.repair.attr.AttributeValidator;

public class StAddValidator implements AttributeValidator {
	public static final String POBox_Prefix = "PO Box";

	@Override
	public boolean validate(String value) {
		try {
			if (value.startsWith(POBox_Prefix)) {
				// PO BOX xxxx
				String[] addrs = value.split("\\s");
				if (addrs.length != 3) {
					return false;
				}
				if (addrs[2].length() > 4 || addrs[2].length() < 1) {
					return false;
				}
				Integer.parseInt(addrs[2]);
				return true;
			} else {
				int len = value.length();
				// verify STADD
				boolean upper = true;
				for (int i = 0; i < len; i++) {
					char c = value.charAt(i);
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
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}
}
