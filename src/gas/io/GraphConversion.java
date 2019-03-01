/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gas.io;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import ds.graph.DynamicNetwork;
import ds.graph.GasEdge;
import ds.graph.GasNode;
import gas.io.tikz.TikZ;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author Martin
 * @param <I>
 * @param <C>
 */
public class GraphConversion<I extends XMLIntersection, C extends XMLConnection> {

   
    private final DynamicNetwork<GasNode, GasEdge> graph;
    private final BiMap<GasNode, I> nodeIntersections;
    private final BiMap<GasEdge, C> edgeConnections;
    private final XMLInformation information;
    private Function<XMLIntersection, Point2D.Double> coordinateTransformation;
    private Function<XMLIntersection, String> idTransformation;
    private Function<GasEdge, String> edgeStyleTransformation;
    private Function<GasNode, String> nodeTransformation;

    public GraphConversion(XMLInformation information, DynamicNetwork<GasNode, GasEdge> graph, BiMap<GasNode, I> nodeIntersections, BiMap<GasEdge, C> edgeConnections) {
        this.graph = graph;
        this.nodeIntersections = nodeIntersections;
        this.edgeConnections = edgeConnections;
        this.information = information;
        this.coordinateTransformation = (XMLIntersection i) -> new Point2D.Double(i.getX() / 700.0, i.getY() / 500.0);
        this.idTransformation = (XMLIntersection i) -> i.getId();
        this.nodeTransformation = (GasNode n) -> {
            String style = "";
            switch (IntersectionType.getType(nodeIntersections.get(n))) {
                case SOURCE:
                    style = "sourceStyle";
                    break;
                case SINK:
                    style = "sinkStyle";
                    break;
                case TERMINAL:
                    style = "terminalStyle";
                    break;
                default:
                    style = "nodeStyle";
                    break;
            }
            return style;
        };
    }

    public GraphConversion<I, C> restrictTo(List<GasNode> nodes) {
        DynamicNetwork<GasNode, GasEdge> restriction = new DynamicNetwork<>(GasEdge::createEdge);
        BiMap<GasNode, I> restrictedIntersections = HashBiMap.create();
        BiMap<GasEdge, C> restrictedConnections = HashBiMap.create();
        for (GasNode node : nodes) {
            restriction.addNode(node);
            restrictedIntersections.put(node, nodeIntersections.get(node));
        }
        for (GasEdge edge : graph.edges()) {
            if (nodes.contains(edge.start()) && nodes.contains(edge.end())) {
                restriction.addEdge(edge);
                restrictedConnections.put(edge, edgeConnections.get(edge));
            }
        }
        return new GraphConversion<>(information, restriction, restrictedIntersections, restrictedConnections);
    }

    public int[] countElements() {
        int[] counts = new int[ConnectionType.values().length];
        for (GasEdge edge : graph.edges()) {
            if (edgeConnections.get(edge) == null) {
                continue;
            }
            counts[edgeConnections.get(edge).getType().ordinal()]++;
        }
        return counts;
    }

    public List<GasEdge> listEdgesOfType(ConnectionType... types) {
        LinkedList<GasEdge> edges = new LinkedList<>();
        for (GasEdge edge : graph.edges()) {
            for (int i = 0; i < types.length; i++) {
                if (ConnectionType.getType(edgeConnections.get(edge)) == types[i]) {
                    edges.add(edge);
                    break;
                }
            }
        }
        return edges;
    }
    
    public void removeNodeAndIntersection(GasNode node) {
        getGraph().removeNode(node);
        //nodeIntersections
        //I intersection = getIntersection(node);
        
    }

    public BiMap<GasNode, I> getNodeIntersections() {
        return nodeIntersections;
    }

    public BiMap<GasEdge, C> getEdgeConnections() {
        return edgeConnections;
    }

    public XMLInformation getInformation() {
        return information;
    }

    public C getConnection(GasEdge edge) {
        return edgeConnections.get(edge);
    }

    public void setConnection(GasEdge edge, C connection) {
        edgeConnections.put(edge, connection);
    }

    public DynamicNetwork<GasNode, GasEdge> getGraph() {
        return graph;
    }

    public I getIntersection(GasNode node) {
        return nodeIntersections.get(node);
    }

    public void setIntersection(GasNode node, I intersection) {
        nodeIntersections.put(node, intersection);
    }

    public <D extends C> void filterEdges(Function<C, D> processor) {
        for (GasEdge edge : graph.edges()) {
            C oldConnection = edgeConnections.get(edge);
            D newConnection = processor.apply(oldConnection);
            edgeConnections.put(edge, newConnection);
        }
    }

    public <J extends I> void filterNodes(Function<I, J> processor) {
        for (GasNode node : graph.nodes()) {
            I oldIntersection = nodeIntersections.get(node);
            J newIntersection = processor.apply(oldIntersection);
            nodeIntersections.put(node, newIntersection);
        }
    }

    public Function<XMLIntersection, Point2D.Double> getTikZCoordinateTransformation() {
        return coordinateTransformation;
    }

