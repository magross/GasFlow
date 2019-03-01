/**
 * MergingGSPGGenerator.java
 *
 */
package ds.graph.generator;

import ds.graph.GSPCompositionType;
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
public class RelaxedMergingGSPGGenerator<N, E extends AbstractEdge<N>> extends MergingGSPGGenerator<N, E> {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(RelaxedMergingGSPGGenerator.class.getName());

    /**
     * The number of tries until invalid compositions are allowed.
     */
    private int maximumTries;

    /**
     * Creates a new graph generator using a MersenneTwister implementation.
     */
    public RelaxedMergingGSPGGenerator() {
        super();
        maximumTries = 1000;
    }

    /**
     * Creates a new graph generator that will use the given random number
     * generator.
     *
     * @param rng the random number generator.
     */
    public RelaxedMergingGSPGGenerator(RandomGenerator rng) {
        super(rng);
        maximumTries = 1000;
    }

    @Override
    public GSPG<N, E> generate(Supplier<N> nodeFactory, BiFunction<N, N, E> edgeFactory) {
        List<GSPG<N, E>> graphs = new LinkedList<>();
        for (int i = 0; i < numberOfEdges; i++) {
            graphs.add(new GSPG<>(nodeFactory, edgeFactory));
        }
        while (graphs.size() > 1) {
            int typeIndex = 1 + weightedDraw(
                    parallelFrequency,
                    seriesFrequency,
                    treeFrequency);
            GSPCompositionType type = GSPCompositionType.values()[typeIndex];
            BiPredicate<GSPG<N, E>,GSPG<N, E>> predicate;
            switch (type) {
                case PARALLEL:
                    predicate = this::isValidParallelComposition;
                    break;
                case SERIES:
                    predicate = this::isValidSeriesComposition;
                    break;
                case TREE:
                    predicate = this::isValidTreeComposition;
                    break;
                default:
                    throw new AssertionError("This should be impossible");
            }
            int i,j;
            GSPG<N, E> g1, g2;
            int tries = 0;
            do {
                i = rng.nextInt(graphs.size());
                j = rng.nextInt(graphs.size()-1);
                if (j >= i) {
                    j++;
                }
                g1 = graphs.get(i);
                g2 = graphs.get(j);
                tries++;
            } while (!predicate.test(g1, g2) && tries <= maximumTries);
            graphs.remove(g1);
            graphs.remove(g2);
            GSPG<N, E> newGraph = new GSPG<>(type, g1, g2);
            graphs.add(newGraph);
        }
        return graphs.get(0);
    }

    /**
     * Returns the number of tries for picking a pair of graphs to be composed
     * until invalid compositions are allowed.
     *
     * @return the number of tries until invalid compositions are allowed.
     */
    public int getMaximumTries() {
        return maximumTries;
    }

    /**
     * Sets the number of tries for picking a pair of graphs to be composed
     * until invalid compositions are allowed.
     *
     * @param maximumTries the number of tries until invalid compositions are allowed.
     */
    public void setMaximumTries(int maximumTries) {
        this.maximumTries = maximumTries;
    }

    public static void main(String[] args) {
        RelaxedMergingGSPGGenerator<DNode, DEdge> spgg = new RelaxedMergingGSPGGenerator<>();
        spgg.setNumberOfEdges(20000);
        for (int i = 0; i < 10; i++) {
            GSPG<DNode, DEdge> g = spgg.generate(DNode::createNode, DEdge::createEdge);
            GML.writeToFile(g, "MRel"+ spgg.getNumberOfEdges() +"_"+i+".gml");
        }
    }
}
