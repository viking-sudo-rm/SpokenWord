package snorri.overlay;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JTextPane;

import snorri.main.GameWindow;
import snorri.main.Main;

public class HelpOverlay extends Overlay {

	private static final long serialVersionUID = 1L;
	
	public HelpOverlay(GameWindow window) {
		
		super(window);
		
		JTextPane display = new JTextPane();
		display.setContentType("text/html");
		display.setEditable(false);
		display.addKeyListener(this);
		display.setMinimumSize(window.getSize());
		try {
			display.setPage(Main.getPath("/info/index.html").toURI().toURL());
		} catch (IOException e) {
			Main.error("could not find HTML info page");
			e.printStackTrace();
		}
		add(display);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}