    public void setTikZCoordinateTransformation(Function<XMLIntersection, Point2D.Double> coordinateTransformation) {
        this.coordinateTransformation = coordinateTransformation;
    }

    public Function<XMLIntersection, String> getTikZIdTransformation() {
        return idTransformation;
    }

    public void setTikZIdTransformation(Function<XMLIntersection, String> idTransformation) {
        this.idTransformation = idTransformation;
    }

    public TikZ toTikZ() {
        return toTikZ(new LinkedList<>());
    }

    public TikZ toTikZ(List<GasNode> nodes) {
        return toTikZ(nodes, new LinkedList<>());
    }

    public Function<GasNode, String> getNodeTransformation() {
        return nodeTransformation;
    }

    public void setNodeTransformation(Function<GasNode, String> nodeTransformation) {
        this.nodeTransformation = nodeTransformation;
    }

    public TikZ toTikZ(List<GasNode> nodes, List<GasEdge> bridges) {
        System.out.println("  Preparing transformations...");

        this.edgeStyleTransformation = (GasEdge edge) -> {
            XMLConnection c = edgeConnections.get(edge);
            String style = c.getType().toString().replaceAll("_", "");
            if (bridges.contains(edge)) {
                style = style + ",bridge";
            }
            return style;
        };

        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
        for (GasNode node : graph.nodes()) {
            XMLIntersection i = getIntersection(node);
            if (i.getX() < minX) {
                minX = i.getX();
            } else if (i.getX() > maxX) {
                maxX = i.getX();
            }
            if (i.getY() < minY) {
                minY = i.getY();
            } else if (i.getY() > maxY) {
                maxY = i.getY();
            }
        }
/*
        double minDist = Double.POSITIVE_INFINITY;
        double minDeltaX = Double.POSITIVE_INFINITY;
        double minDeltaY = Double.POSITIVE_INFINITY;
        for (GasNode node1 : graph.nodes()) {
            XMLIntersection i = getIntersection(node1);
            for (GasNode node2 : graph.nodes()) {
                if (node1.equals(node2)) {
                    continue;
                }
                XMLIntersection j = getIntersection(node2);
                double dist = Math.sqrt(Math.pow((i.getX() - j.getX()), 2) + Math.pow((i.getY() - j.getY()), 2));
                if (dist < minDist) {
                    minDist = dist;
                }
                if (i.getX() != j.getX() && Math.abs(i.getX() - j.getX()) < minDeltaX) {
                    minDeltaX = Math.abs(i.getX() - j.getX());
                }
                if (i.getY() != j.getY() && Math.abs(i.getY() - j.getY()) < minDeltaY) {
                    minDeltaY = Math.abs(i.getY() - j.getY());
                }
            }
        }*/
        //System.out.println(min + " " + minDeltaX + " " + minDeltaY);

        //System.out.println(minDist);
        final double maxXf = maxX;
        final double minXf = minX;
        final double maxYf = maxY;
        final double minYf = minY;
        final double ratio = (maxXf - minXf) / (maxYf - minYf);
        final double xscale;
        final double yscale;
        //final double minDistf = minDist;

        System.out.printf("   Original coordinate area: %1$s x %2$s\n", (maxXf - minXf), (maxYf - minYf));

        if (maxYf - minYf > (maxXf - minXf)) {
            yscale = 500;
            xscale = yscale * (maxXf - minXf) / (maxYf - minYf);
        } else {
            xscale = 500;
            yscale = xscale * (maxYf - minYf) / (maxXf - minXf);
        }
        System.out.printf("   Rescaling to: %1$s x %2$s\n", Math.round(xscale), Math.round(yscale));
        
        if (maxXf > minXf && maxYf > minYf) {
            this.coordinateTransformation = (XMLIntersection i) -> {
                return new Point2D.Double(
                        Math.round(1000*xscale * (i.getX() - minXf) / (maxXf - minXf))/1000.0, 
                        Math.round(1000*yscale * (i.getY() - minYf) / (maxYf - minYf))/1000.0);
            };
        }
        /*
        System.out.println(minDistf);
        this.coordinateTransformation = (XMLIntersection i) -> {
            return new Point2D.Double(1.0 / minDistf * (i.getX() - minXf), 1.0 / minDistf * (i.getY() - minYf));
        };*/

        TikZ t = new TikZ();
        t.begin("tikzpicture");
        for (GasNode node : graph.nodes()) {
            XMLIntersection i = getIntersection(node);
            Point2D.Double coordinate = coordinateTransformation.apply(i);
            String id = idTransformation.apply(i);
            String style = nodeTransformation.apply(node);
            t.addNode(id, coordinate.getX(), coordinate.getY(), style, "");
        }
        for (GasEdge edge : graph.edges()) {
            XMLConnection c = getConnection(edge);
            String start = idTransformation.apply(getIntersection(edge.start()));
            String end = idTransformation.apply(getIntersection(edge.end()));
            String style = "edgeStyle";
            if (c != null) {
                style = edgeStyleTransformation.apply(edge);
            }
            t.addEdge(start, end, style);
        }
        t.end();
        return t;
    }
}
