/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * ResidualNetwork.java
 *
 */
package ds.graph;

import ds.graph.flow.Flow;

/**
 * The <code>ResidualNetwork</code> class provides flow algorithms with the 
 * functionality to create and work with residual networks. The residual 
 * networks implemented by this class are based on the {@link Network} class and
 * make use of the speed of its static implementation as well as its ability to
 * hide nodes and edges.
 */
public class ResidualNetwork extends Network {

    /**
     * The underlying base network.
     */
    private Network network;
    /** 
     * The number of edges that the original Network had (without the residual edges)
     */
    private int originalNumberOfEdges;
    /**
     * The flow associated with this residual network.
     */
    private IdentifiableIntegerMapping<Edge> flow;
    /**
     * The residual capacities of this residual network.
     */
    private IdentifiableIntegerMapping<Edge> residualCapacities;
    /**
     * The residual transit times of this residual network.
     */
    private IdentifiableIntegerMapping<Edge> residualTransitTimes;

    /**
     * A constructor for clone and overriding classes.
     */
    protected ResidualNetwork(int initialNodeCapacity, int initialEdgeCapacity) {
        super(initialNodeCapacity, initialEdgeCapacity);

        originalNumberOfEdges = initialEdgeCapacity;
    }

    /**
     * Creates a new residual network, based on the specified network, the 
     * zero flow and the specidied capacities.
     * @param network the base network for the residual network.
     * @param capacities the base capacities for the residual network.
     */
    public ResidualNetwork(Network network, IdentifiableIntegerMapping<Edge> capacities) {
        super(network.numberOfNodes(), network.numberOfEdges() * 2);
        originalNumberOfEdges = network.numberOfEdges();

        setNodes(network.nodes());
        setEdges(network.edges());
        for (Edge edge : network.edges()) {
            createAndSetEdge(edge.end(), edge.start());
        }
        this.network = network;
        flow = new IdentifiableIntegerMapping<Edge>(network.numberOfEdges());
        residualCapacities = new IdentifiableIntegerMapping<Edge>(network.numberOfEdges() * 2);
        for (Edge edge : edges) {
            if (isReverseEdge(edge)) {
                residualCapacities.set(edge, 0);
                setHidden(edge, true);
            } else {
                flow.set(edge, 0);
                residualCapacities.set(edge, capacities.get(edge));
            }
        }
    }

    /**
     * Creates a new residual network, based on the specified network, the 
     * zero flow and the specidied capacities and transit times.
     * @param network the base network for the residual network.
     * @param capacities the base capacities for the residual network.
     * @param transitTimes the base transit times for the residual network.
     */
    public ResidualNetwork(Network network, IdentifiableIntegerMapping<Edge> capacities, IdentifiableIntegerMapping<Edge> transitTimes) {
        this(network, capacities);
        residualTransitTimes = expandCostFunction(transitTimes);
        /*
        residualTransitTimes = new IdentifiableIntegerMapping<Edge>(network.numberOfEdges() * 2);
        for (Edge edge : edges) {
            System.out.println(edge);
            if (isReverseEdge(edge)) {
                residualTransitTimes.set(edge, -transitTimes.get(edge));
            } else {
                residualTransitTimes.set(edge, transitTimes.get(edge));
            }
        }*/
    }

    /**
     * Augments a specified amount of flow along the specified edge. The 
     * residual capacities of the edge and its reverse edge are updated 
     * automatically. The residual network is updated as well, if neccessary.
     * Runtime O(1).
     * @param edge the edge along which flow is to be augmented.
     * @param amount the amount of flow to augment.
     */
    public void augmentFlow(Edge edge, int amount) {
        Edge reverseEdge = reverseEdge(edge);
        if (isReverseEdge(edge)) {
            flow.decrease(reverseEdge, amount);
        } else {
            flow.increase(edge, amount);
        }
        residualCapacities.decrease(edge, amount);
        residualCapacities.increase(reverseEdge, amount);
        if (0 == residualCapacities.get(edge)) {
            setHidden(edge, true);
        }
        if (0 < residualCapacities.get(reverseEdge)) {
            setHidden(reverseEdge, false);
        }
    }

    /**
     * Returns the capacities of the edges in this residual network (with regard
     * to the flow associated with the network). Runtime O(1).
     * @return the capacities of the edges in this residual network.
     */
    public IdentifiableIntegerMapping<Edge> residualCapacities() {
        return residualCapacities;
    }

    /**
     * Returns the transit times in this residual network (with regard
     * to the flow associated with the network). Returns <code>null</code> if
     * this network has been created without transit times. Runtime O(1).
     * @return the transit times in this residual network.
     */
    public IdentifiableIntegerMapping<Edge> residualTransitTimes() {
        return residualTransitTimes;
    }

