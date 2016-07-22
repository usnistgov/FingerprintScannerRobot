package view.robotpanel;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import robot.RobotCom;

/**
 * Renders RobotCom objects in a JList.
 *
 * @author Jacob Glueck
 *
 */
public class RobotComRenderer extends JLabel implements ListCellRenderer<RobotCom<?>> {

	/**
	 * Default UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Makes a new RobotComRenderer.
	 */
	public RobotComRenderer() {

		setOpaque(true);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends RobotCom<?>> list, RobotCom<?> value, int index, boolean isSelected, boolean cellHasFocus) {

		// Handle selection
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		// Display text
		if (value != null) {
			String location = value.getLocationString();
			setText("<html><font size=+1>" + value.getRobotInfo().getRobotType() + "@" + location + "</font><br>" + value.getRobotInfo().getMacString() + "</html>");
		} else
			setText("<html><font size=+1>No robots found</font><br>Try searching</html>");
		return this;
	}

	// Prevents text in combo box from turning gray when disabled
	@Override
	public void setForeground(Color fg) {

	}
}