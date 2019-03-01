/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gas.io.gml;

import ds.graph.AbstractEdge;
import ds.graph.Graph;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin
 */
public class GML {

    private final Stack<String> stack;
    private final StringBuilder builder;
    private int nodeIndex;

    public GML() {
        builder = new StringBuilder();
        nodeIndex = 0;
        stack = new Stack<>();
    }

    protected void append(String string, Object... arguments) {
        indent();
        builder.append(String.format(string, arguments));
    }

    protected void indent() {
        if (!stack.isEmpty()) {
            builder.append(String.format("%1$" + stack.size() + "s", " "));
        }
    }

    public GML add(String command) {
        append(command);
        return this;
    }

    public GML add(String command, Object... arguments) {
        append(command, arguments);
        return this;
    }

    public GML addNode(int id) {
        begin("node");
        append("id %1$s\n", id);
        append("label %1$s\n", "\"\"");
        begin("graphics");
        append("x %1$s\n", 0);
        append("y %1$s\n", 0);
        append("w %1$s\n", 50);
        append("h %1$s\n", 50);
        append("type %1$s\n", "\"rectangle\"");
        append("width %1$s\n", 1.0);
        end();
        end();
        return this;
    }

    public GML addEdge(int i, int j) {
        begin("edge");
        append("source %1$s\n", i);
        append("target %1$s\n", j);
        append("label %1$s\n", "\"\"");
        begin("graphics");
        append("type %1$s\n", "\"line\"");
        append("arrow %1$s\n", "\"last\"");
        end();
        end();
        return this;
    }

    public GML begin(String command) {
        append("%1$s [\n", command);
        stack.push(command);
        return this;
    }

    public GML end() {
        stack.pop();
        append("]\n");
        return this;
    }

    public static <N, E extends AbstractEdge<N>> String fromGraph(Graph<N, E> graph) {
        GML gml = new GML();
        gml.begin("graph");
        gml.add("directed %1$s\n", 1);
        Map<N, Integer> nodeMap = new HashMap<>();
        int nodeIndex = 0;
        for (N node : graph.nodes()) {
            nodeMap.put(node, nodeIndex);
            gml.addNode(nodeIndex);
            nodeIndex++;
        }
        for (E edge : graph.edges()) {
            int i = nodeMap.get(edge.start());
            int j = nodeMap.get(edge.end());
            gml.addEdge(i, j);
        }
        gml.end();
        return gml.toString();
    }

    public static <N, E extends AbstractEdge<N>> void writeToFile(Graph<N, E> graph, String filename) {
        try {
            PrintStream out = new PrintStream(new FileOutputStream(filename));
            out.append(fromGraph(graph));
            out.flush();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GML.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
