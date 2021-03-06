package snorri.entities;

import snorri.collisions.Collider;
import snorri.world.Vector;
import snorri.world.World;

/**
 * All subclasses should have the following constructors:
 * 	<ul>
 * 		<li><code>Vector pos<code></li>
 * 		<li><code>Vector pos, boolean despawn</code></li>
 * 	</ul>
 */

public abstract class Despawner extends Entity {
	
	private static final long serialVersionUID = 1L;
	
	protected static final int DEFAULT_LIFESPAN = 5;

	protected float age; //set age to -1 to make it not despawn
	private boolean despawnable;
	
	protected Despawner(Vector pos, int r) {
		super(pos, r);
	}
			
	protected Despawner(Entity e) {
		super(e);
	}
	
	protected Despawner(Vector pos, Collider c) {
		super(pos, c);
		setDespawnable(false);
	}
	
	public void setDespawnable(boolean despawn) {
		age = despawn ? 0 : -1;
		despawnable = despawn;
	}
	
	@Override
	public boolean isStaticObject() {
		return !despawnable;
	}

	@Override
	public void update(World world, double deltaTime) {
		
		if (age != -1) {
			age += deltaTime;
		}
		
		if (shouldDespawn()) {
			world.delete(this);
			return;
		}
		
		// FIXME(#75): Probably want this to be inherited, but currently breaks.
		super.update(world, deltaTime);
	}
	
	protected boolean shouldDespawn() {
		return age > getLifeSpan();
	}
	
	protected double getLifeSpan() {
		return DEFAULT_LIFESPAN;
	}
	
	@Override
	public void refreshStats() {
		setDespawnable(false);
	}
	
	@Override
	public Vector getGravity() {
		return null;
	}
	
}
