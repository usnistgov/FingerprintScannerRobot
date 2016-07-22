package view.robotpanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.text.ParseException;
import java.util.List;

import javax.swing.DefaultSingleSelectionModel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import robot.Command;
import robot.Robot;
import view.configurator.GraphicalRobotConfigurator;
import view.configurator.RobotConfigurator;
import view.configurator.TextRobotConfigurator;

/**
 * Represents the robot control panel, which is used to configure the moves.
 *
 * @author Jacob Glueck
 */
public class CommandEditorPanel extends RobotConfigurator {

	/**
	 * Default UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The configuration panel.
	 */
	private final GraphicalRobotConfigurator gConfig;
	/**
	 * The text configuration panel.
	 */
	private final TextRobotConfigurator tConfig;
	/**
	 * The tabbed pane with the two configurators.
	 */
	private final JTabbedPane tabs;
	
	/**
	 * Makes a new RobotControlPanel with the specified connection panel.
	 *
	 * @param robotType
	 *            the type of robot that this configurator configures commands for.
	 */
	public CommandEditorPanel(Class<? extends Robot> robotType) {
	
		super(robotType);

		gConfig = new GraphicalRobotConfigurator(robotType);
		tConfig = new TextRobotConfigurator(robotType);
		tabs = new JTabbedPane();
		VetoableSingleSelectionModel model = new VetoableSingleSelectionModel();
		VetoableChangeListener changeListener = new VetoableChangeListener() {
			
			@Override
			public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {

				int oldSelection = (int) evt.getOldValue();
				if (oldSelection == -1)
					return;
				RobotConfigurator current = (RobotConfigurator) tabs.getComponentAt(oldSelection);
				RobotConfigurator next = (RobotConfigurator) tabs.getComponentAt((int) evt.getNewValue());
				
				try {
					next.load(current.get());
				} catch (ParseException e1) {
					JOptionPane.showMessageDialog(CommandEditorPanel.this, "Unable to change tabs due to error parsing command number " + e1.getErrorOffset(),
							"Error Parsing Command", JOptionPane.ERROR_MESSAGE);
					throw new PropertyVetoException("Error in tab content, change forbidden", evt);
				}
			}
		};
		model.addVetoableChangeListener(changeListener);
		tabs.setModel(model);

		setLayout(new BorderLayout());
		tabs.addTab("Graphical Configurator", gConfig);
		tabs.addTab("Text Configurator", tConfig);
		add(tabs, BorderLayout.CENTER);
		// setMaximumSize(new Dimension((int) getMaximumSize().getWidth(), Integer.MAX_VALUE));
		tabs.setPreferredSize(new Dimension((int) tabs.getPreferredSize().getWidth(), (int) tabs.getMaximumSize().getHeight()));
		// setMaximumSize(new Dimension((int) getMaximumSize().getWidth(), (int) getPreferredSize().getHeight()));
	}
	
	@Override
	public void clear() {

		gConfig.clear();
		tConfig.clear();
	}
	
	/**
	 * Gets the string describing the current move.
	 *
	 * @return the string describing the current move.
	 * @throws ParseException
	 *             if there is an error getting the data from the UI.
	 */
	public String save() throws ParseException {
	
		List<Command> commandList = ((RobotConfigurator) tabs.getSelectedComponent()).get();
		if (commandList == null || commandList.size() == 0)
			return "";
		StringBuilder b = new StringBuilder();
		for (Command c : commandList) {
			b.append(c.toString());
			b.append(System.getProperty("line.separator"));
		}
		
		return b.toString();
	}
	
	// Based off of: http://stackoverflow.com/questions/12389801/forbid-tab-change-in-a-jtabbedpane
	/**
	 * Allows the JTabbed pane to prevent tab changes.
	 */
	private class VetoableSingleSelectionModel extends DefaultSingleSelectionModel {

		/**
		 * Default UID.
		 */
		private static final long serialVersionUID = 1L;
		/**
		 * Used for support.
		 */
		private final VetoableChangeSupport support;

		/**
		 * Makes a new thing.
		 */
		public VetoableSingleSelectionModel() {

			support = new VetoableChangeSupport(this);
		}

		@Override
		public void setSelectedIndex(int index) {

			if (getSelectedIndex() == index)
				return;

			try {
				fireVetoableChange(getSelectedIndex(), index);
			} catch (PropertyVetoException e) {
				// If a change is not allowed, do get do it.
				return;
			}
			super.setSelectedIndex(index);
		}

		/**
		 * Ask listeners if a change is allowed.
		 *
		 * @param oldSelectionIndex
		 *            the old index.
		 * @param newSelectionIndex
		 *            the new index.
		 * @throws PropertyVetoException
		 *             if a change is not allowed.
		 */
		private void fireVetoableChange(int oldSelectionIndex, int newSelectionIndex) throws PropertyVetoException {
		
			if (!isVetoable())
				return;
			support.fireVetoableChange("selectedIndex", oldSelectionIndex, newSelectionIndex);
			
		}
		
		/**
		 * Determines if this can veto anything.
		 *
		 * @return true if this can veto stuff.
		 */
		private boolean isVetoable() {
		
			return support.hasListeners(null);
		}
		
		/**
		 * Adds a change listener.
		 *
		 * @param l
		 *            the listener to add.
		 */
		public void addVetoableChangeListener(VetoableChangeListener l) {
		
			support.addVetoableChangeListener(l);
		}
	}
	
	@Override
	protected boolean handleRobotChange(Class<? extends Robot> newRobotType) {

		int selectedIndex = tabs.getSelectedIndex();
		
		// Checks to see if a change is possible, and if so, changes it.
		if (((RobotConfigurator) tabs.getSelectedComponent()).setRobotType(newRobotType)) {
			for (int x = 0; x < tabs.getTabCount(); x++)
				if (x != selectedIndex) {
					((RobotConfigurator) tabs.getComponentAt(x)).clear();
					((RobotConfigurator) tabs.getComponentAt(x)).setRobotType(newRobotType);
				}
			return true;
		} else
			return false;
	}
	
	@Override
	public List<Command> get() throws ParseException {

		return ((RobotConfigurator) tabs.getSelectedComponent()).get();
	}
	
	@Override
	public void load(List<Command> toLoad) {

		((RobotConfigurator) tabs.getSelectedComponent()).load(toLoad);
	}
}