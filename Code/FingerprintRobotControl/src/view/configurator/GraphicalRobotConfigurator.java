package view.configurator;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

import robot.Command;
import robot.NARFSTR;
import robot.Robot;
import view.robotpanel.RobotMethodLoader;
import view.util.TitledButtonPanel;

/**
 * This class represents a hand move sequence configurator.
 *
 * @author Jacob Glueck
 *
 */
public class GraphicalRobotConfigurator extends RobotConfigurator {
	
	/**
	 * Default UID.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The table that holds the commands.
	 */
	final JTable table;
	/**
	 * The model for the table.
	 */
	final DefaultTableModel tableModel;
	/**
	 * The panel that holds all the buttons for adding stuff.
	 */
	private TitledButtonPanel addButtons;
	
	/**
	 * Makes a new graphical robot configurator for the specified robot type.
	 *
	 * @param robotType
	 *            the type of robot to configure moves for.
	 */
	public GraphicalRobotConfigurator(Class<? extends Robot> robotType) {
	
		super(robotType);
		
		// Set up the table.
		tableModel = new DefaultTableModel(0, 1) {

			/**
			 * Default UID.
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			public Class<?> getColumnClass(int col) {
			
				// Make the table hold commands.
				return Command.class;
			}
		};
		table = new JTable(tableModel);
		table.setTableHeader(null);
		// Allow only single interval selection.
		table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		// Set up the renderer
		CommandRenderer r = new CommandRenderer(NARFSTR.class);
		table.setDefaultRenderer(Command.class, r);
		table.setDefaultEditor(Command.class, r);
		
		// Put the table in a scroll pane and add the scroll pane
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		JScrollPane scrollPane = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		add(scrollPane);
		
		// Put all the buttons in a panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		addButtons = new TitledButtonPanel("Add", 75, 10);
		// Generate the buttons for the panel based on the robot type
		setRobotType(getRobotType());
		buttonPanel.add(addButtons);
		
		// Put some space between the two button sets
		buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));
		
		// Make a button for moving commands up in the table. Put an up arrow on it.
		JButton moveUp = new JButton("\u25B2");
		moveUp.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				int[] rows = table.getSelectedRows();
				if (rows.length != 0 && rows[0] != 0)
					moveRows(rows, -1);
			}
		});
		// Make a button for moving commands down in the table. Put a down arrow on it.
		JButton moveDown = new JButton("\u25BC");
		moveDown.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				int[] rows = table.getSelectedRows();
				if (rows.length != 0 && rows[rows.length - 1] != table.getRowCount() - 1)
					moveRows(rows, +1);
			}
		});
		// Make a button for deleting commands. Put an X on it.
		final JButton delete = new JButton("X");
		delete.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				int[] rows = table.getSelectedRows();
				if (rows.length != 0) {
					for (int x = rows.length - 1; x >= 0; x--)
						tableModel.removeRow(rows[x]);
					tableModel.fireTableStructureChanged();
				}
			}
		});
		
		// Make the delete key work click the delete button.
		InputMap inputMap = table.getInputMap(JComponent.WHEN_FOCUSED);
		ActionMap actionMap = table.getActionMap();
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
		actionMap.put("delete", new AbstractAction() {
			
			/**
			 * Default UID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent evt) {
			
				delete.doClick(0);
			}
		});
		
		buttonPanel.add(new TitledButtonPanel("Edit", 75, 10, moveUp, moveDown, delete));
		add(buttonPanel);
	}
	
	/**
	 * Moves the specified rows by the specified offset. Assumes the rows in continues and sorted in ascending order.
	 *
	 * @param rows
	 *            the rows to move.
	 * @param offset
	 *            the offset.
	 */
	private void moveRows(int[] rows, int offset) {
	
		tableModel.moveRow(rows[0], rows[rows.length - 1], rows[0] + offset);
		tableModel.fireTableStructureChanged();
		table.setRowSelectionInterval(rows[0] + offset, rows[rows.length - 1] + offset);
	}
	
	@Override
	public List<Command> get() throws ParseException {
	
		table.editingStopped(null);
		List<Command> result = new LinkedList<>();
		for (int row = 0; row < tableModel.getRowCount(); row++) {
			Command toAdd = (Command) tableModel.getValueAt(row, 0);
			if (toAdd.containsNullParameters())
				throw new ParseException("Error parsing element", row + 1);
			result.add((Command) tableModel.getValueAt(row, 0));
		}
		
		return result;
	}
	
	@Override
	public void load(List<Command> move) {
	
		clear();
		for (Command c : move)
			tableModel.addRow(new Object[] { c });
	}
	
	@Override
	public void clear() {
	
		tableModel.setRowCount(0);
	}
	
	/**
	 * Makes an empty command that has null for all of its arguments.
	 *
	 * @param name
	 *            the command name.
	 * @return the command in an object array.
	 */
	private Object[] generateBlankCommand(String name) {
	
		Object[] args = new Object[RobotMethodLoader.getCommandMap(getRobotType()).get(name).getParameterTypes().length];
		return new Object[] { new Command(name, args) };
	}
	
	@Override
	protected boolean handleRobotChange(Class<? extends Robot> newRobotType) {

		// Make sure the table is empty. We cannot handle a change if there are commands because the commands will become invalid.
		if (table.getRowCount() != 0)
			return false;

		// Make all the buttons
		JButton[] buttons = new JButton[RobotMethodLoader.getCommandMap(newRobotType).size()];
		int x = 0;
		for (final String name : RobotMethodLoader.getCommandMap(newRobotType).keySet()) {
			// Make a button for each command.
			JButton toAdd = new JButton("<html><u>" + name.substring(0, 1).toUpperCase() + "</u>" + name.substring(1) + "</html>");
			buttons[x] = toAdd;
			toAdd.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {

					// Have the command inserted after the selected row.
					if (table.getRowCount() == 0 || table.getSelectedRow() == -1)
						tableModel.addRow(generateBlankCommand(name));
					else {
						tableModel.insertRow(table.getSelectedRow(), generateBlankCommand(name));
						tableModel.fireTableStructureChanged();
					}
				}
			});
			x++;
		}
		// Add the button.
		addButtons.setButtons(buttons);
		
		return true;
	}
}