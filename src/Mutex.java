public class Mutex {
	private boolean isAvailable = true;

	// if you modify state, do it on both methods
	public synchronized void acquire() {
		while (!isAvailable)
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		isAvailable = false;
	}
	
	public synchronized void release() {
		isAvailable = true;
		notifyAll();
	}
}
