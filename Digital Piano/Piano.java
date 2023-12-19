import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.sound.midi.*;

/**
 * Implements a simulated piano with 36 keys.
 */
public class Piano extends JPanel {
	// DO NOT MODIFY THESE CONSTANTS
	public static int START_PITCH = 48;
	public static int WHITE_KEY_WIDTH = 40;
	public static int BLACK_KEY_WIDTH = WHITE_KEY_WIDTH/2;
	public static int WHITE_KEY_HEIGHT = 200;
	public static int BLACK_KEY_HEIGHT = WHITE_KEY_HEIGHT/2;
	public static int NUM_WHITE_KEYS_PER_OCTAVE = 7;
	public static int NUM_OCTAVES = 3;
	public static int NUM_WHITE_KEYS = NUM_WHITE_KEYS_PER_OCTAVE * NUM_OCTAVES;
	public static int WIDTH = NUM_WHITE_KEYS * WHITE_KEY_WIDTH;
	public static int HEIGHT = WHITE_KEY_HEIGHT;
	
	private java.util.List<Key> _keys = new ArrayList<>();
	private Receiver _receiver;
	private PianoMouseListener _mouseListener;

	/**
	 * Returns the list of keys in the piano.
	 * @return the list of keys.
	 */
	public java.util.List<Key> getKeys () {
		return _keys;
	}

	/**
	 * Sets the MIDI receiver of the piano to the specified value.
	 * @param receiver the MIDI receiver 
	 */
	public void setReceiver (Receiver receiver) {
		_receiver = receiver;
	}

	/**
	 * Returns the current MIDI receiver of the piano.
	 * @return the current MIDI receiver 
	 */
	public Receiver getReceiver () {
		return _receiver;
	}

