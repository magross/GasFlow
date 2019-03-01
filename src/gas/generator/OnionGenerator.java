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
import javax.measure.quantity.Length;
import javax.measure.quantity.Pressure;
import static javax.measure.unit.NonSI.BAR;
import static javax.measure.unit.SI.METER;
import org.jscience.physics.amount.Amount;

/**
 *
 * @author Martin
 */
public class OnionGenerator {

    private Amount<Length> edgeDiameter;
    private Amount<Length> edgeLength;
    private Amount<Length> edgeRoughness;
    private Amount<Length> sinkHeight;
    private Amount<Pressure> sinkPressure;
    private Amount<Length> sourceHeight;
    private Amount<Pressure> sourcePressure;

    public OnionGenerator() {
        edgeDiameter = Amount.valueOf(1, METER);
        edgeLength = Amount.valueOf(1000, METER);
        edgeRoughness = Amount.valueOf(0.0001, METER);
        sinkHeight = Amount.valueOf(0, METER);
        sinkPressure = Amount.valueOf(39.5, BAR);
        sourceHeight = Amount.valueOf(0, METER);
        sourcePressure = Amount.valueOf(40, BAR);
    }

    public SourceSinkForwardComputationProblem generate(int layers, Amount<Pressure> sourcePressure, Amount<Pressure> sinkPressure) {
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
            Amount<Length> heightD = sourceHeight.minus(sinkHeight);
            for (int segment = 0; segment < layer; segment++) {
                Amount<Length> height = sourceHeight.minus(heightD.divide(layer + 1).times(segment + 1));
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

    public Amount<Length> getEdgeDiameter() {
        return edgeDiameter;
    }

    public void setEdgeDiameter(Amount<Length> edgeDiameter) {
        this.edgeDiameter = edgeDiameter;
    }

    public Amount<Length> getEdgeLength() {
        return edgeLength;
    }

    public void setEdgeLength(Amount<Length> edgeLength) {
        this.edgeLength = edgeLength;
    }

    public Amount<Length> getEdgeRoughness() {
        return edgeRoughness;
    }

    public void setEdgeRoughness(Amount<Length> edgeRoughness) {
        this.edgeRoughness = edgeRoughness;
    }

    public Amount<Length> getSinkHeight() {
        return sinkHeight;
    }

    public void setSinkHeight(Amount<Length> sinkHeight) {
        this.sinkHeight = sinkHeight;
    }

    public Amount<Pressure> getSinkPressure() {
        return sinkPressure;
    }

    public void setSinkPressure(Amount<Pressure> sinkPressure) {
        this.sinkPressure = sinkPressure;
    }

    public Amount<Length> getSourceHeight() {
        return sourceHeight;
    }

    public void setSourceHeight(Amount<Length> sourceHeight) {
        this.sourceHeight = sourceHeight;
    }

    public Amount<Pressure> getSourcePressure() {
        return sourcePressure;
    }

    public void setSourcePressure(Amount<Pressure> sourcePressure) {
        this.sourcePressure = sourcePressure;
    }
}
