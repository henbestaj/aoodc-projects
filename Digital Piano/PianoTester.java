import static org.junit.jupiter.api.Assertions.*;
import javax.swing.*;
import javax.sound.midi.*;
import java.awt.event.*;
import java.util.Arrays;

import org.junit.jupiter.api.*;

/**
 * Contains a set of unit tests for the Piano class.
 */
class PianoTester {
	private TestReceiver _receiver;
	private Piano _piano;
	private PianoMouseListener _mouseListener;

	private MouseEvent makeMouseEvent (int x, int y) {
		return new MouseEvent(_piano, 0, 0, 0, x, y, 0, false);
	}

	@BeforeEach
	void setup () {
		// A new TestReceiver will be created before running *each*
		// test. Hence, the "turn on" and "turn off" counts will be
		// reset to 0 before *each* test.
		_receiver = new TestReceiver();
		_piano = new Piano(_receiver);
		_mouseListener = _piano.getMouseListener();
	}

	@Test
	void testClickUpperLeftMostPixel () {
		// Pressing the mouse should cause the key to turn on.
		_mouseListener.mousePressed(makeMouseEvent(0, 0));
		assertTrue(_receiver.isKeyOn(Piano.START_PITCH));
	}
	
	/*
	 * The total number of keys on the piano.
	 */
	private static int TOTAL_KEY_COUNT = 36;
	
	/*
	 * The locations where a white key is made directly after another white key,
	 * with no black key in between.
	 */
	private static Integer[] DOUBLE_WHITES = {5, 12, 17, 24, 29};
	
	/*
	 * The size of a single pixel.
	 */
	private static int SINGLE_PIXEL = 1;
	
	/*
	 * Half the width of a black key.
	 */
	private static int BLACK_KEY_HALF_WIDTH = Piano.BLACK_KEY_WIDTH/2;
	
	/*
	 * The number of white keys in the left portion of an octave.
	 */
	private static int WHITES_IN_LEFT_OCTAVE_PORTION = 3;
	
	/*
	 * The number of black keys in the left portion of an octave.
	 */
	private static int BLACKS_IN_LEFT_OCTAVE_PORTION = 2;
	
	/*
	 * The number of white keys in the right portion of an octave.
	 */
	private static int WHITES_IN_RIGHT_OCTAVE_PORTION = 4;
	
	/*
	 * The number of black keys in the right portion of an octave.
	 */
	private static int BLACKS_IN_RIGHT_OCTAVE_PORTION = 3;
	
	/*
	 * The locations where a middle white key has another white key,
	 * right after it (excluding the black keys).
	 */
	private static Integer[] LEFTMOST_MIDDLE_WHITES = {1, 4, 7};
	
	/*
	 * The locations where a black key has
	 * a right white key right after it.
	 */
	private static Integer[] RIGHTMOST_BLACKS = {1, 4, 6, 9, 11, 14};
		
	@Test
	void testClickMiddleAllKeys () {
		// The number of times two white keys have been successive.
		int numberMissingBlacks = 0;
		
		// Tests clicking the middle top of every key on the piano.
		for (int i = 0; i < TOTAL_KEY_COUNT; i++) {
			if (Arrays.asList(DOUBLE_WHITES).contains(i)) {
				numberMissingBlacks += 1;
			}
			testBasicClick(Piano.BLACK_KEY_WIDTH * (i + 1 + numberMissingBlacks), 0, Piano.START_PITCH + i);
		}
	}
	
