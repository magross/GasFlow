package old;

import ds.graph.Edge;
import ds.graph.IdentifiableDoubleMapping;
import ds.graph.Network;
import ds.graph.Node;
import java.util.ArrayList;
//import timeexpandedgasnets.base.Arc;
import old.GasNetwork;
import old.NoArcShutdownModel;

// Builds an lp model for gas networks and solves it. 
public class TimeExpandedGasNets {/*
    public static void main(String[] args) {
        try{
            // Build the LP model
            GasNetwork net = buildDummyNetwork();
            NoArcShutdownModel gasLP = new NoArcShutdownModel(net);
            gasLP.build();
            
            // Write the model to a text file
            gasLP.exportModel("debug.lp");
            
            // Solve the LP model
            // See here: http://www-01.ibm.com/support/knowledgecenter/SSSA5P_12.6.0/ilog.odms.cplex.help/CPLEX/UsrMan/topics/APIs/Java/11_solve_model.html?lang=en
            boolean success = gasLP.solve();
            if(!success){
                System.out.println("No feasible solution could be found.");
                System.out.println("Cplex stopped with status " + gasLP.getStatus());
                return;
            }
            
            // LP model has been solved, query and print the solution
            // See here: http://www-01.ibm.com/support/knowledgecenter/SSSA5P_12.6.0/ilog.odms.cplex.help/CPLEX/UsrMan/topics/APIs/Java/12_access_soln.html?lang=en
            System.out.println("==================================================");
            System.out.println("Solution status: " + gasLP.getStatus());
            System.out.println("Objective value: " + gasLP.getObjValue());
            System.out.println("==================================================");
            for(Edge edge : net.g.edges()) {
                System.out.println("q" + edge.id() + " = " + gasLP.getQValue(edge));
            }
            System.out.println("==================================================");
            for(Node v : net.g.nodes()) {
                System.out.println("pi" + v.id() + " = " + gasLP.getPiValue(v));
            }
            System.out.println("==================================================");
        } 
        catch(IloException e){
            System.err.print("[ERROR] IloException: " + e);
            e.printStackTrace();
            return;
        }
    }
    
    // Builds a dummy network that consists of a directed 4-cycle. Node i
    // has a beta value of i and a gamma value of i/2. The supply of nodes
    // 1 and 2 is 1, the demand of the remaining two nodes 3 and 4 is -1.
    // The only arc in A_out is (3,4).
    private static GasNetwork buildDummyNetwork(){
        Network g = new Network(4,4);
        Node a = g.getNode(0);
        Node b = g.getNode(1);
        Node c = g.getNode(2);
        Node d = g.getNode(3);
        
        Edge ab = g.createAndSetEdge(a,b);
        Edge bc = g.createAndSetEdge(b,c);
        Edge cd = g.createAndSetEdge(c, d);
        Edge da = g.createAndSetEdge(d, a);

        IdentifiableDoubleMapping<Edge> beta = new IdentifiableDoubleMapping<>(g.numberOfEdges());
        for(Edge arc : g.edges()) {
            beta.set(arc, arc.id());
        }

        IdentifiableDoubleMapping<Edge> gamma = new IdentifiableDoubleMapping<>(g.numberOfEdges());
        for(Edge arc : g.edges()) {
            gamma.set(arc, arc.id() / 2.0);
        }

        IdentifiableDoubleMapping<Node> demand = new IdentifiableDoubleMapping<>(g.numberOfNodes());
        demand.set(a, 1.0);
        demand.set(b, 1.0);
        demand.set(c, -1.0);
        demand.set(d, -1.0);
        
        ArrayList<Edge> aOut = new ArrayList<>();
        aOut.add(cd);
        
        GasNetwork net = new GasNetwork(g, beta, gamma, demand, aOut);
        return net;
    }
*/
}
