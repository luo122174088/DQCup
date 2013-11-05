package dqcup.repair.attr.impl;

import dqcup.repair.attr.AttributeValidator;

public class TaxValidator implements AttributeValidator {

	@Override
	public boolean validate(String value) {
		try {
			int tax = Integer.valueOf(value);
			if (tax > 20500) {
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
