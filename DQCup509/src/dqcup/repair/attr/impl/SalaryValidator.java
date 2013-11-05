package dqcup.repair.attr.impl;

import dqcup.repair.attr.AttributeValidator;

public class SalaryValidator implements AttributeValidator {

	@Override
	public boolean validate(String value) {
		try {
			int salary = Integer.valueOf(value);
			if (salary != 0 && (salary < 500 || salary > 20500)) {
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
