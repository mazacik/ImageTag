package application.backend.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

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
		return add(t, false);
	}
	public boolean add(T t, boolean skipDupCheck) {
		if (t != null) {
			if (skipDupCheck || !super.contains(t)) {
				return super.add(t);
			}
		}
		return false;
	}
	
	public void add(int index, T t) {
		if (t != null && !super.contains(t)) {
			super.add(index, t);
		}
	}
	
	public boolean addAll(Collection<? extends T> c) {
		return addAll(c, false);
	}
	public boolean addAll(Collection<? extends T> c, boolean skipDupCheck) {
		int size = this.size();
		for (T t : c) {
			this.add(t, skipDupCheck);
		}
		return size != this.size();
	}
	public boolean addAll(T[] c) {
		return addAll(c, false);
	}
	public boolean addAll(T[] c, boolean skipDupCheck) {
		int size = this.size();
		for (T t : c) {
			this.add(t, skipDupCheck);
		}
		return size != this.size();
	}
	
	public boolean setAll(Collection<? extends T> c) {
		this.clear();
		return this.addAll(c, true);
	}
	public boolean setAll(T[] c) {
		this.clear();
		return this.addAll(c, true);
	}
	
	public boolean containsAny(Collection<? extends T> c) {
		CustomList<T> helper = new CustomList<>(this);
		helper.retainAll(c);
		return !helper.isEmpty();
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
