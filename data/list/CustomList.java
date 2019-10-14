package application.data.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class CustomList<T> extends ArrayList<T> {
	public CustomList() {
		super();
	}
	public CustomList(Collection<? extends T> c) {
		super(c);
	}
	
	/*
	 * Local method addAll() is using the local method add().
	 * However, for some reason, in classes extending this one,
	 * their addAll() was being directed at THEIR add() method
	 * instead of the local add(), often creating an unwanted loop.
	 * _add is a workaround to this issue.
	 */
	private boolean _add(T t) {
		if (t != null && !super.contains(t)) return super.add(t);
		else return false;
	}
	
	public boolean add(T t) {
		return _add(t);
	}
	public void add(int index, T t) {
		if (t != null && !super.contains(t)) super.add(index, t);
	}
	public boolean addAll(Collection<? extends T> c) {
		int size = this.size();
		for (T t : c) this._add(t);
		return size != c.size();
	}
	public boolean setAll(Collection<? extends T> c) {
		this.clear();
		return this.addAll(c);
	}
	
	public boolean containsAny(Collection<? extends T> c) {
		CustomList<T> helper = new CustomList<>(this);
		helper.retainAll(c);
		return !helper.isEmpty();
	}
	
	public T getFirst() {
		if (this.isEmpty()) return null;
		return this.get(0);
	}
	public T getLast() {
		if (this.isEmpty()) return null;
		return this.get(this.size() - 1);
	}
	public T getRandom() {
		if (this.isEmpty()) return null;
		int index = new Random().nextInt(this.size());
		return this.get(index);
	}
}
