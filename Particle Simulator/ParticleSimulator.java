import java.util.*;
import java.util.function.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.sound.sampled.*;

public class ParticleSimulator extends JPanel {
	private Heap<Event> _events;
	private java.util.List<Particle> _particles;
	private double _duration;
	private int _width;
	HashMap<Event, Tuple<Particle, Particle>> _particleEvents;
	HashMap<Event, Tuple<Particle, String>> _wallEvents;

	/**
	 * @param filename the name of the file to parse containing the particles
	 */
	public ParticleSimulator(String filename) throws IOException {
		_events = new HeapImpl<>();
		_particleEvents = new HashMap<Event, Tuple<Particle, Particle>>();
		_wallEvents = new HashMap<Event, Tuple<Particle, String>>();

		// Parse the specified file and load all the particles.
		Scanner s = new Scanner(new File(filename));
		_width = s.nextInt();
		_duration = s.nextDouble();
		s.nextLine();
		_particles = new ArrayList<>();
		while (s.hasNext()) {
			String line = s.nextLine();
			Particle particle = Particle.build(line);
			_particles.add(particle);
		}

		setPreferredSize(new Dimension(_width, _width));
	}

	@Override
	/**
	 * Draws all the particles on the screen at their current locations DO NOT
	 * MODIFY THIS METHOD
	 */
	public void paintComponent(Graphics g) {
		g.clearRect(0, 0, _width, _width);
		for (Particle p : _particles) {
			p.draw(g);
		}
	}

	// Helper class to signify the final event of the simulation.
	private class TerminationEvent extends Event {
		TerminationEvent(double timeOfEvent) {
			super(timeOfEvent, 0);
		}
	}

	// Basic tuple class.
	private class Tuple<X, Y> {
		// First value.
		public final X x;

		// Second value.
		public final Y y;

		/**
		 * Initializes a new tuple.
		 * 
		 * @param x the first value in the tuple.
		 * @param y the second value in the tuple.
		 */
		public Tuple(X x, Y y) {
			this.x = x;
			this.y = y;
		}
	}

	/**
	 * Helper method to update the positions of all the particles based on their
	 * current velocities.
	 */
	private void updateAllParticles(double delta) {
		for (Particle p : _particles) {
			p.update(delta);
		}
	}

	/**
	 * Helper method to create events when needed.
	 * 
	 * @param involvedParticles the particles that will be tested for new events.
	 * @param initialTime       the time at which these new events are being
	 *                          created.
	 */
	private void makeEvents(java.util.List<Particle> involvedParticles, double initialTime) {
		// Loop through the involved particles.
		for (Particle p1 : involvedParticles) {
			// First test collisions with walls.
			String[] walls = { "left", "right", "top", "bottom" };
			for (String wall : walls) {
				double time = p1.getWallCollisionTime(wall, _width);

				// If a collision occurs add the collision to the events heap and to the hash
				// map that keeps track of which particle and wall go with which event.
				if (Double.isFinite(time)) {
					Event collision = new Event(initialTime + time, initialTime);
					_events.add(collision);
					Tuple<Particle, String> particleAndWall = new Tuple<>(p1, wall);
					_wallEvents.put(collision, particleAndWall);
				}
			}

			// Now test collisions with other particles.
			for (Particle p2 : _particles) {
				if (p1 != p2) {
					double time = p1.getCollisionTime(p2);

					// If a collision occurs add the collision to the events heap and to the hash
					// map that keeps track of which two particles go with which event.
					if (Double.isFinite(time)) {
						Event collision = new Event(initialTime + time, initialTime);
						_events.add(collision);
						Tuple<Particle, Particle> currentParticles = new Tuple<>(p1, p2);
						_particleEvents.put(collision, currentParticles);
					}
				}
			}
		}
	}

	/**
	 * Test if a given event is still valid.
	 * 
	 * @param event the event to test.
	 * @return true if the given event is not valid, false if it is valid.
	 */
	private boolean notValid(Event event) {
		// Return whether or not the particle is still valid.
		return (_particleEvents.containsKey(event)
				&& (!_particleEvents.get(event).x.isValid(event) || !_particleEvents.get(event).y.isValid(event)))
				|| (_wallEvents.containsKey(event) && !_wallEvents.get(event).x.isValid(event));
	}

