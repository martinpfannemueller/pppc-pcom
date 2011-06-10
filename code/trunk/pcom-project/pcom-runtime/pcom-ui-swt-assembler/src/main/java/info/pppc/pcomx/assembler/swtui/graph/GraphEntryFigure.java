package info.pppc.pcomx.assembler.swtui.graph;

import java.util.Hashtable;
import java.util.Vector;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.widgets.Display;

/**
 * The graph entry figure consists of a graph that is shown in the upper
 * part of the figure and a number of entries that are shown centered 
 * below the graph. Nodes can be inserted into the graph using one of
 * the entries. This will cause the nodes to appear at a position close
 * to the entry.
 * 
 * @author Mac
 */
public class GraphEntryFigure extends GraphFigure {

	/**
	 * The spacing between two adjacent entries as well as the
	 * top and the bottom.
	 */
	protected static final int ENTRY_SPACING = 16;
	
	/**
	 * The distance between the entries and the graph.
	 */
	protected static final int ENTRY_DISTANCE = 100;
	
	/**
	 * The size of the entry list in the ui.
	 */
	protected Dimension entrySize = new Dimension(0, 0);
	
	/**
	 * The absolute offset of the entry list in the ui.
	 */
	protected Dimension entryOffset = new Dimension(0, 0);
	
	/**
	 * This hashtable stores the entries that have been added 
	 * since the last call to update graph. The contents of
	 * the table is a vector with nodes.
	 */
	protected Hashtable insertions = new Hashtable();
	
	/**
	 * A vector that contains the entries that have been added to
	 * the figure.
	 */
	protected Vector entries = new Vector();
	
	/**
	 * The graph entry figure is a figure that displays
	 * a graph with a list of figures positioned at the
	 * bottom. The entries of the list can be used to
	 * create graph nodes that animate from their position
	 * to their graph end-position.
	 * 
	 * @param display The display that will be used to
	 * 	perform the animation.
	 */
	public GraphEntryFigure(Display display) {
		super(display);
	}
	
	/**
	 * Adds the specified entry at the end of the entry list.
	 * 
	 * @param entry The entry that needs to be added.
	 * @throws IllegalArgumentException Thrown if the entry is
	 * 	already added.
	 */
	public void addEntry(Figure entry) {
		if (entries.contains(entry)) 
			throw new IllegalArgumentException("Entry exists already.");
		add(entry);
		entries.addElement(entry);
		insertions.put(entry, new Vector());
	}
	
	/**
	 * Inserts the specified node at the position of the specified entry.
	 * 
	 * @param node The node that should be added.
	 * @param entry The entry that serves as origin of the node.
	 */
	public void insertNode(Figure node, Figure entry) {
		if (! entries.contains(entry)) throw new IllegalArgumentException("Entry does not exist.");
		addNode(node);
		Rectangle b = entry.getBounds();
		node.setBounds(new Rectangle(b.x + b.width / 2, b.y - ENTRY_SPACING/2, 0, 0));
		Vector vector = (Vector)insertions.get(entry);
		vector.addElement(node);
	}
	
	
	/**
	 * Inserts the specified entry at the specified position
	 * in the entry list.
	 * 
	 * @param entry The entry that needs to be added.
	 * @param position The position to add the entry.
	 * @throws IllegalArgumentException Thrown if the entry is
	 * 	already contained in the entry list.
	 * @throws IndexOutOfBoundsException Thrown if the position 
	 * 	does not point to a valid position in the entry list.
	 */
	public void insertEntry(Figure entry, int position) {
		if (entries.contains(entry)) 
			throw new IllegalArgumentException("Entry exists already.");
		add(entry);
		entries.insertElementAt(entry, position);
		insertions.put(entry, new Vector());
	}
	
	/**
	 * Returns all entries in the order in which they will
	 * be displayed.
	 * 
	 * @return The entries in the display order.
	 */
	public Figure[] getEntries() {
		Figure[] figures = new Figure[entries.size()];
		for (int i = 0; i < figures.length; i++) {
			figures[i] = (Figure)entries.elementAt(i);
		}
		return figures;
	}
	
	/**
	 * Removes the specified entry from the entry list and
	 * returns whether the removal was successful.
	 * 
	 * @param entry The entry that should be removed.
	 * @return True if the entry has been removed, false
	 * 	otherwise.
	 */
	public boolean removeEntry(Figure entry) {
		if (entries.removeElement(entry)) {
			remove(entry);
			return true;
		}
		return false;
	}
	
