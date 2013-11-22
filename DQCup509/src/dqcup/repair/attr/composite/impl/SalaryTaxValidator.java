package dqcup.repair.attr.composite.impl;

public class SalaryTaxValidator {

	public static final int Invalid_Salary = 0x1;

	public static final int Invalid_Tax = 0x2;

	public static final int Invalid_Conflict = 0x4;

	public int strictValidate(String salary, String tax) {
		int result = 0;

		int iSalary = -1;
		int iTax = -1;

		try {
			iSalary = Integer.valueOf(salary);
		} catch (Exception e) {
			result |= Invalid_Salary;
		}
		try {
			iTax = Integer.valueOf(tax);
		} catch (Exception e) {
			result |= Invalid_Tax;
		}

		if (iSalary < 0 || iTax < 0) {
			return result;
		}
		if (iSalary == 0 && iTax == 0) {
			return result;
		}
		if (iSalary < 500 || iSalary > 20500) {
			result |= Invalid_Salary;
		}
		if (iSalary > 1500 && iTax == 0) {
			result |= Invalid_Conflict;
		}
		if (iSalary < 1500 && iTax > 0) {
			result |= Invalid_Conflict;
		}
		if (iTax > iSalary) {
			result |= Invalid_Conflict;
		}
		return result;

	}

	public boolean validate(String salary, String tax) {
		try {
			int iSalary = Integer.valueOf(salary);
			int iTax = Integer.valueOf(tax);
			if (iSalary == 0 && iTax == 0) {
				return true;
			}
			if (iSalary < 500 || iSalary > 20500) {
				return false;
			}
			if (iSalary > 1500 && iTax == 0) {
				return false;
			}
			if (iSalary < 1500 && iTax > 0) {
				return false;
			}
			if (iTax > iSalary) {
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