	/**
	 * Updates the screen containing the particles.
	 * 
	 * @param show  true if the screen should be updated, false if not.
	 * @param delta the time to wait between screen updates.
	 */
	private void updateScreen(boolean show, double delta) {
		// Update the screen if show is true and catch any exceptions.
		if (show) {
			try {
				Thread.sleep((long) delta);
			} catch (InterruptedException ie) {
			}
		}
	}

	/**
	 * Find an array list that contains all of the particles involved in the given
	 * event.
	 * 
	 * @param event the event to test.
	 * @return an array list of the particles involved in the event.
	 */
	private ArrayList<Particle> findInvolvedParticles(Event event) {
		// Establish the array list.
		ArrayList<Particle> involvedParticles = new ArrayList<Particle>();

		// If the event is between two particles, add both particles to the list.
		if (_particleEvents.containsKey(event)) {
			involvedParticles.add(_particleEvents.get(event).x);
			involvedParticles.add(_particleEvents.get(event).y);
		}

		// If the event is between a particle and a wall, add the single particle to the
		// list.
		else if (_wallEvents.containsKey(event)) {
			involvedParticles.add(_wallEvents.get(event).x);
		}

		// Return the list that was found.
		return involvedParticles;
	}

	/**
	 * Keeps track of and displays all collisions until an event terminates the
	 * simulations.
	 * 
	 * @param show     true if the screen should be updated, false if not.
	 * @param lastTime the lastTime that the simulation is aware of.
	 */
	private void collisionTracker(boolean show, double lastTime) {
		while (_events.size() > 0) {
			// Take the top element out of the heap.
			Event event = _events.removeFirst();

			// Time since last event.
			double delta = event._timeOfEvent - lastTime;

			// Update all particle positions and terminate the simulation if the termination
			// event comes up.
			if (event instanceof TerminationEvent) {
				updateAllParticles(delta);
				break;
			}

			// Check if event still valid; if not, then skip this event
			if (notValid(event)) {
				continue;
			}

			// Since the event is valid, then pause the simulation for the right amount of
			// time, and then update the screen.
			updateScreen(show, delta);

			// Update positions of all particles
			updateAllParticles(delta);

			// Update the velocity of the particle(s) involved in the collision.
			if (_particleEvents.containsKey(event)) {
				_particleEvents.get(event).x.updateAfterCollision(event._timeOfEvent, _particleEvents.get(event).y);
			} else if (_wallEvents.containsKey(event)) {
				_wallEvents.get(event).x.updateAfterWallCollision(event._timeOfEvent, _wallEvents.get(event).y);
			}

			// Enqueue new events for the particle(s) that were involved in this event.
			makeEvents(findInvolvedParticles(event), event._timeOfEvent);

			// Update the time of our simulation
			lastTime = event._timeOfEvent;

			// Redraw the screen
			if (show) {
				repaint();
			}
		}
	}

	/**
	 * Executes the actual simulation.
	 * 
	 * @param show true if the simulation should be shown, false if not.
	 */
	private void simulate(boolean show) {
		// Keeps track of time in the simulation.
		double lastTime = 0;

		// Create initial events.
		makeEvents(_particles, lastTime);

		// Add an event to terminate the simulation at a specific time.
		_events.add(new TerminationEvent(_duration));

		// Keep track of collisions while there are still collisions that can occur.
		collisionTracker(show, lastTime);

		// Print out the final state of the simulation
		System.out.println(_width);
		System.out.println(_duration);
		for (Particle p : _particles) {
			System.out.println(p);
		}
	}

	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.out.println("Usage: java ParticalSimulator <filename>");
			System.exit(1);
		}

		ParticleSimulator simulator;

		simulator = new ParticleSimulator(args[0]);
		JFrame frame = new JFrame();
		frame.setTitle("Particle Simulator");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(simulator, BorderLayout.CENTER);
		frame.setVisible(true);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		simulator.simulate(true);
	}
}
