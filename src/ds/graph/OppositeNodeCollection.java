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
 * OppositeNodeCollection.java
 *
 */

package ds.graph;

import java.util.Iterator;

/**
 * A utility class for obtaining the set of adjacent nodes from a set of 
 * incident edges in O(1) time.
 */    
public class OppositeNodeCollection implements IdentifiableCollection<Node> {

    /**
     * The base node for which the adjacent nodes are to be returned.
     */
    protected Node node;

    /**
     * The edges incident to <code>node</code>.
     */
    protected IdentifiableCollection<Edge> edges;

    /**
     * Creates a new <code>OppositeNodeCollection</code> containing all 
     * nodes that are opposite to the specified node with regard to the
     * specified collection of edges. Runtime O(1).
     * @param node the base node for which the adjacent nodes are to be 
     * returned.
     * @param edges the edges incident to <code>node</code>.
     */
    protected OppositeNodeCollection(Node node, IdentifiableCollection<Edge> edges) {
        this.node = node;
        this.edges = edges;
    }

    /**
     * This operation is not supported by this collection.
     * @exception UnsupportedOperationException if this method is called.
     */
    public boolean add(Node element) {
        throw new UnsupportedOperationException(Localization.getInstance (
		).getString ("ds.graph.NotSupportedException"));
    }

    /**
     * This operation is not supported by this collection.
     * @exception UnsupportedOperationException if this method is called.
     */
    public void remove(Node element) {
        throw new UnsupportedOperationException(Localization.getInstance (
		).getString ("ds.graph.NotSupportedException"));
    }

    /**
     * This operation is not supported by this collection.
     * @exception UnsupportedOperationException if this method is called.
     */
    public Node removeLast() {
        throw new UnsupportedOperationException(Localization.getInstance (
		).getString ("ds.graph.NotSupportedException"));
    }

    /**
     * Returns whether the specified node is contained in this collection.
     * Runtime O(degree(node)).
     * @param element the node to be checked.
     * @return <code>true</code> if the specified node is contained in this
     * collection, false <code>otherwise</code>.
     */
    public boolean contains(Node element) {
        for (Node n : this) {
            if (n.equals(element)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether this <code>IdentifiableCollection</code> is empty.
     * Runtime O(1).
     * @return whether this <code>IdentifiableCollection</code> is empty.
     */
    public boolean empty() {
        return edges.empty();
    }

    /**
     * Returns the number of nodes adjacent to the base node. Runtime 
     * O(degree(node)).
     * @return the number of nodes adjacent to the base node.
     */
    public int size() {
        int sum = 0;
        for (Node n : this) {
            sum++;
        }
        return sum;
    }

   /**
     * This operation is not supported by this collection.
     * @exception UnsupportedOperationException if this method is called.
     */        
    public Node get(int id) {
        throw new UnsupportedOperationException(Localization.getInstance (
		).getString ("ds.graph.NotSupportedException"));
    }

    /**
     * Returns the node opposite to the base node with regard to the first
     * edge. Runtime O(1).
     * @return the node opposite to the base node with regard to the first
     * edge.
     */
    public Node first() {
        return edges.first().opposite(node);
    }

    /**
     * Returns the node opposite to the base node with regard to the last
     * edge. Runtime O(1).
     * @return the node opposite to the base node with regard to the last
     * edge.
     */        
    public Node last() {
        return edges.last().opposite(node);
    }

    /**
     * Returns the predecessor of the specified node with regard to the 
     * order implements imposed by this collections iterator. If such an 
     * element does not exists, <code>null</code> is returned.
     * Runtime O(degree(node)).
     * @param element the node for which the predecessor is to be returned.
     * @return the predecessor of the specified node.
     */
    public Node predecessor(Node element) {
        Node last = null;
        for (Node n : this) {
            if (n.equals(element)) break;
            last = n;
        }
        return last;
    }

    /**
     * Returns the successor of the specified node with regard to the 
     * order implements imposed by this collections iterator.  If such an 
     * element does not exists, <code>null</code> is returned.
 * Runtime O(degree(node)).
     * @param element the node for which the successor is to be returned.
     * @return the successor of the specified node.
     */        
    public Node successor(Node element) {
        Node last = null;
        for (Node n : this) {
            if (last != null && last.equals(element)) return n;
        }
        return null;
    }

    /**
     * Returns an iterator iterating over this set of nodes adjacent to the
     * base nodes. The returned iterator does not return the same node twice,
     * even if the graph has multiple edges. Runtime O(1).
     * @return an iterator iterating over this set of nodes adjacent to the
     * base nodes.
     */
    public Iterator<Node> iterator() {
        return new OppositeNodeIterator(node, edges.iterator());
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        boolean empty = true;
        for (Node n : this) {
            empty = false;
            builder.append(n + ", ");
        }
        if (!empty) builder.delete(builder.length()-2,builder.length());
        builder.append("]");
        return builder.toString();
    }
}
