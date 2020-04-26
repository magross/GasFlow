/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gas.problem;

import ds.graph.Edge;
import ds.graph.Graph;
import ds.graph.IdentifiableAmountMapping;
import ds.graph.Network;
import ds.graph.Node;
import gas.quantity.EdgeConstant;
import units.UnitsTools;
import units.qual.*;

/**
 *
 * @author Martin
 */
public class ForwardComputationProblem {

    private Graph<Node, Edge> network;
    private IdentifiableAmountMapping<Edge, Double> edgeConstants;
    private IdentifiableAmountMapping<Node, Double> pressures;
    private double speedOfSound;
    private double timeStep;
    private IdentifiableAmountMapping<Node, Double> volumes;

    public ForwardComputationProblem() {
    }

    
    
    public ForwardComputationProblem(Network network, IdentifiableAmountMapping<Node, Double> pressures, IdentifiableAmountMapping<Node, Double> volumes, IdentifiableAmountMapping<Edge, Double> edgeConstants) {
        this.network = network;
        this.edgeConstants = edgeConstants;
        this.pressures = pressures;
        this.timeStep = 1.0 * UnitsTools.s;
        this.volumes = volumes;
    }

    public Graph<Node, Edge> getNetwork() {
        return network;
    }

    public IdentifiableAmountMapping<Edge, Double> getEdgeConstants() {
        return edgeConstants;
    }

    public IdentifiableAmountMapping<Node, Double> getPressures() {
        return pressures;
    }

    public @mPERs double getSpeedOfSound() {
        return speedOfSound;
    }

    public void setSpeedOfSound(double speedOfSound) {
        this.speedOfSound = speedOfSound;
    }

    public double getTimeStep() {
        return timeStep;
    }

    public void setTimeStep(double timeStep) {
        this.timeStep = timeStep;
    }

    public IdentifiableAmountMapping<Node, Double> getVolumes() {
        return volumes;
    }


}
