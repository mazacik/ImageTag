package misc;

public abstract class Stopwatch {
	private static long startTime;
	
	public static void start() {
		startTime = System.nanoTime();
	}
	public static void ping() {
		long nanoTime = System.nanoTime();
		System.out.println((nanoTime - startTime) / 1000000);
		startTime = nanoTime;
	}
}
