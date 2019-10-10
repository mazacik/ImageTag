package application.settings;

import application.database.list.CustomList;

public class Stack<T> extends CustomList<T> {
	private int maxSize;
	
	public Stack() {
	
	}
	public Stack(int maxSize) {
		this.maxSize = maxSize;
	}
	
	public void push(T t) {
		super.remove(t);
		super.add(0, t);
		if (this.size() > maxSize) this.removeRange(maxSize, this.size());
	}
	public T pop() {
		T value = this.getLast();
		super.remove(value);
		return value;
	}
	
	public int getMaxSize() {
		return maxSize;
	}
	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}
}
