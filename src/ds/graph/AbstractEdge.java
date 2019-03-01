/**
 * DEdge.java
 *
 */

package ds.graph;

/**
 *
 * @author Martin Groß
 */
public class AbstractEdge<N> {

    /**
     * The start node of this edge.
     */
    private N start;
    /**
     * The end node of this edge.
     */
    private N end;

    /**
     * Constructs a new <code>Edge</code> object with a given given start-
     * and end node and given ID. Runtime O(1).
     * @param id the ID of the new edge.
     * @param start the start node of the new  edge.
     * @param end the end node of the new  edge.
     * @exception NullPointerException if <code>start</code> or
     * <code>end</code> is null.
     */
    public AbstractEdge(N start, N end) {
        if (start == null || end == null) {
            throw new NullPointerException("Start or end node is null.");
        }
        this.start = start;
        this.end = end;
    }

    public boolean isLoop() {
        return start.equals(end);
    }

    public boolean isAdjacentTo(AbstractEdge edge) {
        return start.equals(edge.start) || start.equals(edge.end) || end.equals(edge.end()) || end.equals(edge.start);
    }

    public boolean isIncidentTo(N node) {
        return start.equals(node) || end.equals(node);
    }

    public boolean isParallelTo(AbstractEdge edge) {
        return start.equals(edge.start) && end.equals(edge.end) || start.equals(edge.end) && end.equals(edge.start);
    }

    /**
     * Returns the start node of this edge. Runtime O(1).
     * @return the start node of this edge.
     */
    public N start() {
        return start;
    }

    void setStart(N start) {
        this.start = start;
    } 
    
    void setEnd(N end) {
        this.end = end;
    }
    
    /**
     * Returns the end node of this edge. Runtime O(1).
     * @return the end node of this edge.
     */
    public N end() {
        return end;
    }

    /**
     * Given a node <code>node</code>, this method returns the other node
     * (the node that is not <code>node</code>).
     * @param node the node this method shall give the opposite of.
     * @return the opposite node to <code>node</code>.
     */
    public N opposite(N node) {
        if (node.equals(start) && !node.equals(end)) {
            return end;
        } else if (!node.equals(start) && node.equals(end)) {
            return start;
        } else if (node.equals(start) && node.equals(end)) {
            throw new IllegalArgumentException("Loops have no opposite node.");
        } else {
            throw new IllegalArgumentException("node=" + node);
        }
    }

    /**
     * Returns a String containing the IDs of start- and end node of this edge.
     * @return a String containing the IDs of start- and end node of this edge.
     */
    public String nodesToString() {
        return String.format("(%1$s,%2$s)", start.toString(), end.toString());
    }

    /**
     * Returns a String containing the ID of this edge.
     * @return a String containing the ID of this edge.
     */
    @Override
    public String toString() {
        return nodesToString();
    }

    /**
     * Returns a new <code>Edge</code> with the same ID as this edge.
     * @return a clone of this edge (a edge with the same edgeID, not the same object).
     */
    @Override
    public AbstractEdge clone() {
        return new AbstractEdge(this.start, this.end);
    }
}
