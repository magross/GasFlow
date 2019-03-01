/**
 * GraphToGraphMapping.java
 *
 */

package ds.graph;

import java.util.Map;

/**
 *
 * @author Martin Groß
 */
public class GraphToGraphMapping<N,E extends AbstractEdge<N>,G extends Graph<N,E>,N2,E2 extends AbstractEdge<N2>,G2 extends Graph<N2,E2>> {

    private final G fromGraph;
    private final Map<E,E2> edgeMap;    
    private final Map<N,N2> nodeMap;
    private final G2 toGraph;

    public GraphToGraphMapping(G fromGraph, G2 toGraph, Map<N, N2> nodeMap, Map<E, E2> edgeMap) {
        this.edgeMap = edgeMap;
        this.fromGraph = fromGraph;
        this.nodeMap = nodeMap;
        this.toGraph = toGraph;
    }

    public Map<E, E2> getEdgeMap() {
        return edgeMap;
    }

    public G getFromGraph() {
        return fromGraph;
    }

    public Map<N, N2> getNodeMap() {
        return nodeMap;
    }

    public G2 getToGraph() {
        return toGraph;
    }
}
