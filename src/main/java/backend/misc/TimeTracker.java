package backend.misc;

public abstract class TimeTracker {
	private static double timeNew = -1;
	private static double timeOld = -1;
	
	public static void init() {
		System.out.println("===================");
		timeOld = System.currentTimeMillis();
	}
	
	public static void sout() {
		timeNew = System.currentTimeMillis();
		System.out.println(timeNew - timeOld);
		timeOld = timeNew;
	}
}
