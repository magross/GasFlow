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
import units.UnitsTools;

/**
 *
 * @author Martin
 */
public class Old {
      public static SourceSinkForwardComputationProblem test() {
        DynamicNetwork<GasNode, GasEdge> network = new DynamicNetwork<>(GasEdge::createEdge);
        GasNode s = new GasNode(0, 10 * UnitsTools.m, 10 * UnitsTools.m3);        
        GasNode v1 = new GasNode(1, 10 * UnitsTools.m, 10 * UnitsTools.m3);
        GasNode w1 = new GasNode(2, 10 * UnitsTools.m, 10 * UnitsTools.m3);
        GasNode w2 = new GasNode(3, 10 * UnitsTools.m, 10 * UnitsTools.m3);
        GasNode t = new GasNode(4, 10 * UnitsTools.m, 10 * UnitsTools.m3);
        network.addNode(s);
        network.addNode(v1);
        network.addNode(w1);
        network.addNode(w2);
        network.addNode(t);
        GasEdge e = new GasEdge(s, t, 0, 0.025 * UnitsTools.m, 100 * UnitsTools.m, 0.00001 * UnitsTools.m);
        GasEdge f1 = new GasEdge(s, v1, 1, 0.025 * UnitsTools.m, 100 * UnitsTools.m, 0.00001 * UnitsTools.m);
        GasEdge f2 = new GasEdge(v1, t, 2, 0.025 * UnitsTools.m, 100 * UnitsTools.m, 0.00001 * UnitsTools.m);
        GasEdge g1 = new GasEdge(s, w1, 3, 0.025 * UnitsTools.m, 100 * UnitsTools.m, 0.00001 * UnitsTools.m);
        GasEdge g2 = new GasEdge(w1, w2, 4, 0.025 * UnitsTools.m, 100 * UnitsTools.m, 0.00001 * UnitsTools.m);
        GasEdge g3 = new GasEdge(w2, t, 5, 0.025 * UnitsTools.m, 100 * UnitsTools.m, 0.00001 * UnitsTools.m);
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
