package dqcup.repair.attr.composite.impl;

/**
 * BIRTH:出生月日年,年份范围从1930到1989 AGE:到2013年的年龄
 * 
 * @author luochen
 * 
 */
public class BirthAgeValidator {

	public boolean validate(String birth, String age) {
		try {
			int year = Integer.valueOf(birth.substring(birth.length() - 4));
			int iAge = Integer.valueOf(age);
			return iAge == 2013 - year;
		} catch (Exception e) {
			return false;
		}
	}
}
