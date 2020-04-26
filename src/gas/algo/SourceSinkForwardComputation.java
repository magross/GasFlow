/**
 * Test.java
 *
 */
package gas.algo;

import ds.graph.DynamicNetwork;
import ds.graph.GasEdge;
import ds.graph.GasNode;
import gas.ds.ForwardComputationStep;
import gas.ds.GasFlow;
import gas.generator.OnionGenerator;
import gas.io.ConnectionType;
import gas.io.GraphConversion;
import gas.io.gaslib.GasLibConnection;
import gas.io.gaslib.GasLibIntersection;
import gas.io.gaslib.GasLibZipArchive;
import gas.io.tikz.TikZDataFile;
import gas.problem.SourceSinkForwardComputationProblem;
import java.util.LinkedList;
import java.util.Queue;
import units.UnitsTools;

/**
 *
 * @author Martin Groß
 */
public class SourceSinkForwardComputation {

    public SourceSinkForwardComputation() {

    }

    public SourceSinkForwardComputationProblem onion(int layers) {
        OnionGenerator onionGenerator = new OnionGenerator();
        SourceSinkForwardComputationProblem problem = onionGenerator.generate(layers);
        return problem;
    }

    public static SourceSinkForwardComputationProblem gaslib40() {
        GasLibZipArchive net = new GasLibZipArchive("../gaslib/gaslib-40.zip", false);
        GraphConversion<GasLibIntersection, GasLibConnection> conversion;
        conversion = net.getNetworkFile().getDynamicNetwork();
        DynamicNetwork<GasNode, GasEdge> dynamicNetwork = conversion.getGraph();
        Queue<GasEdge> nonPipes = new LinkedList<>();

        for (GasEdge edge : dynamicNetwork.edges()) {
            if (conversion.getConnection(edge).getType() != ConnectionType.PIPE) {
                nonPipes.add(edge);
            }
        }
        while (!nonPipes.isEmpty()) {
            GasEdge edge = nonPipes.poll();
            dynamicNetwork.merge(edge.start(), edge.end());
        }

        SourceSinkForwardComputationProblem fcp = new SourceSinkForwardComputationProblem();
        fcp.setNetwork(dynamicNetwork);
        //fcp.setSource(dynamicNetwork.nodes().getFirst());
        //fcp.setSink(dynamicNetwork.nodes().getLast());

        return fcp;
    }

    public static void main(String[] args) {
        SourceSinkForwardComputationProblem problem;

        OnionGenerator onionGenerator = new OnionGenerator();
        double[] sourcePressures = {40.0}; //, 5.0, 10.0, 20.0, 50.0, 100.0};
        double[] sinkPressures = {39.5}; // , 1.0, 1.0, 1.0, 1.0, 1.0};
        // Länge eines Zeitschrittes in Sekunden
        double[] timeSteps = {1};// {0.1, 0.2, 0.5, 1.0, 2.0, 5.0, 10.0};
        double maxTime = 3600.0;
        int agglomerate = 1000;
        boolean input = false;
        for (int layers = 0; layers <= 0; layers++) {
            for (int i = 0; i < sourcePressures.length; i++) {
                for (int timeStepIndex = 0; timeStepIndex < timeSteps.length; timeStepIndex++) {

                    problem = onionGenerator.generate(layers, sourcePressures[i] * UnitsTools.bar, sinkPressures[i] * UnitsTools.bar);

                    DynamicNetwork<GasNode, GasEdge> dynamicNetwork = (DynamicNetwork<GasNode, GasEdge>) problem.getNetwork();
                    GasNode source = problem.getSource();
                    GasNode sink = problem.getSink();

                    problem.getInitialPressures().set(dynamicNetwork.nodes().iterator().next(), sourcePressures[i] * UnitsTools.bar);
                    problem.setTimeStep(timeSteps[timeStepIndex] * UnitsTools.s);

                    ISO3ForwardComputationStep algo = new ISO3ForwardComputationStep();

                    TikZDataFile file = new TikZDataFile("Time [s]", "Mass flow arriving at sink [kg]");
                    for (int t = 0; t < (int) (maxTime/timeSteps[timeStepIndex]); t++) {
                        algo.setProblem(problem);
                        algo.run();
                        ForwardComputationStep step = algo.getSolution();
                        GasFlow next = step.getNextGasFlow();

                        problem.setInitialPressures(next.getPressures());
                                                
                        problem.getInitialPressures().set(source, sourcePressures[i] * UnitsTools.bar);

                        problem.assumeExactPressures();

                        double pd = problem.getInitialPressures().get(sink) - sinkPressures[i] * UnitsTools.bar;
                        double x = UnitsTools.g_to_kg(pd/(problem.getSpeedOfSound()*problem.getSpeedOfSound())*sink.getVolume());
                        
                        
                        // Output Mass Floe
                        file.addDataPoint(timeSteps[timeStepIndex]*(t+1), x);
                        
                        problem.getInitialPressures().set(sink, sinkPressures[i] * UnitsTools.bar);
                    }
                    String filename = "Onion_%1$s_%2$s_%3$s_%4$s.txt";
                    //filename = "edge.tex";
                    file.writeToFile(String.format(filename,layers,sourcePressures[i],sinkPressures[i],timeSteps[timeStepIndex]));
                    System.out.println(String.format(filename + " done.",layers,sourcePressures[i],sinkPressures[i],timeSteps[timeStepIndex]));
                }
            }
        }
    }
}
