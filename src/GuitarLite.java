/*
 #############################################################################
 ###                                                                       ###
 ### Title:         Guitar Hero                                            ###
 ###                                                                       ###
 ### Files:         GuitarLite.java            	                           ###
 ### Author(s):     Michael Metz (mime9599@colorado.edu)                   ###
 ### Semester:      Spring 2021                                            ###
 ### Written:       March 16, 2021                                         ###
 ### Description:                                                          ###
 ### License:                                                              ###
 ### Credits:                                                              ###
 #############################################################################
 */
import java.util.LinkedList;
import java.util.NoSuchElementException;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * The GuitarLite class is an application that tests your implementation of
 * FixedSizeQueue and GuitarString. It creates a JavaFX application
 * that simulates plucking two guitar strings (A and C) using the 
 * Karplus-Strong algorithm. If everything in FixedSizeQueue and GuitarString
 * is implemented correctly, the user will be able to press A and C on the keyboard
 * to "strum" an A and C guitar string.
 * 
 * @author Daniel Szafir
 */
public class GuitarLite extends Application {
	
	// Queue of keys the user types
	private static LinkedList<KeyCode> keysTyped = new LinkedList<KeyCode>();
	private static Object keyLock = new Object(); // Used for concurrency protection

	// Application width and height, feel free to modify
	public static final int WIDTH = 600;
	public static final int HEIGHT = 400;

	// The A440 concert pitch reference note (https://en.wikipedia.org/wiki/Concert_pitch)
	public static final double CONCERT_A = 440.0; 

	// How many "dots" to use in the guitar string visualization
	public static final int NUM_TO_VISUALIZE = 100;
	private FixedSizeQueue<Double> samples; // A queue of samples to visualize
	private static Object samplesLock = new Object(); // Used for concurrency protection
	public static final int X_MARGIN = 50; // Horizontal padding for the visualization
	public static final int DOT_RADIUS = 5; // How big the dots should be in the visualization

	/**
	 * Entry point to the program, simply calls launch to start the JavaFX
	 * application.
	 * 
	 * @param args No command-line arguments expected
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Main entry point for JavaFX application.
	 * 
	 * @param stage The main stage for the JavaFX application
	 */
	@Override
	public void start(Stage stage) throws Exception {
		
		// Initialize a queue of samples for the visualization of a guitar string
		samples = new FixedSizeQueue<Double>(NUM_TO_VISUALIZE);
		for (int i = 0; i < NUM_TO_VISUALIZE; i++) {
			samples.enqueue(0.0);
		}

		// Create two guitar strings, for concert A and C
		double CONCERT_C = CONCERT_A * Math.pow(2, 3.0/12.0);
		GuitarString stringA = new GuitarString(CONCERT_A);
		GuitarString stringC = new GuitarString(CONCERT_C);

		// Set up the JavaFX stage, scene, and drawing canvas
		stage.setTitle("Guitar Simulation with JavaFX");

		Group root = new Group();
		Scene scene = new Scene(root, WIDTH, HEIGHT);

		// Whenever the user types a key, add it to a queue of keys that have been
		// typed for later code to deal with
		scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
			synchronized (keyLock) {
				keysTyped.addFirst(key.getCode());
			}
		});

		Canvas canvas = new Canvas(WIDTH, HEIGHT);

		GraphicsContext gc = canvas.getGraphicsContext2D();

		root.getChildren().add(canvas);

		stage.setScene(scene);
		stage.show();

		// Animate the guitar string visualization
		AnimationTimer animator = new AnimationTimer() {

			@Override
			public void handle(long timestamp) {
				gc.clearRect(0, 0, WIDTH, HEIGHT); // Clear the drawing
				gc.setFill(Color.FORESTGREEN); // Use green dots for the visualization
				
				// Draw NUM_TO_VISUALIZE dots to visualize the vibrations of a guitar string
				int numDot = 0;
				int dotDrawingRange = WIDTH - X_MARGIN * 2;
				double spaceBetweenDots = dotDrawingRange / NUM_TO_VISUALIZE;
				int verticalScaleFactor = 50; // Used to make the vibrations more visible
				synchronized (samplesLock) {
					for (Double sample : samples) {
						gc.fillOval(X_MARGIN + numDot * spaceBetweenDots, 
								    HEIGHT/2 + sample * verticalScaleFactor, 
								    DOT_RADIUS, DOT_RADIUS);
						numDot++;
					}
				}
			}
		};
		animator.start();

		// Create a new thread for user input through the keyboard to "pluck" various
		// guitar strings
		Thread musicThread = new Thread() {
			public void run() {

				while (true) {
					
					// If the user has typed a key, pluck the appropriate guitar string
					if (hasNextKeyTyped()) {
						KeyCode key = nextKeyTyped();
						if (key == KeyCode.A) { stringA.pluck(); }
						if (key == KeyCode.C) { stringC.pluck(); }
					}

					// compute the superposition of the samples from all guitar strings
					double sample = stringA.sample() + stringC.sample();

					// send the result to audio
					AudioUtils.play(sample);

					// update visualization
					synchronized (samplesLock) {
						try {
							samples.dequeue();
						} catch(NoSuchElementException e) { /* ignore for now */ }
						samples.enqueue(sample);
					}

					// advance the simulation of each guitar string by one step
					stringA.tic();
					stringC.tic();
				}
			}
		};
		musicThread.setDaemon(true); // Ensure the thread dies when we exit the main application
		musicThread.start();
	}

	/**
	 * Checks if the user has typed a key that we haven't dealt with yet
	 * @return true if there are keys remaining in the queue of typed keys, false otherwise
	 */
	public static boolean hasNextKeyTyped() {
		synchronized (keyLock) {
			return !keysTyped.isEmpty();
		}
	}

	/**
	 * Get the next key that the user has typed
	 * 
	 * @throws NoSuchElementException if the queue of typed keys is empty
	 * @return The KeyCode of the next key in the queue of keys that the user has typed
	 */
	public static KeyCode nextKeyTyped() {
		synchronized (keyLock) {
			if (keysTyped.isEmpty()) {
				throw new NoSuchElementException("Error - your program has already processed all keystrokes");
			}
			return keysTyped.remove(keysTyped.size() - 1);
		}
	}
}
