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
 * DynamicGraph.java
 *
 */
package ds.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.BiFunction;

public class DynamicNetwork<N, E extends AbstractEdge<N>> implements Graph<N, E> {

    //final Logger LOGGER;
    protected Collection<N> nodes;
    protected Collection<E> edges;
    protected transient Map<N, LinkedList<E>> incomingEdges;
    protected transient Map<N, LinkedList<E>> outgoingEdges;
    protected BiFunction<N, N, E> edgeFactory;

    protected boolean allowLoops = false;

    public DynamicNetwork(BiFunction<N, N, E> edgeFactory) {
        nodes = new LinkedHashSet<>();
        edges = new LinkedHashSet<>();
        incomingEdges = new HashMap<>();
        outgoingEdges = new HashMap<>();
        this.edgeFactory = edgeFactory;
    }

    public void addEdge(E edge) {
        if (edge.start() == null || edge.end() == null) {
            throw new IllegalArgumentException("Start or end node is null.");
        }
        if (edge.start() == edge.end() && !allowLoops) {
            throw new IllegalArgumentException("Loops are not allowed.");
        }
        edges.add(edge);
        incomingEdges.get(edge.end()).add(edge);
        outgoingEdges.get(edge.start()).add(edge);
    }

    public E createEdge(N from, N to) {
        E edge = edgeFactory.apply(from, to);
        addEdge(edge);
        return edge;
    }

    public void addEdges(Iterable<E> edges) {
        for (E edge : edges) {
            addEdge(edge);
        }
    }

    public void addGraph(Graph g) {
        addNodes(g.nodes());
        addEdges(g.edges());
    }

    public void addNode(N node) {
        nodes.add(node);
        incomingEdges.put(node, new LinkedList<>());
        outgoingEdges.put(node, new LinkedList<>());
    }

    public void addNodes(Iterable<N> nodes) {
        for (N node : nodes) {
            addNode(node);
        }
    }

    @Override
    public boolean containsEdge(E edge) {
        return edges.contains(edge);
    }

    @Override
    public boolean containsNode(N node) {
        return incomingEdges.containsKey(node);
    }

    public int degree(N node) {
        return indegree(node) + outdegree(node);
    }

    ////
    public Collection<E> edges() {
        return edges;
    }

    public E getFirstEdge(N source, N target) {
        for (E edge : outgoingEdges.get(source)) {
            if (edge.start().equals(source) && edge.end().equals(target)) {
                return edge;
            }
        }
        return null;
    }

    @Override
    public List<E> getEdges(N source, N target) {
        if (!containsNode(source) || !containsNode(target)) {
            throw new IllegalArgumentException("Both nodes need to be contained in the network.");
        }
        //return outgoingEdges.get(source).stream().filter(e -> e.end().equals(target)).collect(Collectors.toList());
        List<E> result = new LinkedList<>();
        for (E edge : outgoingEdges.get(source)) {
            if (edge.end().equals(target)) {
                result.add(edge);
            }
        }
        return result;
    }

    @Override
    public LinkedList<E> incidentEdges(N node) {
        LinkedList<E> incidentEdges = new LinkedList<>();
        incidentEdges.addAll(incomingEdges(node));
        incidentEdges.addAll(outgoingEdges(node));
        return incidentEdges;
    }

    @Override
    public LinkedList<E> incomingEdges(N node) {
        return incomingEdges.get(node);
    }

    @Override
    public int indegree(N node) {
        return incomingEdges(node).size();
    }

    /**
     * Merges node <code>w</code> with <code>v</code>. All incoming and outgoing
     * edges of <code>w</code> are rerouted to <code>v</code> and <code>w</code>
     * is removed in the process.
     *
     * @param v a node to be merged.
     * @param w a node to be merged.
     */
    public void merge(N v, N w) {
        while (!incomingEdges(w).isEmpty()) {
            E edge = incomingEdges(w).poll();
            if (!allowLoops && edge.start().equals(v)) {
                removeEdge(edge);
                continue;
            }
            edge.setEnd(v);
            incomingEdges(v).add(edge);
        }
        while (!outgoingEdges(w).isEmpty()) {
            E edge = outgoingEdges(w).poll();
            if (!allowLoops && edge.end().equals(v)) {
                removeEdge(edge);
                continue;
            }
            edge.setStart(v);
            outgoingEdges(v).add(edge);
        }
        removeNode(w);
    }

    @Override
    public Collection<N> nodes() {
        return nodes;
    }

    @Override
    public int numberOfEdges() {
        return edges.size();
    }

    @Override
    public int numberOfNodes() {
        return nodes.size();
    }

    @Override
    public int outdegree(N node) {
        return outgoingEdges(node).size();
    }

    @Override
    public LinkedList<E> outgoingEdges(N node) {
        return outgoingEdges.get(node);
    }

    public void removeAllEdges() {
        for (N node : nodes) {
            incomingEdges.get(node).clear();
            outgoingEdges.get(node).clear();
        }
        edges.clear();
    }

    public void removeAllNodes() {
        edges.clear();
        nodes.clear();
        incomingEdges.clear();
        outgoingEdges.clear();
    }

    public void removeEdge(E edge) {
        edges.remove(edge);
        incomingEdges.get(edge.end()).remove(edge);
        outgoingEdges.get(edge.start()).remove(edge);
    }

    public void removeEdges(Iterable<E> edges) {
        for (E edge : edges) {
            removeEdge(edge);
        }
    }

    public void removeLoops() {
        LinkedList<E> loops = new LinkedList<>();
        for (E edge : edges) {
            if (edge.isLoop()) {
                loops.add(edge);
            }
        }
        removeEdges(loops);
    }

