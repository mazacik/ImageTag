package application.tools;

public abstract class Stopwatch {
	private static long startTime;
	
	public static void start() {
		startTime = System.nanoTime();
	}
	public static void stop() {
		System.out.println((System.nanoTime() - startTime) / 1000000);
	}
}
