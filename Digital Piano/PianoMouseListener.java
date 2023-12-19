import javax.swing.*;
import java.awt.event.*;
import javax.sound.midi.*;
import java.util.*;

/**
 * Handles mouse press, release, and drag events on the Piano.
 */
public class PianoMouseListener extends MouseAdapter {
	private List<Key> _keys;

	/**
	 * @param keys the list of keys in the piano.
	 */
	public PianoMouseListener (List<Key> keys) {
		_keys = keys;
	}

	@Override
	/**
	 * This method is called by Swing whenever the user drags the mouse.
	 * @param e the MouseEvent containing the (x,y) location, relative to the upper-left-hand corner
	 * of the entire piano, of where the mouse is currently located.
	 */
	public void mouseDragged (MouseEvent e) {
		// Loops through the keys.
		for (Key key: _keys) {
			// Plays the key if it is not on already and the mouse is over it.
			if (!key.getOn() && key.getPolygon().contains(e.getX(), e.getY())) {
				key.play(true);
			}
			
			// Turns off the key if it is on and the mouse is not over it.
			else if (key.getOn() && !key.getPolygon().contains(e.getX(), e.getY())) {
				key.play(false);
			}
		}
	}

	@Override
	/**
	 * This method is called by Swing whenever the user presses the mouse.
	 * @param e the MouseEvent containing the (x,y) location, relative to the upper-left-hand corner
	 * of the entire piano, of where the mouse is currently located.
	 */
	public void mousePressed (MouseEvent e) {
		// Loops through the keys.
		for (Key key: _keys) {
			// Plays the key if the mouse has clicked it.
			if (key.getPolygon().contains(e.getX(), e.getY())) {
				key.play(true);
			}
		}
	}

	@Override
	/**
	 * This method is called by Swing whenever the user releases the mouse.
	 * @param e the MouseEvent containing the (x,y) location, relative to the upper-left-hand corner
	 * of the entire piano, of where the mouse is currently located.
	 */
	public void mouseReleased (MouseEvent e) {
		// Loops through the keys.
		for (Key key: _keys) {
			// Turns off the key if the mouse has let go of it.
			if (key.getPolygon().contains(e.getX(), e.getY())) {
				key.play(false);
			}
		}
	}
}
