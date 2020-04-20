package server.base;

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
	
	public boolean addImpl(T t) {
		return this.addImpl(t, false);
	}
	public boolean addImpl(T t, boolean checkDuplicates) {
		if (t != null) {
			if (!checkDuplicates || !super.contains(t)) {
				return super.add(t);
			}
		}
		return false;
	}
	
	public boolean addAllImpl(Collection<? extends T> c) {
		return this.addAllImpl(c, false);
	}
	public boolean addAllImpl(Collection<? extends T> c, boolean checkDuplicates) {
		int sizeOld = this.size();
		for (T t : c) {
			if (!checkDuplicates || !super.contains(t)) {
				super.add(t);
			}
		}
		return sizeOld != this.size();
	}
	
	public boolean setImpl(T t) {
		this.clear();
		return this.addImpl(t);
	}
	public boolean setAllImpl(Collection<? extends T> c) {
		this.clear();
		return this.addAllImpl(c);
	}
	
	public boolean removeImpl(T t) {
		return super.remove(t);
	}
	public boolean removeAllImpl(Collection<?> c) {
		return super.removeAll(c);
	}
	
	public T getFirstImpl() {
		return (!this.isEmpty()) ? this.get(0) : null;
	}
	public T getLastImpl() {
		return (!this.isEmpty()) ? this.get(this.size() - 1) : null;
	}
	public T getRandomImpl() {
		return (!this.isEmpty()) ? this.get(new Random().nextInt(this.size())) : null;
	}
}
