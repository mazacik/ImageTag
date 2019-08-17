package application.database.list;

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
	 * the LOCAL addAll() was being directed at THEIR add() method
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
	public boolean addAll(Collection<? extends T> c) {
		int n = c.size();
		for (T t : c) _add(t);
		return n == c.size();
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
		if (this == null || this.isEmpty()) return null;
		else return this.get(0);
	}
	public T getLast() {
		if (this == null || this.isEmpty()) return null;
		else return this.get(this.size() - 1);
	}
	public T getRandom() {
		return this.getRandom(this);
	}
	public T getRandom(ArrayList<T> arrayList) {
		if (arrayList != null && !arrayList.isEmpty()) {
			int index = new Random().nextInt(arrayList.size());
			return arrayList.get(index);
		} else return null;
	}
}
