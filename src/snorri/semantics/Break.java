package snorri.semantics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import snorri.entities.Entity;
import snorri.events.CastEvent;
import snorri.parser.Node;
import snorri.triggers.Trigger.TriggerType;
import snorri.world.TileType;
import snorri.world.UnifiedTileType;
import snorri.world.TileLayer;
import snorri.world.Tile;
import snorri.world.Vector;
import snorri.world.World;

public class Break extends TransVerbDef {
	
	private static final List<Vector> TRIPWIRE_CONNECTIONS = new ArrayList<>();
	private static final HashSet<TileType> TRIPWIRES = new HashSet<>();
	
	static {
		
		Vector[] unitVectors = new Vector[] {new Vector(0, 1), new Vector(1, 0)};
		for (Vector unitVector : unitVectors) {
			for (int x = -1; x <= 1; x++) {
				TRIPWIRE_CONNECTIONS.add(unitVector.copy().multiply_(x));
			}
		}
		
		TRIPWIRES.add(UnifiedTileType.TRIPWIRE);
		TRIPWIRES.add(UnifiedTileType.TRIPWIRE_END);
		
	}
	
	/**
	 * Only use this interface with Entities
	 * @author lambdaviking
	 */
	public interface Smashable {
		
		default void smash(World world, double deltaTime) {
			world.delete((Entity) this);
		}
		
	}
	
	public Break() {
		super();
	}

	@Override
	public boolean exec(Node<Object> object, CastEvent e) {
		
		Object obj = object.getMeaning(e);
		
		if (!(obj instanceof Entity)) {
			return false;
		}
		
		Entity target = (Entity) obj;
		
		if (target instanceof Smashable) {
			((Smashable) target).smash(e.getWorld(), e.getDeltaTime());
			return true;
		}
		
		//TODO make this open doors that are locked
		Vector tilePos = ((Entity) obj).getPos().copy().gridPos_();
		return Break.tryToCutTripWire(e.getWorld(),  tilePos) ||
				Open.openDoor(e.getWorld(), tilePos);
		
	}

	@Override
	public boolean eval(Object subj, Object obj, CastEvent e) {
		return (Entity) obj instanceof Smashable;
	}

	@Override
	public String toString() {
		return "break";
	}
	
	/**
	 * Recursively cut the tripwire at this grid position.
	 * @param v The grid position at which to cut.
	 */
	public static boolean tryToCutTripWire(World world, Vector v) {
		TileLayer foreground = world.getTileLayer();
		if (!isTripwire(foreground.getTileGrid(v))) {
			return false;
		}
		world.wrapGridUpdate(v, new Tile(UnifiedTileType.EMPTY));
		TriggerType.TRIP.activate(v);
		for (Vector trans : TRIPWIRE_CONNECTIONS) {
			tryToCutTripWire(world, v.copy().add_(trans));
		}
		return true;
	}

	public static boolean isTripwire(Tile tileGrid) {
		return TRIPWIRES.contains(tileGrid.getType());
	}

}
