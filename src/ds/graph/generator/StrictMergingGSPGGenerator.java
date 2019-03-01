/**
 * MergingGSPGGenerator.java
 *
 */
package ds.graph.generator;

import ds.graph.GSPCompositionType;
import ds.graph.GSPComposition;
import ds.graph.GSPG;
import ds.graph.AbstractEdge;
import ds.graph.DEdge;
import ds.graph.DNode;
import static ds.graph.GSPCompositionType.PARALLEL;
import static ds.graph.GSPCompositionType.SERIES;
import static ds.graph.GSPCompositionType.TREE;
import gas.io.gml.GML;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * Generates generalized series-parallel networks (GSPGs) by first generating a
 * set of edges, which are minimum GSPGs. Subsequently, GSPGs are merged
 * together, until only a single graph remains. This generator allows several
 * restrictions to the merging process, like bounding the number of parallel
 * edges and the maximum degree in the graph. If the generator runs into dead
 * ends, it will break-up a graph into its edges and start anew.
 *
 * @author Martin Groß
 * @param <N> the node class of the generated graph.
 * @param <E> the edge class of the generated graph.
 */
public class StrictMergingGSPGGenerator<N, E extends AbstractEdge<N>> extends MergingGSPGGenerator<N, E> {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(StrictMergingGSPGGenerator.class.getName());

    /**
     * Creates a new graph generator using a MersenneTwister implementation.
     */
    public StrictMergingGSPGGenerator() {
        super();
    }

    /**
     * Creates a new graph generator that will use the given random number
     * generator.
     *
     * @param rng the random number generator.
     */
    public StrictMergingGSPGGenerator(RandomGenerator rng) {
        super(rng);
    }

    @Override
    public GSPG<N, E> generate(Supplier<N> nodeFactory, BiFunction<N, N, E> edgeFactory) {
        List<GSPG<N, E>> graphs = new LinkedList<>();
        for (int i = 0; i < numberOfEdges; i++) {
            graphs.add(new GSPG<>(nodeFactory, edgeFactory));
        }
        List<GSPComposition> parallelCompositions = generateCompositions(PARALLEL, this::isValidParallelComposition, graphs);
        List<GSPComposition> seriesCompositions = generateCompositions(SERIES, this::isValidSeriesComposition, graphs);
        List<GSPComposition> treeCompositions = generateCompositions(TREE, this::isValidTreeComposition, graphs);
        List<GSPComposition> compositions;
        while (graphs.size() > 1) {
            // Pick a composition type.
            int typeIndex = 1 + weightedDraw(
                    parallelCompositions.isEmpty() ? 0 : parallelFrequency,
                    seriesCompositions.isEmpty() ? 0 : seriesFrequency,
                    treeCompositions.isEmpty() ? 0 : treeFrequency);
            GSPCompositionType type;
            GSPG<N, E> g1, g2;
            // If there was no composition to pick, we add an additional edge
            // and perform a series composition with it.
            if (typeIndex == 0) {
                int index = 0;
                int maxSourceDegree = 0;
                int gIndex = 0;
                for (GSPG<N, E> g : graphs) {
                    if (g.degree(g.getSource()) > maxSourceDegree) {
                        maxSourceDegree = g.degree(g.getSource());
                        index = gIndex;
                        gIndex += 1;
                    }
                }
                type = SERIES;
                g1 = new GSPG<>(nodeFactory, edgeFactory);
                g2 = graphs.remove(index);

            } else {
                // Pick a composition from the choosen type.
                type = GSPCompositionType.values()[typeIndex];
                switch (type) {
                    case PARALLEL:
                        compositions = parallelCompositions;
                        break;
                    case SERIES:
                        compositions = seriesCompositions;
                        break;
                    case TREE:
                        compositions = treeCompositions;
                        break;
                    default:
                        throw new AssertionError("This should be impossible");
                }
                List<Double> probabilities = (List<Double>) compositions.stream().map(this::compositionProbability).collect(Collectors.toList());
                int index = weightedDraw(probabilities);
                GSPComposition composition = compositions.remove(index);
                g1 = composition.getGraph1();
                g2 = composition.getGraph2();
                graphs.remove(g1);
                graphs.remove(g2);
            }
            GSPG<N, E> newGraph = new GSPG<>(type, g1, g2);
            parallelCompositions.removeIf(c -> c.contains(g1) || c.contains(g2));
            parallelCompositions.addAll(generateCompositions(PARALLEL, this::isValidParallelComposition, newGraph, graphs));
            seriesCompositions.removeIf(c -> c.contains(g1) || c.contains(g2));
            seriesCompositions.addAll(generateCompositions(SERIES, this::isValidSeriesComposition, newGraph, graphs));
            treeCompositions.removeIf(c -> c.contains(g1) || c.contains(g2));
            treeCompositions.addAll(generateCompositions(TREE, this::isValidTreeComposition, newGraph, graphs));
            graphs.add(newGraph);
        }
        return graphs.get(0);
    }

