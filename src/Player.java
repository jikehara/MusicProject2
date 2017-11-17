
/**
 * Class pulled from Nate Williams: https://github.com/YogoGit/JuiceBottler
 * 
 * @author Joseph Ikehara significant changes made by Joseph, but there is still
 *         some of Nate's code ~ 70% my code Some edits from Nate Williams
 */

public class Player implements Runnable {

	private final Tone tone;
	private final BellNote bellNote;
	private volatile boolean timeToPlay;
	private Thread t;

	/**
	 * constructor assigns a player to a note and 
	 * 
	 * @param plant
	 * @param os
	 */
	public Player(BellNote bellNote, Tone tone) {
		this.bellNote = bellNote;
		this.tone = tone;
		setTimeToPlay(true);
		t = new Thread(this);
		t.start();
	}

	/**
	 * should end the run() method
	 */
	public void stopPlayer() {
		setTimeToPlay(false);
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
	 * check whether the program is running or not
	 * 
	 * @return
	 */
	public boolean isTimeToWork() {
		return timeToPlay;
	}

	/**
	 * begin or end note for the player
	 * 
	 * @param boolean timeToPlay
	 */
	public void setTimeToPlay(boolean timeToPlay) {
		this.timeToPlay = timeToPlay;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(timeToPlay) {
			playNote();
		}
	}

	private void playNote() {
		// TODO Auto-generated method stub
//		tone.playNote(bellNote);
	}
}
