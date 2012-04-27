import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DB<K, V> {
	Map<K, V> data = new HashMap<K, V>();
	Map<V, List<K>> index = new HashMap<V, List<K>>();
	List<tItem<K, V>> transactions = new ArrayList<tItem<K, V>>();

	public void set(K key, V value) {
		if (transactions.size() > 0) {
			transactions.add(new tItem<K, V>("SET", key, value));
		} else {
			this._set(key, value);
		}
	}
	
	private void _set(K key, V value) {
		if (data.get(key) != null) {
			this._unset(key);
		}
		data.put(key, value);
		List<K> list = index.get(value);
		if (list == null) {
			list = new ArrayList<K>();
			index.put(value, list);
		}
		list.add(key);
	}

	public V get(K key) {
		for (int i = transactions.size() - 1; i >= 0; i--) {
			tItem<K, V> t = transactions.get(i);
			if (t.key != null && t.key.equals(key)) {
				if (t.action.equals("SET")) {
					return t.value;
				} else if (t.action.equals("UNSET")) {
					return null;
				}
			}
		}
		return data.get(key);
	}

	public void unset(K key) {
		if (transactions.size() > 0) {
			transactions.add(new tItem<K, V>("UNSET", key, null));
		} else {
			this._unset(key);
		}
	}
	
	private void _unset(K key) {
		V value = data.get(key);
		List<K> list = index.get(value);
		if (list != null) {
			list.remove(key);
		}
		data.remove(key);
	}

	public List<K> equalTo(V value) {
		List<K> list = new ArrayList<K>();
		List<K> unset = new ArrayList<K>();
		for (tItem<K, V> t : transactions) {
			if (t.action.equals("UNSET")) {
				unset.add(t.key);
			} else if (t.action.equals("SET") && t.value.equals(value)) {
				unset.remove(t.key);
				list.add(t.key);
			}
		}
		if (index.get(value) != null) {
			List<K> committed = index.get(value);
			for (K key : committed) {
				if (!unset.contains(key)) {
					list.add(key);
				}
			}
		}
		return list;
	}

	public void begin() {
		transactions.add(new tItem<K, V>("BEGIN", null, null));
	}

	public boolean rollback() {
		if (transactions.isEmpty()) {
			return false;
		}
		for (int i = transactions.size() - 1; i >= 0; i--) {
			String action = transactions.get(i).action;
			transactions.remove(i);
			if (action.equals("BEGIN")) {
				break;
			}
		}
		return true;
	}

	public void commit() {
		for (int i = 0; i < transactions.size(); i++) {
			tItem<K, V> t = transactions.get(i);
			if (t.action.equals("SET")) {
				this._set(t.key, t.value);
			} else if (t.action.equals("UNSET")) {
				this._unset(t.key);
			}
		}
		transactions.clear();
	}

	private static class tItem<IK, IV> {
		public String action;
		public IK key;
		public IV value;

		public tItem(String a, IK k, IV v) {
			action = a;
			key = k;
			value = v;
		}
		
		public String toString() {
			return action + " " + key + " " + value;
		}
	}

}
