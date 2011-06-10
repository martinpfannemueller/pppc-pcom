package info.pppc.pcomx.assembler.swtui.graph;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.DirectedGraphLayout;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.draw2d.graph.NodeList;
import org.eclipse.swt.widgets.Display;

/**
 * The graph figure enables a user to visualize a fully connected directed
 * graph using his or her own figures and connections. After a modification
 * has been made, the update graph must be called. If no animation is currently
 * running, this will start an animation.
 * 
 * @author Mac
 */
public class GraphFigure extends Figure {

	/**
	 * The speed at which the animation will run. Lower values are faster
	 * but they will consume more processor power. Values must be greater 
	 * than 0. 
	 */
	protected static int ANIMATION_SPEED = 100;
	
	/**
	 * The amount of pixels that will be changed in each step. Higher values
	 * will cause faster animations but they will not look smooth any longer.
	 * Values must be greater than 0. 
	 */
	protected static int ANIMATION_STEP = 10;
	
	/**
	 * The default spacing between nodes.
	 */
	protected static final int NODE_SPACING = 16;
	
	/**
	 * The nodes of the graph hashed by the figure that created them.
	 */
	protected Hashtable nodes = new Hashtable();
	
	/**
	 * The edges of the graph hashed by their edge repesentation. 
	 */
	protected Hashtable edges = new Hashtable(); 
	
	/**
	 * The graph used to layout the contents of the figure.
	 */
	protected DirectedGraph graph = new DirectedGraph();
	
	/**
	 * The directed graph layout that is used to layout the graph.
	 */
	protected DirectedGraphLayout layout = new DirectedGraphLayout();
	
	/**
	 * The total size of the graph figure.
	 */
	protected Dimension graphSize = new Dimension(0, 0);
	
	/**
	 * The offset between the preferred size and the client size.
	 * If the client size is smaller than the total size, this
	 * will be 0,0.
	 */
	protected Dimension graphOffset = new Dimension(0, 0);
	
	/**
	 * The runnable that is executed to play the animation.
	 */
	protected Runnable animator = null;
	
	/**
	 * The display that is used to animate the graph figure.
	 */
	protected Display display = null;
	
	/**
	 * The maximum step size during the animation.
	 */
	protected int step = ANIMATION_STEP;
	
	/**
	 * The speed of the animation.
	 */
	protected int speed = ANIMATION_SPEED;
	
	/**
	 * Creates a new graph figure using the specified display to animate
	 * the layout of the graph figure.
	 * 
	 * @param display The display used to perform the animations.
	 */
	public GraphFigure(Display display) {
		if (display == null) throw new NullPointerException("Display is null.");
		this.display = display;
		addFigureListener(new FigureListener() {
			public void figureMoved(IFigure source) {
				center();
			}
		});
	}
	
	/**
	 * Adds the specified node to the figure. If the figure is
	 * already a child of the graph, the method will throw an
	 * illegal argument exception.
	 * 
	 * @param figure The node of the figure.
	 * @throws IllegalArgumentException Thrown if the figure is
	 * 	already part of the graph.
	 */
	public void addNode(Figure figure) {
		if (nodes.containsKey(figure)) 
			throw new IllegalArgumentException("Node already present.");
		add(figure, figure.getBounds());
		Node node = new Node(figure);
		nodes.put(figure, node);
		graph.nodes.add(node);
		Dimension preferred = figure.getPreferredSize();
		node.width = preferred.width;
		node.height = preferred.height;
		node.setPadding(new Insets(NODE_SPACING));
	}
	
	/**
	 * Returns all graph nodes that have been added through
	 * the add node method.
	 * 
	 * @return The graph nodes that have been added through
	 * 	the add method.
	 */
	public Figure[] getNodes() {
		Vector result = new Vector();
		Enumeration e = nodes.keys();
		while (e.hasMoreElements()) {
			result.addElement(e.nextElement());
		}
		Figure[] figures = new Figure[result.size()];
		for (int i = 0; i < figures.length; i++) {
			figures[i] = (Figure)result.elementAt(i);
		}
		return figures;
	}
	
	/**
	 * Removes the specified node from the graph and returns
	 * whether the node has been removed.
	 * 
	 * @param figure The node that should be removed.
	 * @return True if the node was present and has been removed,
	 * 	false if the node was not present.
	 */
	public boolean removeNode(Figure figure) {
		Node node = (Node)nodes.remove(figure);
		if (node == null) return false;
		graph.nodes.remove(node);
		remove(figure);
		return true;
	}

