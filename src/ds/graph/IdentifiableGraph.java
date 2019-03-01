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
 * Graph.java
 *
 */
package ds.graph;

/**
 * The <code>Graph</code> interface provides a common interface for 
 * implementations of graphs.
 */
public interface IdentifiableGraph extends Graph<Node, Edge> {

    /**
     * Checks whether the graph is directed. 
     * @return <code>true</code> if the graph is directed, <code>false</code>
     * otherwise.
     */
    boolean isDirected();

    /**
     * Returns the edge with the specified id or <code>null</code> if the graph
     * does not contain an edge with the specified id.
     * @param id the id of the edge to be returned.
     * @return the edge with the specified id or <code>null</code> if the graph
     * does not contain an edge with the specified id.
     */
    Edge getEdge(int id);

    /**
     * Returns an {@link IdentifiableCollection} containing all the edges of
     * this graph.
     * @return an {@link IdentifiableCollection} containing all the edges of
     * this graph.
     */
    IdentifiableCollection<Edge> edges();

    /**
     * Returns an {@link IdentifiableCollection} containing all the nodes of
     * this graph.
     * @return an {@link IdentifiableCollection} containing all the nodes of
     * this graph.
     */
    IdentifiableCollection<Node> nodes();

    /**
     * Returns an {@link IdentifiableCollection} containing all the edges incident to
     * the specified node.
     * @return an {@link IdentifiableCollection} containing all the edges incident to
     * the specified node.
     */
    IdentifiableCollection<Edge> incidentEdges(Node node);

    /**
     * Returns an {@link IdentifiableCollection} containing all the edges ending at
     * the specified node. This operation is only defined for directed graphs.
     * @return an {@link IdentifiableCollection} containing all the edges ending at
     * the specified node.
     * @exception UnsupportedOperationException if the graph is not directed.
     */
    IdentifiableCollection<Edge> incomingEdges(Node node);

    /**
     * Returns an {@link IdentifiableCollection} containing all the edges starting at
     * the specified node. This operation is only defined for directed graphs.
     * @return an {@link IdentifiableCollection} containing all the edges starting at
     * the specified node.
     * @exception UnsupportedOperationException if the graph is not directed.
     */
    IdentifiableCollection<Edge> outgoingEdges(Node node);

    /**
     * Returns an {@link IdentifiableCollection} containing all the nodes adjacent to
     * the specified node.
     * @return an {@link IdentifiableCollection} containing all the nodes adjacent to
     * the specified node.
     */
    IdentifiableCollection<Node> adjacentNodes(Node node);

    /**
     * Returns an {@link IdentifiableCollection} containing all the nodes that are
     * incident to an edge ending at the specified node. This operation is only
     * defined for directed graphs.
     * @return an {@link IdentifiableCollection} containing all the nodes that are
     * incident to an edge ending at the specified node.
     * @exception UnsupportedOperationException if the graph is not directed.
     */
    IdentifiableCollection<Node> predecessorNodes(Node node);

    /**
     * Returns an {@link IdentifiableCollection} containing all the nodes that are
     * incident to an edge starting at the specified node. This operation is 
     * only defined for directed graphs.
     * @return an {@link IdentifiableCollection} containing all the nodes that are
     * incident to an edge starting at the specified node.
     * @exception UnsupportedOperationException if the graph is not directed.
     */
    IdentifiableCollection<Node> successorNodes(Node node);

    /**
     * Checks whether the specified edge is contained in this graph.
     * @param edge the edge to be checked.
     * @return <code>true</code> if the edge is contained in this graph, 
     * <code>false</code> otherwise.
     */
    boolean contains(Edge edge);

    /**
     * Checks whether the specified node is contained in this graph.
     * @param node the node to be checked.
     * @return <code>true</code> if the node is contained in this graph, 
     * <code>false</code> otherwise.
     */
    boolean contains(Node node);

    /**
     * Returns an edge starting at <code>start</code> and ending at 
     * <code>end</code>. In case of undirected graphs no distinction between
     * <code>start</code> and <code>end</code> is made (i.e. in this case an
     * edge incident to both <code>start</code> and <code>end</code> is
     * returned). If no such edge exists, <code>null</code> is returned. 
     * @param start the start node of the edge to be returned.
     * @param end the end node of the edge to be returned.
     * @return an edge starting at <code>start</code> and ending at 
     * <code>end</code>.
     */
    Edge getFirstEdge(Node start, Node end);

    /**
     * Adds the specified node to the graph.
     * @param node the node to be added to the graph.
     */    
    public void setNode(Node node);
    
    /**
     * Adds the specified edge to the graph.
     * @param edge the edge to be added to the graph.
     */
    public void setEdge(Edge edge);
    	
    /**
     * Returns an {@link IdentifiableCollection} containing all edges starting at 
     * <code>start</code> and ending at 
     * <code>end</code>. In case of undirected graphs no distinction between
     * <code>start</code> and <code>end</code> is made (i.e. in this case all
     * edges incident to both <code>start</code> and <code>end</code> are
     * returned). If no such edge exists, an empty list is returned. 
     * @param start the start node of the edges to be returned.
     * @param end the end node of the edges to be returned.
     * @return an {@link IdentifiableCollection} containing all edges starting at 
     * <code>start</code> and ending at <code>end</code>.
     */    
    IdentifiableCollection<Edge> getEdges(Node start, Node end);

    /**
     * Returns the node with the specified id or <code>null</code> if the graph
     * does not contain a node with the specified id.
     * @param id the id of the node to be returned.
     * @return the node with the specified id or <code>null</code> if the graph
     * does not contain a node with the specified id.
     */
    Node getNode(int id);
    
    /**
     * Checks whether an path between <code>start</code> and <code>end</code>
     * exists. This path must be directed, should the graph be directed.
     * @param start the start node of the path to be checked.
     * @param end the end node of the path to be checked.
     * @return <code>true</code> if an (directed) path between 
     * <code>start</code> and <code>end</code> exists, <code>false</code>
     * otherwise.
     */
    boolean existsPath(Node start, Node end);

    
    String deepToString();

    
    /**
     * Returns the network as a <code>Network</code> object, i.e. as a static graph.
     * @return the network as a <code>Network</code> object.
     */
    public Network getAsStaticNetwork();

}
