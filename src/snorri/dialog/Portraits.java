package snorri.dialog;

import java.awt.Image;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import snorri.main.Debug;
import snorri.main.Main;
import snorri.util.Util;

public class Portraits {

	private static Map<String, Image> portraits = new HashMap<>();
	
	static {
		portraits.put(null, Main.getImage("/textures/portraits/box.png"));
		for (File f : Main.getFile("/textures/portraits").listFiles()) {
			portraits.put(Util.removeExtension(f.getName()), Main.getImage(f));
		}
	}
	
	public static void load() {
		Debug.logger.info(portraits.entrySet().size() + " portraits loaded.");
	}
	
	public static Image get(String name) {
		return portraits.get(name);
	}
	
}
