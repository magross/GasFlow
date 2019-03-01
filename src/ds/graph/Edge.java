/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * Edge.java
 *
 */
package ds.graph;

/**
 * The <code>Edge</code> class represents a edge in a graph.
 * An edge is identifiable, e.g. the class implements the Interface 
 * {@link Identifiable}.
 * This means that every edge has an ID that can be used for storing edges
 * in for example {@link ArraySet}s.
 * The ID must be set at the creation of a new edge.
 * 
 * A edge consists of two nodes of the Type {@link Node}: 
 * a startnode and endnode. Thus edges are by default directed.
 */
public class Edge extends AbstractEdge<Node> implements Identifiable {

    /**
     * The ID of this edge.
     */
    /* Don't rename this field to "id" because XStream will mess up the attribute names
     * in the XML file as it uses the "id" attribute itself. Even using @XStreamAlias("edgeID")
     * here, hasn't solved the problem. */
    private int edgeID;

    /**
     * Constructs a new <code>Edge</code> object with a given given start-
     * and endnode and given ID. Runtime O(1).
     * @param id the ID of the new edge.
     * @param start the startnode of the new  edge.
     * @param end the endnode of the new  edge.
     * @exception NullPointerException if <code>start</code> or 
     * <code>end</code> is null.
     */
    public Edge(int id, Node start, Node end) {
        super(start, end);
        this.edgeID = id;
    }

    /**
     * Returns the ID of this edge. Runtime O(1).
     * @return the ID of this edge.
     */
    @Override
    public int id() {
        return edgeID;
    }

    /**
     * Returns a String containing the ID of this edge.
     * @return a String containing the ID of this edge.
     */
    @Override
    public String toString() {
        return String.format("%1$s = %2$s", edgeID, nodesToString());
    }

    /**
     * Returns the hash code of this edge.
     * The hash code is identical to the ID of this edge.
     * @return the hash code of this edge.
     */
    @Override
    public int hashCode() {
        return this.edgeID;
    }

    /**
     * Returns whether an object is equal to this edge.
     * The result is true if and only if the argument is not null and is an 
     * <code>Edge></code> object having the same ID as this edge.
     * @param o the object to compare.
     * @return <code>true</code> if the given object represents an
     * <code>Edge</code> equivalent to this edge, <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Edge)) {
            return false;
        } else {
            Edge e = (Edge) o;
            return (e.id() == this.edgeID);
        }
    }

    /**
     * Returns a new <code>Edge</code> with the same ID as this edge.
     * @return a clone of this edge (a edge with the same edgeID, not the same object).
     */
    @Override
    public Edge clone() {
        return new Edge(this.edgeID, this.start(), this.end());
    }
}