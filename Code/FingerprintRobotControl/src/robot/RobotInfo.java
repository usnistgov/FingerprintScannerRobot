package robot;

import java.net.InetAddress;
import java.util.Arrays;

/**
 * Contains a robot's type, mac address, and connection location.
 *
 * @author Jacob Glueck
 *
 */
public class RobotInfo {

	/**
	 * The robot's type. Will be whatever the robot says, but will probably be NAFSTR of NARFSTR.
	 */
	private final String robotType;
	/**
	 * The robot's mac address.
	 */
	private final int[] mac;
	/**
	 * The robot's IP address, if it has one. Null if the robot has no IP address.
	 */
	private final InetAddress ip;
	/**
	 * The serial port the robot is plugged into, if it is plugged into a serial port. Null if the robot is not plugged into a serial port.
	 */
	private final String serialPort;
	
	/**
	 * Makes a new RobotInfo with the specified information.
	 *
	 * @param robotType
	 *            the type of robot, either NAFSTR of NARFSTR.
	 * @param mac
	 *            the robot's MAC address.
	 * @param ip
	 *            the robot's IP address or null if unknown.
	 * @param serialPort
	 *            the robot's serial port of null if unknown.
	 */
	public RobotInfo(String robotType, int[] mac, InetAddress ip, String serialPort) {
	
		this.robotType = robotType;
		this.mac = mac;
		this.ip = ip;
		this.serialPort = serialPort;
	}

	/**
	 * @return the robotType
	 */
	public String getRobotType() {
	
		return robotType;
	}

	/**
	 * @return the mac
	 */
	public int[] getMac() {
	
		return mac;
	}

	/**
	 * @return the ip
	 */
	public InetAddress getIp() {
	
		return ip;
	}

	/**
	 * @return the serialPort
	 */
	public String getSerialPort() {
	
		return serialPort;
	}
	
	/**
	 * Makes a new robot info that has all the same information as ri, but with the specified IP.
	 *
	 * @param ri
	 *            the original robot info.
	 * @param ip
	 *            the ip address to put in the new one.
	 * @return a new robot info object that has all the same information as the original, but with the specified IP.
	 */
	public static RobotInfo withIP(RobotInfo ri, InetAddress ip) {

		return new RobotInfo(ri.robotType, ri.mac, ip, ri.serialPort);
	}

	/**
	 * Makes a new robot info that has all the same information as ri, but with the specified serial port.
	 *
	 * @param ri
	 *            the original robot info.
	 * @param serialPort
	 *            the serial port to put in the new one.
	 * @return a new robot info object that has all the same information as the original, but with the specified serial port.
	 */
	public static RobotInfo withSerialPort(RobotInfo ri, String serialPort) {
	
		return new RobotInfo(ri.robotType, ri.mac, ri.ip, serialPort);
	}
	
	/**
	 * Merges two robot info objects, returns null if the merger is invalid.
	 *
	 * @param r1
	 *            the first one.
	 * @param r2
	 *            the second one.
	 * @return the result of merging them or null if their types and MACs are different or if they contain conflicting IPs or serial ports.
	 */
	public static RobotInfo merge(RobotInfo r1, RobotInfo r2) {

		if (r1.robotType.equals(r2.robotType) && Arrays.equals(r1.mac, r2.mac))
			try {
				InetAddress ip = RobotInfo.mergeObject(r1.ip, r2.ip);
				String serialPort = RobotInfo.mergeObject(r1.serialPort, r2.serialPort);
				return new RobotInfo(r1.robotType, r1.mac, ip, serialPort);
			} catch (IllegalMergeException e) {
				return null;
			}
		return null;
	}

	/**
	 * Merges two objects.
	 *
	 * @param <T>
	 *            the type.
	 * @param r1
	 *            the first one.
	 * @param r2
	 *            the second one.
	 * @return null if both objects are null, the non-null object if only one is null or the first object if they are both not null and equal. In any other case, this method throws an
	 *         IllegalMergeEcxeption.
	 * @throws IllegalMergeException
	 *             thrown if both objects are not null and not equal.
	 */
	private static <T> T mergeObject(T r1, T r2) throws IllegalMergeException {

		if (r1 == null && r2 != null)
			return r2;
		else if (r1 != null && r2 == null)
			return r1;
		else if (r1 != null && r2 != null && r1.equals(r2) || r1 == null && r2 == null)
			return r1;
		else
			throw new IllegalMergeException();
	}

	/**
	 * Converts the MAC address to a nice-looking string that used lowercase letters and separates the bytes with colons.
	 *
	 * @return a nice-looking string, as described above.
	 */
	public String getMacString() {
	
		String[] strHexMac = new String[mac.length];
		for (int x = 0; x < strHexMac.length; x++) {
			strHexMac[x] = Integer.toHexString(mac[x]);
			if (strHexMac[x].length() == 1)
				strHexMac[x] = "0" + strHexMac[x];
		}
		String temp = Arrays.toString(strHexMac).replace(", ", ":");
		return temp.substring(1, temp.length() - 1).toUpperCase();
	}
	
	@Override
	public String toString() {

		return "RobotInfo [robotType=" + robotType + ", mac=" + getMacString() + ", ip=" + ip + ", serialPort=" + serialPort + "]";
	}
	
	/**
	 * Calculates a hash code using only the robot type and the MAC address.
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(mac);
		result = prime * result + (robotType == null ? 0 : robotType.hashCode());
		return result;
	}
	
	/**
	 * Determines if two robot info objects are equal by checking the name and MAC address.
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RobotInfo other = (RobotInfo) obj;
		if (!Arrays.equals(mac, other.mac))
			return false;
		if (robotType == null) {
			if (other.robotType != null)
				return false;
		} else if (!robotType.equals(other.robotType))
			return false;
		return true;
	}
	
	/**
	 * Thrown if there is a merging problem.
	 *
	 * @author Jacob Glueck
	 *
	 */
	private static class IllegalMergeException extends Exception {

		/**
		 * Fun!
		 */
		private static final long serialVersionUID = 1073609795046934618L;
		
	}
	
}
