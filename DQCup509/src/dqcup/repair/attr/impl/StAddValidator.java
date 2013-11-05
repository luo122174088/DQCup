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
				Integer.parseInt(addrs[2]);
				return true;
			} else {
				int len = value.length();
				// verify STADD
				for (int i = 0; i < len; i++) {
					char c = value.charAt(i);
					if (!Character.isLetter(c) && c != ' ' && c != ',' && c != '.') {
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
