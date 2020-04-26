/**
 * GasFlow.java
 *
 */

package gas.ds;

import ds.graph.GasEdge;
import ds.graph.GasNode;
import ds.graph.Graph;
import ds.graph.IdentifiableAmountMapping;

/**
 *
 * @author Martin Groß
 */
public class GasFlow {

    private final Graph<GasNode, GasEdge> network;
    private final IdentifiableAmountMapping<GasEdge, Double> massFlowRates;
    private IdentifiableAmountMapping<GasNode, Double> pressures;

    public GasFlow(Graph<GasNode, GasEdge> network) {
        this.network = network;
        massFlowRates = new IdentifiableAmountMapping<>(network.edges());
        pressures = new IdentifiableAmountMapping<>(network.nodes());
    }

    public IdentifiableAmountMapping<GasEdge, Double> getMassFlowRates() {
        return massFlowRates;
    }

    public IdentifiableAmountMapping<GasNode, Double> getPressures() {
        return pressures;
    }

    public void setPressures(IdentifiableAmountMapping<GasNode, Double> pressures) {
        this.pressures = pressures;
    }

}
