package dqcup.repair.attr.composite.impl;

/**
 * SSN:社保号码,9位纯数字,全为0代表无社保卡(无工作),不全为0时是全局唯一的(任何两人社保号不同)
 * SALARY:薪水,在500和20500范围内的纯数字
 * TAX:赋税,起征点为1500,工资超过1500的部分每个州有不同的税率,同一州内,薪水相同的人赋税也相同,薪水越高赋税越高
 * 
 * @author luochen
 * 
 */
@Deprecated
public class SSNSalaryTaxValidator {
	private static final String Null_SSN = "000000000";

	public boolean validate(String SSN, String salary, String tax) {
		try {
			int iSalary = Integer.valueOf(salary);
			int iTax = Integer.valueOf(tax);
			if (SSN.length() != 9) {
				return false;
			}
			Integer.parseInt(SSN);
			if (SSN.equals(Null_SSN)) {
				return iSalary == 0 && iTax == 0;
			} else {
				if (iSalary < 500 || iSalary > 20500) {
					return false;
				}
				if (iSalary > 1500 && iTax == 0) {
					return false;
				}
				return true;
			}

		} catch (Exception e) {
			return false;
		}
	}
}
