package dqcup.repair.attr.impl;

import dqcup.repair.attr.AttributeValidator;

/**
 * 中间名缩写,可为空,或为1位大写字母
 * 
 * @author luochen
 * 
 */
public class MinitValidator implements AttributeValidator {

	@Override
	public boolean valid(String value) {
		if (value == null || value.length() == 0) {
			return true;
		}
		if (value.length() > 1) {
			return false;
		}
		return Character.isUpperCase(value.charAt(0));
	}

}
