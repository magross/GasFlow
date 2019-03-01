/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gas.algo.propagation;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import ds.graph.DynamicNetwork;
import ds.graph.GasEdge;
import ds.graph.GasNode;
import gas.io.gaslib.GasLibConnection;
import gas.io.gaslib.GasLibNetworkFile;
import gas.io.gaslib.GasLibScenario;
import gas.io.gaslib.GasLibScenarioFile;
import gas.io.gaslib.GasLibZipArchive;
import gas.io.water.PowerNetworkFile;
import gas.io.water.WaterNetworkFileGMSDAT;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 *
 * @author Martin Groß
 */
public class BoundPropagationSimpleMain {

    private static final double VERSION = 0.5;
    private static final boolean PRINT_TIKZ = true;
    private static final boolean DEBUG = false;

    public static JSAPResult parseCommandLineArguments(String[] args) throws IOException, JSAPException {
        URL url = BoundPropagationSimpleMain.class.getResource("BoundPropagationSimpleJSAP.xml");
        JSAP jsap = new JSAP(url);
        JSAPResult config = jsap.parse(args);
        if (!config.success()) {
            System.err.println();
            for (java.util.Iterator errs = config.getErrorMessageIterator();
                    errs.hasNext();) {
                System.err.println("Error: " + errs.next());
            }
            System.err.println();
            System.err.println("Usage: java " + BoundPropagationSimpleMain.class.getName());
            System.err.println("                " + jsap.getUsage());
            System.err.println();
            System.err.println(jsap.getHelp());
            System.exit(1);
        }
        return config;
    }

    public static List<Entry> gasResults = new LinkedList<>();
    public static List<Entry> powerResults = new LinkedList<>();
    public static List<Entry> waterResults = new LinkedList<>();

    public static void processFile(File inputFile) {
        System.out.print("Processing " + inputFile.getName() + "...");
        BoundPropagation propagation = new BoundPropagation();
        DynamicNetwork<GasNode, GasEdge> network = null;
        Entry entry = new Entry();
        GasLibNetworkFile netFile = null;
        GasLibScenarioFile scenarioFile = null;
        if (inputFile.getPath().endsWith(".net")) {
            netFile = new GasLibNetworkFile(inputFile.getPath());
            propagation.setGasNetworkFile(netFile);
            entry.setNumberOfSources(netFile.getNumberOfSources());
            entry.setNumberOfSinks(netFile.getNumberOfSinks());
            entry.setNumberOfTerminals(netFile.getNumberOfSources() + netFile.getNumberOfSinks());
            gasResults.add(entry);
        } else if (inputFile.getPath().endsWith(".zip")) {
            GasLibZipArchive archive = new GasLibZipArchive(inputFile.getPath(), true);
            netFile = archive.getNetworkFile();
            scenarioFile = archive.getScenarioFiles().values().iterator().next();
            if (DEBUG) {
                System.out.println("");
                System.out.println("Initial Validation:");
                scenarioFile.validateBalance();
            }
            propagation.setGasNetworkFile(netFile);
            propagation.setGasScenarioFile(scenarioFile);
            entry.setScenarioName(scenarioFile.getScenarios().values().iterator().next().getId());
            entry.setNumberOfSources(netFile.getNumberOfSources());
            entry.setNumberOfSinks(netFile.getNumberOfSinks());
            entry.setNumberOfTerminals(netFile.getNumberOfSources() + netFile.getNumberOfSinks());
            gasResults.add(entry);
        } else if (inputFile.getPath().endsWith(".gmsdat") || inputFile.getPath().endsWith(".dat")) {
            WaterNetworkFileGMSDAT file = new WaterNetworkFileGMSDAT();
            network = file.readFromFile(inputFile.getPath());
            waterResults.add(entry);
            entry.setNumberOfSources(file.getNumberOfSources());
            entry.setNumberOfSinks(file.getNumberOfSinks());
            entry.setNumberOfTerminals(file.getNumberOfTerminals());
            propagation.setNetwork(network);
        } else if (inputFile.getPath().endsWith(".m")) {
            PowerNetworkFile file = new PowerNetworkFile();
            network = file.readFromFile(inputFile.getPath());
            propagation.setNetwork(network);
            entry.setNumberOfSources(file.getNumberOfSources());
            entry.setNumberOfSinks(file.getNumberOfSinks());
            entry.setNumberOfTerminals(file.getNumberOfTerminals());
            powerResults.add(entry);
        } else {
            System.out.println(" Error: File extension is neither .gmsdat, .m, .net nor .zip.");
            return;
        }
        if (network == null) {
            network = propagation.getNetwork();
        }
        entry.setName(inputFile.getName());
        entry.setNumberOfNodes(network.numberOfNodes());
        entry.setNumberOfEdges(network.numberOfEdges());
        long start = System.nanoTime();
        propagation.run();
        long end = System.nanoTime();
        System.out.println(((end - start) / 1000.0) + " µs");
        entry.setRuntime(end - start);
        entry.setReducedNumberOfNodes(propagation.getNetwork().numberOfNodes());
        entry.setReducedNumberOfEdges(propagation.getNetwork().numberOfEdges());
        entry.setLeafReductions(propagation.getLeafReductions());
        entry.setSerialReductions(propagation.getSerialReductions());
        entry.setParallelReductions(propagation.getParallelReductions());

        if (netFile != null) {
            List<String> intersectionIDs = netFile.getIntersections().getMap().values().stream().map(i -> i.getId()).collect(Collectors.toList());
            Set<GasLibConnection> connections = netFile.getConnections().getMap().values();
            if (DEBUG) {
                for (GasLibConnection c : connections) {
                    if (!intersectionIDs.contains(c.getFrom().getId())) {
                        System.out.println("INCONSISTENCY ALERT (from)! " + c.getId() + " " + c.getFrom().getId());
                    }
                    if (!intersectionIDs.contains(c.getTo().getId())) {
                        System.out.println("INCONSISTENCY ALERT (to)!  " + c.getId() + " " + c.getTo().getId());
                    }
                }
            }

            netFile.writeToFile(networkFilename);
        }
        if (scenarioFile != null) {
            scenarioFile.removeNodes(netFile);
            if (DEBUG) {
                if (netFile != null) {
                    List<String> networkIntIDs = netFile.getIntersections().getMap().values().stream().map(i -> i.getId()).collect(Collectors.toList());
                    for (GasLibScenario scen : scenarioFile.getScenarios().values()) {
                        Set<String> keySet = scen.getNodes().keySet();
                        for (String string : keySet) {
                            if (!networkIntIDs.contains(string)) {
                                System.out.println("INCONSISTENCY ALERT (scenario)! " + string);
                            }
                        }
                    }
                }
                scenarioFile.validateBalance();
            }

            scenarioFile.writeToFile(scenarioFilename);
        }
    }