	/**
	 * This method must be called whenever the graph structure
	 * has changed.
	 */
	public void updateGraph() {
		// adapt the entry size according to the entries
		entrySize = computeEntrySize();
		super.updateGraph();
		// determine the width and height delta between the old and the new size
		entryOffset = computeEntryOffset();
		// compute the location for each entry
		int offY = entryOffset.height;
		int offX = entryOffset.width + ENTRY_SPACING;
		for (int i = 0; i < entries.size(); i++) {
			Figure entry = (Figure)entries.elementAt(i);
			Dimension size = entry.getPreferredSize();
			int posY = offY + (entrySize.height - size.height) / 2;
			Rectangle bounds = new Rectangle(offX, posY, size.width, size.height);
			entry.setBounds(bounds);
			Vector vector = (Vector)insertions.get(entry);
			while (! vector.isEmpty()) {
				Figure node = (Figure)vector.elementAt(0);
				vector.removeElementAt(0);
				node.setBounds(new Rectangle
					(bounds.x + bounds.width / 2, bounds.y - ENTRY_SPACING/2, 0, 0));	
			}
			offX = offX + ENTRY_SPACING + size.width;
		}
		revalidate();
	}
	
	/**
	 * Returns the offset of the graph. Since the graph must be moved
	 * higher up, the graph offset must be adjusted accordingly.
	 * 
	 * @return The graph offset.
	 */
	protected Dimension computeGraphOffset() {
		Dimension go = super.computeGraphOffset();
		go.height -= (entrySize.height + ENTRY_DISTANCE) / 2;
		return go;
	}
	
	/**
	 * Recomputes the size of the entry set if they are put togehter
	 * using the spacing. This is the total space that will be
	 * covered by the entries.
	 * 
	 * @return The completely recomputed entry size.
	 */
	protected Dimension computeEntrySize() {
		Dimension es = new Dimension(0, 0);
		for (int i = 0; i < entries.size(); i++) {
			Figure entry = (Figure)entries.elementAt(i);
			Dimension size = entry.getPreferredSize();
			es.width += size.width;
			es.height = Math.max(es.height, size.height);
		}
		if (entries.size() > 0) {
			es.height += (ENTRY_SPACING * 2);
			es.width += ((entries.size() + 1) * ENTRY_SPACING);
		}
		return es;
	}
	
	/**
	 * Returns the offset of the entry set according to 0.0. This
	 * is the upper left point where the entry set starts.
	 * 
	 * @return Returns the offset of the entry set.
	 */
	protected Dimension computeEntryOffset() {
		Dimension dim = new Dimension(graphOffset.width, graphOffset.height);
		dim.height += graphSize.height + ENTRY_DISTANCE;
		dim.width += (graphSize.width - entrySize.width) / 2;
		return dim;
	}
	
	/**
	 * Called whenever the figure has been moved. This will center
	 * the graph and the entry set.
	 */
	protected void center() {
		super.center();
		// retrieve the needed offset for the current graph size
		Dimension eo = computeEntryOffset();
		// determine delta betwenn old and new size
		int xdiff = eo.width - entryOffset.width;
		int ydiff = eo.height - entryOffset.height;
		entryOffset = eo;
		moveEntries(xdiff, ydiff);
	}
	
	/**
	 * Moves all entries by the specified x and y values relative
	 * to their current position.
	 * 
	 * @param x The x value to move.
	 * @param y The y value to move.
	 */
	protected void moveEntries(int x, int y) {
		if (x == 0 && y == 0) return;
		for (int i = 0; i < entries.size(); i++) {
			Figure entry = (Figure)entries.elementAt(i);
			Rectangle b = entry.getBounds();
			entry.setBounds(new Rectangle(b.x + x, b.y + y, b.width, b.height));
		}
	}
	
	/**
	 * Returns the preferred size of the figure according to the
	 * specified width and height hints.
	 * 
	 * @param wHint The width hint.
	 * @param hHint The height hint.
	 * @return The preferred size of the figure.
	 */
	public Dimension getPreferredSize(int wHint, int hHint) {
		Dimension size = super.getPreferredSize(wHint, hHint);
		return new Dimension(Math.max(size.width, entrySize.width), 
			size.height + entrySize.height + ENTRY_DISTANCE);
	}
}
