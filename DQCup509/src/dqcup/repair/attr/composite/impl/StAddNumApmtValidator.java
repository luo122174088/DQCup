package dqcup.repair.attr.composite.impl;

/**
 * 
 * @author luochen
 * 
 */
public class StAddNumApmtValidator {

	private static final String POBox_Prefix = "PO Box";

	public boolean validate(String stAdd, String stNum, String apmt) {
		if (stAdd.startsWith(POBox_Prefix)) {
			return stNum.isEmpty() && apmt.isEmpty();
		} else {
			return true;
		}
	}
}
