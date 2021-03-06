package snorri.entities;

import java.awt.Graphics;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

import snorri.animations.Animation;
import snorri.collisions.CircleCollider;
import snorri.collisions.Collider;
import snorri.entities.Player.Interactor;
import snorri.events.CollisionEvent;
import snorri.events.CastEvent;
import snorri.main.Debug;
import snorri.physics.SurfaceCollisionMode;
import snorri.semantics.nouns.Nominal;
import snorri.triggers.Trigger;
import snorri.triggers.TriggerType;
import snorri.util.Util;
import snorri.windows.DialogMap;
import snorri.windows.FocusedWindow;
import snorri.windows.GameWindow;
import snorri.windows.LevelEditor;
import snorri.world.Tile;
import snorri.world.Vector;
import snorri.world.World;

public class Entity implements Nominal, Serializable, Comparable<Entity>, Cloneable {

	private static final long serialVersionUID = 1L;
	
	/** Default gravity vector. **/
	public static final Vector GRAVITY = new Vector(0.0, 400.0);
	
	private static final int TERMINAL_VELOCITY = 1280;
	
	/** A layer above the player for particle effects. */
	protected static final int PARTICLE_LAYER = 15;
	/** The default layer for objects (below the player). */
	protected static final int DEFAULT_LAYER = 4;
	/** The default layer for units. */
	protected static final int UNIT_LAYER = 0;
	/** The layer for the player. */
	protected static final int PLAYER_LAYER = 5;
	
	protected Collider collider;
	protected Vector pos;
	protected boolean ignoreCollisions = false;
	protected Animation animation;

	/** used to determine which entities should be rendered over others **/
	protected int z;
	protected String tag;
	/** direction the entity is facing **/
	protected Vector dir;
	/** generalizes concept of velocity to all entities */
	Vector velocity = new Vector(0, 0);
		
	private boolean deleted = false;
	private boolean hasCycled = false;
	
	/**
	 * This method will automatically set the collider focus to the entity
	 */
	public Entity(Vector pos, Collider collider) {
		this.pos = (pos == null) ? null : pos.copy();
		if (collider == null) {
			Debug.logger.warning("Entity " + this + " has null collider.");
		} else {
			this.collider = collider.cloneOnto(this);
		}
		z = DEFAULT_LAYER;
		refreshStats();
	}
	
	public Entity(Entity e) {
		this(e.pos, e.collider);
	}
	
	public Entity(Vector pos, int r) {
		this(pos, new CircleCollider(r));
	}
		
	public Entity(Vector pos) {
		this(pos, 1);
	}
	
	public static Entity spawnNew(World world, Vector pos, Class<? extends Entity> c) {
		return spawnNew(world, pos, c, true);
	}
	
