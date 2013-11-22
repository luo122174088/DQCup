package dqcup.repair.comp;

import java.util.ArrayList;
import java.util.List;

import dqcup.repair.attr.AttributeContainer;

/**
 * This a modified tuple version
 * 
 * @see {@link dqcup.repair.Tuple}
 * @author luochen
 * 
 */
public class DQTuple {
	private List<Integer> ruids = null;
	private String cuid = null;

	private AttributeContainer[] attributes = null;

	public DQTuple(String cuid, int ruid) {
		this.cuid = cuid;
		ruids = new ArrayList<Integer>(5);
		ruids.add(ruid);
		attributes = new AttributeContainer[AttrCount];

		for (int i = 0; i < AttrCount; i++) {
			attributes[i] = new AttributeContainer();
		}

	}

	/**
	 * Must be invoked sequentially
	 * 
	 * @param index
	 * @param value
	 * @param ruid
	 */
	public void addSingleValue(int index, String value, String origin) {
		attributes[index].add(value, origin);
	}

	public String getCuid() {
		return cuid;
	}

	public void setCuid(String cuid) {
		this.cuid = cuid;
	}

	public List<Integer> getRuids() {
		return ruids;
	}

	public void addRuid(int ruid) {
		this.ruids.add(ruid);
	}

	public AttributeContainer getAttributeContainer(int i) {
		return attributes[i];
	}

	public static final String RUID = "RUID";
	public static final String CUID = "CUID";

	public static final String SSN = "SSN";
	public static final String FNAME = "FNAME";
	public static final String MINIT = "MINIT";
	public static final String LNAME = "LNAME";

	public static final String STNUM = "STNUM";
	public static final String STADD = "STADD";
	public static final String APMT = "APMT";

	public static final String CITY = "CITY";
	public static final String STATE = "STATE";
	public static final String ZIP = "ZIP";

	public static final String BIRTH = "BIRTH";
	public static final String AGE = "AGE";

	public static final String SALARY = "SALARY";
	public static final String TAX = "TAX";

	public static final int Offset = 2;

	public static final int SSN_INDEX = 0;
	public static final int FNAME_INDEX = 1;
	public static final int MINIT_INDEX = 2;
	public static final int LNAME_INDEX = 3;

	public static final int STNUM_INDEX = 4;
	public static final int STADD_INDEX = 5;
	public static final int APMT_INDEX = 6;

	public static final int CITY_INDEX = 7;
	public static final int STATE_INDEX = 8;
	public static final int ZIP_INDEX = 9;

	public static final int BIRTH_INDEX = 10;
	public static final int AGE_INDEX = 11;

	public static final int SALARY_INDEX = 12;
	public static final int TAX_INDEX = 13;

	public static final int AttrCount = 14;

	public static final String[] Attrs = { RUID, CUID, SSN, FNAME, MINIT, LNAME, STNUM, STADD,
			APMT, CITY, STATE, ZIP, BIRTH, AGE, SALARY, TAX };

	public static final String[] Single_Attrs = { SSN, FNAME, MINIT, LNAME, CITY, STATE, ZIP };

}