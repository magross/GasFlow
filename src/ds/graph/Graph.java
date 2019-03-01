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
public interface Graph<N,E extends AbstractEdge<N>> {

    /**
     * Returns an {@link IdentifiableCollection} containing all the edges of
     * this graph.
     * @return an {@link IdentifiableCollection} containing all the edges of
     * this graph.
     */
    Iterable<E> edges();

    /**
     * Returns an {@link IdentifiableCollection} containing all the nodes of
     * this graph.
     * @return an {@link IdentifiableCollection} containing all the nodes of
     * this graph.
     */
    Iterable<N> nodes();

    /**
     * Returns the number of edges in this graph.
     * @return the number of edges in this graph.
     */
    int numberOfEdges();

    /**
     * Returns the number of nodes in this graph.
     * @return the number of nodes in this graph.
     */
    int numberOfNodes();

    /**
     * Returns an {@link IdentifiableCollection} containing all the edges incident to
     * the specified node.
     * @return an {@link IdentifiableCollection} containing all the edges incident to
     * the specified node.
     */
    Iterable<E> incidentEdges(N node);

    /**
     * Returns an {@link IdentifiableCollection} containing all the edges ending at
     * the specified node. This operation is only defined for directed graphs.
     * @return an {@link IdentifiableCollection} containing all the edges ending at
     * the specified node.
     * @exception UnsupportedOperationException if the graph is not directed.
     */
    Iterable<E> incomingEdges(N node);

    /**
     * Returns an {@link IdentifiableCollection} containing all the edges starting at
     * the specified node. This operation is only defined for directed graphs.
     * @return an {@link IdentifiableCollection} containing all the edges starting at
     * the specified node.
     * @exception UnsupportedOperationException if the graph is not directed.
     */
    Iterable<E> outgoingEdges(N node);

    /**
     * Returns an {@link IdentifiableCollection} containing all the nodes adjacent to
     * the specified node.
     * @return an {@link IdentifiableCollection} containing all the nodes adjacent to
     * the specified node.
     */
    Iterable<N> adjacentNodes(N node);

    /**
     * Returns an {@link IdentifiableCollection} containing all the nodes that are
     * incident to an edge ending at the specified node. This operation is only
     * defined for directed graphs.
     * @return an {@link IdentifiableCollection} containing all the nodes that are
     * incident to an edge ending at the specified node.
     * @exception UnsupportedOperationException if the graph is not directed.
     */
    Iterable<N> predecessorNodes(N node);

    /**
     * Returns an {@link IdentifiableCollection} containing all the nodes that are
     * incident to an edge starting at the specified node. This operation is
     * only defined for directed graphs.
     * @return an {@link IdentifiableCollection} containing all the nodes that are
     * incident to an edge starting at the specified node.
     * @exception UnsupportedOperationException if the graph is not directed.
     */
    Iterable<N> successorNodes(N node);

    /**
     * Returns the degree of the specified node, i.e. the number of edges
     * incident to it.
     * @param node the node for which the degree is to be returned.
     * @return the degree of the specified node.
     */
    int degree(N node);

    /**
     * Returns the indegree of the specified node, i.e. the number of edges
     * ending at it. The indegree is not defined for undirected graphs.
     * @param node the node for which the indegree is to be returned.
     * @return the indegree of the specified node.
     * @exception UnsupportedOperationException if the graph is not directed.
     */
    int indegree(N node);

    /**
     * Returns the outdegree of the specified node, i.e. the number of edges
     * starting at it. The outdegree is not defined for undirected graphs.
     * @param node the node for which the outdegree is to be returned.
     * @return the outdegree of the specified node.
     * @exception UnsupportedOperationException if the graph is not directed.
     */
    int outdegree(N node);

    /**
     * Checks whether the specified edge is contained in this graph.
     * @param edge the edge to be checked.
     * @return <code>true</code> if the edge is contained in this graph,
     * <code>false</code> otherwise.
     */
    boolean containsEdge(E edge);

    /**
     * Checks whether the specified node is contained in this graph.
     * @param node the node to be checked.
     * @return <code>true</code> if the node is contained in this graph,
     * <code>false</code> otherwise.
     */
    boolean containsNode(N node);

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
    E getFirstEdge(N start, N end);

    /**
     * Adds the specified node to the graph.
     * @param node the node to be added to the graph.
     */
    public void addNode(N node);

    /**
     * Adds the specified edge to the graph.
     * @param edge the edge to be added to the graph.
     */
    public void addEdge(E edge);

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
    Iterable<E> getEdges(N start, N end);
}
