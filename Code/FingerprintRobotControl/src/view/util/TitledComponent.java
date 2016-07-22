package view.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

/**
 * A component with title.
 *
 * @author Jacob Glueck
 *
 */
public class TitledComponent extends JPanel {
	
	/**
	 * Default UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Makes a new TitledComponent with the specified title and component.
	 *
	 * @param title
	 *            the title.
	 * @param contents
	 *            the component.
	 */
	public TitledComponent(String title, Component contents) {
	
		setLayout(new BorderLayout());
		JPanel titlePanel = new JPanel(new BorderLayout());
		JLabel label = new JLabel(title);
		label.setFont(new Font(label.getFont().getName(), Font.BOLD, 18));
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.LINE_AXIS));
		labelPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		labelPanel.add(label);
		titlePanel.add(labelPanel, BorderLayout.CENTER);
		titlePanel.add(new JSeparator(SwingConstants.HORIZONTAL), BorderLayout.PAGE_END);
		add(titlePanel, BorderLayout.PAGE_START);
		add(contents, BorderLayout.CENTER);
	}
}