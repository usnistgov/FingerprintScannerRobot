package teststuff;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

@SuppressWarnings("javadoc")
public class SocketClosedTest {
	
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
	
		SocketChannel theSock = SocketChannel.open();
		System.out.println(theSock.isConnected());
		theSock.configureBlocking(false);
		theSock.connect(new InetSocketAddress(InetAddress.getByName("10.0.254.12"), 2424));
		while (!theSock.isConnected())
			System.out.println(theSock.finishConnect());
		theSock.write(ByteBuffer.wrap("stroke".getBytes()));
		
		while (true) {
			ByteBuffer toRead = ByteBuffer.allocate(1);
			int bob = theSock.read(toRead);
			toRead.flip();
			if (bob == 1)
				System.out.print((char) toRead.get());
		}
		/*
		 * final Socket s = new Socket();
		 * s.connect(new InetSocketAddress(InetAddress.getByName("10.0.254.12"), 2424));
		 * System.out.println("MADE!");
		 * new Thread(new Runnable() {
		 * 
		 * @Override
		 * public void run() {
		 * 
		 * while (true)
		 * try {
		 * if (s.isConnected())
		 * System.out.print((char) s.getInputStream().read());
		 * } catch (IOException e) {
		 * // TODO Auto-generated catch block
		 * e.printStackTrace();
		 * }
		 * }
		 * }).start();
		 * System.out.println("SW1!");
		 * s.getOutputStream().write("set 255 0 255 0\n".getBytes());
		 * s.getOutputStream().write("stroke\n".getBytes());
		 * s.getOutputStream().flush();
		 * System.out.println("W!");
		 * Thread.sleep(60 * 1000);
		 * System.out.println("SW2!");
		 * s.getOutputStream().write("set 255 0 255 0\n".getBytes());
		 * s.getOutputStream().write("stroke\n".getBytes());
		 * s.getOutputStream().flush();
		 * System.out.println("DONE");
		 */
	}
}
