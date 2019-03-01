/*
 * Generator.java
 * 
 * 
 */
package ds.graph.generator;

import ds.graph.AbstractEdge;
import ds.graph.Graph;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * An interface for classes that generate graphs.
 * @author Martin Groß
 * @param <N> the class for nodes in the generated graph.
 * @param <E> the class for edges in the generated graph.
 * @param <G> the class of graph that is generated.
 */
public interface Generator<N,E extends AbstractEdge<N>,G extends Graph<N,E>> {

    /**
     * Generates a graph using the supplied node and edge factories.
     * @param nodeFactory the factory for nodes.
     * @param edgeFactory the factory for edges.
     * @return the generated graph.
     */
    public G generate(Supplier<N> nodeFactory, BiFunction<N,N,E> edgeFactory);

    /**
     *
     */
}
