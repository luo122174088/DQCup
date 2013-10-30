package dqcup.repair.comp;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dqcup.repair.Tuple;

/**
 * This a modified tuple version
 * 
 * @see {@link dqcup.repair.Tuple}
 * @author luochen
 * 
 */
public class DQTuple {
	private Set<Integer> ruids = null;

	private String cuid = null;

	private String[] data = new String[AttrCount];

	private BitSet invalidAttrs = null;

	public DQTuple() {
		ruids = new HashSet<Integer>();
	}

	public DQTuple(String[] tuple) {
		ruids = new HashSet<Integer>();
		setDatas(tuple);
	}

	public String getCuid() {
		return cuid;
	}

	public void setCuid(String cuid) {
		this.cuid = cuid;
	}

	public String getData(int index) {
		return data[index];
	}

	public String getData(String name) {
		return data[AttrIndex.get(name)];
	}

	public void setData(int index, String value) {
		data[index] = value;
	}

	public void setData(String name, String value) {
		int index = AttrIndex.get(name);
		data[index] = value;
	}

	public void setDatas(String[] tuple) {
		ruids.clear();
		ruids.add(Integer.valueOf(tuple[0]));
		cuid = tuple[1];

		for (int i = 0; i < AttrCount; i++) {
			data[i] = tuple[i + Offset];
		}
	}

	public String[] getDatas() {
		return data;
	}

	public Set<Integer> getRuids() {
		return ruids;
	}

	public void setInvalidAttrs(BitSet invalidAttrs) {
		this.invalidAttrs = invalidAttrs;
	}

	public BitSet getInvalidAttrs() {
		return this.invalidAttrs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cuid == null) ? 0 : cuid.hashCode());
		return result;
	}

	public boolean equalsTuple(String[] tuple) {
		if (!cuid.equals(tuple[1])) {
			return false;
		}
		for (int i = 0; i < AttrCount; i++) {
			if (!data[i].equals(tuple[i + Offset])) {
				return false;
			}
		}
		return true;
	}

	public boolean equalsWithoutSSN(String[] tuple) {
		if (!cuid.equals(tuple[1])) {
			return false;
		}
		for (int i = 0; i < AttrCount; i++) {
			if (i != SSN_INDEX && !data[i].equals(tuple[i + Offset])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Only test the valid attributes
	 * 
	 * @param tuple
	 * @param invalidAttrs
	 * @return
	 */
	public boolean partialEquals(String[] tuple, BitSet invalidAttrs) {
		if (!cuid.equals(tuple[1])) {
			return false;
		}
		for (int i = 0; i < AttrCount; i++) {
			if (!invalidAttrs.get(i) && !data[i].equals(tuple[i + Offset])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Only test the valid attributes
	 * 
	 * @param tuple
	 * @param invalidAttrs
	 * @return
	 */
	public boolean partialEquals(Tuple tuple, BitSet invalidAttrs) {
		if (!cuid.equals(tuple.getValue(DQTuple.CUID))) {
			return false;
		}
		for (int i = 0; i < AttrCount; i++) {
			if (!invalidAttrs.get(i) && !data[i].equals(tuple.getValue(i + Offset))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DQTuple other = (DQTuple) obj;
		if (cuid == null) {
			if (other.cuid != null)
				return false;
		} else if (!cuid.equals(other.cuid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DQTuple [ruids=" + ruids + ", cuid=" + cuid + ", data=" + Arrays.toString(data)
				+ "]";
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

	public static final int Offset = 2;
	public static final Map<String, Integer> AttrIndex = new HashMap<String, Integer>();
	public static final String[] Attrs = { SSN, FNAME, MINIT, LNAME, STNUM, STADD, APMT, CITY,
			STATE, ZIP, BIRTH, AGE, SALARY, TAX };
	static {
		for (int i = 0; i < Attrs.length; i++) {
			AttrIndex.put(Attrs[i], i);
		}
	}
}