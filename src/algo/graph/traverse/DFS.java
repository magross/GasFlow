/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
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
 * DFS.java
 *
 */
package algo.graph.traverse;

import ds.graph.AbstractEdge;
import ds.graph.DynamicNetwork;
import ds.graph.Edge;
import ds.graph.Graph;
import ds.graph.IdentifiableCollection;
import ds.graph.Node;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 *
 * @author Martin Groß
 */
public class DFS<N,E extends AbstractEdge<N>> {

    public enum State {
        NORMAL, VISITED, DONE;
    }

    public enum Mode {
        FORWARD, BACKWARD, UNDIRECTED;
    }

    private Graph<N,E> graph;

    private List<E> backEdges;
    private List<E> crossEdges;
    private List<E> forwardEdges;
    private List<E> treeEdges;
    private Map<N, Integer> numbering;
    private Map<N, State> states;
    private int currentNumber;

    public DFS() {
        this(null);
    }

    public DFS(Graph<N,E> graph) {
        this.graph = graph;
        backEdges = new ArrayList<>(graph.numberOfEdges());
        crossEdges = new ArrayList<>(graph.numberOfEdges());
        forwardEdges = new ArrayList<>(graph.numberOfEdges());
        treeEdges = new ArrayList<>(graph.numberOfEdges());
        numbering = new HashMap<>(graph.numberOfNodes());
        states = new HashMap<>(graph.numberOfNodes());
        currentNumber = 0;
        for (N node : graph.nodes()) {
            numbering.put(node, 0);
            states.put(node, State.NORMAL);
        }
    }
    
    public List<Graph<N,E>> createConnectedComponents(BiFunction<N, N, E> edgeFactory) {
        if (graph == null) {
            throw new IllegalStateException("");
        }
        List<Graph<N,E>> ccs = new LinkedList<>();
        for (N node : graph.nodes()) {
            if (numbering.get(node) == 0) {
                List<N> nodes = new LinkedList<>();
                run(node, Mode.UNDIRECTED, nodes);
                
                Graph<N,E> cc = new DynamicNetwork<>(edgeFactory);  
                for (N n : nodes) {
                    cc.addNode(n);
                }
                for (E edge : graph.edges()) {
                    if (nodes.contains(edge.start()) && nodes.contains(edge.end())) {
                        cc.addEdge(edge);
                    }
                }  
                ccs.add(cc);
                //System.out.println(cc.numberOfNodes() + " " + cc.numberOfEdges());
                //numbers[currentNumber-lastNumber]++;
                //lastNumber = currentNumber;
            }
        }       
        return ccs;
    }

    public List<E> getBackEdges() {
        if (backEdges == null) {
            throw new IllegalStateException("");
        }
        return backEdges;
    }

    public List<E> getCrossEdges() {
        if (crossEdges == null) {
            throw new IllegalStateException("");
        }
        return crossEdges;
    }

    public List<E> getForwardEdges() {
        if (forwardEdges == null) {
            throw new IllegalStateException("");
        }
        return forwardEdges;
    }

    public List<E> getTreeEdges() {
        if (treeEdges == null) {
            throw new IllegalStateException("");
        }
        return treeEdges;
    }

    public void run() {
        run(Mode.FORWARD);
    }

    public void run(Mode mode) {
        System.out.println("Start");
        if (graph == null) {
            throw new IllegalStateException("");
        }
        int count = 0;
        int lastNumber = 0;
        int[] numbers = new int[120];
        for (N node : graph.nodes()) {
            if (numbering.get(node) == 0) {
                count++;
                run(node, mode);
                numbers[currentNumber-lastNumber]++;
                lastNumber = currentNumber;
            }
        }

        System.out.println("Connected Components: " + count);
        int[] sums = new int[120];
        sums[0] = numbers[0];
        int max = 0;
        for (int i = 1; i < numbers.length; i++) {
            sums[i] = sums[i-1]+numbers[i];
            if (numbers[i] > 0) {
                max = i;
            }
            switch (i) {
                case 5:
                    System.out.printf("2-5 %1$s\n", sums[5]-sums[1]);
                    break;
                case 10:
                    System.out.printf("6-10 %1$s\n", sums[10]-sums[5]);
                    break;
                case 20:
                    System.out.printf("11-20 %1$s\n", sums[20]-sums[10]);
                    break;
                case 50:
                    System.out.printf("21-50 %1$s\n", sums[50]-sums[20]);
                    break;
                case 119:
                    System.out.printf("50+ %1$s\n", sums[119]-sums[50]);
                    break;
            }
        }
        System.out.println("Max: " + max);
        System.out.println(Arrays.toString(numbers));
    }

    public void run(N node) {
        run(node, Mode.FORWARD);
    }

    public void run(N node, Mode mode) {
        if (states.get(node) != State.NORMAL) {
            return;
        }
        states.put(node, State.VISITED);
        currentNumber++;
        numbering.put(node, currentNumber);
        Iterable<E> edges;
        if (mode == Mode.BACKWARD) {
            edges = graph.incomingEdges(node);
        } else if (mode == Mode.FORWARD) {
            edges = graph.outgoingEdges(node);
        } else {
            edges = graph.incidentEdges(node);
        }
        for (E edge : edges) {
            N oppositeNode = edge.opposite(node);
            if (numbering.get(oppositeNode) == 0) {
                treeEdges.add(edge);
                run(oppositeNode, mode);
            } else if (numbering.get(oppositeNode) > numbering.get(node)) {
                forwardEdges.add(edge);
            } else if (numbering.get(oppositeNode) < numbering.get(node) && states.get(oppositeNode) == State.VISITED) {
                backEdges.add(edge);
            } else {
                crossEdges.add(edge);
            }
        }
        states.put(node, State.DONE);
    }
    
    public void run(N node, Mode mode, List<N> nodes) {
        if (states.get(node) != State.NORMAL) {
            return;
        }
        nodes.add(node);
        states.put(node, State.VISITED);
        currentNumber++;
        numbering.put(node, currentNumber);
        Iterable<E> edges;
        if (mode == Mode.BACKWARD) {
            edges = graph.incomingEdges(node);
        } else if (mode == Mode.FORWARD) {
            edges = graph.outgoingEdges(node);
        } else {
            edges = graph.incidentEdges(node);
        }
        for (E edge : edges) {
            N oppositeNode = edge.opposite(node);
            if (numbering.get(oppositeNode) == 0) {
                treeEdges.add(edge);                
                run(oppositeNode, mode, nodes);
            } else if (numbering.get(oppositeNode) > numbering.get(node)) {
                forwardEdges.add(edge);
            } else if (numbering.get(oppositeNode) < numbering.get(node) && states.get(oppositeNode) == State.VISITED) {
                backEdges.add(edge);
            } else {
                crossEdges.add(edge);
            }
        }
        states.put(node, State.DONE);
    }    

    public State state(N node) {
        return states.get(node);
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
        backEdges = null;
        crossEdges = null;
        forwardEdges = null;
        treeEdges = null;
    }
}
