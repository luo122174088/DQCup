package dqcup.repair.attr.composite.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * BIRTH:出生月日年,年份范围从1930到1989 AGE:到2013年的年龄
 * 
 * @author luochen
 * 
 */
public class BirthAgeValidator {
	private static DateFormat format = new SimpleDateFormat("MM-dd-yyyy");
	static {
		format.setLenient(false);
	}
	public static final int Invalid_Birth = 0x1;
	public static final int Invalid_Age = 0x2;
	public static final int Invalid_Conflit = 0x4;

	public int strictValidate(String birth, String age) {
		int result = 0;
		int year = -1;
		try {
			Date date = format.parse(birth);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			year = calendar.get(Calendar.YEAR);
			if (year > 1989 || year < 1930) {
				result |= Invalid_Birth;
			}
		} catch (Exception e) {
			result |= Invalid_Birth;
		}
		try {
			int iAge = Integer.valueOf(age);
			if (year > 0 && iAge != 2013 - year) {
				result |= Invalid_Age;
			}
		} catch (Exception e) {
			result |= Invalid_Age;
		}
		return result;
	}

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
