/*
 * GasLibNetworkFile.java
 * 
 * 
 */
package gas.io.gaslib;

import ds.graph.DynamicNetwork;
import ds.graph.GasEdge;
import ds.graph.GasNode;
import static gas.algo.propagation.BoundPropagation.DEBUG;
import static gas.io.ConnectionType.*;
import gas.io.GraphConversion;
import gas.io.IntersectionType;
import gas.io.XMLConnections;
import gas.io.XMLIntersections;
import gas.io.XMLNetworkFile;
import gas.quantity.MolarMass;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import units.UnitsTools;
import units.qual.*;

/**
 *
 * @author Martin Groß
 */
public class GasLibNetworkFile extends XMLNetworkFile<GasLibConnection, GasLibIntersection> {
    
    public static final boolean DEBUG = false;

    private double meanMolarMass;
    private double meanTemperature;
    private double meanPseudocriticalPressure;
    private double meanPseudocriticalTemperature;
    private int numberOfSources = -1;
    private int numberOfSinks = -1;

    public GasLibNetworkFile() {
    }

    public GasLibNetworkFile(String fileName) {
        readFromFile(fileName);
    }

    public @K double getMeanTemperature() {
        if (meanTemperature == 0) {
            GraphConversion<GasLibIntersection, GasLibConnection> conversion = getDynamicNetwork();
            Map<GasNode, GasLibIntersection> nodeIntersections = (Map) conversion.getNodeIntersections();
            double sum = 0.0;
            int count = 0;
            for (GasLibIntersection i : nodeIntersections.values()) {
                if (i instanceof GasLibSource) {
                    sum += UnitsTools.C_to_K(((GasLibSource) i).getGasTemperature());
                    ++count;
                }
            }
            meanTemperature = sum / count * UnitsTools.K;
        }
        return meanTemperature;
    }

    public @Dimensionless double getMeanReducedTemperature() {
        return getMeanTemperature()/getMeanPseudocriticalTemperature();
    }

    public @bar double getMeanPseudocriticalPressure() {
        if (meanPseudocriticalPressure == 0) {
            GraphConversion<GasLibIntersection, GasLibConnection> conversion = getDynamicNetwork();
            Map<GasNode, GasLibIntersection> nodeIntersections = (Map) conversion.getNodeIntersections();
            double sum = 0.0;
            int count = 0;
            for (GasLibIntersection i : nodeIntersections.values()) {
                if (i instanceof GasLibSource) {
                    sum += ((GasLibSource) i).getPseudocriticalPressure();
                    ++count;
                }
            }
            meanPseudocriticalPressure = sum / count * UnitsTools.bar;
        }
        return meanPseudocriticalPressure;
    }

    public @K double getMeanPseudocriticalTemperature() {
        if (meanPseudocriticalTemperature == 0) {
            GraphConversion<GasLibIntersection, GasLibConnection> conversion = getDynamicNetwork();
            Map<GasNode, GasLibIntersection> nodeIntersections = (Map) conversion.getNodeIntersections();
            double sum = 0.0;
            int count = 0;
            for (GasLibIntersection i : nodeIntersections.values()) {
                if (i instanceof GasLibSource) {
                    sum += ((GasLibSource) i).getPseudocriticalTemperature();
                    ++count;
                }
            }
            meanPseudocriticalTemperature = sum / count * UnitsTools.K;
        }
        return meanPseudocriticalTemperature;
    }

    public @gPERmol double getMeanMolarMass() {
        if (meanMolarMass == 0) {
            GraphConversion<GasLibIntersection, GasLibConnection> conversion = getDynamicNetwork();
            Map<GasNode, GasLibIntersection> nodeIntersections = (Map) conversion.getNodeIntersections();
            double sum = 0.0;
            int count = 0;
            for (GasLibIntersection i : nodeIntersections.values()) {
                if (i instanceof GasLibSource) {
                    sum += ((GasLibSource) i).getMolarMass();
                    ++count;
                }
            }
            meanMolarMass = sum / count * UnitsTools.g/UnitsTools.mol;
        }
        return meanMolarMass;
    }

    public double getMeanSpecificGasConstant() {
        return UnitsTools.R/getMeanMolarMass();
    }

    public int getNumberOfSources() {
        if (numberOfSources == -1) {
            GraphConversion<GasLibIntersection, GasLibConnection> conversion = getDynamicNetwork();
            Map<GasNode, GasLibIntersection> nodeIntersections = (Map) conversion.getNodeIntersections();
            numberOfSources = (int) nodeIntersections.values().stream().filter(i -> IntersectionType.getType(i) == IntersectionType.SOURCE).count();
        }
        return numberOfSources;
    }

