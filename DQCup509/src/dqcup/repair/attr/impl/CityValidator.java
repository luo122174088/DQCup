package dqcup.repair.attr.impl;

import dqcup.repair.attr.AttributeValidator;

public class CityValidator implements AttributeValidator {

	@Override
	public boolean valid(String value) {
		if (value == null || value.length() == 0) {
			return false;
		}
		if (!Character.isUpperCase(value.charAt(0))) {
			return false;
		}
		int length = value.length();
		for (int i = 0; i < length; i++) {
			char c = value.charAt(i);
			if (!Character.isLetter(c) && c != ' ' && c != '\'' && c != '-'
					&& c != '/' && c != '.') {
				return false;
			}
		}

		return true;
	}

}
