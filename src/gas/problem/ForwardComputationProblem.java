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
import javax.measure.quantity.Duration;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Velocity;
import javax.measure.quantity.Volume;
import static javax.measure.unit.SI.SECOND;
import org.jscience.physics.amount.Amount;

/**
 *
 * @author Martin
 */
public class ForwardComputationProblem {

    private Graph<Node, Edge> network;
    private IdentifiableAmountMapping<Edge, EdgeConstant> edgeConstants;
    private IdentifiableAmountMapping<Node, Pressure> pressures;
    private Amount<Velocity> speedOfSound;
    private Amount<Duration> timeStep;
    private IdentifiableAmountMapping<Node, Volume> volumes;

    public ForwardComputationProblem() {
    }

    
    
    public ForwardComputationProblem(Network network, IdentifiableAmountMapping<Node, Pressure> pressures, IdentifiableAmountMapping<Node, Volume> volumes, IdentifiableAmountMapping<Edge, EdgeConstant> edgeConstants) {
        this.network = network;
        this.edgeConstants = edgeConstants;
        this.pressures = pressures;
        this.timeStep = Amount.valueOf(1.0, SECOND);
        this.volumes = volumes;
    }

    public Graph<Node, Edge> getNetwork() {
        return network;
    }

    public IdentifiableAmountMapping<Edge, EdgeConstant> getEdgeConstants() {
        return edgeConstants;
    }

    public IdentifiableAmountMapping<Node, Pressure> getPressures() {
        return pressures;
    }

    public Amount<Velocity> getSpeedOfSound() {
        return speedOfSound;
    }

    public void setSpeedOfSound(Amount<Velocity> speedOfSound) {
        this.speedOfSound = speedOfSound;
    }

    public Amount<Duration> getTimeStep() {
        return timeStep;
    }

    public void setTimeStep(Amount<Duration> timeStep) {
        this.timeStep = timeStep;
    }

    public IdentifiableAmountMapping<Node, Volume> getVolumes() {
        return volumes;
    }


}
