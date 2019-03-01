/**
 * SeriesParallelGraph.java
 *
 */
package ds.graph;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import org.apache.commons.math3.util.Pair;

/**
 *
 * @author Martin Groß
 */
public class GSPG<N,E extends AbstractEdge<N>> extends DynamicNetwork<N,E> implements Planar {

    /**
     * The series-parallel decomposition.
     */
    private BinaryTree<Pair<GSPCompositionType,E>> decomposition;
    /**
     * The second terminal.
     */
    private N sink;
    /**
     * The first terminal.
     */
    private N source;

    /**
     * Creates a new minimum generalized series-parallel graph, i.e. an edge
     * connecting two terminals, using the specified factories to generate nodes
     * and edges.
     * @param nodeFactory the factory for nodes.
     * @param edgeFactory the factory for edges.
     */
    public GSPG(Supplier<N> nodeFactory, BiFunction<N,N,E> edgeFactory) {
        super(edgeFactory);        
        source = nodeFactory.get();
        sink = nodeFactory.get();
        E edge = edgeFactory.apply(source, sink);
        decomposition = new BinaryTree<>(new Pair<>(GSPCompositionType.EDGE, edge));
        addNode(source);
        addNode(sink);
        addEdge(edge);
        assert containsNode(source);
        assert containsNode(sink);
    }

    /**
     * Creates a new generalized series-parallel graph from the composition of
     * two generalized series-parallel graphs.
     * @param type the type of the composition.
     * @param spg1
     * @param spg2
     */
    public GSPG(GSPCompositionType type, GSPG<N,E> spg1, GSPG<N,E> spg2) {
        super(spg1.edgeFactory);
        if (type == GSPCompositionType.EDGE) {
            throw new IllegalArgumentException("This type is no composition and cannot be used here.");
        }
        addGraph(spg1);
        addGraph(spg2);
        switch (type) {
            case PARALLEL:
                merge(spg1.getSink(), spg2.getSink());
                merge(spg1.getSource(), spg2.getSource());
                sink = spg1.getSink();
                source = spg1.getSource();
                break;
            case SERIES:
                merge(spg1.getSink(), spg2.getSource());
                sink = spg2.getSink();
                source = spg1.getSource();
                break;
            case TREE:
                merge(spg1.getSource(), spg2.getSource());
                sink = spg1.getSink();
                source = spg1.getSource();
                break;
            default:
                throw new AssertionError("This should not happen.");
        }
        decomposition = new BinaryTree<>(new Pair(type,null),spg1.getDecomposition(),spg2.getDecomposition());
        assert containsNode(source);
        assert containsNode(sink);
    }

    /**
     * Returns the decomposition tree of the graph.
     * @return
     */
    public BinaryTree<Pair<GSPCompositionType,E>> getDecomposition() {
        return decomposition;
    }

    /**
     * Returns the sink, i.e., the second terminal of the network.
     * @return the sink, i.e., the second terminal of the network.
     */
    public N getSink() {
        return sink;
    }

    /**
     * Returns the source, i.e., the first terminal of the network.
     * @return the source, i.e., the first terminal of the network.
     */    
    public N getSource() {
        return source;
    }

    /**
     * Replaces an edge by the given generalized series-parallel graph. The end-
     * points of the edge are identified with the terminals of the given GSPG.
     * @param edge the edge to be replaced.
     * @param graph the graph that replaces the edge.
     */
    public GSPG<N,E> replaceEdge(E edge, GSPG<N,E> graph) {
        return null;
    }
    
    /*
    public static boolean isSeriesParallel(DynamicNetwork n, IdentifiableObjectMapping<Node, XMLIntersection> nodeIntersections, IdentifiableObjectMapping<Edge, XMLConnection> edgeConnections) {
        DynamicNetwork network = new DynamicNetwork(n);
        // Remove inactive leaves
        Queue<Node> nodeQueue = new LinkedList<>();
        for (Node node : network.nodes()) {
            if (network.degree(node) == 1 || network.degree(node) == 2) {
                nodeQueue.add(node);
            }
        }

        while (!nodeQueue.isEmpty()) {
            Node node = nodeQueue.poll();

            Queue<Edge> edgeQueue = new LinkedList<>();
            IdentifiableIntegerMapping<Edge> checked = new IdentifiableIntegerMapping<>(network.edges());
            for (Node x : network.nodes()) {
                for (Edge e : network.incidentEdges(x)) {
                    if (edgeQueue.contains(e)) {
                        continue;
                    }
                    for (Edge f : network.incidentEdges(x)) {
                        if (edgeQueue.contains(f)) {
                            continue;
                        }
                        if (e.isParallelTo(f) && e.id() != f.id()) {
                            edgeQueue.add(f);
                            //System.out.printf("Queueing %1$s for removal because of %2$s\n", f, e);
                        }
                    }
                }
            }
            while (!edgeQueue.isEmpty()) {
                Edge edge = edgeQueue.poll();
                Node node1 = edge.start();
                Node node2 = edge.end();
                network.removeEdge(edge);
                if (network.degree(node1) == 1 || network.degree(node1) == 2) {
                    nodeQueue.add(node1);
                }
                if (network.degree(node2) == 1 || network.degree(node2) == 2) {
                    nodeQueue.add(node2);
                }                
            }

            if (network.degree(node) == 1) {
                Edge edge = network.incidentEdges(node).first();
                Node other = edge.opposite(node);
                network.removeNode(node);
                network.removeEdge(edge);
                if (network.degree(other) == 1 || network.degree(other) == 2) {
                    nodeQueue.add(other);
                }
            } else if (network.degree(node) == 2) {
                Edge edge1 = network.incidentEdges(node).first();
                Edge edge2 = network.incidentEdges(node).last();
                Node node1 = edge1.opposite(node);
                Node node2 = edge2.opposite(node);
                if (edge1.isParallelTo(edge2)) {
                    network.removeEdge(edge2);
                } else {
                    Edge edge = new Edge(edge1.id(), node1, node2);
                    network.removeNode(node);
                    network.removeEdge(edge1);
                    network.removeEdge(edge2);
                    network.addEdge(edge);
                }
            }
        }

        
         System.out.println("\\begin{figure}[p]\\centering\\begin{tikzpicture}");
         for (Node node : network.nodes()) {
         XMLIntersection intersection = nodeIntersections.get(node);
         System.out.printf("\\node[nodeStyle] (n%1$s) at (%2$s,%3$s) {};\n", node.id(), intersection.getX() / 700, intersection.getY() / 500);
         }
         for (Edge edge : network.edges()) {
         if (edgeConnections.isDefinedFor(edge)) {
         System.out.printf("\\draw[%3$s] (n%1$s) -- (n%2$s);\n", edge.start().id(), edge.end().id(), edgeConnections.get(edge).getType().toString().replace("_", ""));
         } else {
         System.out.printf("\\draw (n%1$s) -- (n%2$s);\n", edge.start().id(), edge.end().id());
         }
         }
         System.out.println("\\end{tikzpicture}\\end{figure}");
         System.out.println("");
         
        System.out.println(network.numberOfNodes());
        for (Node node : network.nodes()) {
            if (network.degree(node) < 3) {
                System.out.println(network.degree(node));
            }

        }
        return true;
    }*/
}
