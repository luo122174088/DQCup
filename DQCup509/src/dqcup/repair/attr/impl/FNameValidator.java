package dqcup.repair.attr.impl;

import dqcup.repair.attr.AttributeValidator;

/**
 * First name,可能包含字母,逗号及句号,首字母大写, shared with Last name
 * 
 * @author luochen
 * 
 */
public class FNameValidator implements AttributeValidator {

	@Override
	public boolean valid(String value) {
		if (value == null || value.length() == 0) {
			return false;
		}
		boolean upper = true;
		int len = value.length();
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
			} else if (c == ',' && c == '.') {
				upper = true;
			} else {
				return false;
			}
		}
		return true;
	}
}
