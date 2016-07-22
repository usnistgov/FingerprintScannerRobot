package view.robotpanel;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import view.util.TitledComponent;

/**
 * A panel with a fancy title.
 *
 * @author Jacob Glueck
 *
 */
public class RobotPanel extends JPanel {
	
	/**
	 * Default UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Makes a new robot panel with the specified information.
	 *
	 * @param title
	 *            the title.
	 * @param contents
	 *            the contents.
	 */
	public RobotPanel(String title, JPanel contents) {
	
		JPanel paddedContents = new JPanel();
		// Center the contents in the panel and ensure a 20 unit margin.
		paddedContents.setLayout(new BoxLayout(paddedContents, BoxLayout.LINE_AXIS));
		paddedContents.add(Box.createHorizontalGlue());
		paddedContents.add(Box.createRigidArea(new Dimension(20, 0)));
		paddedContents.add(contents);
		paddedContents.add(Box.createRigidArea(new Dimension(20, 0)));
		paddedContents.add(Box.createHorizontalGlue());
		
		// Add a cool title.
		TitledComponent robotConnectionTitle = new TitledComponent(title, paddedContents);
		setLayout(new BorderLayout());
		add(robotConnectionTitle, BorderLayout.CENTER);
		setMaximumSize(new Dimension((int) getMaximumSize().getWidth(), (int) getPreferredSize().getHeight()));
	}
}
