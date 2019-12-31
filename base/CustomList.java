package base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
//todo check for refactor
public class CustomList<T> extends ArrayList<T> {
	public CustomList() {
		super();
	}
	public CustomList(T[] c) {
		super(Arrays.asList(c));
	}
	public CustomList(Collection<? extends T> c) {
		super(c);
	}
	
	public boolean add(T t) {
		return this.add(t, false);
	}
	public boolean add(T t, boolean checkDuplicates) {
		if (t != null) {
			if (!checkDuplicates || !super.contains(t)) {
				return super.add(t);
			}
		}
		return false;
	}
	
	public void add(int index, T t) {
		if (t != null) {
			super.remove(t);
			super.add(index, t);
		}
	}
	
	public boolean addAll(Collection<? extends T> c) {
		return this.addAll(c, false);
	}
	public boolean addAll(Collection<? extends T> c, boolean checkDuplicates) {
		int size = this.size();
		c.forEach(t -> this.add(t, checkDuplicates));
		return size != this.size();
	}
	
	public boolean addAll(T[] c) {
		return this.addAll(c, false);
	}
	public boolean addAll(T[] c, boolean checkDuplicates) {
		int size = this.size();
		for (T t : c) {
			this.add(t, checkDuplicates);
		}
		return size != this.size();
	}
	
	public boolean set(T t) {
		this.clear();
		return this.add(t);
	}
	public boolean setAll(Collection<? extends T> c) {
		this.clear();
		return this.addAll(c);
	}
	
	public T getFirst() {
		if (!this.isEmpty()) {
			return this.get(0);
		} else {
			return null;
		}
	}
	public T getLast() {
		if (!this.isEmpty()) {
			return this.get(this.size() - 1);
		} else {
			return null;
		}
	}
	public T getRandom() {
		if (!this.isEmpty()) {
			int index = new Random().nextInt(this.size());
			return this.get(index);
		} else {
			return null;
		}
	}
}
