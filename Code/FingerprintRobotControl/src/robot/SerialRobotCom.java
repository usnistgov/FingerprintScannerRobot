package robot;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

/**
 * This class represents a connection to an Arduino over USB.
 *
 * @author Jacob Glueck
 */
public class SerialRobotCom extends RobotCom<SerialChannel> {

	/**
	 * Default bits per second for COM port.
	 */
	private static final int DATA_RATE = 9600;
	/**
	 * The time to wait for the port to initialize
	 */
	private static int waitTime = 0;
	/**
	 * The time at which the port initialization sequence was started.
	 */
	private long startTime = -1;
	/**
	 * The time at which the port initialization sequence must end.
	 */
	private long endTime;
	/**
	 * The serial channel to use for communication.
	 */
	private SerialChannel serialChannel;
	/**
	 * The serial port.
	 */
	private final SerialPort serialPort;
	
	/**
	 * Makes a new Robot connection over a serial port.
	 *
	 * @param ri
	 *            the information of the robot to connect to.
	 * @throws SerialPortException
	 *             if anything bad happens while trying to set up the port.
	 * @throws IOException
	 *             if the port or I/O streams cannot be opened.
	 */
	public SerialRobotCom(RobotInfo ri) throws SerialPortException, IOException {
	
		super(ri);
		
		if (ri.getSerialPort() == null)
			throw new IllegalArgumentException("The robot info provided contained a null serial port");
		
		// Get the port
		serialPort = new SerialPort(ri.getSerialPort());
	}

	@Override
	public boolean waitForInitialization() throws InterruptedException {
	
		// Check to make sure initialization has started.
		if (startTime == -1)
			throw new IllegalStateException("Initialization not started!");
		
		// Return if the time is up, otherwise wait.
		long timeLeft = endTime - System.currentTimeMillis();
		if (timeLeft <= 0)
			return true;
		else
			Thread.sleep(timeLeft);
		return true;
	}

	@Override
	public boolean isInitialized() {

		return endTime - System.currentTimeMillis() <= 0;
	}
	
	@Override
	public void disconnect() throws IOException {
	
		if (!serialPort.isOpened())
			throw new IOException("Port not open!");

		serialChannel.close();
	}
	
	/**
	 * Gets all the devices connected with USB.
	 *
	 * @return A list of all devices connected over USB.
	 */
	public static List<String> getAllSerialDevices() {
	
		String[] portNames = SerialPortList.getPortNames();
		
		return Arrays.asList(portNames);
	}
	
	@Override
	public SerialChannel getChannel() {
	
		if (!isInitialized())
			throw new IllegalStateException("Port not initialized!");
		
		return serialChannel;
	}
	
	@Override
	public void readPrep() throws IOException {

		if (!isInitialized())
			throw new IllegalStateException("Port not initialized!");

		serialChannel.configureBlocking(false);
	}
	
	@Override
	public void writePrep() throws IOException {
	
		serialChannel.configureBlocking(true);
	}
	
	/**
	 * @return the waitTime
	 */
	public static int getWaitTime() {

		return SerialRobotCom.waitTime;
	}
	
	/**
	 * @param waitTime
	 *            the waitTime to set
	 */
	public static void setWaitTime(int waitTime) {

		SerialRobotCom.waitTime = waitTime;
	}
	
	@Override
	public String getLocationString() {
	
		return serialPort.getPortName();
	}
	
	@Override
	public void startInitialization() throws IOException {

		try {
			// Set port parameters
			if (!serialPort.openPort())
				throw new IOException("Failed to open the port.");
			// The last false prevents the DTR line for pulsing which prevents the robot from resetting.
			serialPort.setParams(SerialRobotCom.DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE, true, false);
			startTime = System.currentTimeMillis();
			endTime = startTime + SerialRobotCom.waitTime;
			serialChannel = new SerialChannel(serialPort);
		} catch (SerialPortException e) {
			throw new IOException(e);
		}
	}
}