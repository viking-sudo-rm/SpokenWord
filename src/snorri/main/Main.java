package snorri.main;

import java.awt.FileDialog;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.UIManager;

import snorri.parser.Lexicon;
import snorri.terrain.TerrainGen;
import snorri.world.World;

public class Main {

	private static GamePanel window;
	private static JComponent outerOverlay;

	private static JFrame frame;
	private static JLayeredPane pane;

	private static Font customFont;

	public static class ResizeListener implements ComponentListener {

		public ResizeListener() {
		}
		
		@Override
		public void componentResized(ComponentEvent e) {
			resize();
		}

		@Override
		public void componentMoved(ComponentEvent e) {
			resize();
		}

		@Override
		public void componentShown(ComponentEvent e) {
		}

		@Override
		public void componentHidden(ComponentEvent e) {
		}
		
		private void resize() {
			resize(window);
			resize(outerOverlay);
		}
		
		private static void resize(JComponent component) {
			if (component != null) {
				component.setBounds(frame.getContentPane().getBounds());
			}
		}
		
	}
	
	public static void main(String[] args) {

		Lexicon.init();

		setupFont();

		frame = new JFrame("Spoken Word");
		frame.setSize(1800, 900);
		frame.addComponentListener(new Main.ResizeListener());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		pane = new JLayeredPane();
		getLayeredPane().setOpaque(true);

		frame.getContentPane().add(getLayeredPane());
		//frame.setLocationRelativeTo(null);
		// FOR FULL SCREEN: frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

		launchMenu();

	}

	public static Rectangle getBounds() {
		return frame.getBounds();
	}

	public static void error(Object o) {
		System.out.println("[ERROR] " + o);
	}

	public static void log(String s) {
		System.out.println("[LOG] " + s);
	}

	public static void log(Object o) {
		System.out.println("[RAW] " + o);
	}

	public static GamePanel getWindow() {
		return window;
	}

	public static File getDir() {
		return new File(".");
	}

	public static File getPath(String path) {
		return new File(getDir(), path);
	}

	public static void setupFont() {
		customFont = loadFont("/fonts/thothDefault.ttf");
		UIManager.put("Button.font", getCustomFont(20));
		Main.log("default font loaded");
	}

	public static Font getCustomFont(float size) {
		return customFont.deriveFont(Font.PLAIN, size);
	}

	public static Font loadFont(String path) {
		try {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			File fontFile = getPath(path);
			Font f = Font.createFont(Font.TRUETYPE_FONT, fontFile);
			ge.registerFont(f);
			return f;
		} catch (IOException | FontFormatException e) {
			Main.error("font not found at " + path);
			return null;
		}
	}

	public static Image getImageResource(String path) {
		try {
			return ImageIO.read(getPath(path));
		} catch (IllegalArgumentException e) {
			Main.error("unable to find image " + path);
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	public static File getFileDialog(String msg, int flag) {
		return getFileDialog(msg, flag, false);
	}

	public static File getFileDialog(String msg, int flag, boolean isLevel) {
		FileDialog fd = new FileDialog(frame, msg, flag);
		fd.setVisible(true);

		if (fd.getFile() == null) {
			return null;
		}

		File f = new File(fd.getDirectory(), fd.getFile());

		// if they select an image (or we are editing a level, not a world),
		// don't grab the folder
		if (fd.getFile().endsWith("png") || isLevel) {
			return f;
		}

		// otherwise return that directory
		if (!f.isDirectory()) {
			return new File(fd.getDirectory());
		}

		// if the file is a directory or doesn't exist, return it
		return f;
	}

	public static String getHTMLGlyph(String raw) {
		File f = getPath("/textures/hieroglyphs/" + raw + ".png");
		if (!f.exists()) {
			return null;
		}
		try {
			return "<img src=\'" + f.toURI().toURL() + "'/>";
		} catch (MalformedURLException e) {
			Main.error("bad URL for file " + raw);
			return null;
		}
	}

	/**
	 * display the window in the main JFrame
	 * 
	 * @param newWindow
	 *            the new window to display
	 */
	public static final void setWindow(GamePanel newWindow) {
		if (window != null) {
			getLayeredPane().remove(window);
			getLayeredPane().revalidate();
		}
		window = newWindow;
		window.setVisible(true);
		ResizeListener.resize(window);
		getLayeredPane().add(window, JLayeredPane.DEFAULT_LAYER);
		getLayeredPane().revalidate();
		getLayeredPane().repaint();
		window.requestFocusInWindow();
	}

	/**
	 * set the outer HUD which should appear over the game screen
	 * 
	 * @param newOverlay
	 *            the new HUD overlay
	 */
	public static final void setOverlay(JComponent newOverlay) {
		if (outerOverlay != null) {
			getLayeredPane().remove(outerOverlay);
			getLayeredPane().revalidate();
		}
		outerOverlay = newOverlay;
		if (newOverlay != null) {
			outerOverlay.setVisible(true);
			ResizeListener.resize(outerOverlay);
			getLayeredPane().add(outerOverlay, JLayeredPane.PALETTE_LAYER);
			getLayeredPane().revalidate();
			getLayeredPane().repaint();
			outerOverlay.requestFocusInWindow();
		}
	}

	public static JLayeredPane getLayeredPane() {
		return pane;
	}

	public static void launchMenu() {
		setOverlay(null);
		setWindow(new MainMenu());
	}

	public static void launchGame(World world) {
		setWindow(new GameWindow(world));
	}
	
	public static void launchGame(TerrainGen gen) {
		loadInto(new Runnable() {
			@Override
			public void run() {
				launchGame(gen.genWorld());
			}
		});
	}

	public static void launchGame() {
		launchGame(new TerrainGen(200, 200));
	}

	public static void launchEditor() {
		setWindow(new LevelEditor());
	}

	/**
	 * show a loading screen while the thread runs
	 * 
	 * @param proc
	 *            a Runnable whose run() method will be invoked. run() should
	 *            change the screen to something cooler when it's done
	 */
	public static void loadInto(Runnable proc) {
		setWindow(new LoadingScreen());
		new Thread(proc).start();
	}

	public static JFrame getFrame() {
		return frame;
	}

}