	/**
	 * Adds the specified connection as edge to the graph. The source
	 * owner and the target owner of the anchor must be nodes that have
	 * been added already through the add method. If the connection is
	 * already present or if the source and target nodes have not been
	 * added, this method will throw an illegal argument exception.
	 * 
	 * @param connection The connection that should be added.
	 * @throws IllegalArgumentException Thrown if the source and target
	 * 	nodes have not been added or if the connection was already present.
	 */
	public void addEdge(Connection connection) {
		if (edges.containsKey(connection)) throw new IllegalArgumentException("Edge already present.");
		Node target = (Node)nodes.get(connection.getTargetAnchor().getOwner());
		Node source = (Node)nodes.get(connection.getSourceAnchor().getOwner());
		if (target == null || source == null) throw new IllegalArgumentException("Anchors not found.");
		add(connection);
		Edge edge = new Edge(connection, source, target);
		edges.put(connection, edge);
		graph.edges.add(edge);
	}
	
	/**
	 * Removes the edge specified by the connection from the graph and
	 * returns whether the process was successful.
	 * 
	 * @param connection The connection to remove.
	 * @return True if the connection was found and thus, could be removed.
	 * 	False otherwise.
	 */
	public boolean removeEdge(Connection connection) {
		Edge edge = (Edge)edges.remove(connection);
		if (edge == null) return false;
		graph.edges.remove(edge);
		remove(connection);
		return true;
	}
	
	/**
	 * Returns the set of connections that has been added through the
	 * add edge method.
	 * 
	 * @return The edges contained in the graph figure.
	 */
	public Connection[] getEdges() {
		Vector result = new Vector();
		Enumeration e = edges.keys();
		while (e.hasMoreElements()) {
			result.addElement(e.nextElement());
		}
		Connection[] connections = new Connection[result.size()];
		for (int i = 0; i < connections.length; i++) {
			connections[i] = (Connection)result.elementAt(i);
		}
		return connections;
	}

	/**
	 * Returns the current size of the graph figure. This is the
	 * minimum bounding box.
	 * 
	 * @param wHint The hint for the width. This is just ignored.
	 * @param hHint The hint for the height. This is just ignored.
	 * @return The current size of the graph figure.
	 */
	public Dimension getPreferredSize(int wHint, int hHint) {
		return graphSize;
	}
	
	/**
	 * Calling this method will compute the next layout and it
	 * will perform the animation between the two layouts.
	 */
	public void updateGraph() {
		if (graph.nodes.size() > 0) {
			layout.visit(graph);
		}
		Dimension gs = computeGraphSize();
		// determine the width and height delta between the old and the new size
		int widthDelta = (graphSize.width - gs.width) / 2;
		int heightDelta = (graphSize.height - gs.height) / 2;
		// now we have the delta that must be applied to the offset
		graphOffset.width += widthDelta;
		graphOffset.height += heightDelta;
		graphSize = gs;
		moveNodes(widthDelta, heightDelta);
		fireMoved();
		revalidate();
		// beginn the potential animation of the graph elements
		if (animator == null) {
			animator = new Runnable() {
				public void run() {
					animate();
				};
			};
			display.timerExec(speed, animator);
		}
	}

	/**
	 * Returns the animation speed in milliseconds of wait time per run.
	 * 
	 * @return The animation speed in milliseconds of wait time per update.
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * Sets the animation speed in milliseconds of wait time per run.
	 * 
	 * @param speed The animation speed in milliseconds of wait time per update.
	 * @throws IllegalArgumentException Thrown if the speed is smaller or equal
	 * 	to 0.
	 */
	public void setSpeed(int speed) {
		if (speed <= 0) throw new IllegalArgumentException("Speed must be larger than 0.");
		this.speed = speed;
	}

	/**
	 * Returns the maximum stepping size in pixels per update.
	 * 
	 * @return The maximum stepping size in pixels per update.
	 */
	public int getStep() {
		return step;
	}

