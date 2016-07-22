package view.robotpanel;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import robot.Command;
import robot.NetworkRobotCom;
import robot.Robot;
import robot.RobotCom;
import robot.RobotInfo;
import robot.SerialRobotCom;
import jssc.SerialPortException;
import view.RobotController;
import view.configurator.RobotConfigurator;

/**
 * Represents a panel used to set up and configure the connection to the Arduino.
 *
 * @author Jacob Glueck
 *
 */
public class RobotConnectionPanel extends JPanel implements PropertyChangeListener {
	
	/**
	 * Default UID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Displays the connection status.
	 */
	private final JLabel connectionStatus;
	
	/**
	 * The text that will be displayed in the connection status label.
	 *
	 */
	private interface ConnectionStatusText {
		
		/**
		 * The text displayed when no robot is connected.
		 */
		public final String NOT_CONNECTED = "Not connected";
		/**
		 * The text displayed when a robot is connected.
		 */
		public final String CONNECTED = "Connected";
		/**
		 * The text displayed when the program is waiting for the serial port to initialize.
		 */
		public final String INITIALIZING = "Initializing port";
		/**
		 * The text displayed while the connection is being verified.
		 */
		public final String VERIFYING = "Verifying connection";
	}

	/**
	 * The text field for the connection location.
	 */
	private final JComboBox<RobotCom<?>> connectionLoc;
	/**
	 * The combo box model
	 */
	private final DefaultComboBoxModel<RobotCom<?>> connectionLocModel;
	/**
	 * The connect button.
	 */
	private final JButton connect;

	/**
	 * The text displayed on the connect button.
	 *
	 */
	private interface ConnectText {
		
		/**
		 * The text displayed when the button is in connect mode.
		 */
		public final String CONNECT = "Connect";
		/**
		 * The text displayed while the program is connecting.
		 */
		public final String CONNECTING = "Connecting";
		/**
		 * The text displayed when the button is in disconnect mode.
		 */
		public final String DISCONNECT = "Disconnect";
		/**
		 * The text displayed when the program is disconnecting the robot
		 */
		public final String DISCONNECTING = "Disconnecting";
	}
	
	/**
	 * The auto connect button.
	 */
	private final JButton search;

	/**
	 * The text displayed on the search button.
	 *
	 */
	private interface SearchText {
		
		/**
		 * The text displayed when the button is in search mode.
		 */
		public final String SEARCH = "Search";
		/**
		 * The text displayed when the program is searching.
		 */
		public final String SEARCHING = "Searching";
	}
	
	/**
	 * The currently connected robot, or null if no robot is connected.
	 */
	private Robot a;
	/**
	 * The execute button.
	 */
	private JButton execute;
	
	/**
	 * The text that might appear on the execute button.
	 */
	private interface ExecuteText {
		
		/**
		 * The text displayed before execution.
		 */
		public final String EXECUTE = "Execute";
		/**
		 * The text displayed during execution.
		 */
		public final String EXECUTING = "Executing";
	}

	/**
	 * The stop button.
	 */
	private JButton stop;
	
	/**
	 * The text displayed on the stop button.
	 */
	private interface StopText {
		
		/**
		 * The text displayed before use.
		 */
		public final String STOP = "Stop";
		/**
		 * The text displayed while the stopping is occurring.
		 */
		public final String STOPPING = "Stopping";
		/**
		 * The text displayed once a force stop become available.
		 */
		public final String FORCE_STOP = "Force Stop";
	}

	/**
	 * The progress bar that displays the current execution status.
	 */
	private JProgressBar executionStatus;
	/**
	 * The label under the bar that displays more status information.
	 */
	private JLabel executionStatusLabel;

	/**
	 * The text the could appear on the execution status label.
	 */
	private interface ExecutionStatusText {
		
		/**
		 * The text displayed when the robot is idle.
		 */
		public final String IDLE = "Idle";
		/**
		 * The text displayed while the robot is executing a command.
		 */
		public final String EXECUTING = "Executing %d/%d: %s";
	}
	
	/**
	 * The panel used to configure the moves.
	 */
	private RobotConfigurator moveConfig;
	/**
	 * The currently executing action.
	 */
	private Execute currentExecute;
	
