/**
 * GasFlow.java
 *
 */

package gas.ds;

import ds.graph.GasEdge;
import ds.graph.GasNode;
import ds.graph.Graph;
import ds.graph.IdentifiableAmountMapping;
import javax.measure.quantity.MassFlowRate;
import javax.measure.quantity.Pressure;

/**
 *
 * @author Martin Groß
 */
public class GasFlow {

    private final Graph<GasNode, GasEdge> network;
    private final IdentifiableAmountMapping<GasEdge, MassFlowRate> massFlowRates;
    private IdentifiableAmountMapping<GasNode, Pressure> pressures;

    public GasFlow(Graph<GasNode, GasEdge> network) {
        this.network = network;
        massFlowRates = new IdentifiableAmountMapping<>(network.edges());
        pressures = new IdentifiableAmountMapping<>(network.nodes());
    }

    public IdentifiableAmountMapping<GasEdge, MassFlowRate> getMassFlowRates() {
        return massFlowRates;
    }

    public IdentifiableAmountMapping<GasNode, Pressure> getPressures() {
        return pressures;
    }

    public void setPressures(IdentifiableAmountMapping<GasNode, Pressure> pressures) {
        this.pressures = pressures;
    }

}
