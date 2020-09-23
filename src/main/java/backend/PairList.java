package backend;

public class PairList<K, V> extends BaseList<Pair<K, V>> {
	public boolean containsKey(K key) {
		for (Pair<K, V> pair : this) {
			if (pair.getKey().equals(key)) {
				return true;
			}
		}
		return false;
	}
	
	public Pair<K, V> getPair(K key) {
		for (Pair<K, V> pair : this) {
			if (pair.getKey().equals(key)) {
				return pair;
			}
		}
		return null;
	}
}
