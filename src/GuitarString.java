/*
 #############################################################################
 ###                                                                       ###
 ### Title:         Guitar Hero                                            ###
 ###                                                                       ###
 ### Files:         GuitarString.java            	                       ###
 ### Author(s):     Michael Metz (mime9599@colorado.edu)                   ###
 ### Semester:      Spring 2021                                            ###
 ### Written:       March 16, 2021                                         ###
 ### Description:   A single guitar string simulated using the             ###
 ###				 Karplus-Strong algorithm                              ###
 ### License:                                                              ###
 ### Credits:                                                              ###
 #############################################################################
 */


import java.util.Iterator;

/**
 * This class represents a single guitar string or piano wire, which is simulated using
 * the Karplus-Strong algorithm. When a guitar string is plucked or a piano wire struck,
 * it vibrates and creates sound. The vibration can be measured by sampling the
 * displacement of the wire at equally spaced points. These displacement measurements
 * can be stored digitally, say in a list or queue structure, then used to recreate
 * the sound wave over a speaker.
 * <p>
 * More specifically, the length of a string or wire determines its fundamental frequency
 * of vibration. We model a guitar string by sampling its displacement
 * (a real number between -1/2 and +1/2) at N equally spaced points (in time),
 * where N equals the sampling rate (44,100) divided by the fundamental frequency
 * (rounding the quotient up to the nearest integer)
 *
 * @author Michael M
 */
public class GuitarString {

	// Karplus-Strong energy decay factor
	public static final double ENGERGY_DECAY_FACTOR = 0.994;

	// Queue to hold the displacement values at equally-spaced points along the guitar string
	private FixedSizeQueue<Double> queue;

	// Number of time steps that have been simulated
	private int numTics;



	/**
	 * Create a new guitar string for a certain frequency. For example, setting
	 * frequency to 440 would give you a string tuned to Concert A.
	 * <p>
	 * Psuedocode:
	 * 1. Calculate N: the length of the queue you will need for the specified frequency
	 * where N = the sampling rate 44,100 divided by frequency, rounded up to the
	 * nearest integer
	 * <p>
	 * 2. Initialize your queue using this calculated capacity N and initialize
	 * the queue to represent a guitar string at rest by enqueueing N zeros.
	 *
	 * @param frequency The frequency for the guitar string
	 */
	public GuitarString(double frequency) {
		int N = (int) Math.ceil(frequency);
		queue = new FixedSizeQueue<>(N);

		for (int i = 0; i < N-1; i++) {
			queue.enqueue(0.0);
		}
	}



	/**
	 * Simulates plucking a guitar string.
	 * <p>
	 * Psuedocode:
	 * Set the queue to white noise by replacing all N items in the queue with N random
	 * values between -0.5 and +0.5
	 * <p>
	 */
	public void pluck() {
		for (int i = 0; i < queue.size(); i++) {
			queue.dequeue();
			queue.enqueue(Math.random() * ((-0.5 - 0.5) + 1.0) + -0.5);
		}
	}



	/**
	 * Simulates one time step by applying the Karplus-Strong update.
	 * <p>
	 * Psuedocode:
	 * 1. Remove the sample at the front of the queue, storing its value in a temporary variable
	 * <p>
	 * 2. Add to the end of the queue the average of the first two samples (i.e., the sample you
	 * just removed and the sample that is now at the front of the queue)
	 * multiplied by the energy decay factor.
	 * <p>
	 * 3. Update numTics
	 */
	public void tic() {
		double temp;

		temp = queue.dequeue();
		queue.enqueue(((temp + queue.peek()) / 2) * ENGERGY_DECAY_FACTOR);
		numTics++;
	}



	/**
	 * Get the sample value that is at the front of the queue (do not remove this value
	 * from the queue).
	 *
	 * @return The sample value currently at the front of the queue
	 */
	public double sample() {
//		if (queue.isEmpty()) { return 0.0; }
		return queue.peek();
	}



	/**
	 * Gets the total number of times tic() has been called (i.e., the numer of time-steps
	 * we have simulated so far).
	 *
	 * @return The number of times the tic() method has been called
	 */
	public int time() {
		return numTics;
	}

}
