package dqcup.repair.attr.impl;

import dqcup.repair.attr.AttributeValidator;

public class AgeValidator implements AttributeValidator {

	@Override
	public boolean validate(String value) {
		try {
			int age = Integer.valueOf(value);
			return (age >= 14 && age <= 83);
		} catch (Exception e) {
			return false;
		}
	}

}
