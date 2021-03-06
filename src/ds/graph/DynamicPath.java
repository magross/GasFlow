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
 * Path.java
 *
 */

package ds.graph;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * The <code>DynamicPath</code> class represents a dynamic path in a {@link Network}.
 * It extends the {@link StaticPath} by the possibility to save delay times
 * in nodes: Flow going through a network can (sometimes) wait in nodes, therefore
 * we consider a dynamic flow as a alternating sequence of delay times and edges.
 * In this class delay times get saved together with edges: The delay time
 * corresponding to an edge is the delay time in the start node of the edge, 
 * i.e. the delays are always directly before the corresponding edge.
 * The delay times are internally stored in a <code>ArrayList</code>.
 */
public class DynamicPath extends StaticPath{
    
    private ArrayList<Integer> delays;
    
    /**
     * Constructs a new <code>DynamicPath</code> without edges. 
     * Edges can be added with the corresponding methods.
     */
    public DynamicPath() {
        super();
        delays = new ArrayList<Integer>();
    }
    
    /**
     * Constructs a new <code>DynamicPath</code> with the given edges. 
     * The delays in the startnode of the edges are set to zero. 
     * They can be changed later by using <code>setDelay(Edge edge, int delay</code>.
     * The edges must be consistent,
     * i.e. the endnode of an edge must be equal to the startnode of the next edge
     * (if there follows one more).
     * If the edges are not consistent, an <code>IllegalArgumentException</code> 
     * is thrown.
     * @param edges the edges the path shall be contained of
     */
    public DynamicPath(Edge... edges) {
        this();
        for (Edge edge : edges) {
            this.addLastEdge(edge);
        }
    }
    
    /**
     * Constructs a new <code>DynamicPath</code> with the given edges. 
     * The delays in the startnode of the edges are set to zero. 
     * They can be changed later by using <code>setDelay(Edge edge, int delay</code>.
     * The edges must be consistent,
     * i.e. the endnode of an edge must be equal to the startnode of the next edge
     * (if there follows one more).
     * If the edges are not consistent, an <code>IllegalArgumentException</code> 
     * is thrown.
     * @param edges the edges the path shall be contained of
     */
    public DynamicPath(Iterable<Edge> edges) {
        this();
        for (Edge edge : edges) {
            this.addLastEdge(edge);
        }
    }
    
    /**
     * Extends the path by adding an edge at the end and sets the delay
     * in the startnode of <code>edge</code> to <code>delay</code>.
     * The edge must be consistent to the current last edge of the path,
     * i.e. i.e. the endnode of the current last edge must be
     * equal to the startnode of <code>edge</code>.
     * @param edge the edge to insert at the end of the path.
     * @param delay the delay in the startnode of <code>edge</code>
     * @return <code>true</code> if the insertion was successful,
     * <code>false</code> else.
     */
    public boolean addLastEdge(Edge edge, int delay) {
        boolean successful = super.addLastEdge(edge);
        if (successful)
            delays.add(delay);
        return successful;
    }
    
    /**
     * Extends the path by adding an edge at the start and sets the delay
     * in the startnode of <code>edge</code> to <code>delay</code>.
     * Until additional edges are added at the start, the delay
     * in this node will be the delay at the beginning of the <code>DynamicPath</code>,
     * i.e. the time before flow on this path would start leaving the first node.
     * The new edge must be consistent to the current first edge of the path,
     * i.e. i.e. the startnode of the current first edge must be
     * equal to the endnode of <code>edge</code>.
     * @param edge the edge to insert at the end of the path.
     * @return <code>true</code> if the insertion was successful,
     *         <code>false</code> else.
     */
    public boolean addFirstEdge(Edge edge, int delay){
        boolean successful = super.addFirstEdge(edge);
        if (successful)
            delays.add(0,delay);
        return successful;
    }
    
    /**
     * Extends the path by adding an edge at the end.
     * The edge must be consistent to the current last edge of the path,
     * i.e. i.e. the endnode of the current last edge must be
     * equal to the startnode of <code>edge</code>.
     * The delay in the startnode of <code>edge</code> is set to zero.
     * @param edge the edge to insert at the end of the path.
     * @return <code>true</code> if the insertion was successful,
     * <code>false</code> else.
     */
    @Override
    public boolean addLastEdge(Edge edge) {        
        return addLastEdge(edge, 0);
    }
    
    /**
     * Extends the path by adding an edge at the start.
     * The edge must be consistent to the current first edge of the path,
     * i.e. i.e. the startnode of the current first edge must be
     * equal to the endnode of <code>edge</code>.
     * The delay in the startnode of <code>edge</code> is set to zero.
     * @param edge the edge to insert at the end of the path.
     * @return <code>true</code> if the insertion was successful,
     *         <code>false</code> else.
     */
    @Override
    public boolean addFirstEdge(Edge edge){
        return addFirstEdge(edge,0);
    }

    /**
     * Returns an iterator for the edges of this path.
     * With the iterator one can iterate comfortable through all the
     * edges of the path.
     * @return an iterator for the edges of this path.
     */
    @Override
    public Iterator<Edge> iterator() {
        return super.iterator();
    }    
    
