/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ds.graph;

import ds.graph.AbstractEdge;

/**
 *
 * @author Martin
 */
public class GSPComposition<N, E extends AbstractEdge<N>> {
    
    private final GSPG<N, E> graph1;
    private final GSPG<N, E> graph2;
    private final GSPCompositionType type;

    public GSPComposition(GSPCompositionType type, GSPG<N, E> graph1, GSPG<N, E> graph2) {
        this.graph1 = graph1;
        this.graph2 = graph2;
        this.type = type;
    }

    public boolean contains(GSPG<N, E> g) {
        return g.equals(graph1) || g.equals(graph2);
    }

    public GSPG<N, E> getGraph1() {
        return graph1;
    }

    public int getGraph1SinkDegree() {
        return graph1.degree(graph1.getSink());
    }

    public int getGraph1SourceDegree() {
        return graph1.degree(graph1.getSource());
    }

    public GSPG<N, E> getGraph2() {
        return graph2;
    }

    public int getGraph2SinkDegree() {
        return graph2.degree(graph2.getSink());
    }

    public int getGraph2SourceDegree() {
        return graph2.degree(graph2.getSource());
    }

    public GSPCompositionType getType() {
        return type;
    }
    public String toString() {
        return String.format("(%1$s,%2$s/%3$s-%6$s,%4$s/%5$s-%7$s)", type,getGraph1SourceDegree(),getGraph1SinkDegree(),getGraph2SourceDegree(),getGraph2SinkDegree(),
                graph1.getEdges(graph1.getSource(), graph1.getSink()).size(),graph2.getEdges(graph2.getSource(), graph2.getSink()).size());
    } 
}
