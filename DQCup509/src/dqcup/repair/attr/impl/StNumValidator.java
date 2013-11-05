package dqcup.repair.attr.impl;

import dqcup.repair.attr.AttributeValidator;

public class StNumValidator implements AttributeValidator {

	@Override
	public boolean validate(String value) {
		// verify STNUM
		if (value.isEmpty()) {
			return true;
		}
		int len = value.length();
		if (len > 4) {
			return false;
		}
		for (int i = 0; i < len; i++) {
			char c = value.charAt(i);
			if (!Character.isDigit(c)) {
				return false;
			}
		}
		return true;

	}

}
