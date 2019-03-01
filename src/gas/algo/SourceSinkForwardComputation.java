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
import javax.measure.quantity.Pressure;
import static javax.measure.unit.NonSI.BAR;
import static javax.measure.unit.SI.KILOGRAM;
import static javax.measure.unit.SI.SECOND;
import org.jscience.physics.amount.Amount;

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

                    problem = onionGenerator.generate(layers, Amount.valueOf(sourcePressures[i], BAR), Amount.valueOf(sinkPressures[i], BAR));

                    DynamicNetwork<GasNode, GasEdge> dynamicNetwork = (DynamicNetwork<GasNode, GasEdge>) problem.getNetwork();
                    GasNode source = problem.getSource();
                    GasNode sink = problem.getSink();

                    problem.getInitialPressures().set(dynamicNetwork.nodes().iterator().next(), Amount.valueOf(sourcePressures[i], BAR));
                    problem.setTimeStep(Amount.valueOf(timeSteps[timeStepIndex], SECOND));

                    ISO3ForwardComputationStep algo = new ISO3ForwardComputationStep();

                    TikZDataFile file = new TikZDataFile("Time [s]", "Mass flow arriving at sink [kg]");
                    for (int t = 0; t < (int) (maxTime/timeSteps[timeStepIndex]); t++) {
                        algo.setProblem(problem);
                        algo.run();
                        ForwardComputationStep step = algo.getSolution();
                        GasFlow next = step.getNextGasFlow();

                        problem.setInitialPressures(next.getPressures());
                        
                        //Amount<Pressure> old = problem.getInitialPressures().get(source);
                        
                        problem.getInitialPressures().set(source, Amount.valueOf(sourcePressures[i], BAR));
                        
                        //System.out.println(Amount.valueOf(sourcePressures[i], BAR) + " " + old);
                        //if (.doubleValue(BAR) > 2 ) {
                            
                        //}
                        //System.out.println(problem.getInitialPressures().get(source));
                        problem.assumeExactPressures();

                        Amount pd = problem.getInitialPressures().get(sink).minus(Amount.valueOf(sinkPressures[i], BAR));
                        Amount x = pd.divide(problem.getSpeedOfSound().pow(2)).times(sink.getVolume()).to(KILOGRAM);
                        
                        
                        // Output Mass Floe
                        //file.addDataPoint(timeSteps[timeStepIndex]*(t+1), x.doubleValue(KILOGRAM));
                        
                        file.addDataPoint(timeSteps[timeStepIndex]*(t+1), x.doubleValue(KILOGRAM));
                        
                        problem.getInitialPressures().set(sink, Amount.valueOf(sinkPressures[i], BAR));
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
