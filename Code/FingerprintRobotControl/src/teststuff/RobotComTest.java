package teststuff;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.swing.JFrame;

import robot.Bounds;
import robot.Command;
import robot.NARFSTR;
import robot.RobotCommand;
import jssc.SerialPortException;
import view.configurator.CommandRenderer;

/**
 * @author jacobg
 *
 */
public class RobotComTest {

	@SuppressWarnings("javadoc")
	public static void main(String[] args) throws IOException, SerialPortException, InterruptedException {

		//
		// for (int x = 0; x < 1; x++) {
		//
		// long startTime = System.currentTimeMillis();
		// // System.out.println(Robot.findNetworkedRobots());
		// // System.out.println(System.currentTimeMillis() - startTime);
		// // startTime = System.currentTimeMillis();
		// // System.out.println(Robot.findSerialRobots());
		// // System.out.println(System.currentTimeMillis() - startTime);
		// startTime = System.currentTimeMillis();
		// System.out.println(Robot.findRobots());
		// System.out.println(System.currentTimeMillis() - startTime);
		// startTime = System.currentTimeMillis();
		// }
		
		// NARFSTR narfstr = new NARFSTR(new NetworkArduinoCom(Robot.findRobots().get(0), Robot.PORT));
		// System.out.println(narfstr.getCom().getRobotInfo().getIp());
		
		Method[] methods = NARFSTR.class.getMethods();
		for (Method m : methods)
			if (m.isAnnotationPresent(RobotCommand.class)) {

				System.out.println(m.getName());
				if (m.getAnnotation(RobotCommand.class).paramNames().length != m.getParameterTypes().length)
					System.out.println("What???");
				else
					for (int x = 0; x < m.getAnnotation(RobotCommand.class).paramNames().length; x++) {
						System.out.print(m.getAnnotation(RobotCommand.class).paramNames()[x] + " [");
						System.out.print(((Bounds) m.getParameterAnnotations()[x][0]).min() + ", ");
						System.out.println(((Bounds) m.getParameterAnnotations()[x][0]).max() + "]");
					}
				
				// System.out.println(m);
			}
		
		JFrame test = new JFrame("");

		Object[] testArgs = { 120, 2000, 120, 2000 };

		test.add(new CommandRenderer(NARFSTR.class).getTableCellRendererComponent(null, new Command("set", testArgs), false, false, 0, 0));
		test.pack();
		test.setVisible(true);
		
		// narfstr.set(120, 0, 120, 0);
		// narfstr.stroke();
		// narfstr.set(255, 0, 255, 0);
		// narfstr.stroke();
		// narfstr.set(255, 0, 200, 1000);
		// narfstr.stroke();
		// narfstr.reset(0, 80);
		// narfstr.disconnect();
	}
}
