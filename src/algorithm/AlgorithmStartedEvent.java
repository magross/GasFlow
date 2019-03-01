/*
 * AlgorithmStartedEvent.java
 *
 */
package algorithm;

/**
 * A special algorithm event that occurs when the execution of an algorithm
 * begins.
 *
 * @author Martin Groß
 */
public class AlgorithmStartedEvent extends AlgorithmEvent {

    /**
     * Creates a {@code AlgorithmStartedEvent} for the specified algorithm.
     * @param algorithm the algorithm whose execution started.
     */
    public AlgorithmStartedEvent(Algorithm algorithm) {
        super(algorithm, algorithm.getStartTime());
    }
}
