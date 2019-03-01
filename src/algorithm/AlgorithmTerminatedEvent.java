package algorithm;

/**
 * A special algorithm event that occurs when the algorithm terminates (either
 * by completing successfully or by an uncaught runtime exception).
 *
 * @author Martin Groß
 */
public class AlgorithmTerminatedEvent extends AlgorithmEvent {

    private long runtime = 0;

    /**
     * Creates an {@code AlgorithmTerminatedEvent} for the specified algorithm.
     *
     * @param algorithm the algorithm that has terminated.
     */
    public AlgorithmTerminatedEvent(Algorithm<?, ?> algorithm) {
        super(algorithm, algorithm.getRuntime());
        runtime = algorithm.getRuntime();
    }

    /**
     * Creates an {@code AlgorithmTerminatedEvent} for the specified algorithm
     * and saves the runtime of the algorithm.
     *
     * @param algorithm the algorithm that has terminated.
     * @param runtime the running time of the algorithm
     */
    public AlgorithmTerminatedEvent(Algorithm<?, ?> algorithm, long runtime) {
        this(algorithm);
        this.runtime = runtime;
    }

    public long getRuntime() {
        return runtime;
    }
}
