package view.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

/**
 * Is like a JList of JPanels.
 *
 * @author Jacob Glueck
 *
 * @param <T>
 *            The type of items in this list.
 */
public class ComponentList<T extends JComponent> extends JPanel {
	
	/**
	 * Default UID.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The list of panels.
	 */
	private final List<ListItem> panels;
	/**
	 * The panel with all the other panels on it.
	 */
	private final JPanel listPanel;
	
	/**
	 * Maks a new ComponentList.
	 */
	public ComponentList() {
	
		panels = new LinkedList<>();
		listPanel = new JPanel();
		// listPanel.setDoubleBuffered(true);
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.PAGE_AXIS));
		setLayout(new BorderLayout());
		add(listPanel, BorderLayout.CENTER);
	}

	/**
	 * Gets the number of panels in this list.
	 *
	 * @return the number of panels in this list.
	 */
	public int getPanelSize() {

		return panels.size();
	}
	
	/**
	 * Gets the panel at the specified index.
	 *
	 * @param index
	 *            the index.
	 * @return the panel at the index.
	 */
	public T getPanel(int index) {
	
		return panels.get(index).original;
	}

	/**
	 * Adds a panel to the end.
	 *
	 * @param toAdd
	 *            the panel to add.
	 */
	public void addPanel(T toAdd) {

		panels.add(new ListItem(toAdd, false));
		listChanged();
	}

	/**
	 * Adds a panel to the specified index.
	 *
	 * @param index
	 *            the index.
	 * @param toAdd
	 *            the panel to add.
	 */
	public void addPanel(int index, T toAdd) {
	
		panels.add(index, new ListItem(toAdd, false));
		listChanged();
	}

	/**
	 * Removes a panel at the specified index.
	 *
	 * @param index
	 *            the index.
	 */
	public void removePanel(int index) {

		panels.remove(index);
		listChanged();
	}
	
	/**
	 * Removes all the panels.
	 */
	public void clearPanels() {
	
		panels.clear();
		listChanged();
	}
	
	/**
	 * Called to update the UI.
	 */
	// @Override
	public void listChanged() {

		listPanel.removeAll();
		for (ListItem x : panels)
			listPanel.add(x.item);
		listPanel.add(Box.createVerticalGlue());
		super.updateUI();
	}

	/**
	 * Gets the first selected index.
	 *
	 * @return the selected index.
	 */
	public int getSelectedIndex() {
	
		for (int x = 0; x < panels.size(); x++)
			if (panels.get(x).isSelected)
				return x;
		return -1;
	}

	/**
	 * Represents an item in this component list.
	 *
	 * @author Jacob Glueck
	 *
	 */
	private class ListItem implements MouseListener {

		/**
		 * The original object.
		 */
		private final T original;
		/**
		 * The formatted item.
		 */
		private final JPanel item;
		/**
		 * True if the item is selected.
		 */
		private boolean isSelected;
		
		/**
		 * Makes a new list item with the specified information.
		 *
		 * @param item
		 *            the item.
		 * @param isSelected
		 *            true if the item should be selected, false otherwise.
		 */
		public ListItem(T item, boolean isSelected) {

			original = item;
			this.item = ComponentList.decorate(item);
			this.item.setMaximumSize(new Dimension((int) this.item.getMaximumSize().getWidth(), (int) this.item.getPreferredSize().getHeight()));
			this.item.addMouseListener(this);
			this.isSelected = isSelected;
		}

		@Override
		public void mouseClicked(MouseEvent e) {

			isSelected = !isSelected;
			if (isSelected) {
				item.setBackground(UIManager.getDefaults().getColor("List.selectionBackground"));
				ComponentList.setOpaque(item, false);
				item.setOpaque(true);
			} else {
				item.setBackground(UIManager.getDefaults().getColor("Panel.background"));
				ComponentList.setOpaque(item, true);
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {

		}

		@Override
		public void mouseReleased(MouseEvent e) {

		}

		@Override
		public void mouseEntered(MouseEvent e) {

		}

		@Override
		public void mouseExited(MouseEvent e) {

		}
	}
	
	/**
	 * Adds a nice border to the component.
	 *
	 * @param p
	 *            the component.
	 * @return the decorated panel.
	 */
	private static JPanel decorate(JComponent p) {

		JPanel result = new JPanel(new BorderLayout());
		result.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		result.add(new JSeparator(SwingConstants.HORIZONTAL), BorderLayout.PAGE_START);
		result.add(p, BorderLayout.CENTER);
		result.add(new JSeparator(SwingConstants.HORIZONTAL), BorderLayout.PAGE_END);
		return result;
	}
	
	/**
	 * Sets the opacity of the JPanel and all child JComponents.
	 *
	 * @param c
	 *            the JPanel.
	 * @param o
	 *            the value passed {@link JComponent#setOpaque(boolean)}.
	 */
	private static void setOpaque(JPanel c, boolean o) {

		c.setOpaque(o);
		synchronized (c.getTreeLock()) {
			for (Component comp : c.getComponents())
				if (comp instanceof JComponent) {
					((JComponent) comp).setOpaque(o);
					if (comp instanceof JPanel)
						ComponentList.setOpaque((JPanel) comp, o);
				}
		}
	}
}