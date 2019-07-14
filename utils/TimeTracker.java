package utils;

import main.InstanceManager;

import java.util.Date;

public class TimeTracker {
	private static Date msStart;
	
	public static long getTime() {
		return new Date().getTime() - msStart.getTime();
	}
	public static void reset() {
		msStart = new Date();
	}
	public static void print() {
		InstanceManager.getLogger().debug(getTime() + "ms");
	}
	public static void printReset() {
		print();
		reset();
	}
}
