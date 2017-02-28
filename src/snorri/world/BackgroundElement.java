package snorri.world;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import snorri.audio.ClipWrapper;
import snorri.main.Main;
import snorri.main.Util;
import snorri.semantics.Nominal;
import snorri.world.TileType;

public enum BackgroundElement implements Nominal, TileType {
	SAND(true, false, new BufferedImage[] {
		getImage("sand00.png"),
		getImage("sand01.png"),
		getImage("sand02.png"),
		getImage("sand03.png")}, false, true),
	WALL(false, new BufferedImage[] {
		getImage("wall00.png"),
		getImage("wall01.png"),
		getImage("wall02.png"),
		getImage("wall03.png"),
		getImage("wall04.png"),
		getImage("wall05.png"),
		getImage("wall06.png"),
		getImage("wall07.png"),
		getImage("wall08.png"),
		getImage("wall09.png"),
		getImage("wall10.png")}, true),
	TREE(false, Main.getImage("tree00.png")),
	FOUNDATION(false, Tile.DEFAULT_TEXTURE),
	HUT(false, Tile.DEFAULT_TEXTURE),
	WATER(false, true, getImage("water00.png")),
	LAVA(false, true, new BufferedImage[] {
		getImage("lava00.png"),
		getImage("lava01.png"),
		getImage("lava02.png")}),
	GRASS(true, false, new BufferedImage[] {
		getImage("grass00.png"),
		getImage("grass01.png")}, false, true),
	VOID(false, false, Main.getImage("void00.png")),
	COLUMN(false, new BufferedImage[] {
		getImage("column00.png"),
		getImage("column01.png")}, true),
	DOOR(false, getImage("door00.png"), true),
	SANDSTONE(false, false, new BufferedImage[] {
			getImage("sandstone00.png"),
			getImage("sandstone01.png"),
			getImage("sandstone02.png"),
			getImage("default00.png")}, false, true), //will change to look like bricks
	FLOOR(true, new BufferedImage[] {getImage("floor00.png"),
		getImage("floor01.png"),
		getImage("floor02.png"),
		getImage("floor03.png"),
		getImage("floor04.png"),
		getImage("floor05.png"),
		getImage("floor06.png"),
		getImage("floor07.png"),
		getImage("floor08.png"),
		getImage("floor09.png"),
		getImage("floor10.png"),
		getImage("floor11.png"),
		getImage("floor12.png")}, true),
	GRAVEL(true, getImage("floor11.png")),
	STONE(false, new BufferedImage[] {
		getImage("stone00.png"),
		getImage("stone01.png")}),
	DEEP_WATER(false, false, getImage("water01.png")),
	CLIFF(false, false, new BufferedImage[] {
		getImage("cliff00.png"),
		getImage("cliff01.png"),
		getImage("cliff02.png"),
		getImage("cliff03.png"),
		getImage("cliff04.png"),
		getImage("cliff05.png"),
		getImage("cliff06.png"),
		getImage("cliff07.png"),
		getImage("cliff08.png"),
		getImage("cliff09.png"),
		getImage("cliff10.png"),
		getImage("cliff11.png")}, true),
	WOOD(true, new BufferedImage[] {
		getImage("wood00.png"),
		getImage("wood01.png"),
		getImage("wood02.png")}, true),
	BRICK(true, new BufferedImage[] {
		getImage("brick00.png"),
		getImage("brick01.png"),
		getImage("brick02.png"),
		getImage("brick03.png"),
		getImage("brick04.png"),
		getImage("brick05.png"),
		getImage("brick06.png")}, true);
	
	private boolean	pathable, canShootOver, atTop, changable;
	private BufferedImage[]	textures;
								
	BackgroundElement() {
		this(true);
	}
	
	private static BufferedImage getImage(String string) {
		return Tile.getImage("/textures/tiles/BackgroundElements/" + string);
	}
	
	BackgroundElement(boolean pathable) {
		this(pathable, new BufferedImage[] {Tile.DEFAULT_TEXTURE});
	}
	
	BackgroundElement(boolean pathable, BufferedImage texture) {
		this(pathable, new BufferedImage[] {texture});
	}
	
	BackgroundElement(boolean pathable, BufferedImage[] textures) {
		this.pathable = pathable;
		this.textures = textures;
		canShootOver = pathable;
		changable = false;
		atTop = false;
	}
	
	/**
	 * Create a a liquid tile
	 * @param pathable
	 * 	Whether this tile can be walked over
	 * @param liquidEditable
	 * 	Whether the player can modify this tile with spells
	 * @param textures
	 * 	A list of textures
	 */
	BackgroundElement(boolean pathable, boolean liquidEditable, BufferedImage[] textures) {
		this(pathable, textures);
		canShootOver = true;
		changable = liquidEditable;
	}
	
