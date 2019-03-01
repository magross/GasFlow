/**
 * GasFlow.java
 *
 */

package gas.ds;

import ds.graph.GasEdge;
import ds.graph.GasNode;
import ds.graph.Graph;
import javax.measure.quantity.Duration;
import org.jscience.physics.amount.Amount;

/**
 *
 * @author Martin Groß
 */
public class ForwardComputationStep {

    private final GasFlow gasFlow;
    private final Graph<GasNode, GasEdge> network;
    private Amount<Duration> timeStep;

    public ForwardComputationStep(Graph<GasNode, GasEdge> network) {
        this.network = network;
        this.gasFlow = new GasFlow(network);
    }

    public GasFlow getNextGasFlow() {
        return gasFlow;
    }

    public Amount<Duration> getTimeStep() {
        return timeStep;
    }

    public void setTimeStep(Amount<Duration> timeStep) {
        this.timeStep = timeStep;
    }
}
