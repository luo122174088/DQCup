package dqcup.repair.attr.impl;

import java.util.HashSet;
import java.util.Set;

import dqcup.repair.attr.AttributeValidator;

public class CityValidator implements AttributeValidator {

	private static Set<String> Preps = new HashSet<String>();
	static {
		Preps.add("about");
		Preps.add("across");
		Preps.add("after");
		Preps.add("against");
		Preps.add("among");
		Preps.add("around");
		Preps.add("at");
		Preps.add("before");
		Preps.add("behind");
		Preps.add("below");
		Preps.add("beside");
		Preps.add("but");
		Preps.add("by");
		Preps.add("down");
		Preps.add("during");
		Preps.add("for");
		Preps.add("from");
		Preps.add("in");
		Preps.add("of");
		Preps.add("or");
		Preps.add("on");
		Preps.add("over");
		Preps.add("near");
		Preps.add("round");
		Preps.add("since");
		Preps.add("to");
		Preps.add("under");
		Preps.add("up");
		Preps.add("with");
	}

	@Override
	public boolean validate(String value) {
		if (value == null || value.length() == 0) {
			return false;
		}
		int length = 0;
		boolean upper = true;
		String[] strs = value.split(" ");
		for (String str : strs) {
			if (Preps.contains(str)) {
				continue;
			}
			upper = true;
			length = str.length();
			for (int i = 0; i < length; i++) {
				char c = str.charAt(i);
				if (c == '\'' || c == '-' || c == '/' || c == '.') {
					upper = true;
				} else if (Character.isLetter(c)) {
					if (upper && Character.isLowerCase(c)) {
						return false;
					}
					upper = false;
				} else {
					return false;
				}
			}
		}
		return true;
	}

}