	@Test
	void testLeftWhiteBoundaries () {
		// The current leftmost boundary of the key being tested.
		int LEFT_BOUND = 0;
		
		// The pitch of the key being tested.
		int PITCH = Piano.START_PITCH;
		
		// Tests the piano at each vertex of the left white keys.
		for (int i = 0; i < 6; i++) {
			testBasicClick(LEFT_BOUND, 0, PITCH); // top left
			testBasicClick(LEFT_BOUND, Piano.WHITE_KEY_HEIGHT - SINGLE_PIXEL, PITCH); // bottom left
			testBasicClick(LEFT_BOUND + Piano.WHITE_KEY_WIDTH - SINGLE_PIXEL, Piano.WHITE_KEY_HEIGHT - SINGLE_PIXEL,
					PITCH); // bottom right
			testBasicClick(LEFT_BOUND + Piano.WHITE_KEY_WIDTH - SINGLE_PIXEL, Piano.BLACK_KEY_HEIGHT,
					PITCH); // middle right
			testBasicClick(LEFT_BOUND + Piano.WHITE_KEY_WIDTH - BLACK_KEY_HALF_WIDTH, Piano.BLACK_KEY_HEIGHT,
					PITCH); // middle left
			testBasicClick(LEFT_BOUND + Piano.WHITE_KEY_WIDTH - BLACK_KEY_HALF_WIDTH - SINGLE_PIXEL, 0,
					PITCH); // top right
			
			// Increments the leftmost boundary and pitch.
			if (i % 2 == 0) {
				LEFT_BOUND += Piano.WHITE_KEY_WIDTH * WHITES_IN_LEFT_OCTAVE_PORTION;
				PITCH += WHITES_IN_LEFT_OCTAVE_PORTION + BLACKS_IN_LEFT_OCTAVE_PORTION;
			}
			else {
				LEFT_BOUND += Piano.WHITE_KEY_WIDTH * WHITES_IN_RIGHT_OCTAVE_PORTION;
				PITCH += WHITES_IN_RIGHT_OCTAVE_PORTION + BLACKS_IN_RIGHT_OCTAVE_PORTION;
			}
		}
	}
	
	@Test
	void testRightWhiteBoundaries () {
		// The current leftmost boundary of the key being tested.
		int LEFT_BOUND = Piano.WHITE_KEY_WIDTH * 2;
		
		// The pitch of the key being tested.
		int PITCH = Piano.START_PITCH + 4;
		
		// Tests the piano at each vertex of the right white keys.
		for (int i = 0; i < 6; i++) {
			testBasicClick(LEFT_BOUND + BLACK_KEY_HALF_WIDTH, 0, PITCH); // top left
			testBasicClick(LEFT_BOUND + BLACK_KEY_HALF_WIDTH, Piano.BLACK_KEY_HEIGHT, PITCH); // middle right
			testBasicClick(LEFT_BOUND, Piano.BLACK_KEY_HEIGHT, PITCH); // middle left
			testBasicClick(LEFT_BOUND, Piano.WHITE_KEY_HEIGHT - SINGLE_PIXEL, PITCH); // bottom left
			testBasicClick(LEFT_BOUND + Piano.WHITE_KEY_WIDTH - SINGLE_PIXEL, Piano.WHITE_KEY_HEIGHT - SINGLE_PIXEL,
					PITCH); // bottom right
			testBasicClick(LEFT_BOUND + Piano.WHITE_KEY_WIDTH - SINGLE_PIXEL, 0, PITCH); // top right

			// Increments the leftmost boundary and pitch.
			if (i % 2 == 0) {
				LEFT_BOUND += Piano.WHITE_KEY_WIDTH * WHITES_IN_RIGHT_OCTAVE_PORTION;
				PITCH += WHITES_IN_RIGHT_OCTAVE_PORTION + BLACKS_IN_RIGHT_OCTAVE_PORTION;
			}
			else {
				LEFT_BOUND += Piano.WHITE_KEY_WIDTH * WHITES_IN_LEFT_OCTAVE_PORTION;
				PITCH += WHITES_IN_LEFT_OCTAVE_PORTION + BLACKS_IN_LEFT_OCTAVE_PORTION;
			}
		}
	}
	
