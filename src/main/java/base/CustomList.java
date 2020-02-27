package base;

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
	
	public boolean addAll(Collection<? extends T> c) {
		return this.addAll(c, false);
	}
	public boolean addAll(Collection<? extends T> c, boolean checkDuplicates) {
		int sizeOld = this.size();
		for (T t : c) {
			if (!checkDuplicates || !super.contains(t)) {
				super.add(t);
			}
		}
		return sizeOld != this.size();
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
		return (!this.isEmpty()) ? this.get(0) : null;
	}
	public T getLast() {
		return (!this.isEmpty()) ? this.get(this.size() - 1) : null;
	}
	public T getRandom() {
		return (!this.isEmpty()) ? this.get(new Random().nextInt(this.size())) : null;
	}
}
