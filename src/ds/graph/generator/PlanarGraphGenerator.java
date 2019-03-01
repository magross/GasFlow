/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ds.graph.generator;

import ds.graph.DynamicNetwork;
import ds.graph.EmbeddedGraph;
import ds.graph.Edge;
import ds.graph.Node;
import gas.io.tikz.TikZ;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author Martin
 */
public class PlanarGraphGenerator {
/*
    private double minimumEdgeAngle;
    private double maximumEdgeDistance;
    private double minimumNodeDistance;
    private double minimumNodeToEdgeDistance;
    private double maxX;
    private double maxY;
    private Random rng;

    public PlanarGraphGenerator() {
        this.maximumEdgeDistance = 2.0;
        this.maxX = 20.0;
        this.maxY = 15.0;
        this.minimumEdgeAngle = 0.2;
        this.minimumNodeDistance = 0.8;
        this.minimumNodeToEdgeDistance = 0.3;
        this.rng = new Random();
    }
    
    protected void generateNodes(EmbeddedGraph<DynamicNetwork> eg, int numberOfNodes) {
        DynamicNetwork network = eg.getGraph();
        for (int i = 0; i < numberOfNodes; i++) {
            Point2D.Double coordinate;
            do {
                //double x = rng.nextDouble()*maxX;
                //double y = rng.nextDouble()*maxY;
                double x = Math.round(rng.nextDouble() * maxX * 100.0) / 100.0;
                double y = Math.round(rng.nextDouble() * maxY * 100.0) / 100.0;
                coordinate = new Point2D.Double(x, y);
                //System.out.println(eg.distance(coordinate));
            } while (eg.distance(coordinate) <= minimumNodeDistance);
            //Node nearest = eg.nearestNode(coordinate);
            Node node = new Node(i);
            network.addNode(node);
            eg.setCoordinate(node, coordinate);
            //Edge edge = new Edge(edgeIndex++,node,nearest);
            //network.addEdge(edge);
            //System.out.println(i + ": " + coordinate);
        }        
    }
    
    protected LinkedList<Node>[] listPartnersByDistance(EmbeddedGraph<DynamicNetwork> eg) {
        DynamicNetwork network = eg.getGraph();
        LinkedList<Node>[] partners = new LinkedList[network.numberOfNodes()];
        for (int i = 0; i < network.numberOfNodes(); i++) {
            partners[i] = new LinkedList<>();
            partners[i].addAll(network.nodes());
            Node n = network.getNode(i);
            partners[i].remove(n);
            LinkedList<Node> list = partners[i];
            Comparator<Node> comparator = new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    Node v = (Node) o1;
                    Node w = (Node) o2;
                    int result = (int) Math.round((eg.distance(n, v) - eg.distance(n, w)) * 1000);
                    if (result == 0) {
                        result = v.id() - w.id();
                    }
                    return result;
                }
            };
            Collections.sort(list, comparator);
        }        
        return partners;
    }

    protected void filterPartners(EmbeddedGraph<DynamicNetwork> eg, LinkedList<Node>[] partners) {
        DynamicNetwork network = eg.getGraph();
        for (int i = 0; i < network.numberOfNodes(); i++) {
            Node v = network.getNode(i);
            int j = 0;
            while (j < partners[i].size()) {
                Node node = network.getNode(j);
                boolean keep = false;
                if (!network.isAdjacent(v, node)) {
                    if (!eg.isIntersecting(v, node)) {
                        //if (!(eg.minimumAngle(v, w) < minimumEdgeAngle)) {
                            if (!(eg.distanceToOtherNodes(v, node) < minimumNodeToEdgeDistance)) {
                                keep = true;
                            } 
                        //}
                    }          
                }
                if (!keep) {
                    partners[i].remove(j);
                } else {
                    j++;
                }
            }
        }        
    }    
    
    public EmbeddedGraph<DynamicNetwork> generate(int numberOfNodes, int numberOfEdges) {
        DynamicNetwork network = new DynamicNetwork();
        EmbeddedGraph<DynamicNetwork> eg = new EmbeddedGraph<>(network);
        generateNodes(eg, numberOfNodes);
        LinkedList<Node>[] possiblePartners = listPartnersByDistance(eg);
       
        for (int i = 0; i < numberOfNodes; i++) {
            Node v = network.getNode(i);
            int edgeCount = network.degree(v);
            int edgeTarget = rng.nextInt(4) + 2 + rng.nextInt(2);
            while (edgeCount < edgeTarget && !possiblePartners[i].isEmpty()) {
                Node w = possiblePartners[i].getFirst();
                Edge e = new Edge(i, v, w);
                if (!network.isAdjacent(v, w)) {
                    if (!eg.isIntersecting(v, w)) {
                        //if (!(eg.minimumAngle(v, w) < minimumEdgeAngle)) {
                            if (!(eg.distanceToOtherNodes(v, w) < minimumNodeToEdgeDistance)) {
                                network.addEdge(e);
                                edgeCount++;
                            } 
                        //}
                    }
                }
                possiblePartners[i].removeFirst();
            }
        }
        filterPartners(eg, possiblePartners);

        
        for (int i = 0; i < numberOfNodes; i++) {
            Node v = network.getNode(i);
            if (possiblePartners[i].size() < 5) {
                continue;
            }
            int[] modifier = {0,1,-1,2,-2,3,-3,4,-4,5-5};
            for (int m = 0; m < modifier.length; m++) {
                int j = possiblePartners[i].size() / 2 + modifier[m];
                j = (j < 0)? 0 : (j >= possiblePartners[i].size())? possiblePartners[i].size()-1 : j;
                Node node = network.getNode(j);
                if (!network.isAdjacent(v, node) && !v.equals(node)) {
                    if (!eg.isIntersecting(v, node)) {
                        //if (!(eg.minimumAngle(v, w) < minimumEdgeAngle)) {
                            if (!(eg.distanceToOtherNodes(v, node) < minimumNodeToEdgeDistance)) {
                                Edge e = new Edge(i, v, node);
                                System.out.println("ADD " + v + " " + node);
                                network.addEdge(e);
                                break;
                            } 
                        //}
                    }          
                }
            }
        }        
        return eg;
    }

    public static void main(String[] args) {
        PlanarGraphGenerator pgg = new PlanarGraphGenerator();
        EmbeddedGraph<DynamicNetwork> eg = pgg.generate(300, 80);
        DynamicNetwork network = eg.getGraph();
        TikZ tikz = new TikZ();
        tikz.begin("tikzpicture");

        for (Node node : network.nodes()) {
            Point2D.Double pair = eg.getCoordinate(node);
            tikz.addNode(pair.getX(), pair.getY());
        }
        for (Edge edge : network.edges()) {
            tikz.addEdge(edge.start().id(), edge.end().id());
        }
        tikz.end();
        System.out.println(tikz);
    }
*/
}
