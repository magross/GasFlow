/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gas;

import ds.graph.Edge;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.Node;
import gas.io.XMLConnection;
import gas.io.XMLIntersection;

/**
 *
 * @author gross
 */
public class GasNetwork {

/*

    public static Amount<MassFlowRate> compute(GasLibPipe connection, Amount<Pressure> p1, Amount<Pressure> p2) {
        Amount<Dimensionless> h_prime = connection.computeSlope();
        Amount<Dimensionless> lambda = computeFrictionFactor(connection.getRoughness(), connection.getDiameter());
        Amount<Length> length = connection.getLength();
        Amount<Length> d = connection.getDiameter();
        Amount x = g.times(h_prime).times(d).divide(c.pow(4).times(lambda));
        Amount y = d.divide(c.pow(2).times(lambda).times(length));
        Amount z = p1.pow(2).minus(p2.pow(2));
        Amount result = x.minus(y).times(z);
        result = result.to(KILOGRAM.divide(METER).divide(METER).divide(SECOND).pow(2));
        boolean negative = false;
        if (result.getEstimatedValue() < 0) {
            negative = true;
        }
        result = result.abs().sqrt();
        if (negative) {
            result = result.times(-1);
        }
        Amount D = d.divide(2).pow(2).times(Constants.π);
        result = result.times(D).to(KILOGRAM.divide(SECOND));
        System.out.println("Mass flow:" + result);

        Amount a = g.times(h_prime).times(d).divide(lambda);
        Amount b = d.times(c.pow(2)).divide(lambda.times(length));
        System.out.println("Edge constant: " + a.minus(b).to(METER.pow(2).divide(SECOND.pow(2))));
        System.out.println("Density Node 1: " + p1.divide(c2).to(KILOGRAM.divide(METER.pow(3))));
        Amount averageDensity = p1.divide(c2);
        //Amount averageDensity = p1.divide(c2).plus(p2.divide(c2).times(0.5));
        result = result.divide(averageDensity).to(METER.pow(3).divide(SECOND));
        result = result.divide(D);
        System.out.println(result.to(SI.METER.divide(SECOND)));
        System.out.println(result.to(SI.KILOMETER.divide(HOUR)));
        return result;
    }
*/
    static IdentifiableObjectMapping<Node, XMLIntersection> nodeIntersections;
    static IdentifiableObjectMapping<Edge, XMLConnection> edgeConnections;

    /**
     * @param args the command line arguments
     */
    /*
    public static void main(String[] args) {
        GasLibNetworkFile gasNetworkFile = new GasLibNetworkFile("network1.xml");

        Amount<Pressure> p1 = Amount.valueOf(8000000, SI.PASCAL);
        Amount<Pressure> p2 = Amount.valueOf(4000000, SI.PASCAL);
        compute((GasLibPipe) gasNetworkFile.getConnections().getMap().get("pipe_1"), p1, p2);

        int numberOfNodes = gasNetworkFile.getIntersections().getMap().size();
        int numberOfEdges = gasNetworkFile.getConnections().getMap().size();

        // graph = new Graph(numberOfNodes, numberOfEdges);
        DynamicNetwork network = new DynamicNetwork();

        nodeIntersections = new IdentifiableObjectMapping<>(numberOfNodes, XMLIntersection.class);
        HashMap<String, Node> intersectionNode = new HashMap<>();

        edgeConnections = new IdentifiableObjectMapping<>(numberOfEdges, XMLConnection.class);
        int i = 0;
        for (GasLibIntersection intersection : gasNetworkFile.getIntersections().getMap().values()) {
            Node node = new Node(i);
            network.addNode(node);
            nodeIntersections.set(node, intersection);
            intersectionNode.put(intersection.getId(), node);
            ++i;
        }
        i = 0;
        for (GasLibConnection connection : gasNetworkFile.getConnections().getMap().values()) {
            //System.out.println(connection.getFrom() + " " + connection.getTo());
            Node fromNode = intersectionNode.get(connection.getFrom().getId());
            Node toNode = intersectionNode.get(connection.getTo().getId());
            //System.out.println(fromNode + " " + toNode);
            Edge edge = new Edge(i, fromNode, toNode);
            //System.out.println(edge);
            network.addEdge(edge);
            edgeConnections.set(edge, connection);
            ++i;

        }
        //SeriesParallelGraph.isSeriesParallel(network, nodeIntersections, edgeConnections);

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
        System.out.println("Valve: " + valve);
    }

    public static boolean PRINT = false;

    public static void print(DynamicNetwork network) {
        if (!PRINT) {
            return;
        }
        System.out.println("\\begin{figure}[p]\\centering\\begin{tikzpicture}");
        for (Node node : network.nodes()) {
            XMLIntersection intersection = nodeIntersections.get(node);
            System.out.printf("\\node[nodeStyle] (n%1$s) at (%2$s,%3$s) {};\n", node.id(), intersection.getX() / 700, intersection.getY() / 500);
        }
        for (Edge edge : network.edges()) {
            if (edgeConnections.isDefinedFor(edge)) {
                System.out.printf("\\draw[%3$s] (n%1$s) -- (n%2$s);\n", edge.start().id(), edge.end().id(), edgeConnections.get(edge).getType().toString().replace("_", ""));
            } else {
                System.out.printf("\\draw (n%1$s) -- (n%2$s);\n", edge.start().id(), edge.end().id());
            }
        }
        System.out.println("\\end{tikzpicture}\\end{figure}");
        System.out.println("");
    }

    public static void printB(DynamicNetwork network) {
        if (!PRINT) {
            return;
        }
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
        System.out.println("");
    }*/
}