    /**
     * Returns the flow associated with this residual network. Runtime O(1).
     * @return the flow associated with this residual network.
     */
    public IdentifiableIntegerMapping<Edge> flow() {
        return flow;
    }

    /**
     * Returns the reverse edge of the specified edge. Runtime O(1).
     * @param edge the edge for which the reverse edge is to be returned. 
     * @return the reverse edge of the specified edge. Runtime O(1).
     */
    public Edge reverseEdge(Edge edge) {
        if (edge.id() < originalNumberOfEdges) {
            return edges.getEvenIfHidden(edge.id() + originalNumberOfEdges);
        } else {
            return edges.getEvenIfHidden(edge.id() - originalNumberOfEdges);
        }
    }

    /**
     * Checks is whether the specified edge is a reverse edge. An edge is called
     * reverse if it does not exist in the original network. Runtime O(1).
     * @param edge the edge to be tested.
     * @return <code>true</code> if the specified edge is a reverse edge,
     * <code>false</code> otherwise.
     */
    public boolean isReverseEdge(Edge edge) {
        return edge.id() >= originalNumberOfEdges;
    }

    /**
     * This method expand the given cost function over some network to cover 
     * also the residual network
     * @param costs The old cost function to be expanded
     * @return an new costs function that is identical with the old function
     * on the old domain. On all other edges in the residual network it returns
     * either the ngated cost of the oposite edge if it exists or 0.
     */
    public IdentifiableIntegerMapping<Edge> expandCostFunction(IdentifiableIntegerMapping<Edge> costs) {
        IdentifiableIntegerMapping<Edge> result = new IdentifiableIntegerMapping<Edge>(getEdgeCapacity());
        for (int id = 0; id < getEdgeCapacity(); id++) {
            Edge edge = edges.getEvenIfHidden(id);
            if (isReverseEdge(edge)) {
                result.set(edge, -costs.get(reverseEdge(edge)));
            } else {
                result.set(edge, costs.get(edge));
            }
        }
        return result;
    }    
    
    /**
     * Creates a copy of this residual network.
     * @return a copy of this residual network.
     */
    @Override
    public ResidualNetwork clone() {
        ResidualNetwork clone = new ResidualNetwork(getNodeCapacity(), getEdgeCapacity());
        boolean[] hidden = new boolean[getEdgeCapacity()];
        for (int i = 0; i < getEdgeCapacity(); i++) {
            hidden[i] = edges.isHidden(i);
        }
        edges.showAll();
        clone.setNodes(nodes);
        clone.setEdges(edges);
        for (int i = 0; i < getEdgeCapacity(); i++) {
            edges.setHidden(i, hidden[i]);
            clone.edges.setHidden(i, hidden[i]);
        }
        clone.network = network;
        clone.flow = flow.clone();
        clone.residualCapacities = residualCapacities.clone();
        if (residualTransitTimes != null) {
            clone.residualTransitTimes = residualTransitTimes.clone();
        }
        return clone;
    }

    /**
     * Compares this object with the specified object. If the specified object
     * is equivalent to this one <code>true</code> is returned, <code>false
     * </code> otherwise. A object is considered equivalent if and only if it is
     * a residual network with equals components (nodes, edges, base network,
     * flow, ...). Runtime O(n + m). 
     * @param o the object to compare this one to.
     * @return <code>true</code> if the specified object
     * is equivalent to this one, <code>false
     * </code> otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof ResidualNetwork) {
            ResidualNetwork rn = (ResidualNetwork) o;
            if (residualTransitTimes == null) {
                return network.equals(rn.network) && residualCapacities.equals(rn.residualCapacities) && super.equals(o);
            } else {
                return network.equals(rn.network) && residualCapacities.equals(rn.residualCapacities) && residualTransitTimes.equals(rn.residualTransitTimes);
            }
        } else {
            return false;
        }
    }

    /**
     * Returns a hash code for this residual network.
     * Runtime O(n + m).
     * @return a hash code computed by the sum of the hash codes of its 
     * components.
     */
    @Override
    public int hashCode() {
        int hashCode = super.hashCode() + network.hashCode() + residualCapacities.hashCode();
        return hashCode;
    }

    /**
     * Returns a string representation of this residual network. The 
     * representation consists of the underlying base network, the nodes and
     * edges of this residual network and its residual capacities 
     * (and transit times, if it has them). Runtime O(n + m).
     * @return  a string representation of this residual network.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Base network: " + network.toString() + "\n");
        builder.append("Residual network: " + super.toString() + "\n");
        builder.append("Residual capacities: " + Mappings.toString(edges(), residualCapacities) + "\n");
        if (residualTransitTimes != null) {
            builder.append("Residual transit times: " + residualTransitTimes.toString() + "\n");
        }
        return builder.toString();
    }
}
