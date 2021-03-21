/* *****************************************************************************
 * Title:            AudioUtils
 * Files:            AudioUtils.java
 * Semester:         Spring 2021
 * 
 * Author:           Daniel Szafir, daniel.szafir@colorado.edu
 * 
 * Description:		 A class for playing in-memory audio.
 * 
 * Written:       	 3/5/2020
 * 
 * Credits:          Based on StdAudio.java 
 * https://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/StdAudio.java.html
 **************************************************************************** */

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 *  <i>AudioUtils</i>. This class provides a basic capability for
 *  creating and playing in-memory audio as a byte buffer. 
 *  <p>
 *  The audio format uses a sampling rate of 44,100 Hz, 16-bit, monaural.
 *
 *  <p>
 *  Based on the StdAudio class 
 *  (https://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/StdAudio.java.html)
 *  by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Daniel Szafir
 */
public final class AudioUtils {

	// The sample rate: 44,100 Hz for CD quality audio.
	public static final int SAMPLE_RATE = 44100;

	private static final int BYTES_PER_SAMPLE = 2;       // 16-bit audio
	private static final int BITS_PER_SAMPLE = 16;       // 16-bit audio
	private static final double MAX_16_BIT = 32768; // 64 bits / double * 2 shorts / byte * 16 bits / sample
	private static final int SAMPLE_BUFFER_SIZE = 4096;

	// Variables for audio format (monaural, sigend, little endian)
	private static final int MONO = 1;
	private static final boolean LITTLE_ENDIAN = false;
	private static final boolean SIGNED = true;

	private static SourceDataLine line;   // to play the sound
	private static byte[] buffer;         // our internal buffer
	private static int bufferSize = 0;    // number of samples currently in internal buffer

	/**
	 * Class (static) initializer to set up audio line
	 */
	static {
		try {
			// 44,100 Hz, 16-bit audio, mono, signed PCM, little endian
			AudioFormat format = new AudioFormat((float) SAMPLE_RATE, BITS_PER_SAMPLE, MONO, SIGNED, LITTLE_ENDIAN);
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(format, SAMPLE_BUFFER_SIZE * BYTES_PER_SAMPLE);

			// the internal buffer is a fraction of the actual buffer size, this choice is arbitrary
			// it gets divided because we can't expect the buffered data to line up exactly with when
			// the sound card decides to push out its samples.
			buffer = new byte[SAMPLE_BUFFER_SIZE * BYTES_PER_SAMPLE/3];
		}
		catch (LineUnavailableException e) {
			System.out.println(e.getMessage());
		}

		// no sound gets made before this call
		line.start();
	}

	/**
	 * Writes one sample (between -1.0 and +1.0) to standard audio.
	 * If the sample is outside the range, it will be clipped.
	 *
	 * @param  sample the sample to play
	 * @throws IllegalArgumentException if the sample is {@code Double.NaN}
	 */
	public static void play(double sample) {
		if (Double.isNaN(sample)) throw new IllegalArgumentException("sample is NaN");

		//System.out.println(sample);

		// clip if outside [-1, +1]
		if (sample < -1.0) sample = -1.0;
		if (sample > +1.0) sample = +1.0;

		// convert to bytes
		short s = (short) (MAX_16_BIT * sample);
		if (sample == 1.0) s = Short.MAX_VALUE;   // special case since 32768 not a short
		buffer[bufferSize++] = (byte) s;
		buffer[bufferSize++] = (byte) (s >> 8);   // little endian

		// send to sound card if buffer is full        
		if (bufferSize >= buffer.length) {
			line.write(buffer, 0, buffer.length);
			bufferSize = 0;
		}
	}

}
