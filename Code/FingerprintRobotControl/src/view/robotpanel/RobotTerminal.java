package view.robotpanel;

import javax.swing.JPanel;

/**
 * The panel used for sending commands to the robot.
 *
 * @author Jacob Glueck
 *
 */
public class RobotTerminal extends JPanel {

	/**
	 * Default UID.
	 */
	private static final long serialVersionUID = 1L;

	// TODO Make this work!
	/*

	 *//**
	 * Default UID.
	 */
	/*
	 * private static final long serialVersionUID = 1L;
	 *//**
	 * The field used for getting the command to send.
	 */
	/*
	 * private final JTextField sendField;
	 *//**
	 * The send button.
	 */
	/*
	 * private final JButton send;
	 *//**
	 * The robot connection panel used for getting the currently connected Arduino.
	 */
	/*
	 * private final RobotConnectionPanel a;
	 *//**
	 * Makes a new RobotSentPanel with the specified RobotConnectionPanel.
	 *
	 * @param comPanel
	 *            the robot connection panel used for getting the currently connected Arduino.
	 */
	/*
	 * public RobotTerminal(RobotConnectionPanel comPanel) {
	 *
	 * a = comPanel;
	 * sendField = new JTextField();
	 * send = new JButton("Send");
	 * getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ENTER"), "execute");
	 * getActionMap().put("execute", new AbstractAction() {
	 *//**
	 * Default UID.
	 */
	/*
	 * private static final long serialVersionUID = 1L;
	 *
	 * @Override
	 * public void actionPerformed(ActionEvent e) {
	 *
	 * send.doClick();
	 * }
	 * });
	 * send.addActionListener(new ActionListener() {
	 *
	 * @Override
	 * public void actionPerformed(ActionEvent e) {
	 *
	 * if (a.getArduino() == null)
	 * JOptionPane.showMessageDialog(RobotTerminal.this, "No Arduino connected.", "No Arduino connected", JOptionPane.ERROR_MESSAGE);
	 * else
	 * a.getArduino().send(sendField.getText());
	 * }
	 * });
	 *
	 * JPanel robotComm = new JPanel();
	 * robotComm.setLayout(new BoxLayout(robotComm, BoxLayout.LINE_AXIS));
	 * robotComm.add(sendField);
	 * robotComm.add(Box.createHorizontalStrut(5));
	 * robotComm.add(send);
	 *
	 * setLayout(new BorderLayout());
	 * TitledComponent robotCommTitle = new TitledComponent("Robot Communication", robotComm);
	 * add(robotCommTitle, BorderLayout.CENTER);
	 * setMaximumSize(new Dimension((int) getMaximumSize().getWidth(), (int) getPreferredSize().getHeight()));
	 * }
	 */
}