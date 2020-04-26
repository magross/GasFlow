/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gas.generator;

import ds.graph.DynamicNetwork;
import ds.graph.GasEdge;
import ds.graph.GasNode;
import gas.problem.SourceSinkForwardComputationProblem;
import units.UnitsTools;

/**
 *
 * @author Martin
 */
public class OnionGenerator {

    private double edgeDiameter;
    private double edgeLength;
    private double edgeRoughness;
    private double sinkHeight;
    private double sinkPressure;
    private double sourceHeight;
    private double sourcePressure;

    public OnionGenerator() {
        edgeDiameter = 1 * UnitsTools.m;
        edgeLength = 1000 * UnitsTools.m;
        edgeRoughness = 0.0001 * UnitsTools.m;
        sinkHeight = 0 * UnitsTools.m;
        sinkPressure = 39.5 * UnitsTools.bar;
        sourceHeight = 0  * UnitsTools.m;
        sourcePressure = 40 * UnitsTools.bar;
    }

    public SourceSinkForwardComputationProblem generate(int layers, double sourcePressure, double sinkPressure) {
        SourceSinkForwardComputationProblem p = generate(layers);
        p.setSourcePressure(sourcePressure);
        p.setSinkPressure(sinkPressure);
        return p;
    }
    
    public SourceSinkForwardComputationProblem generate(int layers) {
        DynamicNetwork<GasNode, GasEdge> network = new DynamicNetwork<>(GasEdge::createEdge);
        GasNode[][] nodes = new GasNode[layers][];
        GasNode source = new GasNode(0, sourceHeight);
        GasNode sink = new GasNode(1, sinkHeight);
        network.addNode(source);
        network.addNode(sink);
        GasEdge st = new GasEdge(source, sink, 0, edgeDiameter, edgeLength, edgeRoughness);
        network.addEdge(st);
        int nodeIndex = 2;
        int edgeIndex = 1;
        for (int layer = 0; layer < layers; layer++) {
            nodes[layer] = new GasNode[layer];
            double heightD = sourceHeight - sinkHeight;
            for (int segment = 0; segment < layer; segment++) {
                double height = sourceHeight - (heightD/(layer + 1)*(segment + 1));
                nodes[layer][segment] = new GasNode(nodeIndex, height);
                network.addNode(nodes[layer][segment]);
                nodeIndex++;
                GasEdge edge;
                if (segment == 0) {
                    edge = new GasEdge(source, nodes[layer][segment], edgeIndex, edgeDiameter, edgeLength, edgeRoughness);
                } else {
                    edge = new GasEdge(nodes[layer][segment - 1], nodes[layer][segment], edgeIndex, edgeDiameter, edgeLength, edgeRoughness);
                }
                edgeIndex++;
                network.addEdge(edge);
                if (segment == layer - 1) {
                    edge = new GasEdge(nodes[layer][segment], sink, edgeIndex, edgeDiameter, edgeLength, edgeRoughness);
                    edgeIndex++;
                    network.addEdge(edge);
                }
            }
        }
        SourceSinkForwardComputationProblem problem = new SourceSinkForwardComputationProblem();
        problem.setNetwork(network);
        problem.setSource(source);
        problem.setSink(sink);
        return problem;
    }

    public double getEdgeDiameter() {
        return edgeDiameter;
    }

    public void setEdgeDiameter(double edgeDiameter) {
        this.edgeDiameter = edgeDiameter;
    }

    public double getEdgeLength() {
        return edgeLength;
    }

    public void setEdgeLength(double edgeLength) {
        this.edgeLength = edgeLength;
    }

    public double getEdgeRoughness() {
        return edgeRoughness;
    }

    public void setEdgeRoughness(double edgeRoughness) {
        this.edgeRoughness = edgeRoughness;
    }

    public double getSinkHeight() {
        return sinkHeight;
    }

    public void setSinkHeight(double sinkHeight) {
        this.sinkHeight = sinkHeight;
    }

    public double getSinkPressure() {
        return sinkPressure;
    }

    public void setSinkPressure(double sinkPressure) {
        this.sinkPressure = sinkPressure;
    }

    public double getSourceHeight() {
        return sourceHeight;
    }

    public void setSourceHeight(double sourceHeight) {
        this.sourceHeight = sourceHeight;
    }

    public double getSourcePressure() {
        return sourcePressure;
    }

    public void setSourcePressure(double sourcePressure) {
        this.sourcePressure = sourcePressure;
    }
}
