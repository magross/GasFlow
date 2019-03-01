/**
 * GasLibZipArchive.java
 *
 */
package gas.io.gaslib;

import ds.graph.DEdge;
import ds.graph.DNode;
import ds.graph.DynamicNetwork;
import ds.graph.GasEdge;
import ds.graph.GasNode;
import gas.io.ConnectionType;
import gas.io.GraphConversion;
import gas.io.XMLIntersection;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 *
 * @author Martin Groß
 */
public class GasLibZipArchive {

    private ZipFile zipFile;
    private GasLibCombinedDecisionFile combinedDecisionFile;
    private GasLibNetworkFile networkFile;
    private ZipEntry networkFileEntry;
    private Map<String, GasLibScenarioFile> scenarioFiles;
    private Map<String, GasLibScenarioFile> scenarioFilesZE;

    public GasLibZipArchive(String filename, boolean readScenarios) {
        this.scenarioFiles = new LinkedHashMap<>();
        try {
            zipFile = new ZipFile(filename);
            for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements();) {
                ZipEntry zipEntry = e.nextElement();
                String name = zipEntry.getName();
                if (name.endsWith(".net")) {
                    networkFileEntry = zipEntry;
                    networkFile = new GasLibNetworkFile();
                    networkFile.readFromFile(zipFile.getInputStream(zipEntry));
                } else if (name.endsWith(".cdf")) {
                    combinedDecisionFile = new GasLibCombinedDecisionFile();
                    combinedDecisionFile.readFromFile(zipFile.getInputStream(zipEntry));
                } else if (name.endsWith(".scn") && readScenarios) {
                    GasLibScenarioFile gasLibScenarioFile = new GasLibScenarioFile();
                    gasLibScenarioFile.readFromFile(zipFile.getInputStream(zipEntry));
                    scenarioFiles.put(name, gasLibScenarioFile);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(GasLibZipArchive.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void extractNetworkToFile(String fileToBeWritten) {
        try {
            //ZipEntry entry = zipFile.getEntry(networkFile.getN);
            InputStream inputStream = zipFile.getInputStream(networkFileEntry);
            File outFile = new File(fileToBeWritten);
            outFile.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(fileToBeWritten);
            byte[] buffer = new byte[9000];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GasLibZipArchive.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GasLibZipArchive.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

        }
    }
    
    public void extractScenarioToFile(String fileToBeExtracted, String fileToBeWritten) {
        try {
            ZipEntry entry = zipFile.getEntry(fileToBeExtracted);
            InputStream inputStream = zipFile.getInputStream(entry);
            File outFile = new File(fileToBeWritten);
            outFile.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(fileToBeWritten);
            byte[] buffer = new byte[9000];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GasLibZipArchive.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GasLibZipArchive.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

        }
    }

    public GasLibCombinedDecisionFile getCombinedDecisionFile() {
        return combinedDecisionFile;
    }

    public GasLibNetworkFile getNetworkFile() {
        return networkFile;
    }

    public Map<String, GasLibScenarioFile> getScenarioFiles() {
        return scenarioFiles;
    }

    static Map<GasNode, GasLibIntersection> nodeIntersections;
    static Map<GasEdge, GasLibConnection> edgeConnections;

    public static void main(String[] args) {
        GasLibZipArchive net = new GasLibZipArchive("../gaslib/gaslib-40.zip", false);

        for (GasLibScenarioFile file : net.getScenarioFiles().values()) {
            for (GasLibScenario scenario : file.getScenarios().values()) {

            }
        }

        if (1 == 1) {
            return;
        }
        GraphConversion<GasLibIntersection, GasLibConnection> conversion;
        conversion = net.getNetworkFile().getDynamicNetwork();
        DynamicNetwork<GasNode, GasEdge> network = conversion.getGraph();

        //SeriesParallelGraph.isSeriesParallel(network, nodeIntersections, edgeConnections);
        nodeIntersections = conversion.getNodeIntersections();
        edgeConnections = conversion.getEdgeConnections();
        /*
        print(network);

        // Remove inactive leaves
        Queue<DNode> queue = new LinkedList<>();
        for (DNode node : network.nodes()) {
            if (network.degree(node) == 1) {
                queue.add(node);
            }
        }
        while (!queue.isEmpty()) {
            DNode node = queue.poll();
            DEdge edge = network.incidentEdges(node).getFirst();
            DNode other = edge.opposite(node);
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
        for (DNode node : network.nodes()) {
            if (network.degree(node) == 1) {
                queue.add(node);
            }
        }
        while (!queue.isEmpty()) {
            DNode node = queue.poll();
            DEdge edge = network.incidentEdges(node).getFirst();
            DNode other = edge.opposite(node);
            network.removeNode(node);
            network.removeEdge(edge);
            if (network.degree(other) == 1) {
                queue.add(other);
            }
        }
        print(network);

        /*
         // Remove parallel edges
         Queue<DEdge> queue2 = new LinkedList<>();
         for (DNode node : network.nodes()) {
         LinkedList<DEdge> list = new LinkedList<>();
         if (network.incidentEdges(node).size() == 2) {
         DEdge edge = network.incidentEdges(node).getFirst();
         DEdge edge2 = network.incidentEdges(node).getLast();
         if (edge.isParallelTo(edge2)) {

         }
         } else {
         for (DEdge edge : network.incidentEdges(node)) {
         boolean isParallel = false;
         for (DEdge listEdge : list) {
         System.out.println(edge + " " + listEdge);
         if (edge.isParallelTo(listEdge)) {
         queue2.add(edge);
         isParallel = true;
         System.out.println("parallel");
         break;
         }
         }
         if (!isParallel) {
         list.add(edge);
         }
         }
         }
         list.clear();
         }
         while (!queue2.isEmpty()) {
         DEdge edge = queue2.poll();
         DNode other = edge.start();
         DNode other2 = edge.end();
         network.removeEdge(edge);
         if (network.degree(other) == 1) {
         queue.add(other);
         }
         if (network.degree(other2) == 1) {
         queue.add(other2);
         }
         }
         // Remove created leaves
         while (!queue.isEmpty()) {
         DNode node = queue.poll();
         DEdge edge = network.incidentEdges(node).getFirst();
         DNode other = edge.opposite(node);
         network.removeNode(node);
         network.removeEdge(edge);
         if (network.degree(other) == 1) {
         queue.add(other);
         }
         }*/
        // Remove degree 2 nodes
        /*
        for (DNode node : network.nodes()) {
            if (network.degree(node) == 2) {
                queue.add(node);
            }
        }
        while (!queue.isEmpty()) {
            DNode node = queue.poll();
            DEdge edge = network.incidentEdges(node).getFirst();
            DEdge edge2 = network.incidentEdges(node).getLast();
            DNode other = edge.opposite(node);
            DNode other2 = edge2.opposite(node);
            if (edge.isParallelTo(edge2)) {
                network.removeEdge(edge2);
            } else {
                DEdge newEdge = DEdge.createEdge(other, other2);
                if (edgeConnections.get(edge).getType().ordinal() < edgeConnections.get(edge2).getType().ordinal()) {
                    edgeConnections.put(newEdge, edgeConnections.get(edge2));
                } else {
                    edgeConnections.put(newEdge, edgeConnections.get(edge));
                }
                network.addEdge(newEdge);
                network.removeNode(node);
                network.removeEdge(edge);
                network.removeEdge(edge2);
            }
        }


        // Remove active leaves
        for (DNode node : network.nodes()) {
            if (network.degree(node) == 1) {
                queue.add(node);
            }
        }
        while (!queue.isEmpty()) {
            DNode node = queue.poll();
            DEdge edge = network.incidentEdges(node).getFirst();
            DNode other = edge.opposite(node);
            network.removeNode(node);
            network.removeEdge(edge);
            if (network.degree(other) == 1) {
                queue.add(other);
            }
        }
        print(network);

                 System.out.println(conversion.toTikZ().toString());

        printB(network);

        /*int valve = 0, controlvalve = 0, compressor = 0, resistor = 0;
         for (DEdge edge : network.edges()) {
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
        System.out.println(net.getNetworkFile().numberOfIntersections());
        System.out.println(net.getNetworkFile().numberOfConnections());
        System.out.println(net.getNetworkFile().numberOf(ConnectionType.VALVE));
        //System.out.println(net.getCombinedDecisionFile().getDecisionGroups());
        //GasLibZipArchive net2 = new GasLibZipArchive("../gaslib/gaslib-135.zip", true);
        //GasLibZipArchive net3 = new GasLibZipArchive("../gaslib/gaslib-582-v2.zip", false);
    }

    public static boolean PRINT = false;

    public static void print(DynamicNetwork<DNode, DEdge> network) {

        if (!PRINT) {
            return;
        }
        System.out.println("\\begin{figure}[p]\\centering\\begin{tikzpicture}");
        for (DNode node : network.nodes()) {
            XMLIntersection intersection = nodeIntersections.get(node);
            //System.out.printf("\\node[nodeStyle] (n%1$s) at (%2$s,%3$s) {};\n", node.id(), intersection.getX() / 700, intersection.getY() / 500);
        }
        for (DEdge edge : network.edges()) {
            if (edgeConnections.containsKey(edge)) {
                //System.out.printf("\\draw[%3$s] (n%1$s) -- (n%2$s);\n", edge.start().id(), edge.end().id(), edgeConnections.get(edge).getType().toString().replace("_", ""));
            } else {
                //System.out.printf("\\draw (n%1$s) -- (n%2$s);\n", edge.start().id(), edge.end().id());
            }
        }
        System.out.println("\\end{tikzpicture}\\end{figure}");
        System.out.println("");
    }

    public static void printB(DynamicNetwork<DNode, DEdge> network) {
        if (!PRINT) {
            return;
        }

        System.out.println("\\begin{figure}[p]\\centering\\begin{tikzpicture}");
        for (DNode node : network.nodes()) {
            XMLIntersection intersection = nodeIntersections.get(node);
            if (network.degree(node) == 2) {
                DEdge edge1 = network.incidentEdges(node).getFirst();
                DEdge edge2 = network.incidentEdges(node).getLast();
                if ((edgeConnections.get(edge1).getType() == ConnectionType.PIPE
                        || edgeConnections.get(edge1).getType() == ConnectionType.SHORT_PIPE)
                        && (edgeConnections.get(edge2).getType() == ConnectionType.PIPE
                        || edgeConnections.get(edge2).getType() == ConnectionType.SHORT_PIPE)) {
                    //System.out.printf("\\coordinate (n%1$s) at (%2$s,%3$s) {};\n", node.id(), intersection.getX() / 700, intersection.getY() / 500);
                } else {
                    //System.out.printf("\\node[nodeStyle] (n%1$s) at (%2$s,%3$s) {};\n", node.id(), intersection.getX() / 700, intersection.getY() / 500);
                }
            } else {
                //System.out.printf("\\node[nodeStyle] (n%1$s) at (%2$s,%3$s) {};\n", node.id(), intersection.getX() / 700, intersection.getY() / 500);
            }
        }
        for (DEdge edge : network.edges()) {
            if (edgeConnections.containsKey(edge)) {
                //System.out.printf("\\draw[%3$s] (n%1$s) -- (n%2$s);\n", edge.start(), edge.end(), edgeConnections.get(edge).getType().toString().replace("_", ""));
            } else {
                //System.out.printf("\\draw (n%1$s) -- (n%2$s);\n", edge.start().id(), edge.end().id());
            }
        }
        System.out.println("\\end{tikzpicture}\\end{figure}");
        System.out.println("");
    }
}
