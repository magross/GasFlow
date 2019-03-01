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
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Temperature;
import static javax.measure.unit.NonSI.BAR;
import javax.measure.unit.SI;
import static javax.measure.unit.SI.KELVIN;
import static javax.measure.unit.SI.METER;
import org.jscience.physics.amount.Amount;
import org.jscience.physics.amount.Constants;

/**
 *
 * @author Martin Groß
 */
public class GasLibNetworkFile extends XMLNetworkFile<GasLibConnection, GasLibIntersection> {
    
    public static final boolean DEBUG = false;

    private Amount<MolarMass> meanMolarMass;
    private Amount<Temperature> meanTemperature;
    private Amount<Pressure> meanPseudocriticalPressure;
    private Amount<Temperature> meanPseudocriticalTemperature;
    private int numberOfSources = -1;
    private int numberOfSinks = -1;

    public GasLibNetworkFile() {
    }

    public GasLibNetworkFile(String fileName) {
        readFromFile(fileName);
    }

    public Amount<Temperature> getMeanTemperature() {
        if (meanTemperature == null) {
            GraphConversion<GasLibIntersection, GasLibConnection> conversion = getDynamicNetwork();
            Map<GasNode, GasLibIntersection> nodeIntersections = (Map) conversion.getNodeIntersections();
            double sum = 0.0;
            int count = 0;
            for (GasLibIntersection i : nodeIntersections.values()) {
                if (i instanceof GasLibSource) {
                    sum += ((GasLibSource) i).getGasTemperature().doubleValue(KELVIN);
                    ++count;
                }
            }
            meanTemperature = Amount.valueOf(sum / count, KELVIN);
        }
        return meanTemperature;
    }

    public Amount<Dimensionless> getMeanReducedTemperature() {
        return (Amount<Dimensionless>) getMeanTemperature().divide(getMeanPseudocriticalTemperature());
    }

    public Amount<Pressure> getMeanPseudocriticalPressure() {
        if (meanPseudocriticalPressure == null) {
            GraphConversion<GasLibIntersection, GasLibConnection> conversion = getDynamicNetwork();
            Map<GasNode, GasLibIntersection> nodeIntersections = (Map) conversion.getNodeIntersections();
            double sum = 0.0;
            int count = 0;
            for (GasLibIntersection i : nodeIntersections.values()) {
                if (i instanceof GasLibSource) {
                    sum += ((GasLibSource) i).getPseudocriticalPressure().doubleValue(BAR);
                    ++count;
                }
            }
            meanPseudocriticalPressure = Amount.valueOf(sum / count, BAR);
        }
        return meanPseudocriticalPressure;
    }

    public Amount<Temperature> getMeanPseudocriticalTemperature() {
        if (meanPseudocriticalTemperature == null) {
            GraphConversion<GasLibIntersection, GasLibConnection> conversion = getDynamicNetwork();
            Map<GasNode, GasLibIntersection> nodeIntersections = (Map) conversion.getNodeIntersections();
            double sum = 0.0;
            int count = 0;
            for (GasLibIntersection i : nodeIntersections.values()) {
                if (i instanceof GasLibSource) {
                    sum += ((GasLibSource) i).getPseudocriticalTemperature().doubleValue(KELVIN);
                    ++count;
                }
            }
            meanPseudocriticalTemperature = Amount.valueOf(sum / count, KELVIN);
        }
        return meanPseudocriticalTemperature;
    }

    public Amount<MolarMass> getMeanMolarMass() {
        if (meanMolarMass == null) {
            GraphConversion<GasLibIntersection, GasLibConnection> conversion = getDynamicNetwork();
            Map<GasNode, GasLibIntersection> nodeIntersections = (Map) conversion.getNodeIntersections();
            double sum = 0.0;
            int count = 0;
            for (GasLibIntersection i : nodeIntersections.values()) {
                if (i instanceof GasLibSource) {
                    sum += ((GasLibSource) i).getMolarMass().doubleValue(MolarMass.UNIT);
                    ++count;
                }
            }
            meanMolarMass = Amount.valueOf(sum / count, MolarMass.UNIT);
        }
        return meanMolarMass;
    }

    public Amount getMeanSpecificGasConstant() {
        return Constants.R.divide(getMeanMolarMass());
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
        //GraphConversion<GasLibIntersection, GasLibConnection> conversion = getDynamicNetwork();
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
        //GraphConversion<GasLibIntersection, GasLibConnection> conversion = getDynamicNetwork();
        DynamicNetwork<GasNode, GasEdge> graph = conversion.getGraph();
        GasLibConnection c = conversion.getEdgeConnections().get(edge);
        graph.removeEdge(edge);
        connections.getMap().inverse().remove(c);
        if (DEBUG) System.out.println("GNLF: Removing " + c.getId() + " from " + c.getFrom().getId() + " to " + c.getTo().getId() + " (Edge: " + edge + ")");
    }

    int index = 0;

