/**
 * GasFlow.java
 *
 */

package gas.ds;

import ds.graph.GasEdge;
import ds.graph.GasNode;
import ds.graph.Graph;

import units.UnitsTools;

/**
 *
 * @author Martin Groß
 */
public class ForwardComputationStep {

    private final GasFlow gasFlow;
    private final Graph<GasNode, GasEdge> network;
    private double timeStep;

    public ForwardComputationStep(Graph<GasNode, GasEdge> network) {
        this.network = network;
        this.gasFlow = new GasFlow(network);
    }

    public GasFlow getNextGasFlow() {
        return gasFlow;
    }

    public double getTimeStep() {
        return timeStep;
    }

    public void setTimeStep(double timeStep) {
        this.timeStep = timeStep;
    }
}
