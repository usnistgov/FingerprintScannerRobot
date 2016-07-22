package view.util;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * A panel that holds buttons and has a title.
 *
 * @author Jacob Glueck
 *
 */
public class TitledButtonPanel extends JPanel {
	
	/**
	 * Default UID.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The button width.
	 */
	private final int buttonWidth;
	/**
	 * The distance between buttons.
	 */
	private final int spacing;

	/**
	 * Makes a new titled button panel with the specified information.
	 *
	 * @param title
	 *            the title.
	 * @param buttonWidth
	 *            the button width.
	 * @param spacing
	 *            the distance between buttons.
	 */
	public TitledButtonPanel(String title, int buttonWidth, int spacing) {
	
		setBorder(BorderFactory.createTitledBorder(title));
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		this.buttonWidth = buttonWidth;
		this.spacing = spacing;
	}

	/**
	 * Makes a new titled button panel with the specified information.
	 *
	 * @param title
	 *            the title.
	 * @param buttonWidth
	 *            the button width.
	 * @param spacing
	 *            the distance between buttons.
	 * @param buttons
	 *            the buttons to use.
	 */
	public TitledButtonPanel(String title, int buttonWidth, int spacing, JButton... buttons) {
	
		this(title, buttonWidth, spacing);
		setButtons(buttons);
	}

	/**
	 * Sets the buttons on the panel.
	 *
	 * @param buttons
	 *            the buttons to put on the panel
	 */
	public void setButtons(JButton... buttons) {
	
		removeAll();
		for (int x = 0; x < buttons.length; x++) {
			JButton b = buttons[x];
			b.setMinimumSize(new Dimension(buttonWidth, (int) b.getMinimumSize().getHeight()));
			b.setPreferredSize(new Dimension(buttonWidth, (int) b.getPreferredSize().getHeight()));
			b.setMaximumSize(new Dimension(buttonWidth, (int) b.getMaximumSize().getHeight()));
			add(b);
			if (x != buttons.length - 1)
				add(Box.createRigidArea(new Dimension(spacing, 0)));
		}
	}
	
	/**
	 * Determines the width of the widest JButton in the list.
	 *
	 * @param buttons
	 *            the buttons to check.
	 * @return the width of the widest JButton.
	 */
	public static int maximumWidth(JButton... buttons) {
	
		int maxWidth = Integer.MIN_VALUE;
		for (JButton button : buttons)
			maxWidth = (int) Math.max(button.getPreferredSize().getWidth(), maxWidth);
		return maxWidth;
	}
	
	/**
	 * Makes a titled button panel with the specified information. Set the width of all the buttons to the width of the widest button.
	 *
	 * @param title
	 *            the title.
	 * @param spacing
	 *            the distance between the buttons.
	 * @param buttons
	 *            the buttons.
	 */
	public TitledButtonPanel(String title, int spacing, JButton... buttons) {
	
		this(title, TitledButtonPanel.maximumWidth(buttons), spacing, buttons);
	}
}
