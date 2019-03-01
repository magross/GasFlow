/**
 * MergingGSPGGenerator.java
 *
 */

package ds.graph.generator;

import ds.graph.AbstractEdge;
import ds.graph.GSPG;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * A base class for generalized series-parallel network (GSPGs) generators,
 * that start by generating a set of edges, which are minimum GSPGs.
 * Subsequently, GSPGs are merged together, until only a single graph remains.
 * These generators should allow several restrictions to the merging process,
 * like bounding the number of parallel edges and the maximum degree in the
 * graph. However, these bounds are only guidelines, and the implementing class
 * has to decide how strictly they are enforced. 
 *
 * @author Martin Groß
 * @param <N> the node class of the generated graph.
 * @param <E> the edge class of the generated graph.
 */
public abstract class MergingGSPGGenerator<N, E extends AbstractEdge<N>> implements Generator<N, E, GSPG<N, E>> {

    /**
     * The maximum degree of a node in the generated graph.
     */
    protected int maximumNodeDegree;
    /**
     * The maximum number of parallel edges between two nodes.
     */
    protected int maximumNumberOfParallelEdges;
    /**
     * The number of edges in the generated graph.
     */
    protected int numberOfEdges;
    /**
     * The frequency of a parallel composition.
     */
    protected double parallelFrequency;
    /**
     * The offset used together with the path threshold.
     */
    protected int pathOffset;
    /**
     * The size threshold upon which no further series compositions are
     * performed, if the number of edges is smaller equal than the number of
     * nodes - 1 + the <code>pathOffset</code>.
     */
    protected int pathThreshold;
    /**
     * The random number generator.
     */
    protected final RandomGenerator rng;
    /**
     * The frequency of a series composition.
     */
    protected double seriesFrequency;
    /**
     * The frequency of a tree composition.
     */
    protected double treeFrequency;

    /**
     * Creates a new graph generator using a MersenneTwister implementation.
     */
    public MergingGSPGGenerator() {
        this(new MersenneTwister());
    }

    /**
     * Creates a new graph generator that will use the given random number
     * generator.
     *
     * @param rng the random number generator.
     */
    public MergingGSPGGenerator(RandomGenerator rng) {
        this.rng = rng;
        maximumNodeDegree = 7;
        maximumNumberOfParallelEdges = 1;
        numberOfEdges = 100;
        parallelFrequency = 0.45;
        pathOffset = 1;
        pathThreshold = 3;
        seriesFrequency = 0.45;
        treeFrequency = 0.05;
    }

    @Override
    public abstract GSPG<N, E> generate(Supplier<N> nodeFactory, BiFunction<N, N, E> edgeFactory);

    /**
     * Returns the maximum degree of nodes in the generated graph.
     *
     * @return the maximum degree of nodes in the generated graph.
     */
    public int getMaximumNodeDegree() {
        return maximumNodeDegree;
    }

    /**
     * Returns the maximum number of parallel edges in the generated graph.
     *
     * @return the maximum number of parallel edges in the generated graph.
     */
    public int getMaximumNumberOfParallelEdges() {
        return maximumNumberOfParallelEdges;
    }

    /**
     * Returns the number of edges in the generated graph.
     *
     * @return the number of edges in the generated graph.
     */
    public int getNumberOfEdges() {
        return numberOfEdges;
    }

    /**
     * Returns the relative frequency of parallel compositions.
     *
     * @return the relative frequency of parallel compositions.
     */
    public double getParallelFrequency() {
        return parallelFrequency;
    }

    /**
     * Returns the offset used together with the path threshold.
     *
     * @return the offset used together with the path threshold.
     */
    public int getPathOffset() {
        return pathOffset;
    }

    /**
     * Returns the size threshold (in number of nodes of the graph) upon which
     * no further series compositions are performed, if the number of edges is
     * smaller equal than the number of nodes - 1 + the <code>pathOffset</code>.
     *
     * @return the size threshold in number of nodes.
     */
    public int getPathThreshold() {
        return pathThreshold;
    }

    /**
     * Returns the relative frequency of series compositions.
     *
     * @return the relative frequency of series compositions.
     */
    public double getSeriesFrequency() {
        return seriesFrequency;
    }

    /**
     * Returns the relative frequency of tree compositions.
     *
     * @return the relative frequency of tree compositions.
     */
    public double getTreeFrequency() {
        return treeFrequency;
    }

    /**
     * Sets the maximum node degree in generated graphs.
     *
     * @param maximumNodeDegree the maximum node degree in generated graphs.
     */
    public void setMaximumNodeDegree(int maximumNodeDegree) {
        this.maximumNodeDegree = maximumNodeDegree;
    }

    /**
     * Sets the maximum number of parallel edges in generated graphs.
     *
     * @param maximumNumberOfParallelEdges the maximum number of parallel edges
     * in generated graphs.
     */
    public void setMaximumNumberOfParallelEdges(int maximumNumberOfParallelEdges) {
        this.maximumNumberOfParallelEdges = maximumNumberOfParallelEdges;
    }

