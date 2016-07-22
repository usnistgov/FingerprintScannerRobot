package robot;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.ClosedChannelException;
import java.util.LinkedList;
import java.util.Queue;

import jssc.SerialPort;
import jssc.SerialPortException;

/**
 * A channel for a serial connection.
 *
 * @author Jacob Glueck
 *
 */
public class SerialChannel implements ByteChannel {
	
	/**
	 * The underlying SerialPort.
	 */
	private final SerialPort p;
	/**
	 * True if the channel is currently in blocking mode.
	 */
	private boolean isBlocking;
	/**
	 * The bytes which have been buffered, but not read.
	 */
	private final Queue<Byte> toRead;
	
	/**
	 * Makes a new serial channel using the specified port.
	 *
	 * @param port
	 *            the port to use. Must be open.
	 * @throws SerialPortException
	 *             if there is an error.
	 */
	public SerialChannel(SerialPort port) throws SerialPortException {

		p = port;
		toRead = new LinkedList<Byte>();
		
		if (!port.isOpened())
			throw new IllegalStateException("Serial port not open!");
	}
	
	@Override
	public int read(ByteBuffer arg0) throws IOException {
	
		// Make sure the channel is open
		if (!isOpen())
			throw new ClosedChannelException();
		
		int count = 0;
		do {
			try {
				
				// Read all the available bytes into the buffer.
				byte[] read = p.readBytes();
				if (read != null)
					for (byte b : read)
						toRead.add(b);
			} catch (SerialPortException e) {
				new IOException(e);
			}
			
			// Read as much out of the buffer as possible.
			while (arg0.hasRemaining() && !toRead.isEmpty()) {
				arg0.put(toRead.remove());
				count++;
			}

			// Look for more if this channel is in blocking mode or if there is stuff currently available and we have room for it.
		} while (isBlocking() || !toRead.isEmpty() && arg0.remaining() != 0);
		
		return count;
	}
	
	@Override
	public int write(ByteBuffer arg0) throws IOException {

		// Make sure the channel is open.
		if (!isOpen())
			throw new ClosedChannelException();
		
		// Read from the buffer.
		byte[] toWrite = new byte[arg0.remaining()];
		arg0.get(toWrite);
		
		try {
			boolean result = p.writeBytes(toWrite);
			if (result)
				return toWrite.length;
			else
				return 0;
		} catch (SerialPortException e) {
			throw new IOException(e.getMessage(), e.getCause());
		}
	}

	/**
	 * @return the p
	 */
	public String getPortName() {
	
		return p.getPortName();
	}

	/**
	 * Sets the blocking state.
	 *
	 * @param block
	 *            true to make the channel blocking, false otherwise.
	 */
	public void configureBlocking(boolean block) {
	
		isBlocking = block;
	}

	/**
	 * Returns true if the channel is currently in blocking mode.
	 *
	 * @return true if the channel is currently in blocking mode.
	 */
	public boolean isBlocking() {
	
		return isBlocking;
	}
	
	@Override
	public boolean isOpen() {

		return p.isOpened();
	}
	
	@Override
	public void close() throws IOException {
	
		if (p.isOpened())
			try {
				p.closePort();
			} catch (SerialPortException e) {
				throw new IOException(e.getMessage(), e.getCause());
			}
		
	}
}