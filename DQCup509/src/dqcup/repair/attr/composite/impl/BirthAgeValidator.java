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

	public boolean strictValidate(String birth, String age) {
		try {
			Date date = format.parse(birth);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int year = calendar.get(Calendar.YEAR);
			if (year > 1989 || year < 1930) {
				return false;
			}
			int iAge = Integer.valueOf(age);
			return iAge == 2013 - year;
		} catch (Exception e) {
			return false;
		}
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
