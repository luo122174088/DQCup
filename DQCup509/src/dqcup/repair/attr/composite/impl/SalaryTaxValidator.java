package dqcup.repair.attr.composite.impl;

public class SalaryTaxValidator {
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
