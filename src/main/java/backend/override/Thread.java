package backend.override;

import main.Root;

public class Thread extends java.lang.Thread {
	public Thread(Runnable target) {
		super(Root.THREADS, target);
	}
}
