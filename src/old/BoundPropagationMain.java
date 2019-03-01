/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package old;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import ds.graph.DynamicNetwork;
import ds.graph.GasEdge;
import ds.graph.GasNode;
import gas.algo.GSPG;
import gas.algo.propagation.BoundPropagation;
import gas.io.gaslib.GasLibNetworkFile;
import gas.io.gaslib.GasLibZipArchive;
import gas.io.water.PowerNetworkFile;
import gas.io.water.WaterNetworkFileGMSDAT;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 *
 * @author Martin Groß
 */
public class BoundPropagationMain {

    //private static final Logger LOGGER = LogManager.getLogger(BoundPropagationMain.class.getName());
    public static JSAPResult parseCommandLineArguments(String[] args) throws IOException, JSAPException {
        URL url = BoundPropagationMain.class.getResource("BoundPropagationJSAP.xml");
        JSAP jsap = new JSAP(url);
        JSAPResult config = jsap.parse(args);
        if (!config.success()) {
            System.err.println();
            for (java.util.Iterator errs = config.getErrorMessageIterator();
                    errs.hasNext();) {
                System.err.println("Error: " + errs.next());
            }
            System.err.println();
            System.err.println("Usage: java " + BoundPropagationMain.class.getName());
            System.err.println("                " + jsap.getUsage());
            System.err.println();
            System.err.println(jsap.getHelp());
            System.exit(1);
        }
        return config;
    }

    public static Properties loadSettings(File file) {
        Properties properties = new Properties();
        if (file == null) {
            System.out.println(" No settings file has been specified. Default settings will be used.");
        } else if (!file.exists()) {
            System.out.println(" Settings file " + file.getPath() + " does not exist. Default settings will be used.");
        } else {
            System.out.println(" Loading settings from " + file.getPath() + " ...");
            try (InputStream in = new FileInputStream(file)) {
                properties.loadFromXML(in);
                for (String property : properties.stringPropertyNames()) {
                    String value = properties.getProperty(property);
                    System.out.println(" " + property + "=" + value);
                }
                System.out.println(" Settings loaded successfully.");
            } catch (IOException ex) {
                //LOGGER.error("Loading settings caused an I/O Error.", ex);
            }
        }
        return properties;
    }

    public static void writeSettings(File file, Properties properties) {
        try (OutputStream out = new FileOutputStream("example.xml")) {
            properties.storeToXML(out, "");
        } catch (IOException ex) {
            //LOGGER.error("Saving settings caused an I/O error.", ex);
        }
    }

    private static final double VERSION = 0.2;

    public enum CommandLineKeys {
        INPUT("input"),
        SETTINGS("settings");

        private final String key;

        private CommandLineKeys(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return key;
        }
    }

    public enum PropertyKeys {
        LAST_NETWORK("lastNetwork");

        private final String key;

        private PropertyKeys(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return key;
        }
    }

    public static List<String> caseNames = new LinkedList<>();
    public static List<Integer> originalNodes = new LinkedList<>();
    public static List<Integer> originalSources = new LinkedList<>();
    public static List<Integer> originalSinks = new LinkedList<>();
    public static List<Integer> originalEdges = new LinkedList<>();
    public static List<Integer> newNodes = new LinkedList<>();
    public static List<Integer> newEdges = new LinkedList<>();

    public static void processPowerFile(File inputFile) {
        BoundPropagation propagation = new BoundPropagation();
        //System.out.println("");
        System.out.println(" Opening network file...");
        caseNames.add(inputFile.getName());
        PowerNetworkFile file = new PowerNetworkFile();
        DynamicNetwork<GasNode, GasEdge> network = file.readFromFile(inputFile.getPath());
        originalNodes.add(network.numberOfNodes());
        originalEdges.add(network.numberOfEdges());
        //System.out.printf("%1$s nodes and %2$s edges.\n", network.numberOfNodes(), network.numberOfEdges());
        propagation.setNetwork(network);
        //propagation.setVerbose(false);
        propagation.run();
        newNodes.add(propagation.getNetwork().numberOfNodes());
        newEdges.add(propagation.getNetwork().numberOfEdges());
        System.out.println("");
        System.out.println("Propagation complete.");
    }
    
