package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.List;
import java.util.Set;

import jssc.SerialPortException;
import robot.Command;
import robot.NARFSTR;
import robot.NetworkRobotCom;
import robot.Robot;
import robot.RobotInfo;
import robot.SerialRobotCom;
import view.RobotController;

/**
 * A simple program for controlling the fingerprint robot.
 *
 * @author Jacob Glueck
 */
public class FingerprintRobotControl {

	/**
	 * The main method.
	 *
	 * @param args
	 *            to launch the GUI version, pass in no arguments. To launch the command line version, pass in two arguments. First, the name of the text file which contains the commands. Second, the
	 *            location of the Arduino to connect to or the autoconnect preference. Acceptable values are a serial port name, an IP address, ethernet, or usb. "ethernet" sets the autoconnect
	 *            preference to Ethernet and "usb" set the autoconnect preference to USB.
	 */
	public static void main(String[] args) {

		if (args.length == 0)
			new RobotController();
		else if (args.length == 3) {
			String robotTypeS = args[0];
			Class<? extends Robot> robotType = null;
			if (robotTypeS.equals("NARFSTR"))
				robotType = NARFSTR.class;
			else {
				System.out.println("Unrecognized robot type: " + robotTypeS);
				System.exit(-1);
			}
			// TODO Make this work with NAFSTR!
			
			File f = new File(args[1]);
			InputStream i = null;
			try {
				i = new FileInputStream(f);
			} catch (FileNotFoundException e) {
				System.out.println("Failed to read file.");
				System.exit(-1);
			}
			StringBuilder b = new StringBuilder();
			int next;
			try {
				while ((next = i.read()) != -1)
					b.append((char) next);
			} catch (IOException e) {
				System.out.println("Failed to read file.");
				System.exit(-1);
			}
			List<Command> c = null;
			try {
				c = Command.parseCommands(b.toString(), robotType);
			} catch (ParseException e1) {
				System.out.println("Syntax error in command file.");
				System.out.println(e1.getMessage());
				System.out.println("Line: " + e1.getErrorOffset());
				System.exit(-1);
			}

			Robot a = null;
			
			try {
				if (args[2].equals("ethernet")) {
					Set<RobotInfo> networked = Robot.findNetworkedRobots();
					if (networked.size() == 0) {
						System.out.println("Failed to find networked Arduinos.");
						System.exit(-1);
					}
					a = new Robot(new NetworkRobotCom(networked.iterator().next()));
				} else if (args[2].equals("usb")) {
					Set<RobotInfo> serial = Robot.findSerialRobots();
					if (serial.size() == 0) {
						System.out.println("Failed to find serial Arduinos.");
						System.exit(-1);
					}
					a = new Robot(new SerialRobotCom(serial.iterator().next()));
				} else if (Robot.isIP(args[2])) {
					RobotInfo toTry = Robot.tryConnection(InetAddress.getByName(args[2]));
					if (toTry == null) {
						System.out.println("Failed to connect to Arduino at " + args[2]);
						System.exit(-1);
					}
					a = new Robot(new NetworkRobotCom(toTry));
				}
				else {
					RobotInfo toTry = Robot.tryConnection(args[2]);
					if (toTry == null) {
						System.out.println("Failed to connect to Arduino at " + args[2]);
						System.exit(-1);
					}
					a = new Robot(new SerialRobotCom(toTry));
				}
			} catch (IOException | SerialPortException e) {
				System.out.println("Failed to connect to Arduino at " + args[2]);
				System.exit(-1);
			}
			
			try {
				a.getCom().startInitialization();
				a.getCom().waitForInitialization();
			} catch (IOException | InterruptedException e1) {
				System.out.println("Error starting communications.");
				System.exit(-1);
			}

			System.out.println("Connected to Arduino at: " + a.getCom().getLocationString());

			// final Robot finalA = a;
			
			// Thread outputThread = new Thread() {
			//
			// @Override
			// public void run() {
			//
			// while (!isInterrupted()) {
			// int letter;
			// try {
			// letter = finalA.getCom().readByte(Integer.MAX_VALUE);
			// if (letter != Integer.MAX_VALUE)
			// System.out.print((char) letter);
			// } catch (IOException e) {
			// e.printStackTrace();
			// return;
			// } catch (InterruptedException e) {
			// return;
			// }
			// }
			// }
			// };
			// outputThread.start();
			int exitStatus = 0;
			try {
				a.executeCommands(c);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
				exitStatus = -1;
			}
			try {
				a.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// outputThread.interrupt();
			// try {
			// outputThread.join();
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			System.exit(exitStatus);

		}

		else
			System.out
			.println(" to launch the GUI version, pass in no arguments. To launch the command line version, pass in trhee arguments. First, the type of robot, either \"NAFSTR\" or \"NARFSTR\". Second, the name of the text file which contains the commands. Third, the location of the Arduino to connect to or the autoconnect preference. Acceptable values are a serial port name, an IP address, ethernet, or usb. \"ethernet\" sets the autoconnectpreference to Ethernet and \"usb\" set the autoconnect preference to USB.");
	}
}