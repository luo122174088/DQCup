package dqcup.repair.attr.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import dqcup.repair.attr.AttributeValidator;

public class BirthValidator implements AttributeValidator {
	private static DateFormat format = new SimpleDateFormat("MM-dd-yyyy");
	static {
		format.setLenient(false);
	}

	public boolean validate(String value) {
		try {
			Date date = format.parse(value);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int year = calendar.get(Calendar.YEAR);
			if (year > 1989 || year < 1930) {
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
