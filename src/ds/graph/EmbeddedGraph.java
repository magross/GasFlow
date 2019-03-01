/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ds.graph;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 *
 * @author Martin
 */
public class EmbeddedGraph<G extends Graph<Node,Edge>> {

    private G graph;
    private IdentifiableObjectMapping<Node, Point2D.Double> coordinates;

    public EmbeddedGraph(G graph) {
        this.graph = graph;
        this.coordinates = new IdentifiableObjectMapping<>(graph.numberOfNodes(), Point2D.Double.class);
    }

    public G getGraph() {
        return graph;
    }

    public Point2D.Double getCoordinate(Node node) {
        return coordinates.get(node);
    }

    public IdentifiableObjectMapping<Node, Point2D.Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinate(Node node, Point2D.Double coordinate) {
        coordinates.set(node, coordinate);
    }

    public double distance(Point2D.Double point) {
        double min = Double.POSITIVE_INFINITY;
        for (Node node : graph.nodes()) {
            double dist = point.distance(coordinates.get(node));
            if (dist < min) {
                min = dist;
            }
        }
        return min;
    }

    public double distance(Node v, Node w) {
        return coordinates.get(v).distance(coordinates.get(w));
    }

    public double minimumAngle(Node v, Point2D.Double point) {
        if (graph.degree(v) == 0) {
            return Double.POSITIVE_INFINITY;
        }
        Point2D o = coordinates.get(v);
        double min = Double.POSITIVE_INFINITY;
        for (Node node : graph.adjacentNodes(v)) {
            Point2D.Double point2 = null;
            point2 = coordinates.get(node);
            double angle1 = Math.atan2(point.getY() - o.getY(), point.getX() - o.getX());
            double angle2 = Math.atan2(point2.getY() - o.getY(), point2.getX() - o.getX());
            if (angle1 < 0) {
                angle1 += 2 * Math.PI;
            }
            if (angle2 < 0) {
                angle2 += 2 * Math.PI;
            }
            double diff = Math.abs(angle1 - angle2);
            if (diff > Math.PI) {
                diff -= Math.PI;
            }
            if (diff < min) {
                min = diff;
            }
        }
        return min;
    }

    public double minimumAngle(Node v, Node w) {
        return Math.min(minimumAngle(v, coordinates.get(w)), minimumAngle(w, coordinates.get(v)));
    }

    public boolean isIntersecting(Node v, Node w) {
        Line2D.Double line2 = new Line2D.Double(coordinates.get(v), coordinates.get(w));
        for (Edge e : graph.edges()) {
            if (e.start().equals(v) || e.end().equals(v) || e.start().equals(w) || e.end().equals(w)) {
                continue;
            }
            Line2D.Double line = new Line2D.Double(coordinates.get(e.start()), coordinates.get(e.end()));
            if (line.intersectsLine(line2)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        /*DynamicNetwork n = new DynamicNetwork();
        Node node = new Node(0);
        n.addNode(node);
        Node node2 = new Node(1);
        n.addNode(node2);
        Edge e = new Edge(0, node, node2);
        n.addEdge(e);
        /*
        EmbeddedGraph eg = new EmbeddedGraph(n);
        eg.setCoordinate(node, new Point2D.Double(1, 1));
        eg.setCoordinate(node2, new Point2D.Double(0, 0.9));*/
    }

    public Node nearestNode(Point2D.Double point) {
        Node nearest = null;
        for (Node node : graph.nodes()) {
            double dist = point.distance(coordinates.get(node));
            if (nearest == null || point.distance(coordinates.get(nearest)) > dist) {
                nearest = node;
            }
        }
        return nearest;
    }

    public double distanceToOtherNodes(Node v, Node w) {
        double min = Double.POSITIVE_INFINITY;
        Node x = null;
        Line2D.Double line2 = new Line2D.Double(coordinates.get(v), coordinates.get(w));
        for (Node node : graph.nodes()) {     
            if (node.equals(v) || node.equals(w)) {
                continue;
            }
            double dist = line2.ptSegDist(coordinates.get(node));
            if (dist < min) {
               x = node; 
                min = dist;
            }
        }
        //System.out.println("v" + coordinates.get(v) + " w " + coordinates.get(w) + " X " + coordinates.get(x) + " " + min);
        return min;
    }
}
