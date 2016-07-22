package view.util;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This is a labeled component.
 *
 * A labeled component is simply a panel with a label and a component. The label is located to the left of the component.
 *
 * @author Jacob Glueck
 *
 * @param <T>
 *            the type of component.
 */
public class LabeledComponent<T extends Component> extends JPanel {

	/**
	 * Generated UID.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The label.
	 */
	private final JLabel label;
	/**
	 * The component.
	 */
	private final T c;

	/**
	 * Makes a LabledComonent with the specified label and component.
	 *
	 * @param label
	 *            the label text.
	 * @param comp
	 *            the component.
	 * @param space
	 *            the space between the label and the component.
	 */
	public LabeledComponent(String label, T comp, int space) {

		// Initialize the label and component
		this.label = new JLabel(label);
		c = comp;

		// Ensure that hte component remains at a nice size
		c.setMaximumSize(new Dimension((int) c.getPreferredSize().getWidth(), (int) c.getPreferredSize().getHeight()));

		// Use a box layout on the line axis
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		add(this.label);
		add(Box.createRigidArea(new Dimension(space, 0)));
		add(Box.createHorizontalGlue());
		add(c);
	}

	/**
	 * Makes a LabledComonent with the specified label and component.
	 *
	 * @param label
	 *            the label text.
	 * @param comp
	 *            the component.
	 * @param space
	 *            the space between the label and the component.
	 * @param labelWidth
	 *            the width of the label.
	 */
	public LabeledComponent(String label, T comp, int space, int labelWidth) {

		this(label, comp, space);
		setLabelWidth(labelWidth);
	}

	/**
	 * Sets the label width to a new width.
	 *
	 * @param labelWidth
	 *            the new width
	 */
	public void setLabelWidth(int labelWidth) {

		this.label.setMinimumSize(new Dimension(labelWidth, (int) this.label.getMinimumSize().getHeight()));
		this.label.setPreferredSize(new Dimension(labelWidth, (int) this.label.getPreferredSize().getHeight()));
		this.label.setMaximumSize(new Dimension(labelWidth, (int) this.label.getMaximumSize().getHeight()));
	}

	/**
	 * Gets the component.
	 *
	 * @return the component.
	 */
	public T getComponent() {

		return c;
	}
}