	BackgroundElement(boolean pathable, boolean liquidEditable, BufferedImage texture) {
		this(pathable, texture);
		canShootOver = true;
		changable = liquidEditable;
	}
	
	BackgroundElement(boolean pathable, boolean liquidEditable, BufferedImage[] textures, boolean atTop) {
		this(pathable, textures);
		canShootOver = true;
		changable = liquidEditable;
		this.atTop = atTop;
	}
	
	BackgroundElement(boolean pathable, boolean liquidEditable, BufferedImage texture, boolean atTop) {
		this(pathable, texture);
		canShootOver = true;
		changable = liquidEditable;
		this.atTop = atTop;
	}
	
	BackgroundElement(boolean pathable, BufferedImage[] textures, boolean atTop) {
		this(pathable , textures);
		this.atTop = atTop;
	}
	
	BackgroundElement(boolean pathable, BufferedImage texture, boolean atTop) {
		this(pathable , texture);
		this.atTop = atTop;
	}
	
	/**
	 * Use to make tile types which can be changed with sandstorms, etc.
	 * @param pathable
	 * 	Whether it can be walked on
	 * @param swimmable
	 * 	Whether it is a liquid
	 * @param texture
	 * 	The texture to display
	 * @param atTop
	 * 	Whether it should be bitmapped over
	 * @param changable
	 * 	Whether it can be changed
	 */
	BackgroundElement(boolean pathable, boolean swimmable, BufferedImage texture, boolean atTop, boolean changable) {
		this(pathable , texture, atTop);
		this.changable = changable;
	}
	

	/**
	 * Use to make tile types which can be changed with sandstorms, etc.
	 * @param pathable
	 * 	Whether it can be walked on
	 * @param swimmable
	 * 	Whether it is a liquid
	 * @param texture
	 * 	The texture to display
	 * @param atTop
	 * 	Whether it should be bitmapped over
	 * @param changable
	 * 	Whether it can be changed
	 */

	BackgroundElement(boolean pathable, boolean swimmable, BufferedImage[] textures, boolean atTop, boolean changable) {
		this(pathable , textures, atTop);
		this.changable = changable;
	}
	


	public boolean isLiquid() {
		return !pathable && canShootOver;
	}
	
	public boolean isChangable() {
		return changable;
	}
	
	@Override
	public BackgroundElement byId(int id) {
		return values()[id];
	}
	
	public static BackgroundElement byIdStatic(int id) {
		return values()[id];
	}
	
	@Override
	public int getId() {
		return ordinal();
	}
	
	public boolean isPathable() {
		return pathable;
	}
	
	public boolean canShootOver() {
		return canShootOver;
	}
	
	@Override
	public BufferedImage[] getTextures() {
		if (textures != null)
				return textures;
		else
			//Main.error("no textures found, returning default texture");
			return new BufferedImage[] {Tile.DEFAULT_TEXTURE};
	}
	
	@Override
	public BufferedImage getTexture(int index) {
		if (index >= textures.length) {
			//Main.error("texture not found, index out of bounds, returning default texture");
			return Tile.DEFAULT_TEXTURE;
		}
		return textures[index];
	}
	
	@Override
	public int getNumberStyles() {
		return textures.length;
	}
	
	@Override
	public String toString() {
		return Util.clean(name());
	}
	
	@Override
	public Object get(World world, AbstractSemantics attr) {
		if (attr == AbstractSemantics.FLOOD && isLiquid()) {
			return new Tile(this);
		}
		
		if (attr == AbstractSemantics.STORM && this == SAND) {
			return new Tile(this, 0);
		}
		
		return Nominal.super.get(world, attr);
	}
	
	public boolean isAtTop() {
		return atTop;
	}
	
	@Override
	public ArrayList<Tile> getSubTypes() {
		int i = getId();
		ArrayList<Tile> list = new ArrayList<Tile>();
		for(int j = 0; j < byId(i).getNumberStyles(); j++) {
			list.add(new Tile(i,j));
		}
		return list;
	}
	
	@Override
	public boolean hasSounds() {
		//return (sounds != null && sounds.length >= 1);
		return Tile.sounds.length >= 1;
	}
	
	@Override
	public int getNumSounds() {
		return Tile.sounds.length;
	}
	
	@Override
	public ClipWrapper[] getSounds() {
		return Tile.sounds;
	}
	
	@Override
	public ClipWrapper getSound(int x) { //FIXME: what happens when a null sound gets played?  Should there be a default sound?
		if (x < Tile.sounds.length)
			return Tile.sounds[x];
		else {
			Main.error("index out of bounds");
			return null;
		}
	}
}