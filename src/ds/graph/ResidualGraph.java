/*
 * ResidualGraph.java
 *
 */
package ds.graph;

/**
 *
 * @author Martin Gro� / Sebastian Schenker
 */
public class ResidualGraph extends StaticGraph {

    private Graph graph;

    private int maxnodeid; //maximal id of nodes in original graph
    private int maxedgeid; //maximal id of edges in original graph

    private DoubleMap<Edge> flow;
    private DoubleMap<Edge> residualCapacities;
    private DoubleMap<Edge> residualTransitTimes;

    public ResidualGraph(IdentifiableGraph gr, DoubleMap<Edge> capacities, int mnodeid, int medgeid) {
        super(true, (mnodeid + 1), (medgeid + 1) * 2);
        maxnodeid = mnodeid;
        maxedgeid = medgeid;
        graph = gr;

        setNodes(gr.nodes());
        setEdges(gr.edges());

        for (Edge edge : gr.edges()) {
            createEdge(edge.end(), edge.start(), edge.id() + (maxedgeid + 1));
        }

        flow = new DoubleMap<Edge>(gr.numberOfEdges());

        residualCapacities = new DoubleMap<Edge>(gr.numberOfEdges() * 2);

        for (Edge edge : edges) {

            if (isReverseEdge(edge)) {
                residualCapacities.set(edge, 0);
                changeVisibility(edge, false);

            } else {
                changeVisibility(edge, true);
                flow.set(edge, 0.0);
                residualCapacities.set(edge, capacities.get(edge));
            }

        }

    }

    public void augmentFlow(Edge edge, double amount) {

        Edge reverseEdge = getReverseEdge(edge);

        if (isReverseEdge(edge)) {
            flow.decrease(reverseEdge, amount);
        } else {
            flow.increase(edge, amount);
        }
        residualCapacities.decrease(edge, amount);
        residualCapacities.increase(reverseEdge, amount);
        if (eq(residualCapacities.get(edge), 0.0)) {
            //setHidden(edge, true);
            changeVisibility(edge, false);
        }
        if (less(0.0, residualCapacities.get(reverseEdge))) {
            //setHidden(reverseEdge, false);
            changeVisibility(reverseEdge, true);
        }
    }

    public DoubleMap<Edge> getResidualCapacities() {
        return residualCapacities;
    }

    public DoubleMap<Edge> getResidualTransitTimes() {
        return residualTransitTimes;
    }

    public DoubleMap<Edge> getFlow() {
        return flow;
    }

    public Graph getGraph() {
        return graph;
    }

    public Edge getReverseEdge(Edge edge) {
        if (edge.id() <= maxedgeid) {       //changed by Sebastian: edge.id() < graph.numberOfEdges()
            return edges.get(edge.id() + (maxedgeid + 1));
        } else {
            return edges.get(edge.id() - (maxedgeid + 1));
        }
    }

    public boolean isReverseEdge(Edge edge) {

        return (edge.id() > maxedgeid); //changed by Sebastian: edge.id() >= graph.numberOfEdges();
    }

    public static boolean eq(double x, double y) {
        return Math.abs(x - y) < 10E-9;
    }

    public static boolean less(double x, double y) {
        return x + 10E-9 < y;
    }

}
