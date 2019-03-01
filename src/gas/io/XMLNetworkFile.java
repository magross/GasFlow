/*
 * XMLNetworkFile.java
 * 
 * 
 */
package gas.io;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import ds.graph.DynamicNetwork;
import ds.graph.GasEdge;
import ds.graph.GasNode;
import javax.measure.quantity.Volume;
import static javax.measure.unit.SI.CUBIC_METRE;
import org.jscience.physics.amount.Amount;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author Martin Groß
 */
public abstract class XMLNetworkFile<C extends XMLConnection,I extends XMLIntersection> extends XMLFile {

    public enum Type {

        GAS, WATER;
    }

    protected  XMLConnections<C> connections;
    protected  transient GraphConversion<I,C> conversion;
    /**
     * Stores the
     */
    protected  XMLInformation information;
    protected  XMLIntersections<I> intersections;
    
    public XMLNetworkFile() {
    }

    public void initializeFrom(GraphConversion<I,C> graphConversion) {
        information = graphConversion.getInformation();
        intersections = createIntersections();
        for (I intersection : graphConversion.getNodeIntersections().values()) {
            intersections.getMap().put(intersection.getId(), intersection);
        }
        connections = createConnections(intersections);
        for (C connection : graphConversion.getEdgeConnections().values()) {
            connections.getMap().put(connection.getId(), connection);
        }
    }

    public void writeTo(Document document, Element parent) {
        Element informationElement = document.createElement("framework:information");
        information.writeTo(document, informationElement);
        parent.appendChild(informationElement);
        Element nodesElement = document.createElement("framework:nodes");
        intersections.writeTo(document, nodesElement);
        parent.appendChild(nodesElement);
        Element connectionsElement = document.createElement("framework:connections");
        connections.writeTo(document, connectionsElement);
        parent.appendChild(connectionsElement);
    }

    public XMLConnections<C> getConnections() {
        return connections;
    }

    public XMLInformation getInformation() {
        return information;
    }

    public XMLIntersections<I> getIntersections() {
        return intersections;
    }

    @Override
    protected void parseChild(Node domXMLNode) {
        switch (domXMLNode.getNodeName()) {
            case "framework:information":
                information = new XMLInformation();
                information.initializeFrom(domXMLNode);
                return;
            case "framework:nodes":
                intersections = createIntersections();
                intersections.initializeFrom(domXMLNode);
                break;
            case "framework:connections":
                connections = createConnections(intersections);
                connections.initializeFrom(domXMLNode);
                break;
            default:
                throw new AssertionError("Unexpected node name: " + domXMLNode.getNodeName());
        }
    }

    protected abstract XMLIntersections<I> createIntersections();

    protected abstract XMLConnections<C> createConnections(XMLIntersections<I> intersections);

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("network");
    } 
    
    public int numberOf(ConnectionType connectionType) {
        return connections.numberOf(connectionType);
    }
    
    public int numberOfConnections() {
        return connections.numberOfConnections();
    }
    
    public int numberOfIntersections() {
        return intersections.numberOfIntersections();
    }    
    
    public GraphConversion<I,C> getDynamicNetwork() {
        if (conversion == null) {
            conversion = extractDynamicNetwork();
        }
        return conversion;
    }
    
    protected GraphConversion<I,C> extractDynamicNetwork() {
        DynamicNetwork<GasNode,GasEdge> network = new DynamicNetwork<>(GasEdge::createEdge);
        BiMap<GasNode, XMLIntersection> nodeIntersections = HashBiMap.create();
        BiMap<String, GasNode> intersectionNode = HashBiMap.create();
        BiMap<GasEdge, XMLConnection> edgeConnections = HashBiMap.create();
        for (XMLIntersection intersection : getIntersections().getMap().values()) {
            GasNode node = new GasNode();
            node.setHeight(intersection.getHeight());
            network.addNode(node);
            nodeIntersections.put(node, intersection);
            intersectionNode.put(intersection.getId(), node);
        }
        for (XMLConnection connection : getConnections().getMap().values()) {
            GasNode fromNode = intersectionNode.get(connection.getFrom().getId());
            GasNode toNode = intersectionNode.get(connection.getTo().getId());
            GasEdge edge = new GasEdge(fromNode, toNode);
            edge.setFlowMax(connection.getFlowMax());
            edge.setFlowMin(connection.getFlowMin());
            if (connection instanceof Pipe) {
                edge.setDiameter(((Pipe) connection).getDiameter());
                edge.setLength(((Pipe) connection).getLength());
                edge.setRoughness(((Pipe) connection).getRoughness());
            }
            network.addEdge(edge);
            edgeConnections.put(edge, connection);
        }
        int index = 0;
        for (GasNode node : network.nodes()) {
            Amount<Volume> volume = Amount.valueOf(0, CUBIC_METRE);
            for (GasEdge edge : network.incidentEdges(node)) {
                volume = volume.plus(edge.getHalfVolume());
            }
            node.setVolume(volume);
            node.setId(index);
            index++;
        }     
        index = 0;
        for (GasEdge edge : network.edges()) {
            edge.setId(index);
            index++;
        }
        return new GraphConversion(information, network, nodeIntersections, edgeConnections);
    }
}