    public GasLibConnection addEdgeAndConnection(GasEdge edge, Amount beta) {
        
        GasNode start = edge.start();
        GasLibIntersection startI = conversion.getNodeIntersections().get(start);
        String startIId = getIntersections().getMap().inverse().get(startI);
        GasNode end = edge.end();
        GasLibIntersection endI = conversion.getNodeIntersections().get(end);
        String endIId = getIntersections().getMap().inverse().get(endI);

        //System.out.println("GLNF: " + edge.toString() + " " + startIId + " " + endIId);
        
        double minOfMax = Math.min(startI.getPressureMax().doubleValue(BAR), endI.getPressureMax().doubleValue(BAR));
        double maxOfMin = Math.max(startI.getPressureMin().doubleValue(BAR), endI.getPressureMin().doubleValue(BAR));
        Amount meanPressure = Amount.valueOf(0.5 * (minOfMax + maxOfMin), BAR);

        double tr = getMeanReducedTemperature().doubleValue(Dimensionless.UNIT);
        double pr = meanPressure.divide(getMeanPseudocriticalPressure()).doubleValue(Dimensionless.UNIT);
        double z = 1 - 3.52 * pr * Math.exp(-2.26 * tr) + 0.247 * pr * pr * Math.exp(-1.878 * tr);

        Amount diameter = Amount.valueOf(500, SI.MILLIMETER);
        Amount area = diameter.divide(2.0).pow(2).times(Math.PI);

        Amount length = beta.times(-1.0).times(diameter).times(area).times(area)
                .divide(getMeanSpecificGasConstant()).divide(getMeanTemperature()).divide(z).divide(1.0 / 83.50344);

        if (length.doubleValue(METER) < 0.0) {
            if (true) System.err.println("Negative Length: ");
            if (true) System.out.println(length + " " + beta + " " + diameter + " " + area + " " + getMeanSpecificGasConstant() + " " + getMeanTemperature() + " " + z);
        } else if (length.doubleValue(METER) == 0.0) {
            if (DEBUG) System.err.println("Zero Length: ");
            if (DEBUG) System.out.println(length + " " + beta + " " + diameter + " " + area + " " + getMeanSpecificGasConstant() + " " + getMeanTemperature() + " " + z);
            length = Amount.valueOf("0.01 mm");
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

    public void updateEdgeConnection(GasEdge edge, Amount beta) {
        //GasNode start = edge.start();
        //GasLibIntersection startI = conversion.getNodeIntersections().get(start);
        //String startIId = getIntersections().getMap().inverse().get(startI);
        //GasNode end = edge.end();
        //GasLibIntersection endI = conversion.getNodeIntersections().get(end);
        //String endIId = getIntersections().getMap().inverse().get(endI);

        GasLibPipe edgeC = (GasLibPipe) conversion.getConnection(edge);

        //connections.equals(end)get
        //double minOfMax = Math.min(startI.getPressureMax().doubleValue(BAR), endI.getPressureMax().doubleValue(BAR));
        //double maxOfMin = Math.max(startI.getPressureMin().doubleValue(BAR), endI.getPressureMin().doubleValue(BAR));
        Amount meanPressure = edgeC.getMeanPressure();// Amount.valueOf(0.5 * (minOfMax + maxOfMin), BAR);

        double tr = getMeanReducedTemperature().doubleValue(Dimensionless.UNIT);
        double pr = meanPressure.divide(getMeanPseudocriticalPressure()).doubleValue(Dimensionless.UNIT);
        double z = 1 - 3.52 * pr * Math.exp(-2.26 * tr) + 0.247 * pr * pr * Math.exp(-1.878 * tr);

        Amount diameter = edgeC.getDiameter();// Amount.valueOf(500, SI.MILLIMETER);
        Amount area = diameter.divide(2.0).pow(2).times(Math.PI);

        Amount length = beta.times(-1.0).times(diameter).times(area).times(area)
                .divide(getMeanSpecificGasConstant()).divide(getMeanTemperature()).divide(z).divide(1.0 / 83.50344);

        edgeC.setLength(length);

        //GasLibPipe c = new GasLibPipe("newpipe_" + (++index), startI, endI, startIId, endIId, length);
        //c.createProperties();
        //startI.getConnections().add(c);
        //endI.getConnections().add(c);
        //connections.getMap().put(c.getId(), c);
        if (DEBUG) System.out.println("GLNF: Connection updated");
        //System.out.println(connections.getMap().get(c.getId()));
    }

    public void update(Map<GasEdge, Amount> edgeParameters) {
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

        /*
        for (GasNode node : conversion.getGraph().nodes()) {
            if (conversion.getIntersection(node) == null) {
                System.out.println(node + " added!.");
            }
            retainedNodes.put(conversion.getIntersection(node), Boolean.TRUE);
        }
        for (GasNode node : conversion.getGraph().nodes()) {
            if (!retainedNodes.get(conversion.getIntersection(node))) {
                System.out.println(node + " deleted!");
            }
        }        
         */
    }

    //public 
    public void changeNodeTo(GasLibIntersection neighborI, GasLibIntersection candidateI) {

        //System.out.println("Try Changing: " + candidateI.getId() + " becomes " + neighborI.getId());   
        /*GasLibIntersection removedI = intersections.getMap().remove(candidateI.getId());
        if (removedI == null) {
            System.out.println("null: " + candidateI.getId());
        }
        GasLibIntersection removedNI = intersections.getMap().remove(neighborI.getId());
        if (removedNI == null) {
            System.out.println("null2: " + neighborI.getId());
        }*/
        //System.out.println("Changing: " + removedI.getId() + " becomes " + removedNI.getId());   
        /*
        candidateI.setId(neighborI.getId());
        intersections.getMap().keySet().stream().forEach(System.out::println);
        System.out.println("");
        intersections.getMap().put(candidateI.getId(), neighborI);*/
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
