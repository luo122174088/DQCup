package dqcup.repair.attr.impl;

import dqcup.repair.attr.AttributeValidator;

public class CityValidator implements AttributeValidator {

	@Override
	public boolean valid(String value) {
		if (value == null || value.length() == 0) {
			return false;
		}
		int length = value.length();
		boolean upper = true;
		for (int i = 0; i < length; i++) {
			char c = value.charAt(i);
			if (c == ' ' || c == '\'' || c == '-' || c == '/' || c == '.') {
				upper = true;
			} else if (Character.isLetter(c)) {
				if (upper && !Character.isUpperCase(c)) {
					return false;
				}
				upper = false;
			} else {
				return false;
			}
		}

		return true;
	}

}
