package control;

public class Logger {
	//	0 = OFF
	//	1 = ERROR
	//	2 = DEBUG
	private static final int LEVEL = 0;
	
	public Logger() {
	
	}
	
	private void out(String type, String message) {
		System.out.println(type + message);
	}
	
	public void error(String message) {
		if (LEVEL >= 1) this.out("ERROR: ", message);
	}
	public void debug(String message) {
		if (LEVEL >= 2) this.out("DEBUG: ", message);
	}
}
