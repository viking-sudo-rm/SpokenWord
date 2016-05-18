package snorri.entities;

import java.awt.Graphics;

import snorri.main.GameWindow;
import snorri.main.Main;
import snorri.world.Position;

public class Entity {
	
	private static final int BASE_SPEED = 2;
	
	protected Position pos;
	protected int r;
	
	public Entity(Entity e) {
		this.pos = e.pos.copy();
		this.r = e.r;
	}
	
	public Entity(Position pos, int r) {
		this.pos = pos;
		this.r = r;
	}
	
	public Entity(Position pos) {
		this(pos, 3);
	}

	public Position getPos() {
		return pos;
	}
	
	public int getRadius() {
		return r;
	}

	public boolean intersects(Position pos1) {
		return pos.distance(pos1) <= r;
	}
	
	public boolean intersects(Entity e) {
		return e.pos.distance(pos) <= r + e.r;
	}
	
	public boolean intersects(Entity e, int rad) {
		return e.pos.distance(pos) <= r + e.r + rad;
	}
	
	public boolean contains(Entity e) {
		return e.pos.distance(pos) + e.r <= r;
	}
	
	public boolean contains(Entity e, int rad) {
		return e.pos.distance(pos) + e.r <= r + rad;
	}
	
	protected void traverse(int depth) {
		String indent = "";
		for (int i = 0; i < depth; i++) {
			indent += "  ";
		}
		Main.log(indent + this.toString());
	}
	
	public void traverse() {
		traverse(0);
	}
	
	public String toString() {
		return this.getClass().getSimpleName() + "{pos: " + pos.toString() + ", r: " + r + "}";
	}
	
	public void renderHitbox(GameWindow g, Graphics gr) {
		Position rel = pos.copy();
		rel.sub(g.getFocus().pos);
		gr.drawOval(rel.x - r + g.getBounds().width / 2, rel.y - r + g.getBounds().height / 2, 2 * r, 2 * r);
	}
	
	//returns true if the two entities are spatially equal
	public boolean equals(Entity e) {
		return e.pos.equals(pos) && e.r == r;
	}
	
	public void renderAround(GameWindow g, Graphics gr) {
		renderAround(g, gr);
	}
	
	public void walk(Position direction, EntityGroup world) {
		Position dir = direction.copy();
		
		if (dir.equals(Position.ZERO)) {
			return;
		}
		
		dir.multiply(getSpeed());
		world.move(this, dir);
	}
	
	//override this for faster entities
	protected int getSpeed() {
		return BASE_SPEED;
	}

}