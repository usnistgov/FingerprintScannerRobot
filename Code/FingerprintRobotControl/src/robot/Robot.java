package robot;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jssc.SerialPortException;

/**
 * This class represents an Arduino.
 *
 * @author Jacob Glueck
 *
 */
public class Robot {

	/**
	 * The challenge used to confirm the Arduino's identity. When this string is sent to the Arduino, the Arduino should reply with a message of the format: found:[robot type]:[MAC]
	 */
	private static final String CHALLENGE = "fingerrobot";
	/**
	 * The response prefix that the Arduino should send back after receiving the challenge.
	 */
	private static final String RESPONSE_PREFIX = "found";
	/**
	 * The separator used to separate elements in the challenge response.
	 */
	private static final String RESPONSE_SEPERATOR = ":";
	/**
	 * The maximum challenge response length
	 */
	private static final int MAX_CHALLENGE_RESPONSE_LENGTH = Robot.RESPONSE_PREFIX.length() + 7 + 8 * Robot.RESPONSE_SEPERATOR.length() + 12;
	/**
	 * The suffix added to the command type when the Arduino receives the command.
	 */
	private static final String RECEIVED = "-received";
	/**
	 * The suffix added to the command type when the Arduino finishes the command.
	 */
	private static final String END = "-end";
	/**
	 * Maximum amount of time to wait for robot communication before timing out.
	 */
	private static final int TIME_OUT = 2000;
	/**
	 * The maximum amount of whitespace characters to skip over when searching for a word
	 */
	private static final int MAX_LEADING_WHITESPACE = 10;
	/**
	 * The All Hosts multicast group
	 */
	private static final byte[] ALL_HOSTS = { (byte) 224, 0, 0, 1 };
	/**
	 * Used for communicating with the Arduino
	 */
	private final RobotCom<?> com;

	/**
	 * Makes a new Arduino by connecting to it over the specified ArduinoCom.
	 *
	 * @param c
	 *            the ArduinoCom that provides a link to the Arduino.
	 */
	public Robot(RobotCom<?> c) {

		com = c;
	}
	
	/**
	 * Makes one of Robot's subclasses (NARFSTR or NAFSTR) with the specified Robot com.
	 *
	 * @param c
	 *            the RobotCom to use.
	 * @return a robot connected using that com.
	 */
	public static Robot createRobot(RobotCom<?> c) {

		if (c.getRobotInfo().getRobotType().equals("NARFSTR"))
			return new NARFSTR(c);
		else
			return null;// TODO Fix this to work with NAFSTR too.
	}

	/**
	 * Checks to see if the connection is valid by comparing the robot's MAC address to the one stored in the program.
	 *
	 * @return true if the two MAC addresses are the same or if the robot type matches, but there is no stored MAC address. False if anything else.
	 * @throws IOException
	 *             if there is an error communicating with the robot.
	 */
	public boolean verifyConnection() throws IOException {

		// Send the challenge
		com.write(Robot.CHALLENGE);

		// Get the token
		String str = null;
		try {
			str = com.readWord(Robot.TIME_OUT, Robot.MAX_CHALLENGE_RESPONSE_LENGTH, Robot.MAX_LEADING_WHITESPACE);
		} catch (InterruptedException e) {
			return false;
		}

		RobotInfo ri = Robot.testResponse(str);
		if (ri == null)
			return false;
		
		return com.getRobotInfo().getMac() == null && ri.getRobotType().equals(com.getRobotInfo().getRobotType()) || ri.equals(com.getRobotInfo());
	}

	/**
	 * Disconnects the Arduino by closing all IO streams and closing the underlying RobotCom.
	 *
	 * @throws IOException
	 *             if there is an error disconnecting.
	 */
	public void disconnect() throws IOException {

		com.disconnect();
	}

	/**
	 * Gets the object used to communicate with the Arduino.
	 *
	 * @return the Arduino used to communicate with the Arduino.
	 */
	public RobotCom<?> getCom() {

		return com;
	}
	
	/**
	 * Executes a command with the specified name and parameters. Does not return until execution is complete, but can be interrupted.
	 *
	 * @param name
	 *            the command name.
	 * @param params
	 *            the parameters.
	 * @throws IOException
	 *             if there is an IO while executing the command.
	 * @throws InterruptedException
	 *             if the command execution is interrupted.
	 */
	public void executeCommand(String name, Object... params) throws IOException, InterruptedException {

		executeCommand(name, Arrays.asList(params));
	}

