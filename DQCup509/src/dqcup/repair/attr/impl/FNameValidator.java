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
	public boolean validate(String value) {
		if (value == null || value.length() == 0) {
			return false;
		}
		int len = value.length();
		if (!Character.isUpperCase(value.charAt(0))) {
			return false;
		}
		for (int i = 1; i < len; i++) {
			char c = value.charAt(i);
			if (!Character.isLetter(c) && c != ',' && c != '.') {
				return false;
			}
		}
		return true;
	}
}
