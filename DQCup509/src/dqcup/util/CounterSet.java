package dqcup.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class CounterSet<K> implements Iterable<Entry<K, Integer>> {
	private Map<K, Integer> map = new HashMap<K, Integer>();

	public void add(K key) {
		Integer count = map.get(key);
		if (count == null) {
			map.put(key, 1);
		} else {
			map.put(key, count + 1);
		}
	}

	public void add(K key, int count) {
		Integer i = map.get(key);
		if (i == null) {
			map.put(key, count);
		} else {
			map.put(key, i + count);
		}
	}

	public Integer get(K key) {
		return map.get(key);
	}

	public K maximal() {
		int count = 0;
		K candidate = null;
		for (Entry<K, Integer> e : map.entrySet()) {
			if (candidate == null || count < e.getValue()) {
				candidate = e.getKey();
				count = e.getValue();
			}
		}
		return candidate;
	}

	public int size() {
		return map.size();
	}

	@Override
	public Iterator<Entry<K, Integer>> iterator() {
		return map.entrySet().iterator();
	}
}
