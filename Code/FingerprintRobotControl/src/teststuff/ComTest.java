package teststuff;

import java.util.Arrays;
import java.util.Scanner;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

/**
 * This class is for testing basic Arduino communications.
 *
 * @author Jacob Glueck
 *
 */
public class ComTest {
	
	/**
	 * The main method.
	 *
	 * @param args
	 *            takes no arguments.
	 * @throws SerialPortException
	 *             if something bad happens.
	 * @throws InterruptedException
	 *             if something bad happens.
	 */
	public static void main(String[] args) throws SerialPortException, InterruptedException {
	
		String[] ports = SerialPortList.getPortNames();
		System.out.println(Arrays.toString(ports));
		final SerialPort sp = new SerialPort("/dev/ttyACM0");
		sp.openPort();
		sp.setDTR(false);
		sp.setParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		System.out.println("START++++++++++++++++++++++");
		Thread.sleep(2000);
		sp.writeString("fingerrobot");
		// Thread.sleep(2000);
		// sp.writeString("fingerrobot");
		while (System.currentTimeMillis() > 0)
			try {
				String cool = sp.readString();
				if (cool != null)
					System.out.print(cool);
			} catch (SerialPortException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		System.out.println("END++++++++++++++++++++++++");
		try {
			Scanner s = new Scanner(System.in);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
				
					while (true)
						try {
							String cool = sp.readString();
							if (cool != null)
								System.out.println(cool);
						} catch (SerialPortException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
				}
			}).start();
			while (System.currentTimeMillis() > 0) {
				String str = s.nextLine();
				sp.writeString(str);
			}
			s.close();
			
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