	// DO NOT MODIFY THIS METHOD.
	/**
	 * @param receiver the MIDI receiver to use in the piano.
	 */
	public Piano (Receiver receiver) {
		// Some Swing setup stuff; don't worry too much about it.
		setFocusable(true);
		setLayout(null);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));

		setReceiver(receiver);
		_mouseListener = new PianoMouseListener(_keys);
		addMouseListener(_mouseListener);
		addMouseMotionListener(_mouseListener);
		makeKeys();
	}

	/**
	 * Returns the PianoMouseListener associated with the piano.
	 * @return the PianoMouseListener associated with the piano.
	 */
	public PianoMouseListener getMouseListener () {
		return _mouseListener;
	}
	
	// Allows the pitch to be changed each time a new key is created.
	private int PITCH_INCREMENT = 0;
	
	/*
	 * Keeps track of the leftmost location of the white key that is being created,
	 * black keys are created centered on this value.
	 */
	private int WHITE_KEY_LEFT_LOCATION = 0;
	
	/**
	 * Instantiate all the Key objects with their correct polygons and pitches, and
	 * add them to the _keys array.
	 */
	private void makeKeys () {
		for(int i = 0; i < 6; i++) {
			if (i % 2 == 0) {
				makeLeftOctavePortion();
			}
			else {
				makeRightOctavePortion();
			}
		}
	}
	
	/**
	 * Creates a left octave portion by creating 3 white keys
	 * with a black key in between both white key pairs.
	 */
	private void makeLeftOctavePortion () {
		makeLeftWhiteKey();
		makeBlackKeyAndNormalWhiteKey();
		makeBlackKeyAndRightWhiteKey();
	}
	
	/**
	 * Creates a right octave portion by creating 4 white keys
	 * with a black key in between all 3 white key pairs.
	 */
	private void makeRightOctavePortion () {
		makeLeftWhiteKey();
		makeBlackKeyAndNormalWhiteKey();
		makeBlackKeyAndNormalWhiteKey();
		makeBlackKeyAndRightWhiteKey();
	}
	
	/**
	 * Creates a black key 
	 * that is centered between two white keys.
	 */
	private void makeBlackKey () {
		// Establish the list of x coordinates that will make this key.
		int[] xCoords = new int[] {
			WHITE_KEY_LEFT_LOCATION - BLACK_KEY_WIDTH/2, // top left
			WHITE_KEY_LEFT_LOCATION + BLACK_KEY_WIDTH/2, // top right
			WHITE_KEY_LEFT_LOCATION + BLACK_KEY_WIDTH/2, // bottom right
			WHITE_KEY_LEFT_LOCATION - BLACK_KEY_WIDTH/2 // bottom left
		};
		
		// Establish the list of y coordinates that will make this key.
		int[] yCoords = new int[] {
			0, // top left
			0, // top right
			BLACK_KEY_HEIGHT, // bottom right
			BLACK_KEY_HEIGHT // bottom left
		};
		
		// Create the polygon for the key and then make the key.
		Polygon polygon = new Polygon(xCoords, yCoords, xCoords.length);
		Key key = new Key(polygon, START_PITCH + PITCH_INCREMENT, this);

		// Add this key to the list of keys so that it gets painted.
		_keys.add(key);
		
		// Increment counters.
		PITCH_INCREMENT += 1;
	}
	
	/**
	 * Create a white key
	 * with a cutout from the top right for a black key.
	 */
	private void makeLeftWhiteKey () {
		// Establish the list of x coordinates that will make this key.
		int[] xCoords = new int[] {
			WHITE_KEY_LEFT_LOCATION + WHITE_KEY_WIDTH - BLACK_KEY_WIDTH/2, // middle left
			WHITE_KEY_LEFT_LOCATION + WHITE_KEY_WIDTH, // middle right
			WHITE_KEY_LEFT_LOCATION + WHITE_KEY_WIDTH, // bottom right
			WHITE_KEY_LEFT_LOCATION, // bottom left
			WHITE_KEY_LEFT_LOCATION, // top left
			WHITE_KEY_LEFT_LOCATION + WHITE_KEY_WIDTH - BLACK_KEY_WIDTH/2 // top right
		};
		
		// Establish the list of y coordinates that will make this key.
		int[] yCoords = new int[] {
			BLACK_KEY_HEIGHT, // middle left
			BLACK_KEY_HEIGHT, // middle right
			WHITE_KEY_HEIGHT, // bottom right
			WHITE_KEY_HEIGHT, // bottom left
			0, // top left
			0 // top right
		};
		
		// Create the polygon for the key and then make the key.
		Polygon polygon = new Polygon(xCoords, yCoords, xCoords.length);
		Key key = new Key(polygon, START_PITCH + PITCH_INCREMENT, this);

		// Add this key to the list of keys so that it gets painted.
		_keys.add(key);
		
		// Increment counters.
		PITCH_INCREMENT += 1;
		WHITE_KEY_LEFT_LOCATION += WHITE_KEY_WIDTH;
	}
	
	/**
	 * Create a white key
	 * with a cutout from the top left for a black key
	 * and then create the black key that is to the left of the created white key.
	 */
	private void makeBlackKeyAndRightWhiteKey () {
		// Create the black key paired with this white key.
		makeBlackKey();
		
		// Establish the list of x coordinates that will make this key.
		int[] xCoords = new int[] {
			WHITE_KEY_LEFT_LOCATION + BLACK_KEY_WIDTH/2, // top left
			WHITE_KEY_LEFT_LOCATION + WHITE_KEY_WIDTH, // top right
			WHITE_KEY_LEFT_LOCATION + WHITE_KEY_WIDTH, // bottom right
			WHITE_KEY_LEFT_LOCATION, // bottom left
			WHITE_KEY_LEFT_LOCATION, // middle left
			WHITE_KEY_LEFT_LOCATION + BLACK_KEY_WIDTH/2 // middle right
		};
		
		// Establish the list of y coordinates that will make this key.
		int[] yCoords = new int[] {
			0, // top left
			0, // top right
			WHITE_KEY_HEIGHT, // bottom right
			WHITE_KEY_HEIGHT, // bottom left
			BLACK_KEY_HEIGHT, // middle left
			BLACK_KEY_HEIGHT // middle right
		};
		
		// Create the polygon for the key and then make the key.
		Polygon polygon = new Polygon(xCoords, yCoords, xCoords.length);
		Key key = new Key(polygon, START_PITCH + PITCH_INCREMENT, this);

		// Add this key to the list of keys so that it gets painted.
		_keys.add(key);
		
		// Increment counters.
		PITCH_INCREMENT += 1;
		WHITE_KEY_LEFT_LOCATION += WHITE_KEY_WIDTH;
	}
	
	/**
	 * Create a white key
	 * with cutouts from the top left and right for black keys
	 * and then create the black key that is to the left of the created white key.
	 */
	private void makeBlackKeyAndNormalWhiteKey () {
		// Create the black key paired with this white key.
		makeBlackKey();
		
		// Establish the list of x coordinates that will make this key.
		int[] xCoords = new int[] {
			WHITE_KEY_LEFT_LOCATION + WHITE_KEY_WIDTH - BLACK_KEY_WIDTH/2, // middle right left
			WHITE_KEY_LEFT_LOCATION + WHITE_KEY_WIDTH, // middle right right
			WHITE_KEY_LEFT_LOCATION + WHITE_KEY_WIDTH, // bottom right
			WHITE_KEY_LEFT_LOCATION, // bottom left
			WHITE_KEY_LEFT_LOCATION, // middle left left
			WHITE_KEY_LEFT_LOCATION + BLACK_KEY_WIDTH/2, // middle left right
			WHITE_KEY_LEFT_LOCATION + BLACK_KEY_WIDTH/2, // top left
			WHITE_KEY_LEFT_LOCATION + WHITE_KEY_WIDTH - BLACK_KEY_WIDTH/2 // top right
		};
		
		// Establish the list of y coordinates that will make this key.
		int[] yCoords = new int[] {
			BLACK_KEY_HEIGHT, // middle right left
			BLACK_KEY_HEIGHT, // middle right right
			WHITE_KEY_HEIGHT, // bottom right
			WHITE_KEY_HEIGHT, // bottom left
			BLACK_KEY_HEIGHT, // middle left left
			BLACK_KEY_HEIGHT, // middle left right
			0, // top left
			0 // top right
		};
		
		// Create the polygon for the key and then make the key.
		Polygon polygon = new Polygon(xCoords, yCoords, xCoords.length);
		Key key = new Key(polygon, START_PITCH + PITCH_INCREMENT, this);

		// Add this key to the list of keys so that it gets painted.
		_keys.add(key);
		
		// Increment counters.
		PITCH_INCREMENT += 1;
		WHITE_KEY_LEFT_LOCATION += WHITE_KEY_WIDTH;
	}

	// DO NOT MODIFY THIS METHOD.
	@Override
	/**
	 * Paints the piano and all its constituent keys.
	 * @param g the Graphics object to use for painting.
	 */
	public void paint (Graphics g) {
		// Delegates to all the individual keys to draw themselves.
		for (Key key: _keys) {
			key.paint(g);
		}
	}
}
