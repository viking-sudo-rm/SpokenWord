package snorri.main;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import snorri.entities.Entity;
import snorri.world.Level;
import snorri.world.Tile;
import snorri.world.Vector;
import snorri.world.World;

//TODO: show selected texture at top of selected texture menu �can this be done?
//TODO: add undo/redo function
//TODO: add image to world feature
//TODO: add default texture
//TODO: add ability to add entities

public class LevelEditor extends FocusedWindow implements ActionListener {

	private static final long serialVersionUID = 1L;

	private static final int SCALE_FACTOR = 10;

	private World world;
	private Entity focus;
	private Tile selectedTile;
	private boolean isClicking = false;

	private boolean canGoLeft;
	private boolean canGoRight;
	private boolean canGoUp;
	private boolean canGoDown;

	JMenuBar menuBar;
	JMenu menu, submenu;
	JMenuItem menuItem;
	JRadioButtonMenuItem rbMenuItem;
	JCheckBoxMenuItem cbMenuItem;

	public LevelEditor() {
		super();

		selectedTile = new Tile(0, 0);
		createMenu();

		repaint();

		focus = new Entity(new Vector(50, 50));
	}

	private void createMenu() {
		// Create the menu bar.
		menuBar = new JMenuBar();

		// Build the first menu.
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);

		// a group of JMenuItems
		menuItem = new JMenuItem("New", KeyEvent.VK_N);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Open", KeyEvent.VK_O);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Save", KeyEvent.VK_S);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Resize", KeyEvent.VK_R);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Tile Selection Menu
		menu = new JMenu("Select Tile");
		menu.setMnemonic(KeyEvent.VK_T);
		menuBar.add(menu);

		ButtonGroup group = new ButtonGroup();

		boolean first = true;
		for (Tile t : Tile.getAll()) {

			if (t == null || t.getTexture() == null) {
				continue;
			}

			rbMenuItem = new JRadioButtonMenuItem(t.toString(), new ImageIcon(t.getTexture()));
			rbMenuItem.setSelected(first);
			rbMenuItem.setActionCommand("set" + t.toNumericString());
			rbMenuItem.addActionListener(this);
			group.add(rbMenuItem);
			menu.add(rbMenuItem);

			first = false;

		}

		Main.getFrame().setJMenuBar(menuBar);
	}

	private boolean isInteger(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private int[] whDialog() {
		int[] wh = { -1, -1 };
		JTextField w = new JTextField("300");
		JTextField h = new JTextField("300");
		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(new JLabel("Width:"));
		panel.add(w);
		panel.add(new JLabel("Height:"));
		panel.add(h);
		int option = JOptionPane.showConfirmDialog(null, panel, "Enter Width & Height", JOptionPane.PLAIN_MESSAGE);
		if (option == JOptionPane.CLOSED_OPTION) {
			return null;
		}

		if (isInteger(w.getText()) && isInteger(h.getText())) {
			wh[0] = Integer.parseInt(w.getText());
			wh[1] = Integer.parseInt(h.getText());
		}
		

		return wh;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().startsWith("set")) {
			selectedTile = new Tile(e.getActionCommand().substring(3));
			return;
		}

		switch (e.getActionCommand()) {
		case "New":
			int[] wh = whDialog();
			
			if (wh == null) {
				return;
			}

			if (wh != null && wh[0] > 0 && wh[1] > 0 && wh[0] <= Level.MAX_SIZE && wh[1] <= Level.MAX_SIZE) {
				world = new World(wh[0], wh[1]);
			} else {
				world = new World();
			}
			break;
		case "Open":
			World w1 = World.wrapLoad();
			if (w1 != null) {
				world = w1;
			}
			break;
		case "Save":

			if (world == null) {
				return;
			}

			world.wrapSave();
			break;
		case "Resize":
			if (world == null) {
				return;
			}
			
			int[] whNew = whDialog();
			
			if (whNew == null) {
				return;
			}
			
			resize(whNew[0],whNew[1]);
		}

		repaint();

	}

	@Override
	public void paintComponent(Graphics gr) {

		super.paintComponent(gr);

		if (world == null) {
			return;
		}

		world.render(this, gr, false);
	}

	@Override
	protected void onFrame() {

		if (world != null) {
			canGoLeft = true;
			canGoRight = true;
			canGoUp = true;
			canGoDown = true;

			if (focus.getPos().getX() <= -SCALE_FACTOR) {
				canGoLeft = false;
			}
			if (focus.getPos().getX() >= world.getLevel().getDimensions().getX() * Tile.WIDTH + SCALE_FACTOR) {
				canGoRight = false;
			}
			if (focus.getPos().getY() <= -SCALE_FACTOR) {
				canGoUp = false;
			}
			if (focus.getPos().getY() >= world.getLevel().getDimensions().getY()  * Tile.WIDTH + SCALE_FACTOR) {
				canGoDown = false;
			}

			if (states.getMovementVector().getX() < 0 && !canGoLeft) {
				focus.getPos().sub(states.getMovementVector().getProjectionX().scale(SCALE_FACTOR));
			}
			if (states.getMovementVector().getX() > 0 && !canGoRight) {
				focus.getPos().sub(states.getMovementVector().getProjectionX().scale(SCALE_FACTOR));
			}
			if (states.getMovementVector().getY() < 0 && !canGoUp) {
				focus.getPos().sub(states.getMovementVector().getProjectionY().scale(SCALE_FACTOR));
			}
			if (states.getMovementVector().getY() > 0 && !canGoDown) {
				focus.getPos().sub(states.getMovementVector().getProjectionY().scale(SCALE_FACTOR));
			}
			focus.getPos().add(states.getMovementVector().scale(SCALE_FACTOR));

			if (isClicking) {
				Vector location = getMousePosAbsolute().copy();
				int x = location.getX();
				int y = location.getY();

				world.getLevel().setTile(x, y, new Tile(selectedTile));
			}
		}

		repaint();

	}

	@Override
	public Entity getFocus() {
		return focus;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			fill();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			isClicking = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		isClicking = false;
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}
	// TOBY do stuff here!

	public void fill() {
		Vector location = getMousePosAbsolute().copy();
		int x = location.getX() / Tile.WIDTH;
		int y = location.getY() / Tile.WIDTH;

		Tile t = world.getLevel().getNewTileGrid(x, y);
		if (selectedTile != null && world.getLevel().getNewTileGrid(x, y) != null && t != null
				&& !t.equals(selectedTile)) {
			world.getLevel().setTileGrid(x, y, selectedTile);
			fill_helper(x + 1, y, t);
			fill_helper(x - 1, y, t);
			fill_helper(x, y + 1, t);
			fill_helper(x, y - 1, t);
		}
	}

	public void fill_helper(int x, int y, Tile t) {
		if (selectedTile != null && world.getLevel().getNewTileGrid(x, y) != null && t != null
				&& world.getLevel().getNewTileGrid(x, y).equals(t)) {
			world.getLevel().setTileGrid(x, y, selectedTile);
			fill_helper(x + 1, y, t);
			fill_helper(x - 1, y, t);
			fill_helper(x, y + 1, t);
			fill_helper(x, y - 1, t);
		}
	}
	
	public void resize(int newWidth, int newHeight) {
		world = new World(world.getLevel().resize(newWidth, newHeight));
	}
	
	//TODO: does nothing right now
	public void autosave() {
		return;
	}
}