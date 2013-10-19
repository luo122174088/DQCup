package dqcup.repair.comp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import dqcup.repair.RepairedCell;

public class DQCupContext {
	private String filePath;

	private HashSet<RepairedCell> repairs;

	private Map<String, Object> map; // used for communicating among processors

	public DQCupContext(String filePath, HashSet<RepairedCell> repairs) {
		this.filePath = filePath;
		this.repairs = repairs;
		this.map = new HashMap<String, Object>();
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
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
