/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gas.algo;

import algo.graph.traverse.DFS;
import ds.graph.DynamicNetwork;
import ds.graph.GasEdge;
import ds.graph.GasNode;
import ds.graph.Graph;
import gas.io.ConnectionType;
import gas.io.GraphConversion;
import gas.io.XMLIntersection;
import gas.io.gaslib.GasLibConnection;
import gas.io.gaslib.GasLibIntersection;
import gas.io.gaslib.GasLibNetworkFile;
import gas.io.gaslib.GasLibZipArchive;
import gas.io.tikz.TikZ;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gross
 */
public class GSPG {

    public static final boolean WRITE = true;
    public static final boolean DELETE_ACTIVE_ELEMENTS = false;
    public static final boolean DELETE_BRIDGES_ONLY = true;
    public static final boolean MARK_BRIDGES = true;

    static Map<GasEdge, GasLibConnection> edgeConnections;
    static Map<GasNode, GasLibIntersection> nodeIntersections;

    public static void printElements(DynamicNetwork<GasNode, GasEdge> network) {
        int valve = 0, controlvalve = 0, compressor = 0, resistor = 0;
        for (GasEdge edge : network.edges()) {
            if (edgeConnections.get(edge) == null) {
                continue;
            }
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
        System.out.println(" Nodes: " + network.numberOfNodes());
        System.out.println(" Edges: " + network.numberOfEdges());
        System.out.println(" Control Valves: " + controlvalve);
        System.out.println(" Compressors: " + compressor);
        System.out.println(" Resistors: " + resistor);
        System.out.println(" Valves: " + valve);
    }

    public static List<GasEdge> deleteActiveElements(DynamicNetwork<GasNode, GasEdge> network) {
        LinkedList<GasEdge> active = new LinkedList<>();
        for (GasEdge edge : network.edges()) {
            switch (edgeConnections.get(edge).getType()) {
                case RESISTOR:
                case COMPRESSOR_STATION:
                case CONTROL_VALVE:
                case VALVE:
                    active.add(edge);
                    break;
            }
        }
        for (GasEdge edge : active) {
            network.removeEdge(edge);
        }
        return active;
    }

    public static List<GasEdge> safelyDeleteActiveElements(DynamicNetwork<GasNode, GasEdge> network) {
        LinkedList<GasEdge> active = new LinkedList<>();
        for (GasEdge edge : network.edges()) {
            switch (edgeConnections.get(edge).getType()) {
                case RESISTOR:
                case COMPRESSOR_STATION:
                case CONTROL_VALVE:
                case VALVE:
                    active.add(edge);
                    break;
            }
        }
        LinkedList<GasEdge> activeBridges = new LinkedList<>();
        for (GasEdge edge : active) {
            network.removeEdge(edge);
            DFS<GasNode, GasEdge> dfs = new DFS<>(network);
            List<Graph<GasNode, GasEdge>> ccs = dfs.createConnectedComponents(GasEdge::createEdge);
            if (ccs.size() == 1) {
                //System.out.println("No bridge " + edge);
            } else {
                activeBridges.add(edge);
                //System.out.println("Bridge! " + edge);
            }
            network.addEdge(edge);
        }
        for (GasEdge edge : activeBridges) {
            network.removeEdge(edge);
        }
        return activeBridges;
    }

    public static void main(String[] args) throws InterruptedException {
        String pathname = "c://svn//tu-berlin-gross//research//gas//instances//";
        //String pathname = "//homes//combi//gross//local//";
        String outputPath = "c://svn//tu-berlin-gross//research//gas//instances//splitted//";
        //String outputPath = "/homes/combi/gross/local/safelySplitted/";
        GasLibNetworkFile gasNetworkFile = new GasLibNetworkFile();
        GasLibZipArchive zip;
        int p = 0;
        switch (p) {
            case 0:
                gasNetworkFile.readFromFile(pathname + "hgas-data//H-Gas_V2_korr.net");
                break;
            case 1:
                gasNetworkFile.readFromFile(pathname + "lgas-data//2013-11-27_Nova_L.net");
                break;
            case 2:
                zip = new GasLibZipArchive(pathname + "GasLib-40.zip", false);
                gasNetworkFile = zip.getNetworkFile();
                break;
            case 3:
                zip = new GasLibZipArchive(pathname + "GasLib-135.zip", false);
                gasNetworkFile = zip.getNetworkFile();
                break;
            case 4:
                zip = new GasLibZipArchive(pathname + "GasLib-582-v2.zip", false);
                gasNetworkFile = zip.getNetworkFile();
        }

        GraphConversion<GasLibIntersection, GasLibConnection> conversion = gasNetworkFile.getDynamicNetwork();
        DynamicNetwork<GasNode, GasEdge> network = conversion.getGraph();
        edgeConnections = conversion.getEdgeConnections();
        nodeIntersections = conversion.getNodeIntersections();

        System.out.println("Loaded a network with:");
        printElements(network);
        System.out.println("");

        List<GasEdge> removedElements = null;
        List<GasNode> linkNodes = new LinkedList<>();
        List<Graph<GasNode, GasEdge>> ccs = null;
        List<GasEdge> bridges = new LinkedList<>();

        if (MARK_BRIDGES) {
            List<GasEdge> allEdges = new LinkedList<>();
            allEdges.addAll(network.edges());
            for (GasEdge edge : allEdges) {
                List<GasEdge> removals = new LinkedList<>();
                removals.addAll(network.getEdges(edge.start(), edge.end()));
                removals.addAll(network.getEdges(edge.end(), edge.start()));
                for (GasEdge removal : removals) {
                    network.removeEdge(removal);
                }
                DFS<GasNode, GasEdge> dfs = new DFS<>(network);
                ccs = dfs.createConnectedComponents(GasEdge::createEdge);
                if (ccs.size() > 1) {
                    bridges.add(edge);
                }
                for (GasEdge removal : removals) {
                    network.addEdge(removal);
                }
            }
        }

        if (DELETE_ACTIVE_ELEMENTS) {
            if (DELETE_BRIDGES_ONLY) {
                System.out.print("Deleting all compressors, control valves, resistors and valves that are bridges... ");
                System.out.println("network now has:");
                removedElements = safelyDeleteActiveElements(network);
                printElements(network);
                System.out.println("");
                System.out.print("Splitting the network into connected components... ");
                DFS<GasNode, GasEdge> dfs = new DFS<>(network);
                ccs = dfs.createConnectedComponents(GasEdge::createEdge);
                System.out.println(ccs.size() + " connected components found.");
            } else {
                System.out.print("Deleting all compressors, control valves, resistors and valves... ");
                System.out.println("network now has:");
                removedElements = deleteActiveElements(network);
                printElements(network);
                System.out.println("");
                System.out.print("Splitting the network into connected components... ");
                DFS<GasNode, GasEdge> dfs = new DFS<>(network);
                ccs = dfs.createConnectedComponents(GasEdge::createEdge);
                System.out.println(ccs.size() + " connected components found.");
            }
            for (GasEdge edge : removedElements) {
                linkNodes.add(edge.start());
                linkNodes.add(edge.end());
            }
        } else {
            ccs = new LinkedList<>();
            ccs.add(network);
        }

        int cccounter = 0;
        int sumEdges = 0, sumNodes = 0, trees = 0, singletons = 0, gspgs = 0, k4 = 0;

        for (Graph<GasNode, GasEdge> cc : ccs) {
            if (cc.numberOfNodes() == 1) {
                continue;
            }

            sumNodes += cc.numberOfNodes();
            sumEdges += cc.numberOfEdges();
            String type, activeType = "passive";
            for (GasEdge edge : cc.edges()) {
                if (edgeConnections.get(edge).getType() != ConnectionType.PIPE
                        && edgeConnections.get(edge).getType() != ConnectionType.SHORT_PIPE
                        && edgeConnections.get(edge).getType() != ConnectionType.RESISTOR) {
                    activeType = "active";
                }
            }
            GraphConversion<GasLibIntersection, GasLibConnection> restrictTo = conversion.restrictTo((List<GasNode>) cc.nodes());

            //restrictTo.shrinkLeaves();
            TikZ tikzed = restrictTo.toTikZ(linkNodes, bridges);
            GasLibNetworkFile networkFile = new GasLibNetworkFile();
            networkFile.initializeFrom(restrictTo);
            int nodenumber = cc.numberOfNodes();
            int edgenumber = cc.numberOfEdges();
            System.out.print(" Component with " + nodenumber + " nodes and " + edgenumber + " edges. ");
            if (cc.numberOfNodes() == 1) {
                type = "Singleton";
                singletons++;
            } else if (cc.numberOfNodes() == cc.numberOfEdges() + 1) {
                type = "Tree";
                trees++;
            } else {
                ((DynamicNetwork) cc).applyGSPGReduction();
                if (cc.numberOfNodes() > 1) {
                    type = "General";
                    k4++;
                } else {
                    type = "GSPG";
                    gspgs++;
                }
            }
            System.out.println("Topology: " + type);
            if (type.equals("General")) {
                System.out.println(" Contains a K_4. Reduction to " + cc.numberOfNodes() + " nodes and " + cc.numberOfEdges() + " edges.");
            }
            String nettype = "";
            switch (p) {
                case 0:
                    nettype = "H-Gas";
                    break;
                case 1:
                    nettype = "L-Gas";
                    break;
                default:
                    nettype = restrictTo.getInformation().getTitle();
            }
            String filename;
            File file;
            int counter = 0;
            do {
                counter++;
                filename = String.format("%1$s-%2$s-%3$s-%4$s-%5$s-%6$s.net",
                        nettype,
                        type,
                        nodenumber,
                        edgenumber,
                        activeType,
                        counter);
                file = new File(outputPath + filename);
            } while (file.exists());
            if (WRITE) {
                System.out.print("  Writing network file... ");
                networkFile.writeToFile(outputPath + filename);
                System.out.println("done.");
                tikzed.writeToFile(outputPath + filename.replace(".net", ".tex"));
                try {
                    System.out.print("  Creating PDF... ");
                    Process exec = Runtime.getRuntime().exec("pdflatex " + " -output-directory=" + outputPath + " " + outputPath + filename.replace(".net", ".tex"));
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(exec.getInputStream()))) {
                        String line;
                        boolean error = false;
                        while ((line = in.readLine()) != null) {
                            if (error || line.contains("Error") || line.contains("error")) {
                                System.out.println(line);
                                error = true;
                            }
                        }
                        exec.waitFor();
                        System.out.println("done.");
                    }
                    Files.deleteIfExists(Paths.get(outputPath + filename.replace(".net", ".aux")));
                    Files.deleteIfExists(Paths.get(outputPath + filename.replace(".net", ".log")));
                } catch (IOException ex) {
                    System.out.println("Error");
                    Logger.getLogger(GSPG.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //assert networkFile.getIntersections().getMap().size() == nodenumber;
            //assert networkFile.getConnections().getMap().size() == edgenumber;
        }
        if (DELETE_ACTIVE_ELEMENTS) {
            System.out.println("Connected Components & Singletons & Trees & GSPGs & General");
            System.out.printf("%1$s & %2$s & %3$s & %4$s & %5$s\n", ccs.size(), singletons, trees, gspgs, k4);
        }

        if (WRITE) {
            //conversion.shrinkLeaves();
            TikZ tikzed = conversion.toTikZ(linkNodes);
            String nettype;
            switch (p) {
                case 0:
                    nettype = "H-Gas";
                    break;
                case 1:
                    nettype = "L-Gas";
                    break;
                default:
                    nettype = conversion.getInformation().getTitle();
            }
            String filename = nettype + ".net";
            tikzed.writeToFile(outputPath + filename.replace(".net", ".tex"));
            try {
                System.out.print("  Creating PDF... ");
                Process exec = Runtime.getRuntime().exec("pdflatex " + " -output-directory=" + outputPath + " " + outputPath + filename.replace(".net", ".tex"));
                try (BufferedReader in = new BufferedReader(new InputStreamReader(exec.getInputStream()))) {
                    String line;
                    boolean error = false;
                    while ((line = in.readLine()) != null) {
                        if (error || line.contains("Error") || line.contains("error")) {
                            System.out.println(line);
                            error = true;
                        }
                    }
                    exec.waitFor();
                    System.out.println("done.");
                }
                Files.deleteIfExists(Paths.get(outputPath + filename.replace(".net", ".aux")));
                Files.deleteIfExists(Paths.get(outputPath + filename.replace(".net", ".log")));
            } catch (IOException ex) {
                System.out.println("Error");
                Logger.getLogger(GSPG.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //assert sumEdges == network.numberOfEdges() : "Edges lost!";
        //assert sumNodes == network.numberOfNodes() : "Nodes lost!";
    }

    public static boolean PRINT = true;

    public static void print(DynamicNetwork<GasNode, GasEdge> network) {
        if (!PRINT) {
            return;
        }
        System.out.println("\\begin{figure}[p]\n \\centering \n\\begin{tikzpicture}");

        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double width = 12.0;
        double height = 8.0;
        for (GasNode node : network.nodes()) {
            XMLIntersection intersection = nodeIntersections.get(node);
            if (intersection.getX() < minX) {
                minX = intersection.getX();
            } else if (intersection.getX() > maxX) {
                maxX = intersection.getX();
            }
            if (intersection.getY() < minY) {
                minY = intersection.getY();
            } else if (intersection.getY() > maxY) {
                maxY = intersection.getY();
            }
            System.out.printf("  \\node[nodeStyle] (n%1$s) at (%2$s,%3$s) {};\n",
                    node.id(),
                    (intersection.getX() - minX) / (maxX - minX) * width,
                    (intersection.getY() - minY) / (maxY - minY) * height);
        }

        for (GasNode node : network.nodes()) {
            XMLIntersection intersection = nodeIntersections.get(node);
            System.out.printf("  \\node[nodeStyle] (n%1$s) at (%2$s,%3$s) {};\n", node.id(), intersection.getX() / 700, intersection.getY() / 500);
        }
        for (GasEdge edge : network.edges()) {
            if (edgeConnections.containsKey(edge)) {
                System.out.printf("  \\draw[%3$s] (n%1$s) -- (n%2$s);\n", edge.start().id(), edge.end().id(), edgeConnections.get(edge).getType().toString().replace("_", ""));
            } else {
                System.out.printf("  \\draw (n%1$s) -- (n%2$s);\n", edge.start().id(), edge.end().id());
            }
        }
        System.out.println(" \\end{tikzpicture}\n\\end{figure}");
        System.out.println("");
    }

    public static void printB(DynamicNetwork<GasNode, GasEdge> network) {
        if (!PRINT) {
            return;
        }
        System.out.println("\\begin{figure}[p]\\centering\\begin{tikzpicture}");
        for (GasNode node : network.nodes()) {
            XMLIntersection intersection = nodeIntersections.get(node);
            if (network.degree(node) == 2) {
                GasEdge edge1 = network.incidentEdges(node).getFirst();
                GasEdge edge2 = network.incidentEdges(node).getLast();
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
        for (GasEdge edge : network.edges()) {
            if (edgeConnections.containsKey(edge)) {
                System.out.printf("\\draw[%3$s] (n%1$s) -- (n%2$s);\n", edge.start().id(), edge.end().id(), edgeConnections.get(edge).getType().toString().replace("_", ""));
            } else {
                System.out.printf("\\draw (n%1$s) -- (n%2$s);\n", edge.start().id(), edge.end().id());
            }
        }
        System.out.println("\\end{tikzpicture}\\end{figure}");
        System.out.println("");
    }
}