    /**
     * Returns the delay in the startnode of an edge.
     * If the edge is not present in the path, -1 will be returned.
     * @param edge the delay in the startnode of this edge is returned.
     * @return the delay in the startnode of <code>edge</code>.
     */
    public int getDelay(Edge edge) {
        if (edges.contains(edge)) {
            int index = edges.indexOf(edge);
            if (delays.size() > index) {
                return delays.get(index);
            } else {
                return 0;
            }
        } else {
            return (-1);
        }
    }
    
    /**
     * Sets the delay in the startnode of edge <code>edge</code>.
     * If the edge is not present in the path, nothing happens.
     * @param edge the delay of the startnode of this edge will be set.
     * @param delay the delay to be set.
     */
    public void setDelay(Edge edge, int delay) {
        if (edges.contains(edge)) {
            while (edges.indexOf(edge) >= delays.size()) {
                delays.add(0);
            }
            delays.set(edges.indexOf(edge), delay);
        }
    }
    
    /**
     * Shortens the path by removing the first edge. The delay time in the
     * startnode of this edge will also be deleted.
     * If the path is empty, nothing happens.
     * @return <code>false</code> if there was no element to be removed,
     *         <code>true</code> else.
     */    @Override
    public boolean removeFirstEdge(){
        boolean successful = super.removeFirstEdge();
        if (successful){
            delays.remove(0);
            return true;
        } else
            return false;
    }

    /**
     * Shortens the path by removing the last edge. The delay time in the
     * startnode of this edge will also be deleted.
     * If the path is empty, nothing happens.
     * @return <code>false</code> if there was no element to be removed,
     *         <code>true</code> else.
     */
    @Override
    public boolean removeLastEdge() {
        boolean successful = super.removeLastEdge();        
        if (successful && delays.size() >= edges.size()){
            delays.remove(delays.size()-1);
            return true;
        } else
            return false;        
    }    

    /**
     * Returns a String containing tuples of delay times and edges.
     * The delay time in a tuple belongs to the startnode of the
     * edge in the tuple.
     * An edge e=(a,b) will be represented by (a,b) in the string. 
     * @return a String containing tuples of delay times and edges.
     */
    public String nodesAndDelaysToString() {
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<edges.size(); i++) {
            builder.append("("+delays.get(i) + ","+edges.get(i).nodesToString()+"),");
        }
        if (length() > 0) builder.deleteCharAt(builder.length()-1);
        return builder.toString();
    } 
    
    /** 
     * Returns a String containing tuples of delay times and edge IDs.
     * The delay time in a tuple belongs to the startnode of the
     * edge in the tuple.
     * @return a String containing tuples of delay times and edge IDs.
     */    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<edges.size(); i++) {
            builder.append("("+delays.get(i) + ","+edges.get(i)+"),");
        }
        if (length() > 0) builder.deleteCharAt(builder.length()-1);
        return builder.toString();
    }

    /**
     * Clones this path by cloning all edges and delays
     * and creating a new <code>DynamicPath</code> object with the clone.
     * @return a <code>Dynamic</code> object with clones of the edges and 
     * delays of this object.
     */
    @Override
    public DynamicPath clone(){
        DynamicPath dynamicPath = new DynamicPath();        
        Iterator<Edge> it = edges.iterator();
        for (int i = 0; i < edges.size(); i++){
            Edge e = (Edge)(it.next()).clone();
            if (delays.size() > i)
                dynamicPath.addLastEdge(e,delays.get(i));
            else 
                dynamicPath.addLastEdge(e,0);
        }
        return dynamicPath;
    }
    
    
    /**
     * Returns the hash code of this dynamic path. 
     * The hash code is calculated by computing the arithmetic mean
     * of the delays together with the hash codes of all edges.
     * Therefore the hash code is equal for dynamic path equal according to
     * the <code>equals</code>-method, but not necessarily different
     * for dynamic path different according to the <code>equals</code>-method.
     * If hashing of dynamic path is heavily used,
     * the implementation of this method should be reconsidered.
     * @return the hash code of this dynamic path.
     */
    @Override
    public int hashCode(){
        int h = 0;
        for (Integer i : delays) {
            h += Math.floor(i / (delays.size()+edges.size()));
        }
        for (Edge e : edges){
            h += Math.floor(e.hashCode() / (delays.size()+edges.size()));
        }
        return h;
    }
    
    /**
     * Returns whether an object is equal to this dynamic path.
     * The result is true if and only if the argument is not null and is a 
     * <code>DynamicPath</code> object having a sequence of edges that is
     * equal to this path's sequence of edges (i.e. all edges must have
     * the same IDs) and all the delays are equal.
     * @param o object to compare.
     * @return <code>true</code> if the given object represents a
     * <code>DynamicPath</code> equivalent to this node, <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object o){
        if (o == null || !(o instanceof DynamicPath)) {
            return false;
        } else {
            DynamicPath p = (DynamicPath) o;
            if (p.getEdges().size() != edges.size())
                return false;
            Iterator<Edge> it = edges.iterator();
            for (Edge e: p.getEdges()){
                if (! e.equals(it.next()) || getDelay(e) != p.getDelay(e))
                    return false;
            }
            return true;
        }
    }
    
}
