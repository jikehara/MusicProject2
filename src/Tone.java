
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Tone {

	public static void main(String[] args) throws Exception {
		final AudioFormat af = new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, false);
		Tone t = new Tone(af);
		List<BellNote> readInNotes;
		// test whether we have a song, else play Mary Had a Little Lamb
		// must be run in "ant run"
		if (args[0] == null)
			readInNotes = loadNotes("MaryHadALittleLamb.txt");
		else
			readInNotes = loadNotes(args[0]);

		t.playSong(readInNotes);
	}

	/**
	 * Adapted from Nate's TicTacToe_V2 Reads text file of notes in, turns it into a
	 * note list string to enum parser taken from Stack Overflow
	 * https://stackoverflow.com/questions/7056959/convert-string-to-equivalent-enum-value
	 * 
	 * @param filename
	 * @return
	 */
	private static List<BellNote> loadNotes(String filename) {
		final List<BellNote> notes = new ArrayList<>();
		final File file = new File(filename);
		if (file.exists()) {
			try (FileReader fileReader = new FileReader(file); BufferedReader br = new BufferedReader(fileReader)) {
				String line = null;
				while ((line = br.readLine()) != null) {
					String[] noteInfo = line.split(" ");// read note and note length
					String n = noteInfo[0]; // note name
					String nl = noteInfo[1]; // note length as string
					if (noteInfo.length != 2) {
						System.out.println("Too many inputs on this line.");
					}
					nl = formatNoteLength(nl);
					System.out.println(n+" "+nl);
					notes.add(new BellNote(Note.valueOf(n), NoteLength.valueOf(nl)));// add note to the song
				}
			} catch (IOException ignored) {
			}
		} else {
			System.err.println("File '" + filename + "' not found");
		}
		return notes;
	}

	/**
	 * AudioFormat
	 */
	private final AudioFormat af;

	Tone(AudioFormat af) {
		this.af = af;
	}

	void playSong(List<BellNote> song) throws LineUnavailableException {
		try (final SourceDataLine line = AudioSystem.getSourceDataLine(af)) {
			line.open();
			line.start();

			for (BellNote bn : song) {
				playNote(line, bn);
			}
			line.drain();
		}
	}

	/**
	 * Play note according to passed in noteLength
	 * 
	 * @param SourceDataLine
	 *            line
	 * @param BellNote
	 *            bn
	 */
	void playNote(SourceDataLine line, BellNote bn) {
		final int ms = Math.min(bn.length.timeMs(), Note.MEASURE_LENGTH_SEC * 1000);
		final int length = Note.SAMPLE_RATE * ms / 1000;
		line.write(bn.note.sample(), 0, length);
		line.write(Note.REST.sample(), 0, 50);
	}

	/**
	 * Method that changes note length read in to the format that our bell choir can
	 * read Switch Statement syntax from Oracle documentation:
	 * https://docs.oracle.com/javase/tutorial/java/nutsandbolts/switch.html
	 * 
	 * @param length
	 * @return
	 */
	private static String formatNoteLength(String length) {
		String properLength = "WHOLE"; // default to whole note
		// System.out.println(length);
		switch (length) {
		case "8":
			properLength = "EIGHTH";
			break;
		case "4":
			properLength = "QUARTER";
			break;
		case "2":
			properLength = "HALF";
			break;
		case "1":
			properLength = "WHOLE";
			break;
		}
		return properLength;
	}
}

/**
 * 
 * @author Nate Williams
 *
 */
class BellNote {
	final Note note;
	final NoteLength length;

	BellNote(Note note, NoteLength length) {
		this.note = note;
		this.length = length;
	}
}

/**
 * 
 * @author Nate Williams
 *
 */
enum NoteLength {
	WHOLE(1.0f), HALF(0.5f), QUARTER(0.25f), EIGHTH(0.125f);

	private final int timeMs;

	private NoteLength(float length) {
		timeMs = (int) (length * Note.MEASURE_LENGTH_SEC * 1000);// change how long notes are
	}

	public int timeMs() {
		return timeMs;
	}
}

/**
 * 
 * @author Nate Williams
 *
 */
enum Note {
	// REST Must be the first 'Note' - all of the notes playable by the bell choir
	REST, A4, A4S, B4, C4, C4S, D4, D4S, E4, F4, F4S, G4, G4S, A5;

	public static final int SAMPLE_RATE = 48 * 1024; // ~48KHz
	public static final int MEASURE_LENGTH_SEC = 1;

	// Circumference of a circle divided by # of samples
	private static final double step_alpha = (2.0d * Math.PI) / SAMPLE_RATE;

	private final double FREQUENCY_A_HZ = 440.0d;
	private final double MAX_VOLUME = 127.0d;

	private final byte[] sinSample = new byte[MEASURE_LENGTH_SEC * SAMPLE_RATE];

	private Note() {
		int n = this.ordinal();
		if (n > 0) {
			// Calculate the frequency!
			final double halfStepUpFromA = n - 1;
			final double exp = halfStepUpFromA / 12.0d;
			final double freq = FREQUENCY_A_HZ * Math.pow(2.0d, exp);

			// Create sinusoidal data sample for the desired frequency
			final double sinStep = freq * step_alpha;
			for (int i = 0; i < sinSample.length; i++) {
				sinSample[i] = (byte) (Math.sin(i * sinStep) * MAX_VOLUME);
			}
		}
	}

	public byte[] sample() {
		return sinSample;
	}
}
