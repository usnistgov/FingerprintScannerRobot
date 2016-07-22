package teststuff;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

/**
 * A simple program which lists all the connected network interfaces.
 *
 * @author Jacob Glueck
 *
 */
public class ListNets {
	
	/**
	 * The main method.
	 *
	 * @param args
	 *            takes no arguments.
	 * @throws SocketException
	 *             if something bad happens.
	 */
	public static void main(String args[]) throws SocketException {
	
		final Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
		for (final NetworkInterface netint : Collections.list(nets))
			ListNets.displayInterfaceInformation(netint);
	}
	
	/**
	 * Displays a network interface.
	 *
	 * @param netint
	 *            the NetworkInterface to display.
	 * @throws SocketException
	 *             if something bad happens.
	 */
	static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
	
		System.out.printf("Display name: %s\n", netint.getDisplayName());
		System.out.printf("Name: %s\n", netint.getName());
		final Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
		for (final InetAddress inetAddress : Collections.list(inetAddresses))
			System.out.printf("InetAddress: %s\n", inetAddress);
		System.out.printf("\n");
	}
}
