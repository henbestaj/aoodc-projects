import java.awt.*;
import java.util.*;

public class Particle {
	private String _name;
	private double _x, _y;
	private double _vx, _vy;
	private double _radius;
	private double _lastUpdateTime;

	/**
	 * Helper method to parse a string into a Particle. DO NOT MODIFY THIS METHOD
	 * 
	 * @param str the string to parse
	 * @return the parsed Particle
	 */
	public static Particle build(String str) {
		String[] tokens = str.split("\\s+");
		double[] nums = Arrays.stream(Arrays.copyOfRange(tokens, 1, tokens.length)).mapToDouble(Double::parseDouble)
				.toArray();
		return new Particle(tokens[0], nums[0], nums[1], nums[2], nums[3], nums[4]);
	}

	/**
	 * @param name   name of the particle (useful for debugging)
	 * @param x      x-coordinate of the particle
	 * @param y      y-coordinate of the particle
	 * @param vx     x-velocity of the particle
	 * @param vy     y-velocity of the particle
	 * @param radius radius of the particle
	 */
	Particle(String name, double x, double y, double vx, double vy, double radius) {
		_name = name;
		_x = x;
		_y = y;
		_vx = vx;
		_vy = vy;
		_radius = radius;
		_lastUpdateTime = 0;
	}

	/**
	 * Draws the particle as a filled circle. DO NOT MODIFY THIS METHOD
	 */
	void draw(Graphics g) {
		g.fillOval((int) (_x - _radius), (int) (_y - _radius), (int) (2 * _radius), (int) (2 * _radius));
	}

	/**
	 * Determine if the event if still valid in regards to the current particle.
	 * 
	 * @param event the event to test.
	 * @return true if still valid, false if not.
	 */
	public boolean isValid(Event event) {
		// Return true if the time the event occurs is above when this particle was last
		// updated.
		return event._timeEventCreated >= _lastUpdateTime;
	}

	/**
	 * Useful for debugging.
	 */
	public String toString() {
		return (_name.equals("") ? "" : _name + " ") + _x + "  " + _y + " " + _vx + " " + _vy + " " + _radius;
	}

	/**
	 * Updates the position of the particle after an elapsed amount of time, delta,
	 * using the particle's current velocity.
	 * 
	 * @param delta the elapsed time since the last particle update
	 */
	public void update(double delta) {
		double newX = _x + delta * _vx;
		double newY = _y + delta * _vy;
		_x = newX;
		_y = newY;
	}

	/**
	 * Updates both this particle's and another particle's velocities after a
	 * collision between them. DO NOT CHANGE THE MATH IN THIS METHOD
	 * 
	 * @param now   the current time in the simulation
	 * @param other the particle that this one collided with
	 */
	public void updateAfterCollision(double now, Particle other) {
		double vxPrime, vyPrime;
		double otherVxPrime, otherVyPrime;
		double common = ((_vx - other._vx) * (_x - other._x) + (_vy - other._vy) * (_y - other._y))
				/ (Math.pow(_x - other._x, 2) + Math.pow(_y - other._y, 2));
		vxPrime = _vx - common * (_x - other._x);
		vyPrime = _vy - common * (_y - other._y);
		otherVxPrime = other._vx - common * (other._x - _x);
		otherVyPrime = other._vy - common * (other._y - _y);

		_vx = vxPrime;
		_vy = vyPrime;
		other._vx = otherVxPrime;
		other._vy = otherVyPrime;

		_lastUpdateTime = now;
		other._lastUpdateTime = now;
	}

	/**
	 * Updates the velocity of a particle after it collides with a given wall.
	 * 
	 * @param now  the time that this collision is occurring.
	 * @param wall the wall with which the particle is colliding.
	 */
	public void updateAfterWallCollision(double now, String wall) {
		// Reverse the velocity in the x direction if the wall is vertical.
		if (wall.equals("left") || wall.equals("right")) {
			_vx *= -1;
		}

		// Reverse the velocity in the y direction if the wall is horizontal.
		else if (wall.equals("top") || wall.equals("bottom")) {
			_vy *= -1;
		}

		// Update the last update time.
		_lastUpdateTime = now;
	}

	/**
	 * Computes and returns the time when (if ever) this particle will collide with
	 * another particle, or infinity if the two particles will never collide given
	 * their current velocities. DO NOT CHANGE THE MATH IN THIS METHOD
	 * 
	 * @param other the other particle to consider
	 * @return the time with the particles will collide, or infinity if they will
	 *         never collide
	 */
	public double getCollisionTime(Particle other) {
		// See
		// https://en.wikipedia.org/wiki/Elastic_collision#Two-dimensional_collision_with_two_moving_objects
		double a = _vx - other._vx;
		double b = _x - other._x;
		double c = _vy - other._vy;
		double d = _y - other._y;
		double r = _radius;

		double A = a * a + c * c;
		double B = 2 * (a * b + c * d);
		double C = b * b + d * d - 4 * r * r;

		// Numerically more stable solution to QE.
		// https://people.csail.mit.edu/bkph/articles/Quadratics.pdf
		double t1, t2;
		if (B >= 0) {
			t1 = (-B - Math.sqrt(B * B - 4 * A * C)) / (2 * A);
			t2 = 2 * C / (-B - Math.sqrt(B * B - 4 * A * C));
		} else {
			t1 = 2 * C / (-B + Math.sqrt(B * B - 4 * A * C));
			t2 = (-B + Math.sqrt(B * B - 4 * A * C)) / (2 * A);
		}

		// Require that the collision time be slightly larger than 0 to avoid
		// numerical issues.
		double SMALL = 1e-6;
		double t;
		if (t1 > SMALL && t2 > SMALL) {
			t = Math.min(t1, t2);
		} else if (t1 > SMALL) {
			t = t1;
		} else if (t2 > SMALL) {
			t = t2;
		} else {
			// no collision
			t = Double.POSITIVE_INFINITY;
		}

		return t;
	}

	/**
	 * Find the time it takes for the particle to collide with one of the walls.
	 * 
	 * @param wall  the wall that should be tested.
	 * @param width the width of the box.
	 * @return the time when the particle will collide with the given wall, or
	 *         infinity if they will never collide.
	 */
	public double getWallCollisionTime(String wall, int width) {
		// Test left wall.
		if (wall.equals("left") && this._vx < 0) {
			return -(this._x - this._radius) / this._vx;
		}

		// Test right wall.
		else if (wall.equals("right") && this._vx > 0) {
			return (width - this._x - this._radius) / this._vx;
		}

		// Test top wall.
		else if (wall.equals("top") && this._vy < 0) {
			return -(this._y - this._radius) / this._vy;
		}

		// Test bottom wall.
		else if (wall.equals("bottom") && this._vy > 0) {
			return (width - this._y - this._radius) / this._vy;
		}

		// Return infinity if it does not hit the tested wall.
		return Double.POSITIVE_INFINITY;
	}
}
