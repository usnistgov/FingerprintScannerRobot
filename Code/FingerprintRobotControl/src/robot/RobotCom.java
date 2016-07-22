package robot;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

/**
 * This class represents a connection to an Arduino.
 *
 * @author Jacob Glueck
 * @param <T>
 *            the type of channel this robot com uses.
 *
 */
public abstract class RobotCom<T extends ByteChannel> {

	/**
	 * The information of the connected robot.
	 */
	private final RobotInfo robotInfo;
	
	/**
	 * Makes a new RobotCom with the specified robot information.
	 *
	 * @param ri
	 *            the robot information.
	 */
	public RobotCom(RobotInfo ri) {
	
		robotInfo = ri;
	}
	
	/**
	 * Gets the channel associated with this RobotCom.
	 *
	 * @return the channel used to communicate with the robot.
	 */
	public abstract T getChannel();

	/**
	 * Gets the information of the connected robot.
	 *
	 * @return the connected robot's information.
	 */
	public RobotInfo getRobotInfo() {
	
		return robotInfo;
	}
	
	/**
	 * Starts the processes of connecting to the robot.
	 *
	 * @throws IOException
	 *             if there is an error with the connection process.
	 */
	public abstract void startInitialization() throws IOException;
	
	/**
	 * Waits for the initialization process to end.
	 *
	 * @return true if the robot was initialized successfully, false otherwise.
	 * @throws InterruptedException
	 *             if the initialization process was interrupted.
	 */
	public abstract boolean waitForInitialization() throws InterruptedException;
	
	/**
	 * Determines the current initialization state.
	 *
	 * @return true if the robot is currently connected, false otherwise.
	 */
	public abstract boolean isInitialized();

	/**
	 * Returns the location to which the ArduinoCom is connected. For example, if it is connected through a serial port, this method might return "COM3" or if it is connected through a network, the IP
	 * address.
	 *
	 * @return A string describing what the ArduinoCom is connected to.
	 */
	public abstract String getLocationString();

	/**
	 * Disconnects the Arduino by closing the underlying I/O pipes and sockets.
	 *
	 * @throws IOException
	 *             if their is a problem disconnecting.
	 */
	public abstract void disconnect() throws IOException;

	/**
	 * Attempts to read a byte within the timeout. If no byte is read, returns {@link Integer#MAX_VALUE}.
	 *
	 * @param timeout
	 *            the maximum amount of time to wait for a byte, in ms.
	 * @return the byte read of {@link Integer#MAX_VALUE} if no byte was read withing the timeout.
	 * @throws IOException
	 *             if there is an error reading the byte.
	 * @throws InterruptedException
	 *             if this method's waiting was interrupted.
	 *
	 */
	public int readByte(int timeout) throws IOException, InterruptedException {
	
		readPrep();
		
		ByteBuffer b = ByteBuffer.allocate(1);
		
		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() - startTime < timeout)
			if (getChannel().read(b) == b.capacity()) {
				b.flip();
				int toReturn = b.get();
				return toReturn;
			} else if (Thread.interrupted())
				throw new InterruptedException();
		return Integer.MAX_VALUE;
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
	
		readPrep();
		
		// Clear out leading whitespace
		int nextChar = ' ';
		int whitespaceCount = 0;
		while (Character.isWhitespace(nextChar)) {
			if (whitespaceCount > maxLeadingWhitespace)
				return null;
			int next = readByte(timeout);
			if (next == Integer.MAX_VALUE)
				return null;
			else
				nextChar = next;
			whitespaceCount++;
		}
		
		StringBuilder b = new StringBuilder();
		b.append((char) nextChar);

		while (b.length() < maxLength) {
			nextChar = readByte(timeout);
			if (nextChar == Integer.MAX_VALUE || Character.isWhitespace(nextChar))
				break;
			else
				b.append((char) nextChar);
		}
		return b.toString();
	}
	
	/**
	 * Prepares the channel for reading by putting it into non-blocking mode.
	 *
	 * @throws IOException
	 *             if there was an error.
	 */
	public abstract void readPrep() throws IOException;

	/**
	 * Prepares the channel for writing by putting it into blocking mode.
	 *
	 * @throws IOException
	 *             if there was an error.
	 */
	public abstract void writePrep() throws IOException;

	/**
	 * Writes the specified string.
	 *
	 * @param str
	 *            the string to write
	 * @return the number of characters written
	 * @throws IOException
	 *             if an error occurs.
	 */
	public int write(String str) throws IOException {

		writePrep();
		return getChannel().write(ByteBuffer.wrap(str.getBytes()));
	}
}