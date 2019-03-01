/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aaa.gurobi;

import ds.graph.Edge;
import ds.graph.Network;
import ds.graph.Node;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin
 */
public class FanStarInstance extends Network {
    
    private List<Edge> links;
    private Network network;
    private int numberOfFans;
    private int numberOfLeaves;
    
    public FanStarInstance() {        
    }
    
    public void generateGraph() {
        network = new Network();
        int numberOfNodes = 1 + numberOfFans * (numberOfLeaves + 1);
        network.setNodeCapacity(numberOfNodes);
        network.setEdgeCapacity(numberOfNodes-1);
        Node root = network.getNode(0);
        for (int i = 1; i <= numberOfFans; i += numberOfLeaves+1) {
            network.createAndSetEdge(root, network.getNode(i));
            for (int j = 1; j <= numberOfLeaves; j++) {
                network.createAndSetEdge(network.getNode(i), network.getNode(i+j));
            }
        }        
    }
    
    public void generateLinks() {
        links = new LinkedList<Edge>();
        
        //return links;
    }
}