    public static void processPowerDirectory(File inputFile)  {
        
        try {
            for (Path path : Files.list(inputFile.toPath()).collect(Collectors.toList())) {
                System.out.println(path.toString());
                processPowerFile(path.toFile());
            }
        } catch (IOException ex) {
            Logger.getLogger(BoundPropagationMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        System.out.println("BoundPropagation " + VERSION + ": ");
        JSAPResult config = null;
        try {
            config = parseCommandLineArguments(args);
        } catch (IOException | JSAPException ex) {
            System.out.println(ex);
        }
        Properties properties = loadSettings(config.getFile("settings"));

        File inputFile;
        if (config.contains("input")) {
            inputFile = config.getFile("input");
            System.out.println(" Input network specified in command line: " + inputFile.getPath());
        } else if (properties.containsKey(PropertyKeys.LAST_NETWORK.toString())) {
            inputFile = new File(properties.getProperty(PropertyKeys.LAST_NETWORK.toString()));
            System.out.println(" No input network specified in command line, trying the last network file instead: " + inputFile.getPath());
        } else {
            inputFile = null;
            System.out.println(" No input network specified.");
            JFileChooser chooser = new JFileChooser();
            
            FileDialog dialog = new FileDialog((JFrame) null);
            dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setVisible(true);
            //inputFile = new File(dialog.getDirectory() + dialog.getFile()).;
            File[] files = dialog.getFiles();
            if (files.length == 0) {
                inputFile = new File(dialog.getDirectory());
            } else {
                inputFile = files[0];
            }
            dialog.dispose();
            
            System.out.println(Arrays.deepToString(files));
            System.out.println();
            System.out.println(inputFile);
        }

        File inputTikzFile;
        if (config.contains("inputtikz")) {
            inputTikzFile = config.getFile("inputtikz");
            System.out.println(" TikZ file for the input specified in command line: " + inputTikzFile.getPath());
        } else {
            inputTikzFile = null;
            System.out.println(" No TikZ file for the input specified.");
        }

        File outputTikzFile;
        if (config.contains("outputtikz")) {
            outputTikzFile = config.getFile("outputtikz");
            System.out.println(" TikZ file for the output specified in command line: " + outputTikzFile.getPath());
        } else {
            outputTikzFile = null;
            System.out.println(" No TikZ file for the output specified.");
        }

        if (inputFile != null && inputFile.exists()) {
            BoundPropagation propagation = new BoundPropagation();
            GasLibNetworkFile netFile = null;
            if (inputFile.getPath().endsWith(".net")) {
                netFile = new GasLibNetworkFile(inputFile.getPath());
            } else if (inputFile.getPath().endsWith(".zip")) {
                GasLibZipArchive archive = new GasLibZipArchive(inputFile.getPath(), false);
                netFile = archive.getNetworkFile();
            } else if (inputFile.getPath().endsWith(".gmsdat")) {
            } else if (inputFile.getPath().endsWith(".m")) {
            } else {
                System.out.println(" Error: File extension is neither .gmsdat, .net nor .zip.");
            }
            if (netFile == null && inputFile.getPath().endsWith(".gmsdat")) {
                System.out.println("");
                System.out.println(" Opening network file...");
                WaterNetworkFileGMSDAT file = new WaterNetworkFileGMSDAT();
                DynamicNetwork<GasNode, GasEdge> network = file.readFromFile(inputFile.getPath());
                System.out.printf("%1$s nodes and %2$s edges.\n", network.numberOfNodes(), network.numberOfEdges());
                propagation.setNetwork(network);
                //propagation.setVerbose(config.getBoolean("verbose"));
                if (config.contains("inputtikz")) {
                    System.out.println("");
                    System.out.printf(" Creating TikZ file (%1$s) of the original network...\n", inputTikzFile.getPath());
                    propagation.write(config.getFile("inputtikz"));
                }
                propagation.run();
                if (config.contains("outputtikz")) {
                    System.out.println("");
                    System.out.printf(" Creating TikZ file (%1$s) of the reduced network...\n", outputTikzFile.getPath());
                    propagation.write(config.getFile("outputtikz"));
                }
                System.out.println("");
                System.out.println("Propagation complete.");
            } else if (netFile == null && inputFile.getPath().endsWith(".m")) {
                processPowerDirectory(inputFile.getParentFile());
                System.out.println("");
                for (int i = 0; i < originalEdges.size(); i++) {
                    System.out.printf("%1$s & %2$s & %3$s & %4$s & %5$s & %6$s",
                            caseNames.get(i),                            
                            originalNodes.get(i),
                            originalEdges.get(i),                            
                            newNodes.get(i),
                            newEdges.get(i),
                            100 - newEdges.get(i) * 100 / originalEdges.get(i)
                            );
                    System.out.println("\\%\\\\");
                }
                System.out.println("");
                //processPowerFile(inputFile);
            } else if (netFile == null && inputFile.isDirectory()) {
                processPowerDirectory(inputFile);
            } else if (netFile != null) {
                System.out.println("");
                System.out.print(" Opening network file...");
                propagation.setGasNetworkFile(netFile);
                System.out.printf(" loaded %1$s.\n", netFile.getInformation().getTitle());
                System.out.printf(" %1$s contains:\n", netFile.getInformation().getTitle());
                //propagation.printElements(System.out);
                //propagation.setVerbose(config.getBoolean("verbose"));
                if (config.contains("inputtikz")) {
                    System.out.println("");
                    System.out.printf(" Creating TikZ file (%1$s) of the original network...\n", inputTikzFile.getPath());
                    propagation.write(config.getFile("inputtikz"));
                }
                propagation.run();
                //propagation.printElements(System.out);
                if (config.contains("outputtikz")) {
                    System.out.println("");
                    System.out.printf(" Creating TikZ file (%1$s) of the reduced network...\n", outputTikzFile.getPath());
                    propagation.write(config.getFile("outputtikz"));
                }
                System.out.println("");
                System.out.println("Propagation complete.");
            }
        } else if (inputFile != null) {
            System.out.println(" Error: Specified input file does not exist.");
        }
        writeSettings(config.getFile("settings"), properties);
    }
}