    public void removeNode(N node) {
        
        for (E edge : incomingEdges.get(node)) {
            outgoingEdges.get(edge.start()).remove(edge);
            edges.remove(edge);
        }
        for (E edge : outgoingEdges.get(node)) {
            incomingEdges.get(edge.start()).remove(edge);
            edges.remove(edge);
        }
        incomingEdges.get(node).clear();
        outgoingEdges.get(node).clear();
        removeEdges(incidentEdges(node));
        nodes.remove(node);
    }

    public void removeNodes(Iterable<N> nodes) {
        for (N node : nodes) {
            removeNode(node);
        }
    }

    public void applyGSPGReduction() {
        do {
            removeParallelEdges();
            for (N node : nodes) {
                if (degree(node) == 0) {
                    System.err.println("I -> 0");
                }
            }
            removeLeavesRecursively();
            for (N node : nodes) {
                if (degree(node) == 0 && numberOfNodes() > 1) {
                    System.err.println("II -> 0");
                }
            }
        } while (contractDegreeTwoNodes());
    }

    public void removeLeavesRecursively() {
        Queue<N> leaves = new LinkedList<>();
        for (N node : nodes) {
            if (degree(node) == 1) {
                leaves.add(node);
            }
        }
        while (!leaves.isEmpty()) {
            N node = leaves.poll();
            if (degree(node) == 0) {
                if (numberOfNodes() > 1) {
                    System.out.println("Degree 0 node: " + node);
                }
                continue;
            }
            E edge = incidentEdges(node).getFirst();
            N other = edge.opposite(node);
            removeNode(node);
            removeEdge(edge);
            if (degree(other) == 1) {
                leaves.add(other);
            }
        }
    }

    /**
     *
     */
    public boolean contractDegreeTwoNodes() {
        boolean rerun = false;
        Queue<N> degreeTwo = new LinkedList<>();
        for (N node : nodes) {
            if (((GasNode) node).id() == 28 || ((GasNode) node).id() == 53 || ((GasNode) node).id() == 94 || ((GasNode) node).id() == 26) {
                //System.out.println("A: " + incidentEdges(node));
            }
            if (degree(node) == 2) {
                degreeTwo.add(node);
            }
        }

        while (!degreeTwo.isEmpty()) {
            N node = degreeTwo.poll();
            if (((GasNode) node).id() == 28) {
                //System.out.println("A: " + incidentEdges(node));
            }
            if (degree(node) != 2) {
                //System.out.println(degree(node) + " " + node);
                continue;
            }
            E edge = incidentEdges(node).getFirst();
            E edge2 = incidentEdges(node).getLast();
            N other = edge.opposite(node);
            N other2 = edge2.opposite(node);
            if (((GasNode) other).id() == 53 || ((GasNode) other2).id() == 53) {
                //System.out.println(node + " " + edge + " " + edge2);
            }
            removeNode(node);
            removeEdge(edge);
            removeEdge(edge2);
            if (getEdges(other, other2).isEmpty() && other != other2) {
                if (((GasNode) other).id() == 53 || ((GasNode) other2).id() == 53) {
                    //System.out.println("Create " + other + " " + other2);
                }
                addEdge(edgeFactory.apply(other, other2));
            } else {
                switch (degree(other)) {
                    case 0:
                        removeNode(other);
                        break;
                    case 1:
                        E edge3 = incidentEdges(other).getFirst();
                        removeEdge(edge3);
                        removeNode(other);
                        rerun = true;
                        break;
                    case 2:
                        degreeTwo.add(other);
                        break;
                    default:
                }
                switch (degree(other2)) {
                    case 0:
                        removeNode(other2);
                        break;
                    case 1:
                        E edge3 = incidentEdges(other2).getFirst();
                        removeEdge(edge3);
                        removeNode(other2);
                        rerun = true;
                        break;
                    case 2:
                        degreeTwo.add(other2);
                        break;
                    default:
                }
            }
        }
        return rerun;
    }

    public void removeParallelEdges() {
        for (N start : nodes) {
            for (N end : nodes) {
                //System.out.println(start + " " + end);
                List<E> edges = getEdges(start, end);
                edges.addAll(getEdges(end, start));
                while (edges.size() > 1) {
                    //System.out.println(" A: " + edges + " " + edges.get(0));
                    removeEdge(edges.get(0));
                    edges = getEdges(start, end);
                    edges.addAll(getEdges(end, start));
                    //System.out.println(" B: " + edges + " " + edges.get(0));
                }
            }
        }
    }

    public void setEdges(Iterable<E> edges) {
        removeAllEdges();
        addEdges(edges);
    }

    public void setNodes(Iterable<N> nodes) {
        removeAllNodes();
        addNodes(nodes);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("V = {");
        for (N node : nodes) {
            builder.append(node).append(",");
        }
        if (!nodes.isEmpty()) {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("}\n");
        builder.append("E = {");
        int counter = 0;
        for (E edge : edges) {
            if (counter == 10) {
                counter = 0;
                builder.append("\n");
            }
            builder.append(edge).append(",");
            counter++;
        }
        if (!edges.isEmpty()) {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("}");
        return builder.toString();
    }

    public String deepToString() {
        StringBuilder builder = new StringBuilder();
        builder.append("V = {");
        for (N node : nodes) {
            builder.append(node).append(",");
        }
        if (!nodes.isEmpty()) {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("}\n");
        builder.append("E = {");
        int counter = 0;
        for (E edge : edges) {
            if (counter == 10) {
                counter = 0;
                builder.append("\n");
            }
            builder.append(edge.nodesToString()).append(",");
            counter++;
        }
        if (!edges.isEmpty()) {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("}");
        return builder.toString();
    }

    @Override
    public Iterable<N> adjacentNodes(N node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterable<N> predecessorNodes(N node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterable<N> successorNodes(N node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