	/**
	 * Executes a command with the specified name and parameters. Does not return until execution is complete, but can be interrupted.
	 *
	 * @param name
	 *            the command name.
	 * @param params
	 *            the parameters.
	 * @throws IOException
	 *             if there is an IO while executing the command.
	 * @throws InterruptedException
	 *             if the command execution is interrupted.
	 */
	public void executeCommand(String name, List<Object> params) throws IOException, InterruptedException {
	
		executeCommand(new Command(name, params));
	}

	/**
	 * Executes a command with the specified name and parameters. Does not return until execution is complete, but can be interrupted.
	 *
	 * @param c
	 *            the command to execute.
	 * @throws IOException
	 *             if there is an IO while executing the command.
	 * @throws InterruptedException
	 *             if the command execution is interrupted.
	 */
	public void executeCommand(Command c) throws IOException, InterruptedException {
	
		String paramString = c.getArgumentsString();
		send(c.getName() + paramString);
		waitForResponse(c.getName(), paramString, Robot.RECEIVED);
		waitForResponse(c.getName(), paramString, Robot.END);
	}
	
	/**
	 * Executes a list of commands. Does not return until execution is complete, but can be interrupted.
	 *
	 * @param commands
	 *            the commands to execute.
	 * @throws IOException
	 *             if there is an IO while executing the command.
	 * @throws InterruptedException
	 *             if the command execution is interrupted.
	 */
	public void executeCommands(List<Command> commands) throws IOException, InterruptedException {

		for (Command c : commands)
			executeCommand(c);
	}

	/**
	 * Waits for a command response from the robot.
	 *
	 * @param name
	 *            the name of the command sent.
	 * @param paramString
	 *            the parameters send with it.
	 * @param suffix
	 *            the suffix to wait for
	 * @throws IOException
	 *             if there is an IO while executing the command.
	 * @throws InterruptedException
	 *             if the command execution is interrupted.
	 */
	private void waitForResponse(String name, String paramString, String suffix) throws IOException, InterruptedException {

		String response = "";
		if (!(response = readWord(Integer.MAX_VALUE, name.length() + suffix.length(), Robot.MAX_LEADING_WHITESPACE)).equals(name + suffix))
			throw new RobotCommandExecutionException(name, paramString, response);
	}

	/**
	 * Sends the specified string to the robot.
	 *
	 * @param str
	 *            the string to send.
	 * @throws IOException
	 *             if there is an IO while executing the command.
	 */
	public void send(String str) throws IOException {
	
		if (com.write(str) != str.length())
			throw new IOException("Whole message not sent!");
		
	}

	/**
	 * Reads a word (a sequence of non-whitespace characters surrounded by whitespace) from the robot.
	 *
	 * @param timeout
	 *            the maximum amount of time to wait for each byte of the word.
	 * @param maxLength
	 *            the maximum length of the word.
	 * @param maxLeadingWhitespace
	 *            the maximum of amount of leading whitespace to read.
	 * @return the word read or #<code>null</code> if no word was read.
	 * @throws IOException
	 *             if there is an IO while executing the command.
	 * @throws InterruptedException
	 *             if the command execution is interrupted.
	 */
	public String readWord(int timeout, int maxLength, int maxLeadingWhitespace) throws IOException, InterruptedException {

		return com.readWord(timeout, maxLength, maxLeadingWhitespace);
	}

	/**
	 * Tests a response to see if it is valid.
	 *
	 * @param str
	 *            the response to test.
	 * @return if the response is valid, the info of the robot that sent the response, or null the response was not valid.
	 */
	private static RobotInfo testResponse(String str) {
	
		if (str == null)
			return null;
		
		String[] split = str.split(Robot.RESPONSE_SEPERATOR, 3);
		if (split.length != 3 || !split[0].equals(Robot.RESPONSE_PREFIX) || !(split[1].equals("NARFSTR") || split[1].equals("NAFSTR")))
			return null;
		String[] macSplit = split[2].split(Robot.RESPONSE_SEPERATOR);
		if (macSplit.length != 6)
			return null;
		int[] mac = new int[6];
		for (int x = 0; x < mac.length; x++) {
			if (macSplit[x].length() != 2)
				return null;
			mac[x] = Integer.parseInt(macSplit[x], 16);
		}
		return new RobotInfo(split[1], mac, null, null);
	}

