package view.util;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JPanel;

/**
 * Used for centering components in a JPanel; works by putting each component into a JPanel with a flow layout.
 *
 * @author Jacob Glueck
 */
public class CenteredComponent extends JPanel {

	/**
	 * Default UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Makes a new centered component with the specified components.
	 *
	 * Makes a JPanel with a flow layout and puts each component, in the order in which they are provided, into the JPanel.
	 *
	 * @param c
	 *            the components to put into the JPanel.
	 */
	public CenteredComponent(Component... c) {
	
		setLayout(new FlowLayout());
		for (Component element : c)
			add(element);
	}
}
