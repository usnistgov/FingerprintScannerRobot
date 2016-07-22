package robot;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * This class represents a connection to an Arduino over Ethernet.
 *
 * @author Jacob Glueck
 */
public class NetworkRobotCom extends RobotCom<SocketChannel> {

	/**
	 * The port on which all communications are done.
	 */
	public static final int PORT = 2424;
	/**
	 * The maximum amount of time to wait for a socket connection, in ms.
	 */
	private static int waitTime = 1000;
	/**
	 * The time at which the connection was initiated. -1 means that no connection attempt has been initiated.
	 */
	private long connectStartTime = -1;
	/**
	 * The time at which the connection must be over.
	 */
	private long connectEndTime;
	/**
	 * The IP address of the robot.
	 */
	private final InetAddress ip;
	/**
	 * The socket.
	 */
	private SocketChannel theSock;

	/**
	 * Makes a new NetworkArduinoCom at the specified IP. This puts the socket into non-blocking and calls connect. Thus, {@link SocketChannel#finishConnect()} should be called before attempting to
	 * read.
	 *
	 * @param ri
	 *            the info of the robot to connect to.
	 * @throws IOException
	 *             if there is a problem.
	 */
	public NetworkRobotCom(RobotInfo ri) throws IOException {

		super(ri);

		if (ri.getIp() == null)
			throw new IllegalArgumentException("The robot info provided contained a null ip");

		ip = ri.getIp();
	}

	@Override
	public void disconnect() throws IOException {

		theSock.close();
	}

	@Override
	public SocketChannel getChannel() {

		return theSock;
	}

	@Override
	public void readPrep() throws IOException {

		theSock.configureBlocking(false);
	}

	@Override
	public void writePrep() throws IOException {

		theSock.configureBlocking(true);
	}
	
	@Override
	public String getLocationString() {

		return ip.toString().substring(1);
	}

	@Override
	public boolean waitForInitialization() throws InterruptedException {

		// Make sure the initialization has started
		if (connectStartTime == -1)
			throw new IllegalStateException("Initialization not started!");

		// Determine the amount of time left. If the initialization period is over, return the current initialization state.
		long timeLeft = connectEndTime - System.currentTimeMillis();
		if (timeLeft <= 0)
			return theSock.isConnected();
		
		// Wait for the initialization period to end while checking for interrupts and a connection.
		while (connectEndTime - System.currentTimeMillis() > 0)
			if (Thread.interrupted())
				throw new InterruptedException();
			else
				try {
					if (theSock.finishConnect())
						return true;
				} catch (IOException e) {
					return false;
				}
		return false;
	}

	@Override
	public boolean isInitialized() {
	
		return theSock.isConnected();
	}

	@Override
	public void startInitialization() throws IOException {

		// Sets up the channel and starts the connection process.
		theSock = SocketChannel.open();
		theSock.configureBlocking(false);
		theSock.connect(new InetSocketAddress(ip, NetworkRobotCom.PORT));
		connectStartTime = System.currentTimeMillis();
		connectEndTime = connectStartTime + NetworkRobotCom.waitTime;
	}
}