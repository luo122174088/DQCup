package dqcup.repair.attr.impl;

import dqcup.repair.attr.AttributeValidator;

public class SSNValidator implements AttributeValidator {

	@Override
	public boolean validate(String value) {
		try {
			if (value.length() != 9) {
				return false;
			}
			Integer.parseInt(value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