    /**
     * Sets the number of edges in generated graphs.
     *
     * @param numberOfEdges the number of edges in generated graphs.
     */
    public void setNumberOfEdges(int numberOfEdges) {
        this.numberOfEdges = numberOfEdges;
    }

    /**
     * Sets the relative frequency of parallel compositions.
     *
     * @param parallelFrequency the relative frequency of parallel compositions.
     */
    public void setParallelFrequency(double parallelFrequency) {
        this.parallelFrequency = parallelFrequency;
    }

    /**
     * Sets the offset used together with the path threshold.
     *
     * @param pathOffset the offset used together with the path threshold.
     */
    public void setPathOffset(int pathOffset) {
        this.pathOffset = pathOffset;
    }

    /**
     * Sets the size threshold (in number of nodes of the graph) upon which
     * no further series compositions are performed, if the number of edges is
     * smaller equal than the number of nodes - 1 + the <code>pathOffset</code>.
     *
     * @param pathThreshold the size threshold in number of nodes.
     */
    public void setPathThreshold(int pathThreshold) {
        this.pathThreshold = pathThreshold;
    }

    /**
     * Sets the relative frequency of series compositions.
     *
     * @param seriesFrequency the relative frequency of series compositions.
     */
    public void setSeriesFrequency(double seriesFrequency) {
        this.seriesFrequency = seriesFrequency;
    }

    /**
     * Sets the relative frequency of tree compositions.
     *
     * @param treeFrequency the relative frequency of tree compositions.
     */
    public void setTreeFrequency(double treeFrequency) {
        this.treeFrequency = treeFrequency;
    }

    /**
     * Tests whether a parallel composition of the given graphs is feasible
     * according to the parameters of this generator.
     *
     * @param g1 the first graph of the composition.
     * @param g2 the second graph of the composition.
     * @return whether the corresponding parallel composition is feasible.
     */
    protected boolean isValidParallelComposition(GSPG<N, E> g1, GSPG<N, E> g2) {
        N s1 = g1.getSource();
        N s2 = g2.getSource();
        N t1 = g1.getSink();
        N t2 = g2.getSink();
        int ds1 = g1.degree(s1);
        int ds2 = g2.degree(s2);
        int dt1 = g1.degree(t1);
        int dt2 = g2.degree(t2);
        return g1.getEdges(s1, t1).size() + g2.getEdges(s2, t2).size() <= maximumNumberOfParallelEdges
                && Math.max(ds1 + ds2, dt1 + dt2) <= maximumNodeDegree;
    }

    /**
     * Tests whether a series composition of the given graphs is feasible
     * according to the parameters of this generator.
     *
     * @param g1 the first graph of the composition.
     * @param g2 the second graph of the composition.
     * @return whether the corresponding series composition is feasible.
     */
    protected boolean isValidSeriesComposition(GSPG<N, E> g1, GSPG<N, E> g2) {
        int ds2 = g2.degree(g2.getSource());
        int dt1 = g1.degree(g1.getSink());
        return (g1.numberOfNodes() < pathThreshold || g1.numberOfEdges() >= g1.numberOfNodes() + pathOffset)
                && (g2.numberOfNodes() < pathThreshold || g2.numberOfEdges() >= g2.numberOfNodes() + pathOffset)
                && dt1 + ds2 <= maximumNodeDegree;
    }

    /**
     * Tests whether a tree composition of the given graphs is feasible
     * according to the parameters of this generator.
     *
     * @param g1 the first graph of the composition.
     * @param g2 the second graph of the composition.
     * @return whether the corresponding tree composition is feasible.
     */
    protected boolean isValidTreeComposition(GSPG<N, E> g1, GSPG<N, E> g2) {
        int ds1 = g1.degree(g1.getSource());
        int ds2 = g2.degree(g2.getSource());
        return ds1 + ds2 <= maximumNodeDegree;
    }

    /**
     * Randomly picks an index from the given array, using the numbers in the
     * array as frequency of the corresponding index. All entries must be
     * non-negative. If the array is empty or contains only zeroes, <code>-1
     * </code> is returned.
     *
     * @param frequencies the array of index-frequencies from which to draw.
     * @return an index from the array drawn according to the frequencies.
     */
    protected int weightedDraw(Double... frequencies) {
        if (frequencies.length == 0) {
            return -1;
        }
        double sum = 0;
        for (double f : frequencies) {
            if (f < 0) {
                throw new IllegalArgumentException("All entries must be non-negative.");
            }
            sum += f;
        }
        if (sum == 0.0) {
            return -1;
        }
        double r = rng.nextDouble() * sum;
        for (int i = 0; i < frequencies.length; i++) {
            if (r <= frequencies[i]) {
                return i;
            }
            r -= frequencies[i];
        }
        throw new AssertionError("This should be impossible.");
    }

    /**
     * A convenience method for accessing
     * <code>weightedDraw(Double... frequencies)</code> using lists.
     *
     * @param frequencies the list of index-frequencies from which to draw.
     * @return an index from the list drawn according to the frequencies.
     */
    protected int weightedDraw(List<Double> frequencies) {
        Double[] d = frequencies.toArray(new Double[0]);
        return weightedDraw(d);
    }
}