    public int getNumberOfSinks() {
        if (numberOfSinks == -1) {
            GraphConversion<GasLibIntersection, GasLibConnection> conversion = getDynamicNetwork();
            Map<GasNode, GasLibIntersection> nodeIntersections = (Map) conversion.getNodeIntersections();
            numberOfSinks = (int) nodeIntersections.values().stream().filter(i -> IntersectionType.getType(i) == IntersectionType.SINK).count();
        }
        return numberOfSinks;
    }

    public int getNumberOfTerminals() {
        return getNumberOfSources() + getNumberOfSinks();
    }

    public void printElements(PrintStream out) {
        GraphConversion<GasLibIntersection, GasLibConnection> conversion = getDynamicNetwork();
        DynamicNetwork<GasNode, GasEdge> network = (DynamicNetwork) conversion.getGraph();
        Map<GasEdge, GasLibConnection> edgeConnections = (Map) conversion.getEdgeConnections();
        Map<GasNode, GasLibIntersection> nodeIntersections = (Map) conversion.getNodeIntersections();
        int valve = 0, controlvalve = 0, compressor = 0, resistor = 0, pipe = 0;
        for (GasEdge edge : network.edges()) {
            if (edgeConnections.get(edge) == null) {
                continue;
            }
            switch (edgeConnections.get(edge).getType()) {
                case PIPE:
                case SHORT_PIPE:
                    pipe++;
                    break;
                case RESISTOR:
                    resistor++;
                    break;
                case COMPRESSOR_STATION:
                    compressor++;
                    break;
                case CONTROL_VALVE:
                    controlvalve++;
                    break;
                case VALVE:
                    valve++;
                    break;
            }
        }
        out.println("  Nodes: " + network.numberOfNodes());
        out.println("  Edges: " + network.numberOfEdges());
        out.println("  Pipes: " + pipe);
        out.println("  Control Valves: " + controlvalve);
        out.println("  Compressors: " + compressor);
        out.println("  Resistors: " + resistor);
        out.println("  Valves: " + valve);
    }

    @Override
    protected XMLIntersections<GasLibIntersection> createIntersections() {
        return new GasLibIntersections();
    }

    @Override
    protected XMLConnections<GasLibConnection> createConnections(XMLIntersections<GasLibIntersection> intersections) {
        return new GasLibConnections(intersections);
    }

    public void removeNodeAndIntersection(GasNode node) {
        DynamicNetwork<GasNode, GasEdge> graph = conversion.getGraph();

        graph.removeNode(node);

        GasLibIntersection i = conversion.getNodeIntersections().get(node);
        if (i != null) {
            intersections.getMap().inverse().remove(i);
        }
        if (DEBUG) {
            System.out.println("GLNF: Removing intersection " + i.getId() + " (Node "+ node + ")");
        }

    }

    public void removeEdgeAndConnection(GasEdge edge) {
        DynamicNetwork<GasNode, GasEdge> graph = conversion.getGraph();
        GasLibConnection c = conversion.getEdgeConnections().get(edge);
        graph.removeEdge(edge);
        connections.getMap().inverse().remove(c);
        if (DEBUG) System.out.println("GNLF: Removing " + c.getId() + " from " + c.getFrom().getId() + " to " + c.getTo().getId() + " (Edge: " + edge + ")");
    }

    int index = 0;

