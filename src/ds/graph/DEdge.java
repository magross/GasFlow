/**
 * DEdge.java
 *
 */

package ds.graph;

/**
 *
 * @author Martin Groß
 */
public class DEdge extends AbstractEdge<DNode> {

    public DEdge(DNode start, DNode end) {
        super(start, end);
    }

    public static DEdge createEdge(DNode v, DNode w) {
        return new DEdge(v,w);
    }
}
