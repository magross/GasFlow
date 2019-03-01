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
 * DynamicResidualNetwork.java
 *
 */
package ds.graph;

import java.util.List;
import ds.graph.flow.EarliestArrivalAugmentingPath;
import ds.graph.flow.EarliestArrivalAugmentingPath.NodeTimePair;

/**
 *
 * @author Martin Groß
 */
public class DynamicResidualNetwork extends Network {

    private IdentifiableObjectMapping<Edge, IntegerIntegerMapping> flow;
    private Network network;
    private IdentifiableObjectMapping<Edge, IntegerIntegerArrayMapping> residualEdgeCapacities;
    private IdentifiableObjectMapping<Node, IntegerIntegerArrayMapping> residualWaitCapacities;
    private IdentifiableObjectMapping<Node, IntegerIntegerArrayMapping> residualWaitCancellingCapacities;
    private IdentifiableIntegerMapping<Edge> residualTransitTimes;
    //private IdentifiableObjectMapping<Edge, Boolean> isReverseEdge;
    //private IdentifiableObjectMapping<Edge, Edge> reverseEdge;
    private int originalNumberOfEdges;
/*
    @Deprecated
    public DynamicResidualNetwork(Network network, IdentifiableIntegerMapping<Edge> edgeCapacities, IdentifiableIntegerMapping<Node> nodeCapacities, IdentifiableIntegerMapping<Edge> transitTimes) {
        super(network.numberOfNodes(), network.numberOfEdges() * 2);
        originalNumberOfEdges = network.numberOfEdges();
        setNodes(network.nodes());
        setEdges(network.edges());
        for (Edge edge : network.edges()) {
            createAndSetEdge(edge.end(), edge.start());
        }
        this.network = network;
        flow = new IdentifiableObjectMapping<Edge, IntegerIntegerMapping>(network.edges(), IntegerIntegerMapping.class);
        residualEdgeCapacities = new IdentifiableObjectMapping<Edge, IntegerIntegerMapping>(edges(), IntegerIntegerMapping.class);
        residualTransitTimes = new IdentifiableIntegerMapping<Edge>(edges());
        for (Edge edge : edges) {
            if (isReverseEdge(edge)) {
                residualEdgeCapacities.set(edge, new IntegerIntegerMapping());
            } else {
                flow.set(edge, new IntegerIntegerMapping());
                residualEdgeCapacities.set(edge, new IntegerIntegerMapping());
                residualEdgeCapacities.get(edge).set(0, edgeCapacities.get(edge));
            }
        }
        for (Edge edge : edges) {
            if (isReverseEdge(edge)) {
                residualTransitTimes.set(edge, -transitTimes.get(reverseEdge(edge)));
            } else {
                residualTransitTimes.set(edge, transitTimes.get(edge));
            }
        }
        residualWaitCapacities = new IdentifiableObjectMapping<Node, IntegerIntegerMapping>(nodes(), IntegerIntegerMapping.class);
        residualWaitCancellingCapacities = new IdentifiableObjectMapping<Node, IntegerIntegerMapping>(nodes(), IntegerIntegerMapping.class);
        for (Node node : nodes) {
            residualWaitCapacities.set(node, new IntegerIntegerMapping());
            residualWaitCapacities.get(node).set(0, nodeCapacities.get(node));
            residualWaitCancellingCapacities.set(node, new IntegerIntegerMapping());
        }
    }*/

    private int timeHorizon;
    
    public DynamicResidualNetwork(Network network, IdentifiableIntegerMapping<Edge> edgeCapacities, IdentifiableIntegerMapping<Node> nodeCapacities, IdentifiableIntegerMapping<Edge> transitTimes, List<Node> sources, IdentifiableIntegerMapping<Node> supplies, int timeHorizon) {
        super(network.numberOfNodes() + 1, network.numberOfEdges() * 2 + sources.size());
        //System.out.println(outgoingEdges(new Node(network.numberOfNodes())));        
        this.timeHorizon = timeHorizon;
        originalNumberOfEdges = network.numberOfEdges();
        setNodes(network.nodes());
        superSource = getNode(network.numberOfNodes());
        setEdges(network.edges());
        for (Edge edge : network.edges()) {
            createAndSetEdge(edge.end(), edge.start());
        }
        for (Node source : sources) {
            createAndSetEdge(superSource, source);
        }
        //System.out.println(outgoingEdges(superSource));        
        this.network = network;
        flow = new IdentifiableObjectMapping<Edge, IntegerIntegerMapping>(network.edges(), IntegerIntegerMapping.class);
        residualEdgeCapacities = new IdentifiableObjectMapping<Edge, IntegerIntegerArrayMapping>(edges(), IntegerIntegerArrayMapping.class);
        residualTransitTimes = new IdentifiableIntegerMapping<Edge>(edges());
        for (Edge edge : edges) {
            if (edge.id() >= originalNumberOfEdges && edge.id() < 2 * originalNumberOfEdges) {
                residualEdgeCapacities.set(edge, new IntegerIntegerArrayMapping(timeHorizon));
            } else if (edge.id() < originalNumberOfEdges) {
                flow.set(edge, new IntegerIntegerMapping());
                residualEdgeCapacities.set(edge, new IntegerIntegerArrayMapping(timeHorizon, edgeCapacities.get(edge)));
            } else {
                residualEdgeCapacities.set(edge, new IntegerIntegerArrayMapping(timeHorizon));
                residualTransitTimes.set(edge, 0);
                residualEdgeCapacities.get(edge).set(0, supplies.get(sources.get(edge.id() - 2 * originalNumberOfEdges)));
            }
        }
        for (Edge edge : edges) {
            if (edge.id() >= originalNumberOfEdges && edge.id() < 2 * originalNumberOfEdges) {
                residualTransitTimes.set(edge, -transitTimes.get(reverseEdge(edge)));
            } else if (edge.id() < originalNumberOfEdges) {
                residualTransitTimes.set(edge, transitTimes.get(edge));
            }
        }
        residualWaitCapacities = new IdentifiableObjectMapping<Node, IntegerIntegerArrayMapping>(nodes(), IntegerIntegerArrayMapping.class);
        residualWaitCancellingCapacities = new IdentifiableObjectMapping<Node, IntegerIntegerArrayMapping>(nodes(), IntegerIntegerArrayMapping.class);
        for (Node node : nodes) {
            if (node == superSource) {
                residualWaitCapacities.set(node, new IntegerIntegerArrayMapping(timeHorizon));
                residualWaitCancellingCapacities.set(node, new IntegerIntegerArrayMapping(timeHorizon));
            } else {
                residualWaitCapacities.set(node, new IntegerIntegerArrayMapping(timeHorizon, nodeCapacities.get(node)));
                residualWaitCancellingCapacities.set(node, new IntegerIntegerArrayMapping(timeHorizon));
            }
        }
    }
    private Node superSource;