    public GasLibConnection addEdgeAndConnection(GasEdge edge, double beta) {
        
        GasNode start = edge.start();
        GasLibIntersection startI = conversion.getNodeIntersections().get(start);
        String startIId = getIntersections().getMap().inverse().get(startI);
        GasNode end = edge.end();
        GasLibIntersection endI = conversion.getNodeIntersections().get(end);
        String endIId = getIntersections().getMap().inverse().get(endI);
        
        double minOfMax = Math.min(startI.getPressureMax(), endI.getPressureMax());
        double maxOfMin = Math.max(startI.getPressureMin(), endI.getPressureMin());
        double meanPressure = 0.5 * (minOfMax + maxOfMin) * UnitsTools.bar;

        double tr = getMeanReducedTemperature();
        double pr = meanPressure/getMeanPseudocriticalPressure();
        double z = 1 - 3.52 * pr * Math.exp(-2.26 * tr) + 0.247 * pr * pr * Math.exp(-1.878 * tr);

        double diameter = 500 * UnitsTools.mm;
        double area = (diameter/2)*(diameter/2)*Math.PI;

        double length = beta*-1.0*diameter*area*area
                /getMeanSpecificGasConstant()/getMeanTemperature()/z/(1.0 / 83.50344);

        if (UnitsTools.mm_to_m(length) < 0.0) {
            if (true) System.err.println("Negative Length: ");
            if (true) System.out.println(length + " " + beta + " " + diameter + " " + area + " " + getMeanSpecificGasConstant() + " " + getMeanTemperature() + " " + z);
        } else if (UnitsTools.mm_to_m(length) == 0.0) {
            if (DEBUG) System.err.println("Zero Length: ");
            if (DEBUG) System.out.println(length + " " + beta + " " + diameter + " " + area + " " + getMeanSpecificGasConstant() + " " + getMeanTemperature() + " " + z);
            length = 0.01 * UnitsTools.mm;
        } else {
            if (DEBUG) System.out.println("Positive Length: ");
            if (DEBUG) System.out.println(length + " " + beta + " " + diameter + " " + area + " " + getMeanSpecificGasConstant() + " " + getMeanTemperature() + " " + z);
        }
        
        
        GasLibPipe c = new GasLibPipe("newpipe_" + (++index), startI, endI, startIId, endIId, length);
        c.createProperties();

        startI.getConnections().add(c);
        endI.getConnections().add(c);

        connections.getMap().put(c.getId(), c);
        if (DEBUG) System.out.println("GLNF: Connection added " + c.getId() + " from " + c.getFrom().getId() + " to " + c.getTo().getId() + " (Edge: " + edge + ")");
        //System.out.println(connections.getMap().get(c.getId()));
        return c;
    }
    


    public boolean validate() {
        if (!DEBUG) {
            return true;
        }        
        List<String> intersectionIDs = getIntersections().getMap().values().stream().map(i -> i.getId()).collect(Collectors.toList());
        Set<GasLibConnection> connections = getConnections().getMap().values();
        for (GasLibConnection c : connections) {
            if (!intersectionIDs.contains(c.getFrom().getId())) {
                System.out.println("ALERT! ALERT! " + c.getId() + " from " + c.getFrom().getId() + " to " + c.getTo().getId() + ": " + c.getFrom().getId());
                return false;
            }
            if (!intersectionIDs.contains(c.getTo().getId())) {
                System.out.println("ALERT! ALERT! " + c.getId() + " from " + c.getFrom().getId() + " to " + c.getTo().getId() + ": " + c.getTo().getId());
                return false;
            }
        }
        return true;
    }

    public void updateEdgeConnection(GasEdge edge, double beta) {
        GasLibPipe edgeC = (GasLibPipe) conversion.getConnection(edge);

        double meanPressure = edgeC.getMeanPressure();

        double tr = getMeanReducedTemperature();
        double pr = meanPressure/getMeanPseudocriticalPressure();
        double z = 1 - 3.52 * pr * Math.exp(-2.26 * tr) + 0.247 * pr * pr * Math.exp(-1.878 * tr);

        double diameter = edgeC.getDiameter();
        double area = (diameter/2)*(diameter/2)*Math.PI;

        double length = beta*-1.0*diameter*area*area
                /getMeanSpecificGasConstant()/getMeanTemperature()/z/(1.0 / 83.50344);

        edgeC.setLength(length);

        if (DEBUG) System.out.println("GLNF: Connection updated");
    }

    public void update(Map<GasEdge, Double> edgeParameters) {
        if (DEBUG) System.out.println("GasLibNetworkFile: Updating!");

        GraphConversion<GasLibIntersection, GasLibConnection> conversion = getDynamicNetwork();

        if (DEBUG) System.out.println(conversion.getNodeIntersections().size());

        Queue<String> delete = new LinkedList<>();
        for (String originalNodeId : getIntersections().getMap().keySet()) {
            GasLibIntersection i = getIntersections().getMap().get(originalNodeId);
            if (!conversion.getNodeIntersections().containsValue(i)) {
                delete.add(originalNodeId);
                if (DEBUG) System.out.println("GasLibNetworkFile: deleting " + originalNodeId);
            }
        }
        while (!delete.isEmpty()) {
            getIntersections().getMap().remove(delete.poll());
        }

        for (String originalEdgeId : getConnections().getMap().keySet()) {
            GasLibConnection i = getConnections().getMap().get(originalEdgeId);
            if (!conversion.getEdgeConnections().containsValue(i)) {
                delete.add(originalEdgeId);
                if (DEBUG) System.out.println("GasLibNetworkFile: deleting " + originalEdgeId);
            }
        }
        while (!delete.isEmpty()) {
            getConnections().getMap().remove(delete.poll());
        }
    }

    //public 
    public void changeNodeTo(GasLibIntersection neighborI, GasLibIntersection candidateI) {
    }

}