	/**
	 * Makes a new RobotConnectionPanel.
	 *
	 * @param moveConfig
	 *            the configurator used to get the moves.
	 */
	public RobotConnectionPanel(RobotConfigurator moveConfig) {

		this.moveConfig = moveConfig;
		// Initialize all the instance variables
		connectionStatus = new JLabel(ConnectionStatusText.NOT_CONNECTED);
		connectionLocModel = new DefaultComboBoxModel<>();
		connectionLoc = new JComboBox<>(connectionLocModel);
		connectionLoc.setRenderer(new RobotComRenderer());
		
		connect = new JButton(ConnectText.CONNECT);
		
		// Add an action listener to the connect button
		connect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				if (connectionLoc.getSelectedItem() == null)
					return;
				
				setButtonsEnabled(false);
				connectionLoc.setEnabled(false);
				if (connect.getText().equals(ConnectText.CONNECT))
					new ConnectToRobot((RobotCom<?>) connectionLoc.getSelectedItem()).execute();
				else
					new DisconnectFromRobot(a).execute();
			}
		});
		
		// Set up the search button
		search = new JButton(SearchText.SEARCH);
		search.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				setButtonsEnabled(false);
				new SearchForRobots().execute();
			}
		});
		
		// Make a JPanel to hold all the stuff
		JPanel robotConnection = new JPanel();
		robotConnection.setLayout(new BoxLayout(robotConnection, BoxLayout.LINE_AXIS));

		// Set the size of the connection status field
		connectionStatus.setMinimumSize(new Dimension((int) connectionStatus.getPreferredSize().getWidth() * 2, (int) connectionStatus.getMinimumSize().getHeight()));
		connectionStatus.setPreferredSize(new Dimension((int) connectionStatus.getPreferredSize().getWidth() * 2, (int) connectionStatus.getMinimumSize().getHeight()));
		connectionStatus.setMaximumSize(new Dimension((int) connectionStatus.getPreferredSize().getWidth() * 2, (int) connectionStatus.getMinimumSize().getHeight()));
		robotConnection.add(connectionStatus);

		// Stack the two buttons vertically in a JPanel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
		buttonPanel.add(search);
		buttonPanel.add(connect);
		
		// Put the combo box and the button panel side by side into another panel
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.LINE_AXIS));
		controlPanel.add(connectionLoc);
		controlPanel.add(buttonPanel);

		// Set the button width
		int maxButtonWidth = (int) (Math.max(search.getPreferredSize().getWidth(), connect.getPreferredSize().getWidth()) * 1.5);
		search.setMaximumSize(new Dimension(maxButtonWidth, (int) search.getMaximumSize().getHeight()));
		connect.setMaximumSize(new Dimension(maxButtonWidth, (int) connect.getMaximumSize().getHeight()));
		search.setPreferredSize(new Dimension(maxButtonWidth, (int) search.getPreferredSize().getHeight()));
		connect.setPreferredSize(new Dimension(maxButtonWidth, (int) connect.getPreferredSize().getHeight()));
		search.setMinimumSize(new Dimension(maxButtonWidth, (int) search.getPreferredSize().getHeight()));
		connect.setMinimumSize(new Dimension(maxButtonWidth, (int) connect.getPreferredSize().getHeight()));
		
		// Set the combo box size
		int maxHeight = (int) Math.max(connectionLoc.getPreferredSize().getHeight(), buttonPanel.getPreferredSize().getHeight());
		int maxComboBoxWidth = (int) (maxButtonWidth * 2.5);
		connectionLoc.setMaximumSize(new Dimension(maxComboBoxWidth, maxHeight));
		connectionLoc.setPreferredSize(new Dimension(maxComboBoxWidth, maxHeight));
		connectionLoc.setMinimumSize(new Dimension(maxComboBoxWidth, maxHeight));

		// Add the control panel to the robot connection panel
		robotConnection.add(controlPanel);
		
		// Title the panel
		robotConnection.setBorder(BorderFactory.createTitledBorder("Connection Control"));

		// Make the execution panel
		JPanel upperExecutionPanel = new JPanel();
		upperExecutionPanel.setLayout(new BoxLayout(upperExecutionPanel, BoxLayout.LINE_AXIS));
		executionStatus = new JProgressBar();
		executionStatus.setIndeterminate(false);
		executionStatus.setStringPainted(true);
		executionStatus.setValue(100);
		upperExecutionPanel.add(executionStatus);
		execute = new JButton(ExecuteText.EXECUTE);
		stop = new JButton(StopText.STOP);
		execute.setMaximumSize(new Dimension(maxButtonWidth, (int) execute.getMaximumSize().getHeight()));
		stop.setMaximumSize(new Dimension(maxButtonWidth, (int) stop.getMaximumSize().getHeight()));
		execute.setPreferredSize(new Dimension(maxButtonWidth, (int) execute.getPreferredSize().getHeight()));
		stop.setPreferredSize(new Dimension(maxButtonWidth, (int) stop.getPreferredSize().getHeight()));
		execute.setMinimumSize(new Dimension(maxButtonWidth, (int) execute.getPreferredSize().getHeight()));
		stop.setMinimumSize(new Dimension(maxButtonWidth, (int) stop.getPreferredSize().getHeight()));
		execute.setEnabled(false);
		stop.setEnabled(false);
		upperExecutionPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		upperExecutionPanel.add(execute);
		upperExecutionPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		upperExecutionPanel.add(stop);
		
		execute.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				connect.setEnabled(false);
				stop.setEnabled(true);
				execute.setEnabled(false);
				execute.setText(ExecuteText.EXECUTING);
				Execute execute = new Execute();
				execute.addPropertyChangeListener(RobotConnectionPanel.this);
				execute.execute();
				currentExecute = execute;
			}
		});

		stop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			
				stop.setText(StopText.FORCE_STOP);
				executionStatusLabel.setText(StopText.STOPPING);
				currentExecute.requestCancel();
			}
		});

		JPanel executionPanel = new JPanel();
		executionPanel.setLayout(new BoxLayout(executionPanel, BoxLayout.PAGE_AXIS));
		executionPanel.add(upperExecutionPanel);
		executionStatusLabel = new JLabel(ExecutionStatusText.IDLE);
		JPanel leftLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		leftLabelPanel.add(executionStatusLabel);
		executionPanel.add(leftLabelPanel);
		
		// Title the panel
		executionPanel.setBorder(BorderFactory.createTitledBorder("Execution Control"));
		
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.add(robotConnection);
		this.add(executionPanel);
	}
	
	/**
	 * Gets the currently connected Arduino.
	 *
	 * @return the currently connected Arduino.
	 */
	public Robot getArduino() {

		return a;
	}
	
	/**
	 * Sets the state of the buttons.
	 *
	 * @param e
	 *            the state of the buttons.
	 */
	private void setButtonsEnabled(boolean e) {

		connect.setEnabled(e);
		search.setEnabled(e);
	}

	/**
	 * Executes robot commands in the background.
	 */
	private class Execute extends SwingWorker<Void, String> {

		/**
		 * True if a cancel has been requested.
		 */
		private boolean cancel = false;
		
		/**
		 * Requests a cancel. All cancel requests after the first one will immediately interrupt the background thread.
		 */
		public void requestCancel() {

			if (cancel)
				cancel(true);
			cancel = true;
		}
		
		@Override
		protected Void doInBackground() throws Exception {
		
			List<Command> commands = moveConfig.get();
			ListIterator<Command> iter = commands.listIterator();
			while (iter.hasNext()) {
				if (cancel)
					throw new InterruptedException();
				setProgress((int) Math.round(iter.nextIndex() / (double) commands.size() * 100));
				Command toExecute = iter.next();
				publish(String.format(ExecutionStatusText.EXECUTING, iter.previousIndex() + 1, commands.size(), toExecute.toString()));
				a.executeCommand(toExecute);
			}
			return null;
		}
		
		@Override
		protected void done() {

			try {
				get();
			} catch (InterruptedException e) {
			} catch (CancellationException e) {
				connect.setEnabled(true);
				connect.doClick();
			} catch (ExecutionException e) {
				if (e.getCause() instanceof ParseException)
					RobotController.displayErrorMessage("<html>Parse error on line " + ((ParseException) e.getCause()).getErrorOffset() + ": <br>" + ((ParseException) e.getCause()).getMessage()
							+ "</html>",
							"Command Parse Error");
				else if (!(e.getCause() instanceof InterruptedException))
					RobotController.displayErrorMessage(e, "Execution Error");
			}

			connect.setEnabled(true);
			stop.setText(StopText.STOP);
			stop.setEnabled(false);
			execute.setEnabled(true);
			execute.setText(ExecuteText.EXECUTE);
			executionStatusLabel.setText(ExecutionStatusText.IDLE);
			// Always set percent to 100.
			executionStatus.setValue(100);
		}

		@Override
		protected void process(List<String> chunks) {

			executionStatusLabel.setText(chunks.get(chunks.size() - 1));
		}

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (!((SwingWorker<?, ?>) evt.getSource()).isDone()) {
			executionStatus.setString(null);
			executionStatus.setValue(((SwingWorker<?, ?>) evt.getSource()).getProgress());
		}

	}

	/**
	 * A swing worker class to search for robots and populate the list.
	 *
	 */
	private class SearchForRobots extends SwingWorker<List<RobotInfo>, String> {

		@Override
		protected List<RobotInfo> doInBackground() throws Exception {

			publish(SearchText.SEARCHING);
			List<RobotInfo> result = Robot.findRobots();
			return result;
		}

		@Override
		protected void done() {

			try {
				connectionLocModel.removeAllElements();
				List<RobotInfo> result = get();
				for (RobotInfo i : result) {
					if (i.getIp() != null)
						try {
							connectionLocModel.addElement(new NetworkRobotCom(i));
						} catch (IOException e) {
							e.printStackTrace();
						}
					if (i.getSerialPort() != null)
						try {
							connectionLocModel.addElement(new SerialRobotCom(i));
						} catch (IOException | SerialPortException e) {
							e.printStackTrace();
						}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				RobotController.displayErrorMessage(e, "Searching Error");
			}

			search.setText(SearchText.SEARCH);
			setButtonsEnabled(true);
		}

		@Override
		protected void process(List<String> chunks) {

			search.setText(chunks.get(chunks.size() - 1));
		}
	}

	/**
	 * A swing worker class to disconnect from a robot.
	 *
	 */
	private class DisconnectFromRobot extends SwingWorker<Void, String> {

		/**
		 * The robot to disconnect from.
		 */
		private final Robot r;

		/**
		 * Makes a new object that will disconnect the specified robot.
		 *
		 * @param r
		 *            the robot to disconnect.
		 */
		public DisconnectFromRobot(Robot r) {

			this.r = r;
		}

		@Override
		protected Void doInBackground() throws Exception {

			publish(ConnectText.DISCONNECTING);
			r.disconnect();

			return null;
		}

		@Override
		protected void done() {

			try {
				connectionStatus.setText(ConnectionStatusText.NOT_CONNECTED);
				execute.setEnabled(false);
				stop.setEnabled(false);
				connect.setText(ConnectText.CONNECT);
				get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				RobotController.displayErrorMessage(e, "Disconnection Error");
			}

			setButtonsEnabled(true);
			connectionLoc.setEnabled(true);
		}

	}

	/**
	 * A string worker class to connect to a robot.
	 *
	 */
	private class ConnectToRobot extends SwingWorker<Robot, String> {

		/**
		 * The info of the robot to connect to.
		 */
		private final RobotCom<?> toConnect;

		/**
		 * Makes a new connect to robot object that will connect to the specified robot.
		 *
		 * @param toConnect
		 *            the info of the robot to connect to.
		 */
		public ConnectToRobot(RobotCom<?> toConnect) {

			this.toConnect = toConnect;
		}

		@Override
		protected Robot doInBackground() throws Exception {

			publish(ConnectText.CONNECTING);
			publish(ConnectionStatusText.INITIALIZING);
			toConnect.startInitialization();
			if (!toConnect.waitForInitialization()) {
				toConnect.disconnect();
				return null;
			}

			publish(ConnectionStatusText.VERIFYING);
			Robot result = Robot.createRobot(toConnect);
			if (result.verifyConnection())
				return result;
			else {
				toConnect.disconnect();
				return null;
			}

		}

		@Override
		protected void done() {

			boolean comboBoxState = true;
			a = null;
			try {
				Robot result = get();
				if (result != null) {
					connectionStatus.setText(ConnectionStatusText.CONNECTED);
					execute.setEnabled(true);
					comboBoxState = false;
					connect.setText(ConnectText.DISCONNECT);
				} else
					connectionStatus.setText(ConnectionStatusText.NOT_CONNECTED);
				a = result;
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				RobotController.displayErrorMessage(e, "Connection Error");
			}

			setButtonsEnabled(true);
			connectionLoc.setEnabled(comboBoxState);
			search.setEnabled(comboBoxState);
		}

		@Override
		protected void process(List<String> chunks) {

			connectionStatus.setText(chunks.get(chunks.size() - 1));
		}
	}
}