	/**
	 * Sets the maximum stepping size in pixels per update. Set this to
	 * max integer to disable animations altogether.
	 * 
	 * @param step The maximum stepping size in pixels per update.
	 * @throws IllegalArgumentException Thrown if the size is smaller or equal
	 * 	to 0.
	 */
	public void setStep(int step) {
		if (step <= 0) throw new IllegalArgumentException("Stepping must be larger than 0.");
		this.step = step;
	}
	
	
	/**
	 * Recomputes the size of the graph. The size of the graph
	 * is the size that would be covered with the current layout
	 * of the graph. It is not the size that is covered! The
	 * size that is covered may be larger or smaller according
	 * to the position where nodes are inserted.
	 * 
	 * @return The size of the graph.
	 */
	protected Dimension computeGraphSize() {
		Dimension gs = new Dimension(0, 0);
		NodeList nodes = graph.nodes;
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.getNode(i);
			if (node.x + node.width > gs.width) gs.width = node.x + node.width;
			if (node.y + node.height > gs.height) gs.height = node.y + node.height;
		}
		if (nodes.size() > 0) {
			gs.height += NODE_SPACING;
			gs.width += NODE_SPACING * 2;			
		}
		return gs;
	}
	
	/**
	 * This method is called whenever the figure is moved or resized.
	 * This will recompute the offset of the graph.
	 */
	protected void center() {
		// retrieve the needed offset for the current graph size
		Dimension go = computeGraphOffset();
		// determine delta betwenn old and new size
		int xdiff = go.width - graphOffset.width;
		int ydiff = go.height - graphOffset.height;
		graphOffset = go;
		moveNodes(xdiff, ydiff);		
	}
	
	/**
	 * Computes the graph offset according to the current client
	 * size of the figure and returns it. The graph offset is the
	 * upper left point of the graph.
	 * 
	 * @return The new graph offset.
	 */
	protected Dimension computeGraphOffset() {
		Rectangle r = getBounds();
		int width = (r.width - r.x - graphSize.width) / 2 + NODE_SPACING;
		//if (width < 0) width = 0 ;
		int height = (r.height - r.y - graphSize.height) / 2;
		//if (height < 0) height = 0;
		return new Dimension(width, height);
	}
	
	/**
	 * Moves the nodes for the specified x and y values. The
	 * movement is immediate and it is relative to their 
	 * current position.
	 * 
	 * @param x The x value to move.
	 * @param y The y value to move.
	 */
	protected void moveNodes(int x, int y) {
		if (x == 0 && y == 0) return;
		// apply to all nodes
		NodeList nodes = graph.nodes;
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.getNode(i);
			Figure figure = (Figure)node.data;
			Rectangle source = figure.getBounds();
			figure.setBounds(new Rectangle(source.x + x, source.y + y, source.width, source.height));
		}
		revalidate();
	}
		
	/**
	 * This method performs the next step of the animation and determines
	 * whether the animation must continue or whether it has finished. If
	 * it has finished, it will not reschedule the animator thread. If it
	 * has not finished, it will just reschedule the animator thread which
	 * will then lead to another call of this method.
	 */
	protected void animate() {
		if (display.isDisposed()) return;
		boolean animate = false;
		NodeList nodes = graph.nodes;
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.getNode(i);
			Figure figure = (Figure)node.data;
			Node target = nodes.getNode(i);
			Rectangle source = figure.getBounds();
			int targetX = target.x + graphOffset.width;
			int targetY = target.y + graphOffset.height;
			int targetW = target.width;
			int targetH = target.height;
			int sourceX = source.x;
			int sourceY = source.y;
			int sourceW = source.width;
			int sourceH = source.height;
			if (sourceX != targetX) {
				animate = true;
				if (sourceX > targetX) sourceX -= Math.min(step, sourceX - targetX);
				else sourceX += Math.min(step, targetX - sourceX);
			}
			if (sourceY != targetY) {
				animate = true;
				if (sourceY > targetY) sourceY -= Math.min(step, sourceY - targetY);
				else sourceY += Math.min(step, targetY - sourceY);
			}
			if (sourceW != targetW) {
				animate = true;
				if (sourceW > targetW) sourceW -= Math.min(step, sourceW - targetW);
				else sourceW += Math.min(step, targetW - sourceW);
			}
			if (sourceH != targetH) {
				animate = true;
				if (sourceH > targetH) sourceH -= Math.min(step, sourceH - targetH);
				else sourceH += Math.min(step, targetH - sourceH);
			}
			figure.setBounds(new Rectangle(sourceX, sourceY, sourceW, sourceH));
		}
		if (animate) {
			revalidate();	
			display.timerExec(speed, animator);
		} else {
			animator = null;
		}
	}

}
