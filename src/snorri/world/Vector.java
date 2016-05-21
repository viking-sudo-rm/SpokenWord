package snorri.world;

import java.awt.Point;
import java.awt.Rectangle;

public class Vector {

	//TODO: generalize vectors with generic types to hold 
	//either ints or doubles
	
	public static final Vector ZERO = new Vector(0, 0);
	
	public double x, y;
	
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector(Point location) {
		this.x = location.getX();
		this.y = location.getY();
	}
	
	public Vector(Rectangle r) {
		this.x = r.getWidth();
		this.y = r.getHeight();
	}

	public int getX() {
		return (int) x;
	}
	
	public int getY() {
		return (int) y;
	}
	
	//all of this stuff could be cleaned up, maybe
	//these all have side effects and return a pointer to obj
	
	public Vector multiply(double n) {
		x *= n;
		y *= n;
		return this;
	}
	
	public Vector divide(double n) {
		x /= n;
		y /= n;
		return this;
	}
	
	public Vector add(Vector pos) {
		x += pos.x;
		y += pos.y;
		return this;
	}
	
	public Vector sub(Vector pos) {
		x -= pos.x;
		y -= pos.y;
		return this;
	}
	
	public Vector scale(double magnitude) {
		return multiply(magnitude / distance(ZERO));
	}
	
	public Vector normalize() {
		return scale(1);
	}
	
	//rounds to an int for convenience
	public int distance(Vector pos) {
		return (int) Math.sqrt((x - pos.x) * (x - pos.x) + (y - pos.y) * (y - pos.y));
	}
	
	public int magnitude() {
		return distance(ZERO);
	}
	
	public boolean notInPlane() {
		return Double.isNaN(x) || Double.isNaN(y);
	}
	
	public Vector copy() {
		return new Vector(x, y);
	}
	
	public String toString() {
		return "(x: " + x + ", y: " + y + ")";
	}
	
	public boolean equals(Vector pos) {
		return x == pos.x && y == pos.y;
	}
	
}