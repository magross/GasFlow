/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
public class PowerNetworkFile {

    private int numberOfSources;
    private int numberOfEdges;
    private int numberOfNodes;
    private int numberOfSinks;
    private int numberOfTerminals;

    public int getNumberOfTerminals() {
        return numberOfTerminals;
    }
    
    public int getNumberOfSources() {
        return numberOfSources;
    }

    public void setNumberOfSources(int numberOfSources) {
        this.numberOfSources = numberOfSources;
    }

    public int getNumberOfEdges() {
        return numberOfEdges;
    }

    public void setNumberOfEdges(int numberOfEdges) {
        this.numberOfEdges = numberOfEdges;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    public void setNumberOfNodes(int numberOfNodes) {
        this.numberOfNodes = numberOfNodes;
    }

    public int getNumberOfSinks() {
        return numberOfSinks;
    }

    public void setNumberOfSinks(int numberOfSinks) {
        this.numberOfSinks = numberOfSinks;
    }
    
    
    
    public DynamicNetwork<GasNode, GasEdge> readFromFile(String fileName) {
        List<String> list = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            list = (List<String>) stream
                    .filter(line -> !line.isEmpty() && !line.startsWith("%"))
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            Logger.getLogger(XMLNetworkFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        DynamicNetwork<GasNode, GasEdge> network = new DynamicNetwork<>(GasEdge::createEdge);
        Map<String, GasNode> idToString = new HashMap<>();
        Map<String, Boolean> idToBool = new HashMap<>();
        int i = 0;
        while (!list.get(i).startsWith("mpc.bus")) {
            i++;
        }
        Map<String, IntersectionType> stringToType = new HashMap<>();
        while (!list.get(i).startsWith("];")) {
            i++;
            String[] parts = list.get(i).trim().split("\\s");
            if (parts.length < 10) {
                continue;
            }
            //System.out.println(Arrays.toString(parts));
            numberOfNodes++;            
            Double d = Double.parseDouble(parts[2].trim());
            if (d > 0.0) {
                stringToType.put(parts[0], IntersectionType.SINK);
                numberOfSinks++;
                numberOfTerminals++;
            }
            
        }        
                    
            
        
        while (!list.get(i).startsWith("mpc.gen")) {            
            i++;
        }
        while (!list.get(i).startsWith("];")) {
            i++;
            String[] parts = list.get(i).trim().split("\\s");
            if (parts.length < 10) {
                continue;
            }
            ///System.out.println(Arrays.toString(parts));      
            if (!stringToType.containsKey(parts[0]) || !stringToType.get(parts[0]).equals(IntersectionType.SINK)) {
                if (!idToBool.containsKey(parts[0])) {
                    numberOfTerminals++;
                idToBool.put(parts[0], true);
                }
                
            }
            numberOfSources++;
            
        }        
        while (!list.get(i).startsWith("mpc.branch")) {
            i++;
        }
        i++;
        int nodeIndex = 0;
        int edgeIndex = 0;
        for (; i < list.size(); i++) {
            if (list.get(i).contains("];")) {
                break;
            }
            numberOfEdges++;
            String[] parts = list.get(i).trim().split("\\s+");
            
            GasNode from, to;
            if (idToString.containsKey(parts[0])) {
                from = idToString.get(parts[0]);
            } else {
                from = new GasNode(nodeIndex++);
                network.addNode(from);
                idToString.put(parts[0], from);
            }
            if (idToString.containsKey(parts[1])) {
                to = idToString.get(parts[1]);
            } else {
                to = new GasNode(nodeIndex++);
                network.addNode(to);
                idToString.put(parts[1], to);
            }
            GasEdge edge = new GasEdge(from, to, edgeIndex);
            network.addEdge(edge);
            edgeIndex++;
        }
        return network;
    }
}
