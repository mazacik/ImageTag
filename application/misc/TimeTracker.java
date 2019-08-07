package application.misc;

import java.util.Date;

public abstract class TimeTracker {
	private static Date msStart;
	
	public static long getTime() {
		return new Date().getTime() - msStart.getTime();
	}
	public static void reset() {
		msStart = new Date();
	}
	public static void print() {
		System.out.println(getTime() + "ms");
	}
	public static void printReset() {
		print();
		reset();
	}
}
