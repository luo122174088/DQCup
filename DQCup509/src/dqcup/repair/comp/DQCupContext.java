package dqcup.repair.comp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import dqcup.repair.RepairedCell;
import dqcup.repair.Tuple;

public class DQCupContext {
	private LinkedList<Tuple> tuples;

	private HashSet<RepairedCell> repairs;

	private Map<String, Object> map; // used for communicating among processors

	public DQCupContext(LinkedList<Tuple> tuples, HashSet<RepairedCell> repairs) {
		this.tuples = tuples;
		this.repairs = repairs;
		this.map = new HashMap<String, Object>();
	}

	public LinkedList<Tuple> getTuples() {
		return tuples;
	}

	public void setTuples(LinkedList<Tuple> tuples) {
		this.tuples = tuples;
	}

	public HashSet<RepairedCell> getRepairs() {
		return repairs;
	}

	public void setRepairs(HashSet<RepairedCell> repairs) {
		this.repairs = repairs;
	}

	public Object get(String key) {
		return map.get(key);
	}

	public void set(String key, Object value) {
		map.put(key, value);
	}

}
