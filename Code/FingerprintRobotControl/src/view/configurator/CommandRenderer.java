package view.configurator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import robot.Bounds;
import robot.Command;
import robot.Robot;
import robot.RobotCommand;
import view.robotpanel.RobotMethodLoader;

/**
 * Renders command objects for a JTable.
 *
 * @author Jacob Glueck
 *
 */
public class CommandRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
	
	/**
	 * Default UID.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The type of robot this renderer is rendering commands for.
	 */
	private Class<? extends Robot> r;
	/**
	 * The map for command names and methods.
	 */
	private Map<String, Method> commandMap;
	/**
	 * The list of configurators in the order they appear on the panel.
	 */
	private LabeledConfigurator<?>[] configs;
	/**
	 * The component which holds everything.
	 */
	private JPanel component;
	/**
	 * The name of the command on component.
	 */
	private String commandName;

	/**
	 * Makes a new command renderer the renders commands for the specified robot type.
	 *
	 * @param r
	 *            the type of robot to render commands for.
	 */
	public CommandRenderer(Class<? extends Robot> r) {
	
		this.r = r;
		commandMap = RobotMethodLoader.getCommandMap(r);
	}

	/**
	 * Generates a panel for the command.
	 *
	 * @param table
	 *            the parent table.
	 * @param value
	 *            the current value.
	 * @param isSelected
	 *            true if selected.
	 * @param hasFocus
	 *            true if focused.
	 * @param row
	 *            the row.
	 * @param column
	 *            the column.
	 * @return the array of configurators that are on the panel.
	 */
	private LabeledConfigurator<?>[] generatePanel(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	
		// Clear the panel
		component = new JPanel();
		component.setLayout(new BoxLayout(component, BoxLayout.LINE_AXIS));
		
		Command command = (Command) value;
		Method m = commandMap.get(command.getName());
		// If the command is empty, give it null paramaters.
		String[] paramNames = m.getAnnotation(RobotCommand.class).paramNames();
		if (command.getParams().length == 0 && m.getParameterTypes().length != 0)
			command = new Command(command.getName(), new Object[m.getParameterTypes().length]);
		
		// Check for sanity
		if (paramNames.length != command.getParams().length || paramNames.length != m.getParameterTypes().length)
			throw new IllegalStateException("The number of parameters in the robot class, the number of paramaters in the command, and the number of parameter names do not equal!");
		// Check types
		for (int x = 0; x < m.getParameterTypes().length; x++)
			if (command.getParams()[x] != null && !m.getParameterTypes()[x].isAssignableFrom(command.getParams()[x].getClass()))
				throw new IllegalStateException("The types " + m.getParameterTypes()[x] + " and " + command.getParams()[x].getClass() + " are not compatable.");

		// Build UI
		JPanel mainComponent = new JPanel();
		mainComponent.setLayout(new BoxLayout(mainComponent, BoxLayout.PAGE_AXIS));
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.LINE_AXIS));
		labelPanel.add(new JLabel("<html><font size=+1><u>" + command.getName().substring(0, 1).toUpperCase() + "</u>" + command.getName().substring(1) + "</font></html>"));
		labelPanel.add(Box.createHorizontalGlue());
		mainComponent.add(labelPanel);
		
		// Make all the configurators
		LabeledConfigurator<?>[] configs = new LabeledConfigurator<?>[paramNames.length];
		for (int x = 0; x < paramNames.length; x++) {
			// TODO: Make this handle more stuff than just integers!
			Class<?> paramClass = m.getParameterTypes()[x];
			if (paramClass.isAssignableFrom(Integer.class)) {
				int min = Integer.MIN_VALUE;
				int max = Integer.MAX_VALUE;
				for (Annotation a : m.getParameterAnnotations()[x])
					if (a.annotationType().isAssignableFrom(Bounds.class)) {
						min = ((Bounds) a).min();
						max = ((Bounds) a).max();
					}
				LabeledConfigurator<Integer> config = new LabeledConfigurator<>(paramNames[x], new IntegerConfigurator(min, max, 5), 10, 100);
				if (command.getParams() != null)
					config.load((Integer) command.getParams()[x]);
				mainComponent.add(config);
				configs[x] = config;

			}
		}
		
		// Ensure the component is sized properly
		mainComponent.add(Box.createVerticalGlue());
		mainComponent.setMaximumSize(new Dimension((int) mainComponent.getPreferredSize().getWidth(), (int) mainComponent.getPreferredSize().getHeight()));

		// Add a number
		JLabel number = new JLabel("<html><font size=+1>" + row + "</font></html>", SwingConstants.RIGHT);
		number.setMinimumSize(new Dimension(40, (int) mainComponent.getMinimumSize().getHeight()));
		number.setPreferredSize(new Dimension(40, (int) mainComponent.getPreferredSize().getHeight()));
		number.setMaximumSize(new Dimension(40, (int) mainComponent.getMaximumSize().getHeight()));
		number.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 3, Color.LIGHT_GRAY), BorderFactory.createEmptyBorder(0, 0, 0, 5)));
		Color numberBackground = number.getBackground();
		component.add(number);
		component.add(mainComponent);
		component.add(Box.createHorizontalGlue());

		// Handle colors when selected
		if (isSelected) {
			CommandRenderer.setOpaque(component, false);
			component.setOpaque(true);
			component.setBackground(table.getSelectionBackground());
		} else {
			CommandRenderer.setOpaque(component, false);
			component.setOpaque(true);
			component.setBackground(table.getBackground());
			number.setOpaque(true);
			number.setBackground(numberBackground);
		}

		// Fix the row height
		int rowHeight = table.getRowHeight(row);
		rowHeight = Math.max(rowHeight, component.getPreferredSize().height);
		table.setRowHeight(row, rowHeight);

		return configs;
	}
	
	@Override
	public boolean stopCellEditing() {
	
		// Prevent cell editing from stopping; we cannot accept partial values.
		super.stopCellEditing();
		return false;
	}
	
	/**
	 * @return the r
	 */
	public Class<? extends Robot> getRobotType() {

		return r;
	}
	
	/**
	 * @param r
	 *            the r to set
	 */
	public void setRobotType(Class<? extends Robot> r) {

		this.r = r;
		commandMap = RobotMethodLoader.getCommandMap(r);
	}
	
	/**
	 * Sets the opacity of the JPanel and all child JComponents.
	 *
	 * @param c
	 *            the JPanel.
	 * @param o
	 *            the value passed {@link JComponent#setOpaque(boolean)}.
	 */
	private static void setOpaque(Component c, boolean o) {
	
		if (c instanceof JComponent)
			((JComponent) c).setOpaque(o);
		if (c instanceof Container)
			synchronized (c.getTreeLock()) {
				for (Component comp : ((Container) c).getComponents())
					CommandRenderer.setOpaque(comp, o);
			}
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	
		generatePanel(table, value, isSelected, hasFocus, row, column);
		return component;
	}
	
	@Override
	public Object getCellEditorValue() {
	
		// Read everything from the component.
		LinkedList<Object> params = new LinkedList<>();
		for (LabeledConfigurator<?> lc : configs)
			try {
				params.add(lc.get());
			} catch (ParseException e) {
				params.add(null);
			}
		
		Command result = new Command(commandName, params);
		
		return result;
	}
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
	
		commandName = ((Command) value).getName();
		configs = generatePanel(table, value, isSelected, true, row, column);
		return component;
	}
}