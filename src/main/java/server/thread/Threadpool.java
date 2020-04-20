package server.thread;

public class Threadpool extends ThreadGroup {
	public Threadpool(String name) {
		super(name);
	}
	public Threadpool(ThreadGroup parent, String name) {
		super(parent, name);
	}
	
	public void interruptAll() {
		Thread[] threads = new Thread[this.activeCount()];
		this.enumerate(threads);
		for (Thread thread : threads) {
			if (thread.isAlive()) {
				thread.interrupt();
			}
		}
	}
}
