package dqcup.repair.attr.impl;

import dqcup.repair.attr.AttributeValidator;

/**
 * 邮政编码,五位纯数字
 * 
 * @author luochen
 * 
 */
public class ZipValidator implements AttributeValidator {
	@Override
	public boolean validate(String value) {
		if (value == null) {
			return false;
		}
		if (value.length() != 5) {
			return false;
		}
		for (int i = 0; i < 5; i++) {
			if (!Character.isDigit(value.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}
