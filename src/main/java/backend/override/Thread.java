package backend.override;

import main.Main;

public class Thread extends java.lang.Thread {
	public Thread(Runnable target) {
		super(Main.THREADS, target);
	}
}
