/*
 * 
 * 
 * 
 */
package gas.io.water;

import ds.graph.DynamicNetwork;
import ds.graph.GasEdge;
import ds.graph.GasNode;
import gas.io.IntersectionType;
import gas.io.XMLNetworkFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Martin
 */
public class WaterNetworkFileDAT {

    private DynamicNetwork<GasNode, GasEdge> network;
    private Map<GasNode, IntersectionType> nodeTypes;
    private int numberOfNodes;
    private int numberOfPipes;
    private int numberOfSinks;
    private int numberOfSources;
    private int numberOfTerminals;
    private Map<String, GasNode> stringToNode;

    public DynamicNetwork<GasNode, GasEdge> readFromFile(String fileName) {
        List<String> list = new ArrayList<>();
        nodeTypes = new HashMap<>();
        numberOfNodes = 0;
        numberOfPipes = 0;
        numberOfSources = 0;
        numberOfSinks = 0;
        numberOfTerminals = 0;
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            list = stream
                    .filter(line -> (!line.isEmpty() && !line.contains("#") && !line.contains("=") && !line.trim().equals(";")
                            && !line.contains("alias") && !line.contains("set")) || line.contains("source"))
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            Logger.getLogger(XMLNetworkFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        network = new DynamicNetwork<>(GasEdge::createEdge);
        stringToNode = new HashMap<>();
        int i;
        for (i = 2; i < list.size(); i++) {
            if (list.get(i).contains("Table")) {
                i++;
                break;
            }
            String[] parts = list.get(i).trim().split("\\s+");
            GasNode node = new GasNode(i - 2);
            network.addNode(node);
            stringToNode.put(parts[0].trim(), node);
            numberOfNodes++;
            Double demand = Double.parseDouble(parts[2].trim());
            if (demand.equals(0.0)) {
                nodeTypes.put(node, IntersectionType.NODE);
            } else if (demand.compareTo(0.0) > 0) {
                nodeTypes.put(node, IntersectionType.SINK);
                numberOfSinks++;
                numberOfTerminals++;
            } else {
                nodeTypes.put(node, IntersectionType.SOURCE);
                numberOfSources++;
                numberOfTerminals++;
            }
        }
        int edgeIndex = 0;
        for (i++; i < list.size(); i++) {
            if (list.get(i).contains("Table") || list.get(i).contains("source")) {
                break;
            }
            String[] parts = list.get(i).trim().split("\\s+");
            String f, t;
            if (parts.length == 6) {
                f = parts[1].substring(1);
                t = parts[2].substring(1);
            } else {
                f = parts[2];
                t = parts[4];
            }
            GasNode fnode = stringToNode.get(f);
            GasNode tnode = stringToNode.get(t);
            GasEdge edge = new GasEdge(fnode, tnode, edgeIndex);
            network.addEdge(edge);
            edgeIndex++;
            numberOfPipes++;
        }
        String line = list.get(i);
        if (list.get(i).startsWith("source('")) {            
            int first = line.indexOf("'")+1;
            String id = line.substring(first, line.indexOf("'", first));
            if (nodeTypes.get(stringToNode.get(id)).equals(IntersectionType.NODE)) {
                numberOfTerminals++;
            }
            nodeTypes.put(stringToNode.get(id), IntersectionType.SOURCE);
            numberOfSources++;
        } else if (list.get(i).startsWith("set source(N) /") || list.get(i).startsWith("set source(n) /")) {            
            int first = line.indexOf("/")+1;            
            int second = line.indexOf("/", first);
            String segment = line.substring(first, second);
            String[] parts = segment.split(",");
            numberOfSources += parts.length;
            for (String part : parts) {
                nodeTypes.put(stringToNode.get(part), IntersectionType.SOURCE);
            }
        } else {
            System.out.println(list.get(i));
            throw new AssertionError("Unexpected Case.");
        }
        return network;
    }

    public DynamicNetwork<GasNode, GasEdge> getNetwork() {
        return network;
    }

    public void setNetwork(DynamicNetwork<GasNode, GasEdge> network) {
        this.network = network;
    }

    public Map<GasNode, IntersectionType> getNodeTypes() {
        return nodeTypes;
    }

    public void setNodeTypes(Map<GasNode, IntersectionType> nodeTypes) {
        this.nodeTypes = nodeTypes;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    public void setNumberOfNodes(int numberOfNodes) {
        this.numberOfNodes = numberOfNodes;
    }

    public int getNumberOfPipes() {
        return numberOfPipes;
    }

    public void setNumberOfPipes(int numberOfPipes) {
        this.numberOfPipes = numberOfPipes;
    }

    public int getNumberOfSinks() {
        return numberOfSinks;
    }

    public void setNumberOfSinks(int numberOfSinks) {
        this.numberOfSinks = numberOfSinks;
    }

    public int getNumberOfSources() {
        return numberOfSources;
    }

    public void setNumberOfSources(int numberOfSources) {
        this.numberOfSources = numberOfSources;
    }    

    public int getNumberOfTerminals() {
        return numberOfTerminals;
    }
    
    
}
