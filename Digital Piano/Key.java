import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.sound.midi.*;
import javax.swing.*;

/**
 * Implements a key on a simulated piano keyboard.
 */
public class Key {
	private Polygon _polygon;
	private int[] _polygonYValues;
	private int _pitch;
	private boolean _isOn;
	private Piano _piano;

	/**
	 * Returns the polygon associated with this key.
	 * @return the polygon associated with this key.
	 */
	public Polygon getPolygon () {
		return _polygon;
	}
	
	/**
	 * Returns whether or not this key is on.
	 * @return the on state of this key.
	 */
	public boolean getOn () {
		return _isOn;
	}

	/**
	 * @param polygon the Polygon that describes the shape and position of this key.
	 * @param pitch the pitch value of the key.
	 * @param piano the Piano associated with this key.
	 */
	public Key (Polygon polygon, int pitch, Piano piano) {
		_polygon = polygon;
		_polygonYValues = polygon.ypoints;
		_pitch = pitch;
		_piano = piano;
	}

	// DO NOT MODIFY THIS METHOD.
	/**
	 * Turns the note either on or off.
	 * @param isOn whether the note should be turned on.
	 */
	public void play (boolean isOn) {
		try {
			// Some MIDI technicalities; don't worry too much about it.
			ShortMessage myMsg = new ShortMessage();
			final int VELOCITY = 93;
			myMsg.setMessage(isOn ? ShortMessage.NOTE_ON : ShortMessage.NOTE_OFF, 0, _pitch, VELOCITY);
			final int IMMEDIATELY = -1;
			// Send the message to the receiver (either local or remote).
			_piano.getReceiver().send(myMsg, IMMEDIATELY);
			// Set the key to "on".
			_isOn = isOn;
			// Ask the piano to redraw itself (since one of its keys has changed).
			_piano.repaint();
		} catch (InvalidMidiDataException imde) {
			System.out.println("Could not play key!");
		}
	}

	// Establishes a static variable with the height of the white key in pixels.
	public static int WHITE_KEY_HEIGHT = 200;
	
	/*
	 * Establishes a static variable with the location in the coordinate arrays
	 * of the coordinate for the bottom right point of each white key.
	 */
	public static int LOCATION_OF_BOTTOM_RIGHT_COORDINATE = 2;
	
	/**
	 * Paints the key using the specified Swing Graphics object.
	 * @param g the Graphics object to be used for painting.
	 */
	public void paint (Graphics g) {
		// Paints the key gray when it is pressed down.
		if (_isOn) {
			g.setColor(Color.GRAY);
			g.fillPolygon(_polygon);
		}
		
		// Paints the key white when it is a white key not pressed down.
		else if (_polygonYValues[LOCATION_OF_BOTTOM_RIGHT_COORDINATE] == WHITE_KEY_HEIGHT) {
			g.setColor(Color.WHITE);
			g.fillPolygon(_polygon);
		}
		
		// Paints the key black when it is a black key not pressed down.
		else {
			g.setColor(Color.BLACK);
			g.fillPolygon(_polygon);
		}
		
		// Gives the key a black outline.
		g.setColor(Color.BLACK);
		g.drawPolygon(_polygon);
	}

	/**
	 * Returns a String representation describing the key.
	 * @return a String representation describing the key.
	 */
	public String toString () {
		return "Key: " + _pitch;
	}
}
