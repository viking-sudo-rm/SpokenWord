package snorri.main;

import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.UIManager;

import snorri.dialog.DropMessage;
import snorri.dialog.Message;
import snorri.dialog.Objective;
import snorri.dialog.SpellMessage;
import snorri.entities.Player;
import snorri.events.SpellEvent.Caster;
import snorri.inventory.Droppable;
import snorri.keyboard.Key;
import snorri.overlay.DeathScreen;
import snorri.triggers.Trigger.TriggerType;
import snorri.world.Playable;
import snorri.world.World;

public class GameWindow extends FocusedWindow<Player> {
		
	/**
	 * Main game window
	 */
	private static final long serialVersionUID = 1L;
	
	private Playable universe;
	private Queue<Message> messageQ;
	private boolean hasDied;
	private long lastTime;
	
	private Objective objective;
		
	public GameWindow(Playable universe, Player focus) {
		super(focus);
		this.universe = universe;
		setCustomCenter(universe.findCenter());
		
		messageQ = new LinkedList<>();
		lastTime = getTimestamp();
		hasDied = false;
	}
	
	public GameWindow(Playable universe) {
		this(universe, universe.computeFocus());
	}
	
	@Override
	protected void onStart() {
		while (true) {
			if (TriggerType.TIMELINE.activate("start")) {
				break;
			}
		}
	}
	
	@Override
	protected void onFrame() {
		
		long time = getTimestamp();
		double deltaTime = (time - lastTime) / 1000000000d;
		lastTime = time;
		
		if (messageQ != null && messageQ.peek() != null && messageQ.peek().update(deltaTime)) {
			messageQ.poll().onClear(); //why is this sometimes null?
		}
				
		if (isPaused()) {
			return;
		}
				
		if (!hasDied && player != null && player.isDead()) {			
			TriggerType.TIMELINE.activate("death");
			hasDied = true;
			Main.setOverlay(new DeathScreen());
		}
		
		if (universe == null || universe.getCurrentWorld() == null) {
			return;
		}
		
		universe.update(getFocus(), deltaTime);
		repaint();
				
	}
	
	@Override
	public void paintComponent(Graphics g){
			
		super.paintComponent(g);
		if (player == null) {
			return;
		}
		
		long time = getTimestamp();
		double deltaTime = (time - lastRenderTime) / 1000000000d;
		lastRenderTime = time;
						
		universe.getCurrentWorld().render(this, g, deltaTime, true);
		player.getInventory().render(this, g);
		player.renderHealthBar(g);
		
		g.setFont(UIManager.getFont("Label.font"));
		if (objective != null) {
			objective.render(g, this);
		}
		
		//otherwise new ArrayList<>(messageQ) throws an error
		if (messageQ.isEmpty()) {
			return;
		}
		
		int xTrans = 0;
		for (Iterator<Message> iter = ((LinkedList<Message>) messageQ).descendingIterator(); iter.hasNext();) {
			xTrans += iter.next().render(this, g, xTrans);
		}
		
		if (Debug.LOG_FOCUS) {
			Debug.log("Focused component: " + KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner());
		}
		
		g.dispose();
		
	}
	
	public void showMessage(String msg) {
		showMessage(new SpellMessage(msg, true));
	}
	
	public void showMessage(Droppable drop) {
		showMessage(new DropMessage(drop));
	}
	
	public void showMessage(Message m) {
		Debug.log(m.toString());
		messageQ.add(m);
	}
	
	@Override
	public World getWorld() {
		return universe.getCurrentWorld();
	}
	
	@Override
	public Playable getUniverse() {
		return universe;
	}
	
	public void setObjective(Objective objective) {
		this.objective = objective;
	}
	
	public String getObjectiveInfo() {
		return objective.longDesc;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		
		super.keyPressed(e);
				
		if (Key.ESC.isPressed(e)) {
			pause();
		}
		
		if (player == null || player.isDead() || isPaused()) {
			return;
		}
		
	}
	
	public void openInventory() {
		openInventory(player.getInventory());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	@Override
	public void unpause() {
		super.unpause();
		lastTime = getTimestamp();
	}

	public Caster getFocusAsCaster() {
		return (Caster) getFocus();
	}
	
	/*@Override
	public Entity getCenterObject() {
		Debug.log("SUP!?!");
		if (universe.getCurrentWorld() != null) {
			//Debug.log("getCenterObject() location: " + universe.getCurrentWorld().getCenterObject().getPos());
			if (universe.getCurrentWorld().getCenterObject() != null) {
				return universe.getCurrentWorld().getCenterObject();
			}
			else {
				Debug.log("BRO!!!");
				return getFocus();
			}
		}
		else {
			return null;
		}
	}*/
	
}