	/**
	 * Tries to make a connection to the specified IP address.
	 *
	 * @param ip
	 *            the ip address to connect to.
	 * @return if there is a robot at the specified ip, the info of that robot, or null otherwise.
	 */
	public static RobotInfo tryConnection(InetAddress ip) {

		try {
			Robot testRobot = new Robot(new NetworkRobotCom(new RobotInfo(null, null, ip, null)));
			testRobot.com.write(Robot.CHALLENGE);
			// Get the token
			String str = null;
			try {
				str = testRobot.com.readWord(Robot.TIME_OUT, Robot.MAX_CHALLENGE_RESPONSE_LENGTH, Robot.MAX_LEADING_WHITESPACE);
			} catch (InterruptedException e) {
				return null;
			}
			RobotInfo result = Robot.testResponse(str);
			if (result == null)
				return null;
			return new RobotInfo(result.getRobotType(), result.getMac(), ip, null);
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Tries to make a connection to the specified serial port.
	 *
	 * @param serialPort
	 *            the serial port to connect to.
	 * @return if there is a robot at the specified port, the info of that robot, or null otherwise.
	 */
	public static RobotInfo tryConnection(String serialPort) {

		try {
			Robot testRobot = new Robot(new SerialRobotCom(new RobotInfo(null, null, null, serialPort)));
			testRobot.com.write(Robot.CHALLENGE);
			// Get the token
			String str = null;
			try {
				str = testRobot.com.readWord(Robot.TIME_OUT, Robot.MAX_CHALLENGE_RESPONSE_LENGTH, Robot.MAX_LEADING_WHITESPACE);
			} catch (InterruptedException e) {
				return null;
			}
			RobotInfo result = Robot.testResponse(str);
			if (result == null)
				return null;
			return new RobotInfo(result.getRobotType(), result.getMac(), null, serialPort);
		} catch (SerialPortException | IOException e) {
			return null;
		}
	}

	/**
	 * Sends a UDP broadcast to try and find robots.
	 *
	 * @throws IOException
	 *             if there is an error sending the broadcast.
	 */
	private static void sendUDPBroadcast() throws IOException {

		// Broadcast code based on:
		// http://michieldemey.be/blog/network-discovery-using-udp-broadcast/

		// Find the server using UDP broadcast
		// Make a DatagramSocket
		DatagramSocket c = new DatagramSocket(NetworkRobotCom.PORT);
		c.setBroadcast(true);

		byte[] sendData = Robot.CHALLENGE.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByAddress(Robot.ALL_HOSTS), NetworkRobotCom.PORT);
		c.send(sendPacket);

		// Broadcast the message over all the network interfaces
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()) {
			NetworkInterface networkInterface = interfaces.nextElement();
			if (networkInterface.isLoopback() || !networkInterface.isUp())
				continue;
			for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
				InetAddress broadcast = interfaceAddress.getBroadcast();
				if (broadcast == null)
					continue;

				// Send the broadcast package!
				DatagramPacket pack = new DatagramPacket(sendData, sendData.length, broadcast, NetworkRobotCom.PORT);
				c.send(pack);
			}
		}
		c.close();
	}

	/**
	 * Reads the data in the channels to determine if any of the channels are connected to robots. Returns the RobotInfo of all the connected robots.
	 *
	 * @param toRead
	 *            the channels to try and read.
	 * @return a set of the RobotInfo of all the robots found.
	 */
	private static Set<RobotInfo> findRobotInfo(List<ByteChannel> toRead) {

		ListIterator<ByteChannel> toReadIterator = toRead.listIterator();
		Set<RobotInfo> result = new HashSet<RobotInfo>();

		while (toReadIterator.hasNext()) {

			ByteChannel c = toReadIterator.next();
			StringBuilder data = new StringBuilder();

			// Read all the data available in 128 byte sections
			ByteBuffer inputBuf = ByteBuffer.allocate(128);
			int read = 0;
			do {
				// Can assume that it is a byte channel because will only be used privately with SerialChannels and SocketChannels, both of which are byte channels.
				try {
					read = c.read(inputBuf);
				} catch (IOException e) {
					try {
						c.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					continue;
				}
				if (read != -1) {
					byte[] toMakeString = new byte[read];
					inputBuf.flip();
					inputBuf.get(toMakeString);
					data.append(new String(toMakeString));
				}
				inputBuf.clear();
			} while (read == inputBuf.capacity());

			// Test each token received
			String[] tokens = data.toString().trim().split("\\s+");

			for (String toTest : tokens) {
				RobotInfo ri = Robot.testResponse(toTest);
				if (ri != null) {
					RobotInfo toAdd;
					if (c instanceof SocketChannel)
						toAdd = RobotInfo.withIP(ri, ((SocketChannel) c).socket().getInetAddress());
					else
						toAdd = RobotInfo.withSerialPort(ri, ((SerialChannel) c).getPortName());
					result.add(toAdd);

					// Get out, we found a match
					break;
				}
			}
			try {
				c.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * Opens and initializes all the available serial ports and then returns a list of ByteChannels bound to the ports.
	 *
	 * @return a list of the ByteChannels bound to the ports.
	 */
	private static List<ByteChannel> getSerialByteChannelsToCheck() {

		List<String> ports = SerialRobotCom.getAllSerialDevices();
		List<ByteChannel> toRead = new LinkedList<ByteChannel>();
		List<SerialRobotCom> coms = new LinkedList<SerialRobotCom>();

		for (String port : ports) {
			SerialRobotCom com = null;
			try {

				com = new SerialRobotCom(new RobotInfo(null, null, null, port));
				com.startInitialization();
				coms.add(com);
				
			} catch (IOException | SerialPortException e) {
				try {
					if (com != null)
						com.disconnect();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		for (SerialRobotCom com : coms)
			try {
				com.waitForInitialization();
				toRead.add(com.getChannel());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		for (ByteChannel c : toRead)
			try {
				c.write(ByteBuffer.wrap(Robot.CHALLENGE.getBytes()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		return toRead;
	}

	/**
	 * Finds robots connected over USB.
	 *
	 * @return a set of information about the robots connected over USB.
	 */
	public static Set<RobotInfo> findSerialRobots() {

		List<ByteChannel> toRead = Robot.getSerialByteChannelsToCheck();

		try {
			Thread.sleep(Robot.TIME_OUT);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Set<RobotInfo> result = Robot.findRobotInfo(toRead);
		return result;
	}

	/**
	 * Finds robots that are connected over the network.
	 *
	 * @return a set of information about the robots on the network.
	 * @throws IOException
	 *             if there is an error.
	 */
	public static Set<RobotInfo> findNetworkedRobots() throws IOException {

		Robot.sendUDPBroadcast();

		// The robot will send the response over a TCP connection, so start a server.
		ServerSocketChannel theSock = ServerSocketChannel.open();
		theSock.bind(new InetSocketAddress(NetworkRobotCom.PORT));

		// Make the server non-blocking so that we can have a timeout.
		theSock.configureBlocking(false);

		long startUDPTime = System.currentTimeMillis();
		List<ByteChannel> toRead = new LinkedList<ByteChannel>();

		// Keep checking until we time out
		while (System.currentTimeMillis() - startUDPTime < Robot.TIME_OUT) {
			// If there is a connection available, register it with the selector
			SocketChannel accept = theSock.accept();
			if (accept != null) {
				toRead.add(accept);
				accept.configureBlocking(false);
			}
		}
		Set<RobotInfo> result = Robot.findRobotInfo(toRead);
		theSock.close();
		return result;
	}

	/**
	 * Searches for robots and returns a list of all robots found.
	 *
	 * @return a list of all robots found.
	 * @throws IOException
	 *             if there is an error making the list.
	 */
	public static List<RobotInfo> findRobots() throws IOException {

		LinkedList<RobotInfo> full = new LinkedList<RobotInfo>();

		// Make the serial channels
		List<ByteChannel> serialChannels = Robot.getSerialByteChannelsToCheck();
		// Find all the networked robots. This will take at least TIME_OUT to do, so by the time it is done, the serial channels are ready for reading.
		full.addAll(Robot.findNetworkedRobots());
		full.addAll(Robot.findRobotInfo(serialChannels));

		// Merge the information. There might be a robot connected over both USB and Ethernet.
		Map<RobotInfo, RobotInfo> robotInfoMap = new HashMap<RobotInfo, RobotInfo>();

		for (RobotInfo toAdd : full)
			if (robotInfoMap.containsKey(toAdd)) {
				RobotInfo previous = robotInfoMap.get(toAdd);
				robotInfoMap.put(toAdd, RobotInfo.merge(previous, toAdd));
			} else
				robotInfoMap.put(toAdd, toAdd);
		List<RobotInfo> result = new ArrayList<RobotInfo>(robotInfoMap.size());
		for (RobotInfo key : robotInfoMap.keySet())
			result.add(robotInfoMap.get(key));
		return result;
	}

	/**
	 * Determines if a string is an IP address. Taken from <a href=
	 * "http://answers.oreilly.com/topic/318-how-to-match-ipv4-addresses-with-regular-expressions/"
	 * >http://answers.oreilly.com/topic/318-how-to
	 * -match-ipv4-addresses-with-regular-expressions/</a>
	 *
	 * @param text
	 *            the text.
	 * @return true if the text is an IPv4 address, false otherwise.
	 */
	public static boolean isIP(String text) {

		// TOOD check this out

		Pattern p = Pattern
				.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
		Matcher m = p.matcher(text);
		return m.find();
	}
}