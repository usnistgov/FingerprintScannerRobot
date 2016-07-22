package view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import robot.Command;
import robot.NARFSTR;
import robot.Robot;
import view.robotpanel.CommandEditorPanel;
import view.robotpanel.RobotConnectionPanel;
import view.robotpanel.RobotPanel;

/**
 * This class is the main class for GUI robot control.
 *
 * @author Jacob Glueck
 *
 */
public class RobotController extends JFrame {

	/**
	 * The RobotController instance.
	 */
	private static RobotController instance;
	
	/**
	 * Default UID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The menu bar
	 */
	private final JMenuBar menuBar;
	/**
	 * The file chooser
	 */
	private final JFileChooser chooser;
	/**
	 * The connection panel.
	 */
	private final RobotConnectionPanel robotConnection;
	/**
	 * The robot control panel.
	 */
	private final CommandEditorPanel robotControl;
	/**
	 * The robot classes.
	 */
	private final static Class<?>[] robotTypes = { NARFSTR.class };
	/**
	 * The radio buttons used for selecting the robot type in the JFileChooser.
	 */
	private final JRadioButton[] robotTypeSelection;
	
	/**
	 * Makes a new RobotController
	 */
	public RobotController() {
	
		// Make radio buttons that go in the file chooser so the user can choose the robot type when they open a file
		robotTypeSelection = new JRadioButton[RobotController.robotTypes.length];
		final JPanel robotTypePicker = new JPanel();
		robotTypePicker.setLayout(new BoxLayout(robotTypePicker, BoxLayout.PAGE_AXIS));
		robotTypePicker.add(new JLabel("Robot Type:"));
		ButtonGroup radioGroup = new ButtonGroup();
		for (int x = 0; x < robotTypeSelection.length; x++) {
			robotTypeSelection[x] = new JRadioButton(RobotController.robotTypes[x].getSimpleName(), x == 0);
			robotTypePicker.add(robotTypeSelection[x]);
			radioGroup.add(robotTypeSelection[x]);
		}
		robotTypePicker.add(Box.createVerticalGlue());

		chooser = new JFileChooser();

		// Set up the menu
		menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem open = new JMenuItem("Open");
		open.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			
				// Add the robot type panel as an accessory so the user can pick the robot type when they open a file
				chooser.setAccessory(robotTypePicker);

				// If they chose to open a file
				if (chooser.showOpenDialog(RobotController.this) == JFileChooser.APPROVE_OPTION) {
					File f = chooser.getSelectedFile();
					try {

						// Read the whole file
						StringBuilder b = new StringBuilder();
						InputStreamReader reader = new InputStreamReader(new FileInputStream(f));
						int next;
						while ((next = reader.read()) != -1)
							b.append((char) next);
						reader.close();
						
						// Get the robot type, clear the configurator, load the configurator
						Class<? extends Robot> newType = getCurrentRobotType();
						robotControl.clear();
						robotControl.setRobotType(newType);
						robotControl.load(Command.parseCommands(b.toString(), newType));
					} catch (IOException e1) {
						RobotController.displayErrorMessage(e1, "IO Error");
					} catch (ParseException e1) {
						RobotController.displayErrorMessage("<html>Parse error on line " + e1.getErrorOffset() + ": <br>" + e1.getMessage()
								+ "</html>",
								"Command Parse Error");
					}
				}
				// Remove the accessory so when the user saves, they do not see it.
				chooser.setAccessory(null);
			}
		});
		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				if (chooser.showSaveDialog(RobotController.this) == JFileChooser.APPROVE_OPTION) {

					// Write it to a file
					File f = chooser.getSelectedFile();
					try {
						OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(f));
						writer.write(robotControl.save());
						writer.close();
					} catch (IOException e1) {
						RobotController.displayErrorMessage(e1, "IO Error");
					} catch (ParseException e1) {
						RobotController.displayErrorMessage("<html>Parse error on line " + e1.getErrorOffset() + ": <br>" + e1.getMessage()
								+ "</html>",
								"Command Parse Error");
					}
				}
			}
		});
		JMenuItem newProgram = new JMenuItem("New");
		newProgram.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				// Clear everything
				robotControl.clear();
			}
		});
		file.add(open);
		file.add(save);
		file.add(newProgram);
		
		// Allow robot type selection from a menu. This way if there is no program, the user can change the robot type.
		JMenu robotType = new JMenu("Robot Type");
		JRadioButtonMenuItem[] menuRobotTypes = new JRadioButtonMenuItem[RobotController.robotTypes.length];
		ButtonGroup menuRadioGroup = new ButtonGroup();
		for (int x = 0; x < robotTypeSelection.length; x++) {
			menuRobotTypes[x] = new JRadioButtonMenuItem(RobotController.robotTypes[x].getSimpleName(), x == 0);
			robotType.add(menuRobotTypes[x]);
			menuRadioGroup.add(menuRobotTypes[x]);
			final int finalX = x;
			menuRobotTypes[x].addActionListener(new ActionListener() {
				
				@SuppressWarnings("unchecked")
				@Override
				public void actionPerformed(ActionEvent e) {
				
					// Check to make sure it can be changed
					if (robotControl.setRobotType((Class<? extends Robot>) RobotController.robotTypes[finalX]))
						robotTypeSelection[finalX].setSelected(true);
					else
						RobotController.displayErrorMessage("Unable to change robot type because the currently open program is for a different robot type.", "Robot Type Error");
				}
			});
		}
		menuBar.add(file);
		menuBar.add(robotType);
		
		// Set up the main panel
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		
		// Add all the panels
		robotControl = new CommandEditorPanel(getCurrentRobotType());
		robotConnection = new RobotConnectionPanel(robotControl);
		
		mainPanel.add(robotConnection);
		mainPanel.add(new RobotPanel("Robot Connection", robotConnection));
		mainPanel.add(new RobotPanel("Command Editor", robotControl));
		add(mainPanel, BorderLayout.CENTER);
		pack();
		setSize(getWidth(), 800);
		setJMenuBar(menuBar);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Fingerprint Robot Control");
		setVisible(true);
	}
	
	/**
	 * The main method.
	 *
	 * @param args
	 *            takes no arguments.
	 */
	public static void main(String[] args) {
	
		RobotController.instance = new RobotController();
	}
	
	/**
	 * Displays an error message with the specified exception and title.
	 *
	 * @param e
	 *            the exception.
	 * @param title
	 *            the title.
	 */
	public static void displayErrorMessage(Exception e, String title) {
	
		// Turns an exception into a nice error message
		StringBuilder sb = new StringBuilder(e.toString());
		for (StackTraceElement ste : e.getStackTrace()) {
			sb.append("\n\tat ");
			sb.append(ste);
		}
		String trace = sb.toString();
		JTextArea a = new JTextArea();
		a.setText(trace);
		a.setEditable(false);
		JOptionPane.showMessageDialog(RobotController.instance, new JScrollPane(a), title, JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Displays an error message with the specified message and title.
	 *
	 * @param message
	 *            the message.
	 * @param title
	 *            the title.
	 */
	public static void displayErrorMessage(String message, String title) {
	
		JOptionPane.showMessageDialog(RobotController.instance, message, title, JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Gets the current robot type from the radio buttons in the JFileChooser.
	 *
	 * @return the current robot type.
	 */
	@SuppressWarnings("unchecked")
	private Class<? extends Robot> getCurrentRobotType() {
	
		int selectedIndex = -1;
		for (int x = 0; x < robotTypeSelection.length; x++)
			if (robotTypeSelection[x].isSelected()) {
				selectedIndex = x;
				break;
			}
		return (Class<? extends Robot>) RobotController.robotTypes[selectedIndex];
	}
}