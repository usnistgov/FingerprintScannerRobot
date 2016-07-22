package teststuff;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import robot.NARFSTR;
import view.robotpanel.CommandEditorPanel;
import view.robotpanel.RobotConnectionPanel;
import view.robotpanel.RobotPanel;

@SuppressWarnings("javadoc")
public class UITest {

	public static void main(String[] args) {

		JFrame test = new JFrame();
		test.setLayout(new BorderLayout());
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// test.add(new RobotPanel("RobotConnection", new RobotConnectionPanel()));
		// GraphicalRobotConfigurator grc = new GraphicalRobotConfigurator(NARFSTR.class);
		// grc.load(Arrays.asList(new Command("set", 120, 2000, 120, 2000), new Command("stroke")));
		CommandEditorPanel cep = new CommandEditorPanel(NARFSTR.class);
		mainPanel.add(new RobotPanel("Robot Connection", new RobotConnectionPanel(cep)));
		mainPanel.add(new RobotPanel("Command Editor", cep));
		// mainPanel.setMaximumSize(new Dimension((int) mainPanel.getMaximumSize().getWidth(), Integer.MAX_VALUE));
		test.add(mainPanel, BorderLayout.CENTER);
		test.pack();
		test.setSize(test.getWidth(), 800);
		test.setVisible(true);
	}
}
