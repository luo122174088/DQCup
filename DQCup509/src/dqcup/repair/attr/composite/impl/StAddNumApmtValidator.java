package dqcup.repair.attr.composite.impl;

/**
 * 
 * @author luochen
 * 
 */
public class StAddNumApmtValidator {

	public static final String POBox_Prefix = "PO Box ";

	public static final int Invalid_StAdd = 0x1;

	public static final int Invalid_StNum = 0x2;

	public static final int Invalid_Apmt = 0x4;

	public static final int Invalid_Conflict = 0x8;

	public int strictValidate(String stAdd, String stNum, String apmt) {
		int result = 0;
		if (stAdd.startsWith(POBox_Prefix)) {
			// PO BOX xxxx
			String[] addrs = stAdd.split("\\s");
			if (addrs.length != 3) {
				result = result | Invalid_StAdd;
			} else {
				for (int i = 0; i < addrs[2].length(); i++) {
					if (!Character.isDigit(addrs[2].charAt(i))) {
						result = result | Invalid_StAdd;
						break;
					}
				}
			}
			if (!stNum.isEmpty()) {
				result = result | Invalid_StNum;
			}
			if (!apmt.isEmpty()) {
				result = result | Invalid_Apmt;
			}
			return result;
		} else {
			int len = stAdd.length();
			// verify STADD
			for (int i = 0; i < len; i++) {
				char c = stAdd.charAt(i);
				if (!Character.isLetter(c) && c != ' ' && c != ',' && c != '.') {
					result |= Invalid_StAdd;
					break;
				}
			}
			len = stNum.length();
			if (len < 1 || len > 4) {
				result |= Invalid_StNum;
			} else {
				for (int i = 0; i < len; i++) {
					if (!Character.isDigit(stNum.charAt(i))) {
						result |= Invalid_StNum;
						break;
					}
				}
			}

			len = apmt.length();
			if (len != 3) {
				result |= Invalid_Apmt;
			} else if (!(Character.isDigit(apmt.charAt(0)) && Character.isLowerCase(apmt.charAt(1)) && Character
					.isDigit(apmt.charAt(2)))) {
				result |= Invalid_Apmt;
			}
			return result;
		}

	}

	public boolean validate(String stAdd, String stNum, String apmt) {
		if (stAdd.startsWith(POBox_Prefix)) {
			return stNum.isEmpty() && apmt.isEmpty();
		} else if (stNum.isEmpty() || apmt.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}
}
