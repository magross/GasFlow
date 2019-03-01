/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gas.io.tikz;

import ds.graph.AbstractEdge;
import ds.graph.DynamicNetwork;
import ds.graph.Edge;
import ds.graph.Graph;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.Node;
import gas.io.GraphConversion;
import gas.io.XMLConnection;
import gas.io.XMLIntersection;
import gas.io.gaslib.GasLibNetworkFile;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Martin
 */
public class GraphToTikZ extends TikZ {

    private int nodeIndex;

    public TikZ addEdge(int i, int j) {
        return addEdge(i, j, "edgeStyle");
    }

    public TikZ addEdge(int i, int j, String edgeStyle) {
        addLine("\\draw[%1$s] (%2$s) -- (%3$s);",edgeStyle,"n"+i,"n"+j);
        return this;
    }

    public TikZ addNode(double x, double y) {
        return addNode(x, y, "nodeStyle", "");
    }

    public TikZ addNode(double x, double y, String nodeStyle, String caption) {
        addLine("\\node[%1$s] (%2$s) at (%3$s,%4$s) {%5$s};",nodeStyle,"n"+(nodeIndex),x,y,caption);
        nodeIndex++;
        return this;
    }

 
    
    private String nodeFormat = "\\node[nodeStyle] (n%1$s) at (%2$s,%3$s) {};\n";

    public void writeToFile(String filename) {

    }

    public void generateGraph(GasLibNetworkFile gasNetworkFile) {

    }
/*
    public void writeToStream(GraphConversion conversion, PrintStream out) {
        Graph network = conversion.getGraph();
        out.println("\\begin{figure}[p]\\centering\\begin{tikzpicture}");
        for (Node node : network.nodes()) {
            XMLIntersection intersection = conversion.getIntersection(node);
            if (network.degree(node) == 2) {
                Edge edge1 = network.incidentEdges(node).first();
                Edge edge2 = network.incidentEdges(node).last();
                if ((edgeConnections.get(edge1).getType() == ConnectionType.PIPE
                        || edgeConnections.get(edge1).getType() == ConnectionType.SHORT_PIPE)
                        && (edgeConnections.get(edge2).getType() == ConnectionType.PIPE
                        || edgeConnections.get(edge2).getType() == ConnectionType.SHORT_PIPE)) {
                    out.printf("\\coordinate (n%1$s) at (%2$s,%3$s) {};\n", node.id(), intersection.getX() / 700, intersection.getY() / 500);
                } else {
                    out.printf("\\node[nodeStyle] (n%1$s) at (%2$s,%3$s) {};\n", node.id(), intersection.getX() / 700, intersection.getY() / 500);
                }
            } else {
                out.printf("\\node[nodeStyle] (n%1$s) at (%2$s,%3$s) {};\n", node.id(), intersection.getX() / 700, intersection.getY() / 500);
            }
        }
        for (Edge edge : network.edges()) {
            if (edgeConnections.isDefinedFor(edge)) {
                out.printf("\\draw[%3$s] (n%1$s) -- (n%2$s);\n", edge.start().id(), edge.end().id(), edgeConnections.get(edge).getType().toString().replace("_", ""));
            } else {
                out.printf("\\draw (n%1$s) -- (n%2$s);\n", edge.start().id(), edge.end().id());
            }
        }
        out.println("\\end{tikzpicture}\\end{figure}");
        out.println("");
    }*/

    static IdentifiableObjectMapping<Node, XMLIntersection> nodeIntersections;
    static IdentifiableObjectMapping<Edge, XMLConnection> edgeConnections;

    public static void main(String[] args) {
        /*
        GasLibNetworkFile gasNetworkFile = new GasLibNetworkFile("network1.xml");

        GraphConversion<DynamicNetwork,GasLibIntersection,GasLibConnection> conversion = gasNetworkFile.extractDynamicNetwork();
        SeriesParallelGraph.isSeriesParallel(network, nodeIntersections, edgeConnections);

        print(network);

        // Remove inactive leaves
        Queue<Node> queue = new LinkedList<>();
        for (Node node : network.nodes()) {
            if (network.degree(node) == 1) {
                queue.add(node);
            }
        }
        while (!queue.isEmpty()) {
            Node node = queue.poll();
            Edge edge = network.incidentEdges(node).first();
            Node other = edge.opposite(node);
            if (edgeConnections.get(edge).getType() == ConnectionType.PIPE
                    || edgeConnections.get(edge).getType() == ConnectionType.SHORT_PIPE) {
                network.removeNode(node);
                network.removeEdge(edge);
                if (network.degree(other) == 1) {
                    queue.add(other);
                }
            }
        }
        print(network);

        // Remove active leaves
        for (Node node : network.nodes()) {
            if (network.degree(node) == 1) {
                queue.add(node);
            }
        }
        while (!queue.isEmpty()) {
            Node node = queue.poll();
            Edge edge = network.incidentEdges(node).first();
            Node other = edge.opposite(node);
            network.removeNode(node);
            network.removeEdge(edge);
            if (network.degree(other) == 1) {
                queue.add(other);
            }
        }
        print(network);

        printB(network);

        int valve = 0, controlvalve = 0, compressor = 0, resistor = 0;
        for (Edge edge : network.edges()) {
            switch (edgeConnections.get(edge).getType()) {
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
        System.out.println("Control Valve: " + controlvalve);
        System.out.println("Compressor: " + compressor);
        System.out.println("Resistor: " + resistor);
        System.out.println("Valve: " + valve);*/
    }

    public static boolean PRINT = false;


    public static void printB(DynamicNetwork network) {
        if (!PRINT) {
            return;
        }
        /*
        System.out.println("\\begin{figure}[p]\\centering\\begin{tikzpicture}");
        for (Node node : network.nodes()) {
            XMLIntersection intersection = nodeIntersections.get(node);
            if (network.degree(node) == 2) {
                Edge edge1 = network.incidentEdges(node).first();
                Edge edge2 = network.incidentEdges(node).last();
                if ((edgeConnections.get(edge1).getType() == ConnectionType.PIPE
                        || edgeConnections.get(edge1).getType() == ConnectionType.SHORT_PIPE)
                        && (edgeConnections.get(edge2).getType() == ConnectionType.PIPE
                        || edgeConnections.get(edge2).getType() == ConnectionType.SHORT_PIPE)) {
                    System.out.printf("\\coordinate (n%1$s) at (%2$s,%3$s) {};\n", node.id(), intersection.getX() / 700, intersection.getY() / 500);
                } else {
                    System.out.printf("\\node[nodeStyle] (n%1$s) at (%2$s,%3$s) {};\n", node.id(), intersection.getX() / 700, intersection.getY() / 500);
                }
            } else {
                System.out.printf("\\node[nodeStyle] (n%1$s) at (%2$s,%3$s) {};\n", node.id(), intersection.getX() / 700, intersection.getY() / 500);
            }
        }
        for (Edge edge : network.edges()) {
            if (edgeConnections.isDefinedFor(edge)) {
                System.out.printf("\\draw[%3$s] (n%1$s) -- (n%2$s);\n", edge.start().id(), edge.end().id(), edgeConnections.get(edge).getType().toString().replace("_", ""));
            } else {
                System.out.printf("\\draw (n%1$s) -- (n%2$s);\n", edge.start().id(), edge.end().id());
            }
        }
        System.out.println("\\end{tikzpicture}\\end{figure}");
        System.out.println("");*/
    }
}