	@Test
	void testMiddleWhiteBoundaries () {
		// The current leftmost boundary of the key being tested.
		int LEFT_BOUND = Piano.WHITE_KEY_WIDTH;

		// The pitch of the key being tested.
		int PITCH = Piano.START_PITCH + 2;
		
		// Tests the piano at each vertex of the middle white keys.
		for (int i = 0; i < 9; i++) {
			testBasicClick(LEFT_BOUND + BLACK_KEY_HALF_WIDTH, 0, PITCH); // top left
			testBasicClick(LEFT_BOUND + BLACK_KEY_HALF_WIDTH, Piano.BLACK_KEY_HEIGHT, PITCH); // middle left right
			testBasicClick(LEFT_BOUND, Piano.BLACK_KEY_HEIGHT, PITCH); // middle left left
			testBasicClick(LEFT_BOUND, Piano.WHITE_KEY_HEIGHT - SINGLE_PIXEL, PITCH); // bottom left
			testBasicClick(LEFT_BOUND + Piano.WHITE_KEY_WIDTH - SINGLE_PIXEL, Piano.WHITE_KEY_HEIGHT - SINGLE_PIXEL,
					PITCH); // bottom right
			testBasicClick(LEFT_BOUND + Piano.WHITE_KEY_WIDTH - SINGLE_PIXEL, Piano.BLACK_KEY_HEIGHT,
					PITCH); // middle right right
			testBasicClick(LEFT_BOUND + Piano.WHITE_KEY_WIDTH - BLACK_KEY_HALF_WIDTH, Piano.BLACK_KEY_HEIGHT,
					PITCH); // middle right left
			testBasicClick(LEFT_BOUND + Piano.WHITE_KEY_WIDTH - BLACK_KEY_HALF_WIDTH - SINGLE_PIXEL, 0,
					PITCH); // top right
			
			// Increments the leftmost boundary and pitch.
			if (Arrays.asList(LEFTMOST_MIDDLE_WHITES).contains(i)) {
				LEFT_BOUND += Piano.WHITE_KEY_WIDTH;
				PITCH += 2;
			}
			else {
				LEFT_BOUND += Piano.WHITE_KEY_WIDTH * WHITES_IN_LEFT_OCTAVE_PORTION;
				PITCH += WHITES_IN_LEFT_OCTAVE_PORTION + BLACKS_IN_LEFT_OCTAVE_PORTION;
			}
		}
	}
	
	@Test
	void testBlackBoundaries () {
		// The current leftmost boundary of the key being tested.
		int LEFT_BOUND = Piano.WHITE_KEY_WIDTH - BLACK_KEY_HALF_WIDTH;
		
		// The pitch of the key being tested.
		int PITCH = Piano.START_PITCH + 1;
		
		// Tests the piano at each vertex of the black keys.
		for (int i = 0; i < 15; i++) {
			testBasicClick(LEFT_BOUND, 0, PITCH); // top left
			testBasicClick(LEFT_BOUND, Piano.BLACK_KEY_HEIGHT - SINGLE_PIXEL, PITCH); // bottom left
			testBasicClick(LEFT_BOUND + Piano.BLACK_KEY_WIDTH - SINGLE_PIXEL, Piano.BLACK_KEY_HEIGHT - SINGLE_PIXEL,
					PITCH); // bottom right
			testBasicClick(LEFT_BOUND + Piano.BLACK_KEY_WIDTH - SINGLE_PIXEL, 0, PITCH); // top right
			
			// Increments the leftmost boundary and pitch.
			if (Arrays.asList(RIGHTMOST_BLACKS).contains(i)) {
				LEFT_BOUND += Piano.WHITE_KEY_WIDTH * 2;
				PITCH += WHITES_IN_LEFT_OCTAVE_PORTION;
			}
			else {
				LEFT_BOUND += Piano.WHITE_KEY_WIDTH;
				PITCH += 2;
			}
		}
	}

	@Test
	void testDragWithinKey () {
		// The number of times two white keys have been successive.
		int numberMissingBlacks = 0;
		
		// Tests that pressing and dragging the mouse within the same key only causes the key to be turned on once.
		for (int i = 0; i < TOTAL_KEY_COUNT; i++) {
			if (Arrays.asList(DOUBLE_WHITES).contains(i)) {
				numberMissingBlacks += 1;
			}
			_mouseListener.mouseDragged(makeMouseEvent(Piano.BLACK_KEY_WIDTH * (i + 1 + numberMissingBlacks), 0));
			_mouseListener.mouseDragged(makeMouseEvent(Piano.BLACK_KEY_WIDTH * (i + 1 + numberMissingBlacks),
					Piano.BLACK_KEY_HEIGHT - SINGLE_PIXEL));
			assertTrue(_receiver.getKeyOnCount(Piano.START_PITCH + i) == 1);
		}
	}
	
	/**
	 * This method is called to test a basic click and release of the mouse.
	 * @param x the x value of the click
	 * @param y the y value of the click
	 * @param pitch the pitch that the click should result in
	 */
	private void testBasicClick (int x, int y, int pitch) {
		_mouseListener.mousePressed(makeMouseEvent(x, y));
		assertTrue(_receiver.isKeyOn(pitch));
		_mouseListener.mouseReleased(makeMouseEvent(x, y));
		assertFalse(_receiver.isKeyOn(pitch));
	}
}
