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
 * EdgeBasedFlowOverTime.java
 *
 */

package ds.graph.flow;

import ds.graph.Edge;
import ds.graph.IdentifiableCollection;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.IntegerIntegerMapping;
import ds.graph.Network;

/**
 * The <code>EdgeBasedFlowOverTime</code> class represents an edge based representation
 * of a dynamic flow. For each edge a <code>EdgeBasedFlowOverTime</code> stores an
 * {@link IntegerIntegerMapping} representing the value of the flow depending
 * on the time. The mapping between edges and flow functions is internally
 * stored as an {@link IdentifiableObjectMapping} from {@link Edge} objects to
 * {@link IntegerIntegerMapping} objects.
 */
public class EdgeBasedFlowOverTime {   
    
    /**
     * The mapping from edges to flow functions depending on the time.
     */
    private IdentifiableObjectMapping<Edge,IntegerIntegerMapping> map;
    
    /**
     * Creates a new <code>EdgeBasedFlowOverTime</code> for the network <code>network</code>
     * where the flow on all edges is zero all the time.
     * The flow functions can later be set by 
     * <code>set(Edge edge, IntegerIntegerMapping flowFunction)</code>.
     * @param network the network for which the empty flow shall be created.
     */
    public EdgeBasedFlowOverTime(Network network) {
        map = new IdentifiableObjectMapping<Edge, IntegerIntegerMapping>(network.getEdgeCapacity(), IntegerIntegerMapping.class);
        IdentifiableCollection<Edge> edges = network.edges();
        for (Edge edge : edges){
            map.set(edge, new IntegerIntegerMapping());
        }
    }
    
    /**
     * Creates a new <code>EdgeBasedFlowOverTime</code> object where the edges are mapped
     * to flow functions as given in <code>flowOnEdges</code>.
     * @param flowOnEdges mapping of edges to flow functions that shall be 
     *        used in this <code>EdgeBasedFlowOverTime</code>
     */
    public EdgeBasedFlowOverTime(IdentifiableObjectMapping<Edge,IntegerIntegerMapping> flowOnEdges){
        map = flowOnEdges;
    }
/*
    public EdgeBasedFlowOverTime(IdentifiableObjectMapping<Edge, IntegerIntegerArrayMapping> flow) {
    }*/
    
    /**
     * Gets the <code>IntegerIntegerMapping</code> that represents the flow
     * function on the edge <code>edge</code>. If the flow function is not
     * stored for this edge, null is returned.
     * @param edge
     * @return the <code>IntegerIntegerMapping</code> that represents the flow
     * function on the edge <code>edge</code>
     */
    public IntegerIntegerMapping get(Edge edge) {
        if (map.isDefinedFor(edge))
            return map.get(edge);
        else
            return null;
    }   
    
    /**
     * Sets the flow function of the edge <code>edge</code> to the
     * <code>IntegerIntegerMapping</code> <code>flowFunction</code>.
     * @param edge the edge which flow function shall be set
     * @param flowFunction the flow function that shall be set for <code>edge</code>.
     */
    public void set(Edge edge, IntegerIntegerMapping flowFunction){
        map.set(edge, flowFunction);
    }
    
    /**
     * Returns whether the object <code>o</code> is equal to this
     * <code>EdgeBasedFlowOverTime</code> object. 
     * The result is true if and only if the argument is not null and is a 
     * <code>EdgeBasedFlowOverTime</code> object having which flow function is equal
     * of the one of this <code>EdgeBasedFlowOverTime</code>.
     * @param o  o object to compare.
     * @return <code>true</code> if the given object represents a
     * <code>EdgeBasedFlowOverTime</code> equivalent to this node, <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof EdgeBasedFlowOverTime) {
            EdgeBasedFlowOverTime f = (EdgeBasedFlowOverTime) o;            
            return map.equals(f.map);
        } else {
            return false;
        }
    }
    
    /**
     * Returns a hash code for this <code>EdgeBasedFlowOverTime</code> object.
     * The hash code is equal to the hashCode of the underlying 
     * <code>IdentifiableObjectMapping</code>.
     * @return the hash code of this object
     */
    @Override
    public int hashCode(){
        return map.hashCode();
    }
    
    /**
     * Returns a String describing the flow. 
     * For a description see the <code>toString()</code> method of
     * {@link IdentifiableObjectMapping}.
     * @return a String describing the flow, see the <code>toString()</code> method of
     * {@link IdentifiableObjectMapping}.
     */
    @Override
    public String toString() {
        return map.toString();
    }
    
    /**
     * Clones this <code>EdgeBasedFlowOverTime</code> by cloning the underlying 
     * <code>IdentifiableObjectMapping</code>.
     * @return a clone of this <code>EdgeBasedFlowOverTime</code> object.
     */
    @Override
    public EdgeBasedFlowOverTime clone(){
        return new EdgeBasedFlowOverTime(map.clone());
    }
    
}
