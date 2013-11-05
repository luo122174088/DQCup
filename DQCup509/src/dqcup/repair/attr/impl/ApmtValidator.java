package dqcup.repair.attr.impl;

import dqcup.repair.attr.AttributeValidator;

public class ApmtValidator implements AttributeValidator {

	@Override
	public boolean validate(String value) {
		if (value.isEmpty()) {
			return true;
		}
		int len = value.length();
		if (len != 3) {
			return false;
		}
		return Character.isDigit(value.charAt(0)) && Character.isLowerCase(value.charAt(1))
				&& Character.isDigit(value.charAt(2));
	}

}
