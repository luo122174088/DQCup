package dqcup.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MultiMap<K, V extends Comparable<V>> implements Iterable<Entry<K, List<V>>> {
	protected Map<K, List<V>> map;

	public MultiMap() {
		map = new HashMap<K, List<V>>();
	}

	public int size() {
		return map.size();
	}

	public void add(K key, V value) {
		List<V> list = map.get(key);
		if (list == null) {
			list = new LinkedList<V>();
			map.put(key, list);
		}
		list.add(value);
	}

	public List<V> get(K key) {
		return map.get(key);
	}

	public K maximalKey() {
		int count = 0;
		K candidate = null;
		for (Entry<K, List<V>> e : map.entrySet()) {
			int size = e.getValue().size();
			if (candidate == null || count < size) {
				candidate = e.getKey();
				count = size;
			}
		}
		return candidate;
	}

	@Override
	public Iterator<Entry<K, List<V>>> iterator() {
		return map.entrySet().iterator();
	}

}