    public static String networkFilename = "c:\\data\\test.net";
    public static String scenarioFilename = "c:\\data\\test.scn";

    public static void processDirectory(File inputFile) {
        try {
            for (Path path : Files.list(inputFile.toPath()).collect(Collectors.toList())) {
                processFile(path.toFile());
            }
        } catch (IOException ex) {
            Logger.getLogger(BoundPropagationSimpleMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
            char[] test = new char[6];
            System.out.println(test);
        System.out.println("BoundPropagation " + VERSION + ": ");
        JSAPResult config = null;
        try {
            config = parseCommandLineArguments(args);
        } catch (IOException | JSAPException ex) {
            System.out.println(ex);
        }

        File inputFile;
        if (config == null || !config.contains("input")) {
            System.out.println(" No input network specified, opening dialog.");
            JFileChooser fileChooser = new JFileChooser();

            fileChooser.setVisible(true);
            FileDialog dialog = new FileDialog((JFrame) null);
            dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setVisible(true);
            File[] files = dialog.getFiles();
            if (files == null || files.length == 0) {
                inputFile = null;
                System.exit(1);
            } else {
                inputFile = files[0];
            }
            dialog.dispose();
        } else {
            inputFile = config.getFile("input");
            System.out.println(" Input network specified in command line: " + inputFile.getPath());
        }

        if (inputFile == null || !inputFile.exists()) {
            System.out.println(" Error: Specified input file does not exist.");
            System.exit(1);
        }
        if (inputFile.isFile()) {
            processFile(inputFile);
        } else if (inputFile.isDirectory()) {
            processDirectory(inputFile);
        } else {
            throw new AssertionError("This should not happen.");
        }

        gasResults.sort((Entry e, Entry e2) -> e.getNumberOfEdges() - e2.getNumberOfEdges());
        powerResults.sort((Entry e, Entry e2) -> e.getNumberOfEdges() - e2.getNumberOfEdges());
        waterResults.sort((Entry e, Entry e2) -> e.getNumberOfEdges() - e2.getNumberOfEdges());
        printEntryList(System.out, waterResults);
        printEntryList(System.out, gasResults);
        printEntryList(System.out, powerResults);
    }

    public static void printResults() {
        gasResults.sort((Entry e, Entry e2) -> e.getNumberOfEdges() - e2.getNumberOfEdges());
        printEntryList(System.out, gasResults);
    }

    public static void printEntryList(PrintStream out, List<Entry> entries) {
        if (!PRINT_TIKZ) {
            return;
        }
        out.println("");
        out.println(" \\begin{tabular}{@{}lrrrrrrrrrr@{}}");
        out.println("  \\toprule");
        out.println("  Instance & Scenario & $\\card{\\nodes}$ & $\\card{\\nodes_{\\pm}}$ & $\\card{\\arcs}$ & $\\card{\\nodes'}$ & $\\card{\\arcs'}$ & L & P & S & $t$ & $\\rho$\\\\");
        out.println("  \\midrule");
        entries.sort((Entry e, Entry e2) -> e.getNumberOfEdges() - e2.getNumberOfEdges());
        for (Entry e : entries) {
            String line = String.format("  \\instName{%s} & & \\instName{%s} %s & %s & %s & %s & %s & %s & %s & %s & %.4f & \\SI{%s}{\\percent} \\\\\n",
                    e.getName().substring(0, e.getName().indexOf(".")).replaceAll("_", ""),
                    e.getScenarioName(),
                    e.getNumberOfNodes(),
                    e.getNumberOfTerminals(),
                    e.getNumberOfEdges(),
                    e.getReducedNumberOfNodes(),
                    e.getReducedNumberOfEdges(),
                    e.getLeafReductions(),
                    e.getParallelReductions(),
                    e.getSerialReductions(),
                    Math.round(e.getRuntime() / 100000.0) / 10000.0,
                    Math.round(100 - e.getReducedNumberOfEdges() * 100.0 / e.getNumberOfEdges())
            ).replace(",", ".");
            out.printf(line);
        }
        out.println("  \\bottomrule");
        out.println(" \\end{tabular}");
    }

}
