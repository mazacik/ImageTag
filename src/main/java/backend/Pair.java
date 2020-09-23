package backend;

import javafx.beans.NamedArg;

import java.io.Serializable;
import java.util.Objects;

public class Pair<K, V> implements Serializable {
	private K key;
	private V value;
	
	public Pair(@NamedArg("key") K key, @NamedArg("value") V value) {
		this.key = key;
		this.value = value;
	}
	
	public K getKey() {
		return key;
	}
	public V getValue() {
		return value;
	}
	
	public void setKey(K key) {
		this.key = key;
	}
	public void setValue(V value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return key + "=" + value;
	}
	
	@Override
	public int hashCode() {
		// name's hashCode is multiplied by an arbitrary prime number (13)
		// in order to make sure there is a difference in the hashCode between
		// these two parameters:
		// name: a  value: aa
		// name: aa value: a
		return key.hashCode() * 13 + (value == null ? 0 : value.hashCode());
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o instanceof Pair) {
			@SuppressWarnings("unchecked") Pair<K, V> pair = (Pair<K, V>) o;
			if (!Objects.equals(key, pair.key)) return false;
			return Objects.equals(value, pair.value);
		}
		return false;
	}
}

