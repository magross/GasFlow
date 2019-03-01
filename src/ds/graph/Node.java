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
 * Node.java
 *
 */

package ds.graph;

/**
 * The <code>Node</code> class represents a node in a graph. A node is 
 * identifiable, e.g. the class implements the Interface 
 * {@link Identifiable}. 
 * This means that every node has a ID that can be used for storing nodes in
 * for example {@link ArraySet}s. 
 * The ID must be set at creation of new nodes.
 */
public class Node implements Identifiable {
    
    /**
     * The ID of this node. Must be set at creation of the node.
     */ 
    private int nodeID;
     
    /**
     * Constructs a new <code>Node</code> object with a given ID. Runtime O(1).
     * @param id the ID of the new node.
     */
    public Node(int id) {
        this.nodeID = id;
    }
    
    /**
     * Returns the ID of this node. Runtime O(1).
     * @return the ID of this node.
     */
    @Override
    public int id() {
        return nodeID;
    }
    
    /**
     * Returns the ID of this node as a string.
     * @return a String containing the ID of this node.
     */
    @Override
    public String toString(){
        return String.valueOf(nodeID);
    }
    
    /**
     * Returns a new <code>Node</code> with the same ID as this node.
     * @return a clone of this node (a node with the same nodeID, not the same object).
     */
    @Override
    public Node clone(){
        return new Node(this.nodeID);
    }
        
    /**
     * Returns the hash code of this node. 
     * The hash code is identical to the ID of this node.
     * @return the hash code of this node.
     */
    @Override
    public int hashCode(){
        return nodeID;
    }    

    /**
     * Shifts the node ID by the given offset. This is sometimes necessary when
     * merging graphs.
     * @param offset the offset by which to shift the node.
     */
    public void shiftID(int offset) {
        nodeID += offset;
    }

    /**
     * Returns whether an object is equal to this node.
     * The result is true if and only if the argument is not null and is a 
     * <code>Node</code> object having the same ID as this node.
     * @param o object to compare.
     * @return <code>true</code> if the given object represents a
     * <code>Node</code> equivalent to this node, <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object o){
        if (o == null || !(o instanceof Node)) {
            return false;
        } else {
            Node n = (Node)o;
            return n.id() == this.nodeID;
        }
    }
    
}
