/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gas.algo;

import ds.graph.DynamicNetwork;
import ds.graph.GasEdge;
import ds.graph.GasNode;
import gas.problem.SourceSinkForwardComputationProblem;
import javax.measure.quantity.Length;
import static javax.measure.unit.SI.CUBIC_METRE;
import static javax.measure.unit.SI.METER;
import org.jscience.physics.amount.Amount;

/**
 *
 * @author Martin
 */
public class Old {
      public static SourceSinkForwardComputationProblem test() {
        DynamicNetwork<GasNode, GasEdge> network = new DynamicNetwork<>(GasEdge::createEdge);
        GasNode s = new GasNode(0, Amount.valueOf(10, METER), Amount.valueOf(10, CUBIC_METRE));        
        GasNode v1 = new GasNode(1, Amount.valueOf(10, METER), Amount.valueOf(10, CUBIC_METRE));
        GasNode w1 = new GasNode(2, Amount.valueOf(10, METER), Amount.valueOf(10, CUBIC_METRE));
        GasNode w2 = new GasNode(3, Amount.valueOf(10, METER), Amount.valueOf(10, CUBIC_METRE));
        GasNode t = new GasNode(4, Amount.valueOf(10, METER), Amount.valueOf(10, CUBIC_METRE));
        network.addNode(s);
        network.addNode(v1);
        network.addNode(w1);
        network.addNode(w2);
        network.addNode(t);
        GasEdge e = new GasEdge(s, t, 0, (Amount<Length>) Amount.valueOf(0.025, METER), (Amount<Length>) Amount.valueOf(100, METER), (Amount<Length>) Amount.valueOf(0.00001, METER));
        GasEdge f1 = new GasEdge(s, v1, 1, (Amount<Length>) Amount.valueOf(0.025, METER), (Amount<Length>) Amount.valueOf(100, METER), (Amount<Length>) Amount.valueOf(0.00001, METER));
        GasEdge f2 = new GasEdge(v1, t, 2, (Amount<Length>) Amount.valueOf(0.025, METER), (Amount<Length>) Amount.valueOf(100, METER), (Amount<Length>) Amount.valueOf(0.00001, METER));
        GasEdge g1 = new GasEdge(s, w1, 3, (Amount<Length>) Amount.valueOf(0.025, METER), (Amount<Length>) Amount.valueOf(100, METER), (Amount<Length>) Amount.valueOf(0.00001, METER));
        GasEdge g2 = new GasEdge(w1, w2, 4, (Amount<Length>) Amount.valueOf(0.025, METER), (Amount<Length>) Amount.valueOf(100, METER), (Amount<Length>) Amount.valueOf(0.00001, METER));
        GasEdge g3 = new GasEdge(w2, t, 5, (Amount<Length>) Amount.valueOf(0.025, METER), (Amount<Length>) Amount.valueOf(100, METER), (Amount<Length>) Amount.valueOf(0.00001, METER));
        network.addEdge(e);
        network.addEdge(f1);
        network.addEdge(f2);
        network.addEdge(g1);
        network.addEdge(g2);
        network.addEdge(g3);

        SourceSinkForwardComputationProblem problem = new SourceSinkForwardComputationProblem();
        problem.setNetwork(network);
        problem.setSource(s);
        problem.setSink(t);
        return problem;
    }  
}
