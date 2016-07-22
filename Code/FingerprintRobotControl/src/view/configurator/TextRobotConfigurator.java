package view.configurator;

import java.awt.BorderLayout;
import java.text.ParseException;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import robot.Command;
import robot.Robot;
import view.util.TextLineNumber;

/**
 * A text-based move editor.
 *
 * @author Jacob Glueck
 *
 */
public class TextRobotConfigurator extends RobotConfigurator {
	
	/**
	 * Default UID.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The text area.
	 */
	private final JTextArea text;
	/**
	 * The scroll pane the holds the text area.
	 */
	private final JScrollPane textScrollPane;

	/**
	 * Makes a new text robot configurator.
	 *
	 * @param robotType
	 *            the type of robot this TextRobotConfigurator configures moves for.
	 */
	public TextRobotConfigurator(Class<? extends Robot> robotType) {

		super(robotType);
		
		text = new JTextArea();
		textScrollPane = new JScrollPane(text);
		textScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		textScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		setLayout(new BorderLayout());
		textScrollPane.setRowHeaderView(new TextLineNumber(text));
		add(textScrollPane, BorderLayout.CENTER);
	}
	
	@Override
	public List<Command> get() throws ParseException {

		return Command.parseCommands(text.getText(), getRobotType());
	}
	
	@Override
	public void clear() {
	
		text.setText("");
	}
	
	@Override
	public void load(List<Command> toLoad) {
	
		clear();
		for (Command c : toLoad) {
			text.append(c.toString());
			text.append("\n");
		}
	}
	
	@Override
	protected boolean handleRobotChange(Class<? extends Robot> newRobotType) {
	
		return true;
	}
}
