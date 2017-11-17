public class Player implements Runnable {

	private final Tone tone;
	private final String noteName;
	private volatile boolean isPlaying = false;
	private Thread t;
	Mutex m = new Mutex();

	/**
	 * constructor assigns a player to a note
	 * 
	 * @param plant
	 * @param os
	 */
	public Player(String noteName, Tone tone) {
		this.noteName = noteName;
		this.tone = tone;
		t = new Thread(this);
		t.start();
	}

	/**
	 * should join all the threads back together
	 */
	public void waitToStop() {
		try {
			t.join();
		} catch (InterruptedException e) {
			System.err.println(t.getName() + " stop malfunction");
		}
	}

	/**
	 * begin or end note for the player
	 * 
	 * @param boolean timeToPlay
	 */
	public void setPlaying() {
		this.isPlaying = !isPlaying;
	}

	@Override
	public void run() {
		
	}

	private void playNote() {
		// TODO Auto-generated method stub
//		tone.playNote(bellNote);
	}
}
