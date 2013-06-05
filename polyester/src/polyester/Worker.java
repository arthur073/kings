package polyester;

import lights.interfaces.ITupleSpace;

public abstract class Worker implements Runnable {

	public static final int VERB = 0;
	public static final int PAYLOAD = 1;
	protected ITupleSpace space;
	
	private volatile Thread blinker; //used for thread stopping condition
	public static final int PAUSE = 10; //wait in thread loop in millisecs 
	//alan 2009/apr/12 : pause reduced to 10 ms since now the worker thread waits for new messages without wasting resources, it should be reactive 

	public Worker(ITupleSpace space) {
		this.space = space;
	}
	
	public abstract void work();
	
	public void start() {
		blinker = new Thread(this);
		blinker.start();
	}
	
	public void stop() {
		blinker = null;
	}
	
	public void run() {
		Thread thisThread = Thread.currentThread();
		while (blinker == thisThread) {
			try {
				thisThread.sleep(PAUSE);
				work();
				} 
			catch (InterruptedException e) {
					return;
			}
		}
	}


}