	public static Entity spawnNew(World world, Vector pos, Class<? extends Entity> c, boolean checkCollisions) {
		try {
			Entity e = c.getConstructor(Vector.class).newInstance(pos);
			if (e instanceof Despawner) {
				((Despawner) e).setDespawnable(true);
			}
			if (checkCollisions && !e.shouldIgnoreCollisions() && world.getEntityTree().getFirstCollision(e) != null) {
				return null;
			}
			if (!world.add(e)) {
				return null;
			}
			return e;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Vector getPos() {
		return pos;
	}
	
	public Animation getAnimation() {
		return animation;
	}
	
	public boolean intersects(Vector pos1) {
		return collider.intersects(pos1);
	}
	
	public boolean intersects(Entity e) {
		return intersects(e.collider);
	}
	
	public boolean intersects(Collider c) {
		return intersects(c.getShape());
	}
	
	public boolean intersects(Shape shape) {
		return collider.intersects(shape);
	}
	
	public boolean intersectsWall(World world) {
		for (int i = (pos.getX() - collider.getRadiusX()) / Tile.WIDTH - 1; i <= (pos.getX() + collider.getRadiusX()) / Tile.WIDTH; i++) {
			for (int j = (pos.getY() - collider.getRadiusY()) / Tile.WIDTH - 1; j <= (pos.getY() + collider.getRadiusY()) / Tile.WIDTH; j++) {
				if (!intersects(Tile.getRectangle(i, j))) {
					continue;
				}
				if (world.isOccupied(i, j)) {
					return true;
				}
			}
		}
		return false;	
	}
	
	private boolean willHitTopOfTile(World world, Vector newPos) {
		Vector testPos = newPos.add(new Vector(0, collider.getRadiusY()));
		int j = testPos.getY() / Tile.WIDTH;
		for (int i = (testPos.getX() - collider.getRadiusX() + 1) / Tile.WIDTH; i <= (testPos.getX() + collider.getRadiusX() - 1) / Tile.WIDTH; i++) {
			if (world.isOccupied(i, j) && world.getTileLayer().getTileGrid(i, j) != null) {
				return true;
			}
		}
		return false;	
	}
	
	private boolean willHitBottomOfTile(World world, Vector newPos) {
		Vector testPos = newPos.sub(new Vector(0, collider.getRadiusY()));
		int j = testPos.getY() / Tile.WIDTH;
		for (int i = (testPos.getX() - collider.getRadiusX() + 1) / Tile.WIDTH; i <= (testPos.getX() + collider.getRadiusX() - 1) / Tile.WIDTH; i++) {
			if (world.isOccupied(i, j) && world.getTileLayer().getTileGrid(i, j) != null) {
				return true;
			}
		}
		return false;
	}
	
	public boolean contains(Entity e) {
		if (e == null) {
			return false;
		}
		return collider != null && collider.contains(e.collider);
	}
	
	protected void traverse(int depth) {
		String indent = "";
		for (int i = 0; i < depth; i++) {
			indent += "  ";
		}
		Debug.logger.info(indent + toString());
	}
	
	/** Print out a human-readable representation of an Entity/EntityTree. */
	public void traverse() {
		traverse(0);
	}
	
	@Override
	public String toString() {
		String tag = getTag();
		String name = tag == null ? Util.clean(this.getClass().getSimpleName()) : tag;
		return name.equals("entity") ? null : name;
	}
	
	public String toStringDebug() {
		return toString() + "{pos: " + pos + ", col: " + collider + "}";
	}
	
	/**
	 * 
	 * @param world
	 * @param deltaTime
	 */
	public void update(World world, double deltaTime) {
		if (!hasCycled && (animation == null || animation.hasCycled())) {
			onCycleComplete(world);
			hasCycled = true;
		}
		
		// This is for compatibility with old-style entities that lack velocity.
		if (velocity == null) {
			velocity = Vector.ZERO;
		}
		
		// Do gravity if this entity has gravity.
		if (getGravity() != null) {
			addVelocity(getGravity().multiply(deltaTime));
		}	
		updatePosition(world, deltaTime);
	}
	
	/** Default movement logic for entities.
	 * 
	 * This should only be overriden in exceptional cases.
	 */
	protected void updatePosition(World world, double deltaTime) {
		// Update position according to velocity.
		Vector newPos = pos.add(getVelocity().multiply(deltaTime));
		if (willHitTopOfTile(world, newPos)) {
			setVelocity(getSurfaceCollisionMode().getNewVelocity(getVelocity(), true));
			// TODO(snorri): Can snap to tile by adding that method to SurfaceCollisionMode.
		} else if (willHitBottomOfTile(world, newPos)) {
			// This case might actually never get hit?
			setVelocity(getSurfaceCollisionMode().getNewVelocity(getVelocity(), false));
		} else {
			setPos(newPos);
		}
	}
	
	public void renderAround(FocusedWindow<?> window, Graphics gr, double timeDelta) {
		if (Debug.collidersRendered() || (animation == null && window instanceof LevelEditor)  || inInteractRange(window)) {
			collider.render(window, gr);
		}
		
		if (animation == null) {
			return;
		}
				
		BufferedImage sprite = animation.getSprite(timeDelta);
		if (sprite == null) {
			return;
		}
		
		Vector rel = pos.sub(window.getCenterObject().getPos());
		gr.drawImage(sprite, rel.getX() + (window.getBounds().width - sprite.getWidth()) / 2, rel.getY() + (window.getBounds().height - sprite.getHeight()) / 2, sprite.getWidth(null), sprite.getHeight(null), null);
	}
	
	private boolean inInteractRange(FocusedWindow<?> g) {
		return this instanceof Interactor && g instanceof GameWindow &&
				((Interactor) this).inRange(((GameWindow) g).getFocus());
	}

	@Override
	public Nominal get(AbstractSemantics attr, CastEvent e) {
		
		if (attr == AbstractSemantics.POSITION) {
			return pos;
		}
		if (attr == AbstractSemantics.TILE) {
			return e.getWorld().getTileLayer().getTile(pos);
		}
		
		return Nominal.super.get(attr, e);
	}
	
	/**
	 * if dir is zero, then this function will always return false
	 * @return whether moving in direction dir would bring entity into wall
	 */
	private boolean wouldIntersectSomething(World world, Vector dir) {				
		return wouldIntersectSomethingAt(world, pos.add(dir));	
	}
	
	public boolean wouldIntersectSomethingAt(World world, Vector pos) {
		Entity newEnt = new Entity(pos, collider);
		return newEnt.intersectsWall(world) || world.getEntityTree().getFirstCollisionOtherThan(newEnt, this) != null;
	}
	
	/**
	 * moves entity WITHOUT recalculating entity tree radii
	 * @param world
	 * 	world we're moving in
	 * @param direction
	 * 	direction to move (magnitude is irrelevant)
	 * @param speed
	 * 	speed to move at
	 * @return
	 * 	whether or not we were able to move
	 */
	public boolean moveNicely(World world, Vector dir) {
		if (dir.equals(Vector.ZERO) || wouldIntersectSomething(world, dir)) {
			return false;
		}
		world.getEntityTree().move(this, pos.add(dir));
		return true;
	}
		
	public boolean shouldIgnoreCollisions() {
		return ignoreCollisions;
	}
	
	public void setPos(Vector pos) {
		this.pos = pos.copy();
	}
	
	public Collider getCollider() {
		return collider;
	}

	@Override
	public int compareTo(Entity other) {
		return Integer.compare(z, other.z);
	}
	
	@Override @Deprecated
	public Entity clone() {		
		try {
			Entity copy = (Entity) super.clone();
			copy.pos = copy.pos.copy();
			copy.collider = copy.collider.cloneOnto(this);
			return copy;
		} catch (CloneNotSupportedException e) {
			Debug.log("issue cloning entity " + this);
			e.printStackTrace();
			return null;
		}
	}
	
	public Entity copy() {
		try {
			Entity newEnt = getClass().getConstructor(Vector.class).newInstance(pos.copy());
			newEnt.collider = collider.cloneOnto(newEnt);
			newEnt.animation = new Animation(animation);
			return newEnt;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | SecurityException | InvocationTargetException | NoSuchMethodException e) {
			Debug.logger.log(Level.SEVERE, "Could not copy Entity.", e);
			return null;
		}
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
		Trigger.setTag(tag, this);
	}

	public boolean isStaticObject() {
		return false;
	}
	
	/**
	 * Refresh the core attributes of an object that has possibly been saved from old versions to be up to date.
	 * 
	 * Inventory and other dynamic fields must not be modified here. This will actually lead to critical failures.
	 * 
	 * This method is not fully supported; use at your own risk. However, in some cases it can prove to be useful
	 * for making old maps forward-compatible.
	 */
	public void refreshStats() {
	}
	
	public Vector getGridBounds() {
		return new Vector(collider.getRadiusX(), collider.getRadiusY()).multiply_(2).gridPosRounded_();
	}
	
	/**
	 * To prevent infinite loops, this method calls another method, which is the one that implementations should extend.
	 * @return Whether the deletion was successful.
	 */
	public final boolean onDelete(World world) {
		if (!deleted) {
			deleted = true;
			onSafeDelete(world);
			TriggerType.DESTROY.activate(tag);
			return true;
		}
		return false;
	}
	
	protected void onSafeDelete(World world) {
	}

	public void onExplosion(CollisionEvent e) {
	}
	
	/**
	 * this method is used to set the direction of an entity for animation purposes
	 * @param dir direction the entity is facing
	 */
	public void setDirection(Vector dir) {
		this.dir = dir.copy();
		if (animation != null) {
			setAnimation(animation);
		}
	}
	
	/**
	 * Set the animation of this entity to a copy of the specified animation with
	 * the correct rotation.
	 * @param animation
	 * 	The animation to copy.
	 */
	public void setAnimation(Animation animation) {
		hasCycled = false;
		if (dir == null) {
			this.animation = new Animation(animation);
			return;
		}
		this.animation = new Animation(animation).getRotated(dir);
	}
	
	/**
	 * This event fires after the entity's animation completes a cycle.
	 * Note that this is called by <code>update</code>, so it won't get called in the LevelEditor view.
	 * @param world
	 * The world in which the cycle was completed.
	 */
	protected void onCycleComplete(World world) {
	}
	
	/**
	 * @return the velocity
	 */
	public Vector getVelocity() {
		return velocity;
	}
	/** Destruct this entity and create an explosion.
	 * 
	 * @param world The world to explode in.
	 * @param damage The damage the explosion should yield.
	 */
	public void explode(World world, double damage) {
		world.delete(this);
		TriggerType.EXPLODE.activate(tag);
		world.add(new Explosion(getPos(), damage));
	}

	/**
	 * @param velocity the velocity to set
	 */
	public void setVelocity(Vector velocity) {
		this.velocity = velocity;
	}
	
	/**
	 * adds the new velocity vector to the current velocity
	 * @param velocity the velocity vector to be added to the current velocity
	 */
	public void addVelocity(Vector deltaVelocity) {
		setVelocity(velocity.add(deltaVelocity));
	}

	/**
	 * @return the terminalVelocity
	 */
	public static final int getTerminalVelocity() {
		return TERMINAL_VELOCITY;
	}
	
	protected SurfaceCollisionMode getSurfaceCollisionMode() {
		// Entities with different surface collision behaviors should override this.
		return SurfaceCollisionMode.STOP;
	}
	
	public boolean isFalling() {
		// With p=1, the magnitude will be exactly zero only when it has been reset for being on a surface.
		// At the top of a trajectory, the velocity may get close to zero, but it is very unlikely to be exactly zero.
		return getVelocity().magnitude() != 0;
	}
	
	/** Event hook for when an entity is spawned in the world. */
	public void onSpawn(World world) {}
	
	protected Vector getGravity() {
		return GRAVITY;
	}
	
	public DialogMap prepareDialogMap() {
		DialogMap inputs = new DialogMap();
		inputs.put("Tag", getTag());
		return inputs;
	}
	
	public void processDialogMap(DialogMap inputs, World world) {
		String tag = inputs.getText("Tag");
		setTag(tag.isEmpty() ? null : tag);
	}
	
}
