package backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

public class BaseList<T> extends ArrayList<T> {
	public BaseList() {
		super();
	}
	public BaseList(T[] c) {
		super(Arrays.asList(c));
	}
	public BaseList(Collection<? extends T> c) {
		super(c);
	}
	
	public boolean add(T t, boolean checkDuplicates) {
		if (!checkDuplicates || !super.contains(t)) {
			return super.add(t);
		}
		return false;
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
		super.clear();
		return super.add(t);
	}
	public boolean setAll(Collection<? extends T> c) {
		super.clear();
		return super.addAll(c);
	}
	
	public void replace(T t1, T t2) {
		int index = indexOf(t1);
		if (index != -1) set(index, t2);
	}
	
	public boolean containsAny(Collection<? extends T> c) {
		for (T t : c)
			if (contains(t))
				return true;
		return false;
	}
	
	public final T getFirst() {
		return (!this.isEmpty()) ? this.get(0) : null;
	}
	public final T getLast() {
		return (!this.isEmpty()) ? this.get(this.size() - 1) : null;
	}
	public final T getRandom() {
		return (!this.isEmpty()) ? this.get(new Random().nextInt(this.size())) : null;
	}
}
