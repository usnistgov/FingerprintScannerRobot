package teststuff;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * A class for testing network Arduinos.
 *
 * @author Jacob Glueck
 *
 */
public class NetworkTest {
	
	/**
	 * The main method.
	 *
	 * @param args
	 *            takes no arguments.
	 * @throws UnknownHostException
	 *             if something bad happens.
	 * @throws IOException
	 *             if something bad happens.
	 */
	@SuppressWarnings("resource")
	public static void main(String[] args) throws UnknownHostException, IOException {

		final Socket theSock = new Socket("10.0.34.100", 80);
		theSock.getOutputStream().write("move 1 0".getBytes());
		System.out.println("SOCK!");
		// PrintStream ps = new PrintStream(theSock.getOutputStream());
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
			
				while (true)
					try {
						System.out.print((char) theSock.getInputStream().read());
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}).start();
		new Scanner(System.in).nextLine();
		/*
		 * theSock.close();
		 * Scanner s = new Scanner(System.in);
		 * while (System.currentTimeMillis() > 0)
		 * ps.println(s.nextLine());
		 * s.close();
		 */
	}
}