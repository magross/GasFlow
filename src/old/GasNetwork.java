package old;

import ds.graph.Edge;
import ds.graph.Graph;
import ds.graph.IdentifiableDoubleMapping;
import ds.graph.IdentifiableGraph;
import ds.graph.Node;
import java.util.ArrayList;

public class GasNetwork {

    public IdentifiableGraph g;
    public IdentifiableDoubleMapping<Edge> beta = null;
    public IdentifiableDoubleMapping<Edge> gamma = null;
    public IdentifiableDoubleMapping<Node> demand = null;
    public ArrayList<Edge> aOut = null;
    
    public GasNetwork(IdentifiableGraph graph, IdentifiableDoubleMapping<Edge> beta, IdentifiableDoubleMapping<Edge> gamma,
            IdentifiableDoubleMapping<Node> demand, ArrayList<Edge> aOut){
        this.g = graph;
        this.beta = beta;
        this.gamma = gamma;
        this.demand = demand;
        this.aOut = aOut;
    }
}