    /**
     * Computes a relative frequency with which a composition should happen.
     *
     * @param composition the composition for which a frequency is computed.
     * @return the relative frequency for the composition;
     */
    protected Double compositionProbability(GSPComposition<N, E> composition) {
        GSPG<N, E> g1 = composition.getGraph1();
        GSPG<N, E> g2 = composition.getGraph2();
        N s1 = composition.getGraph1().getSource();
        N s2 = composition.getGraph2().getSource();
        N t1 = composition.getGraph1().getSink();
        N t2 = composition.getGraph2().getSink();
        int ds1 = g1.degree(s1);
        int ds2 = g2.degree(s2);
        int dt1 = g1.degree(t1);
        int dt2 = g2.degree(t2);
        double exp = 0;
        switch (composition.getType()) {
            case PARALLEL:
                exp = Math.max(ds1 + ds2, dt1 + dt2);
                break;
            case SERIES:
                exp = (ds1 + dt2 < dt1 + ds2) ? ds1 + dt2 : dt1 + ds2;
                break;
            case TREE:
                exp = ds1 + ds2;
                break;
            default:
                throw new AssertionError("This should be impossible.");
        }
        return Math.pow(2, -exp);
    }

    /**
     * Generates all compositions of the specified type for the given list of
     * graphs. A validity check can be specified to filter the results.
     *
     * @param type the type of compositions to generate.
     * @param validityCheck the predicate for the validity check.
     * @param graphs the list of possible graphs.
     * @return all valid compositions of the given type using the specified list
     * of graphs.
     */
    protected List<GSPComposition> generateCompositions(GSPCompositionType type, BiPredicate<GSPG<N, E>, GSPG<N, E>> validityCheck, List<GSPG<N, E>> graphs) {
        List<GSPComposition> result = new LinkedList<>();
        graphs.stream().forEach((g) -> {
            graphs.stream().filter((h) -> !(g == h) && validityCheck.test(g, h)).forEach((h) -> {
                result.add(new GSPComposition(type, g, h));
            });
        });
        return result;
    }

    /**
     * Generates all compositions of the specified type for a given graph with
     * the given list of graphs. A validity check can be specified to filter the
     * results.
     *
     * @param type the type of compositions to generate.
     * @param validityCheck the predicate for the validity check.
     * @param g the given graph.
     * @param graphs the list of possible partner graphs.
     * @return all valid compositions of the given type using the specified list
     * of graphs.
     */
    protected List<GSPComposition> generateCompositions(GSPCompositionType type, BiPredicate<GSPG<N, E>, GSPG<N, E>> validityCheck, GSPG<N, E> g, List<GSPG<N, E>> graphs) {
        List<GSPComposition> result = new LinkedList<>();
        graphs.stream().filter((h) -> !(g == h) && validityCheck.test(g, h)).forEach((h) -> {
            result.add(new GSPComposition(type, g, h));
        });
        graphs.stream().filter((h) -> !(g == h) && validityCheck.test(h, g)).forEach((h) -> {
            result.add(new GSPComposition(type, h, g));
        });
        return result;
    }

    public static void main(String[] args) {
        StrictMergingGSPGGenerator<DNode, DEdge> spgg = new StrictMergingGSPGGenerator<>();
        spgg.setNumberOfEdges(10000);
        for (int i = 0; i < 10; i++) {
            GSPG<DNode, DEdge> g = spgg.generate(DNode::createNode, DEdge::createEdge);
            //g.nodes().stream().forEach((node) -> System.out.println("Node: " + g.degree(node)));
            GML.writeToFile(g, "MStr" + spgg.getNumberOfEdges() + "_" + i + ".gml");
        }

    }
}