    public Node getSuperSource() {
        return superSource;
    }

    public Edge getEdge(Node start, Node end, int departure, int arrival) {
        //System.out.println("getEdges: " + start + " " + end + " " + departure + " " + arrival);
        Iterable<Edge> candidates = getEdges(start, end);
        //System.out.println("DRN.getFirstEdge: " + edges);
        Edge result = null;
        for (Edge edge : candidates) {
            /*
            System.out.println(edge + " " + edge.id());
            System.out.println(residualEdgeCapacities.get(edge));
            System.out.println(residualTransitTimes.get(edge));
            System.out.println(residualEdgeCapacities.get(edge).get(departure));
            System.out.println(residualTransitTimes.get(edge).get(departure));
             */ 
            if (departure + residualTransitTimes.get(edge) == arrival/* && residualEdgeCapacities.get(edge).get(departure) > 0*/) {
                result = edge;
                break;
            }
        }
        if (result == null) {
            System.out.println(String.format("DRN.getEdge(%1$s,%2$s,%3$s,%4$s) failed",start,end,departure,arrival));
        }
        return result;
    }

    public int getTimeHorizon() {
        return timeHorizon;
    }
    
    private void augmentEdge(NodeTimePair first, NodeTimePair second, int amount) {
        //System.out.println("Augment: " + first.getNode() + " " + second.getNode() + " " + superSource);
        Edge edge = getEdge(first.getNode(), second.getNode(), first.getEnd(), second.getStart());
        if (edge.id() >= 2 * originalNumberOfEdges) {
            residualEdgeCapacities.get(edge).decrease(first.getEnd(), amount);
        } else {
            Edge reverseEdge = reverseEdge(edge);
            if (isReverseEdge(edge)) {
                flow.get(reverseEdge).decrease(first.getEnd(), amount);
            } else {
                flow.get(edge).increase(first.getEnd(), amount);
            }
            residualEdgeCapacities.get(edge).decrease(first.getEnd(), amount);
            residualEdgeCapacities.get(reverseEdge).increase(second.getStart(), amount);
            /*
            if (!residualEdgeCapacities.get(reverseEdge).isZero()) {
                setHidden(reverseEdge, false);
            }*/            
        }/*
        if (residualEdgeCapacities.get(edge).isZero()) {
            setHidden(edge, true);
        }*/
    }

    private void augmentNode(Node node, int start, int end, int amount) {
        if (start == end) {
            return;
        }
        //System.out.println(" Augment Node " + start + " " + end + " " + amount);
        if (start < end) {
            waitCapacities().get(node).decrease(start, end, amount);
            waitCancellingCapacities().get(node).increase(start+1, end+1, amount);
        } else {
            waitCancellingCapacities().get(node).decrease(end+1, start+1, amount);
            waitCapacities().get(node).increase(end, start, amount);
        }        
        //System.out.println(" WCC: " + waitCancellingCapacities().get(node));
    }

    public void augmentPath(EarliestArrivalAugmentingPath path) {
        if (path.isEmpty()) {
            return;
        }
        NodeTimePair first = path.getFirst();
        augmentNode(first.getNode(), 0, first.getStart(), path.getCapacity());
        for (NodeTimePair ntp : path) {
            if (ntp.getStart() != ntp.getEnd()) {
                augmentNode(ntp.getNode(), ntp.getStart(), ntp.getEnd(), path.getCapacity());
            }
            if (ntp == first) {
                continue;
            } else {
                augmentEdge(first, ntp, path.getCapacity());
                first = ntp;
            }
        }
    }

    public Edge reverseEdge(Edge edge) {
        if (edge.id() < originalNumberOfEdges) {
            return edges.getEvenIfHidden(edge.id() + originalNumberOfEdges);
        } else {
            return edges.getEvenIfHidden(edge.id() - originalNumberOfEdges);
        }
    }

    public boolean isReverseEdge(Edge edge) {
        return edge.id() >= originalNumberOfEdges;
    }

    public IdentifiableObjectMapping<Edge, IntegerIntegerArrayMapping> capacities() {
        return residualEdgeCapacities;
    }

    public IdentifiableIntegerMapping<Edge> transitTimes() {
        return residualTransitTimes;
    }

    public IdentifiableObjectMapping<Edge, IntegerIntegerMapping> flow() {
        return flow;
    }

    public IdentifiableObjectMapping<Node, IntegerIntegerArrayMapping> waitCapacities() {
        return residualWaitCapacities;
    }

    public IdentifiableObjectMapping<Node, IntegerIntegerArrayMapping> waitCancellingCapacities() {
        return residualWaitCancellingCapacities;
    }
}
