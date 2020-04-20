package server.thread;

import main.Root;

public class Thread extends java.lang.Thread {
	public Thread(Runnable target) {
		super(Root.THREADPOOL, target);
	}
}
