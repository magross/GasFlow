package algorithm;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The basic framework class for algorithms. It allows to define input and
 * output of an algorithm by using generics and provides the framework to run an
 * algorithm in its own thread by implementing {@link Runnable}. Furthermore, it
 * keeps track of the current state of the algorithm and provides an generic
 * exception handling that can be extended by overwriting
 * {@code handleException}. It also keeps track of the algorithms runtime and
 * offers simple logging features that should be preferred to System.out
 * logging. Finally it offers the possibility to dispatch information about the
 * algorithm'Seconds state and progress to listeners, that can register and
 * unregister themselves for the algorithms events.
 *
 * @param <P> the type of the input the algorithm receives.
 * @param <S> the type of output the algorithm produces.
 * @author Martin Groß
 */
public abstract class Algorithm<P, S> {

    private Throwable cause;

    /* An enumeration type that specifies the current state of the algorithm. */
    public enum State {

        /**
         * If no problem has been set yet.
         */
        UNINITIALIZED,
        /**
         * If a problem is defined, but solving is not started.
         */
        WAITING,
        /**
         * If the algorithm is executing. Problem cannot be changed anymore.
         */
        SOLVING,
        /**
         * If an error during the execution occurred.
         */
        SOLVING_FAILED,
        /**
         * If the algorithm executed. Only state where {@link #getSolution() }
         * is allowed to be called.
         */
        SOLVED;
    }
    /**
     * The change in progress that has at least be done to fire an
     * {@link AlgorithmProgressEvent}.
     */
    private double accuracy = 0;
    /**
     * The set of listeners that receives events from this algorithm.
     */
    private Set<AlgorithmListener> algorithmListeners;
    /**
     * The description of the algorithm.
     */
    private String description = "";
    /**
     * Whether events are also logged to the console.
     */
    private boolean loggingEvents;
    /**
     * The name of the algorithm.
     */
    private String name;
    /**
     * The parameters of this algorithm.
     */
    //private ParameterSet parameterSet;
    /**
     * The instance of the problem.
     */
    private P problem;
    /**
     * The current progress of the algorithm. The progress begins with 0.0 and
     * ends with 1.0.
     */
    private double progress = -1;
    /**
     * The runtime of the algorithm in milliseconds.
     */
    private long runtime;
    /**
     * The solution to the problem instance, once available.
     */
    private S solution;
    /**
     * The point of time at which the execution of the algorithm started.
     */
    private long startTime;
    /**
     * The state of execution of the algorithm.
     */
    private State state = State.UNINITIALIZED;
    /**
     * Stores, if the algorithm execution is paused.
     */
    private boolean paused;
    /**
     * The logger object of this algorithm.
     */
    protected Logger LOG = Logger.getLogger(Algorithm.class.getName());

    /**
     * Creates a new algorithm with the name of the actual class, empty
     * description and parameter set.
     */
    public Algorithm() {
        description = "";
        name = getClass().getSimpleName().isEmpty() ? getClass().getSuperclass().getSimpleName() : getClass().getSimpleName();
        //parameterSet = new ParameterSet();
    }

    public Algorithm(String name) {
        description = "";
        this.name = name;
        //parameterSet = new ParameterSet();
    }

    /**
     * Adds the specified listener to the set of listeners receiving events from
     * this algorithm. If the specified listener is already part of this list,
     * nothing happens. When the problem is already solved, no more events will
     * be fired and an {@link IllegalStateException} is thrown.
     *
     * @param listener the listener to be added to the notification list.
     */
    public final void addAlgorithmListener(AlgorithmListener listener) {
        if (isProblemSolved()) {
            throw new IllegalStateException("The problem has already been solved. There"
                    + " will be no more events that could be listened to anymore.");
        } else {
            if (algorithmListeners == null) {
                algorithmListeners = new LinkedHashSet<>();
            }
            algorithmListeners.add(listener);
        }
    }

    /**
     * Adds all algorithm listeners of the specified algorithm to this
     * algorithm.
     *
     * @param algorithm the algorithm whose listeners are added.
     */
    public final void addAlgorithmListener(Algorithm<P, S> algorithm) {
        Set<AlgorithmListener> s = algorithm.algorithmListeners;
        for (AlgorithmListener listener : s) {
            addAlgorithmListener(listener);
        }
    }

    /**
     * Removes the specified listener from the set of listeners receiving events
     * from this algorithm.
     *
     * @param listener the listener to be removed from the notification list.
     */
    public final void removeAlgorithmListener(AlgorithmListener listener) {
        if (algorithmListeners != null) {
            algorithmListeners.remove(listener);
        }
    }

    /**
     * Dispatches the specified event to all registered listeners.
     *
     * @param event the event to be dispatched to the listeners.
     */
    protected final void fireEvent(AlgorithmEvent event) {
        if (algorithmListeners != null) {
            for (AlgorithmListener listener : algorithmListeners) {
                listener.eventOccurred(event);
            }
        }
    }

    /**
     * Dispatches an algorithm progress event with the specified message and
     * current progress value to all listeners.
     *
     * @param message the message to be dispatched.
     */
    protected final void fireEvent(String message) {
        //fireEvent(new AlgorithmDetailedProgressEvent(this, progress, message));
    }

    /**
     * Dispatches an algorithm progress event with the specified message and
     * current progress value to all listeners. The method is a shortcut for
     * {@code fireEvent(String.format(formatStr, params))}.
     *
     * @param formatStr the format string part of the message to be dispatched.
     * @param params the parameters used by the format string.
     */
    protected final void fireEvent(String formatStr, Object... params) {
        fireEvent(String.format(formatStr, params));
    }

    /**
     * Updates the progress value to broadcasts the new value to all listeners.
     *
     * @param progress the new progress value.
     * @throws IllegalArgumentException if the progress value is less than the
     * previous one.
     */
    protected final void fireProgressEvent(double progress) {
        if (checkProgress(progress)) {
            this.progress = progress;
            //fireEvent(new AlgorithmProgressEvent(this, progress));
        }
    }

    /**
     * Updates the progress value to broadcasts the new value together with a
     * message to all listeners.
     *
     * @param progress the current progress value.
     * @param message a message describing the current task and progress of the
     * algorithm.
     * @throws IllegalArgumentException if the progress value is less than the
     * previous one.
     */
    protected final void fireProgressEvent(double progress, String message) {
        if (checkProgress(progress)) {
            this.progress = progress;
            //fireEvent(new AlgorithmDetailedProgressEvent(this, progress, message));
        }
    }

    /**
     * Checks if the progress is valid, i.e. is larger than the old progress and
     * it is larger than the {@link #accuracy}.
     *
     * @param progress the new progress
     * @return {@literal true} if an event for the given progress should be
     * fired, {
     * @false otherwise}
     */
    private boolean checkProgress(double progress) {
        if (progress < this.progress) {
            throw new IllegalArgumentException("The progress values must be monotonically increasing.");
        }
        return progress - this.progress >= accuracy;
    }

    /**
     * Returns a description of the algorithm that can be displayed to a human.
     * This should describe what the algorithm expects and what it produces, as
     * well as providing information on interesting properties like runtime,
     * etc.
     *
     * @return the description of the algorithm.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the algorithm.
     *
     * @param description the description of the algorithm.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the name of the algorithm currently running. This name is
     * intended for a human user.
     *
     * @return the name of the algorithm.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the algorithm. The name should be aimed to be read by a
     * human.
     *
     * @param name the name of the algorithm.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the parameter set of the algorithm, which stores all parameters
     * that are exposed to the UI.
     *
     * @return the parameter set of the algorithm.
     */
    //public ParameterSet getParameterSet() {
    //    return parameterSet;
    //}

    /**
     * Sets the parameter set for this algorithm, which stores parameters in a
     * way accessible to modification by the UI.
     *
     * @param parameterSet the new parameter set object.
     */
    //public void setParameterSet(ParameterSet parameterSet) {
    //    this.parameterSet = parameterSet;
    //}

    /**
     * Returns the instance of the problem that is to be solved.
     *
     * @return the instance of the problem that is to be solved.
     */
    public final P getProblem() {
        return problem;
    }

    /**
     * Specifies the instance of the problem this algorithm is going to solve.
     *
     * @param problem the instance of the problem that is to be solved.
     * @throws IllegalStateException if the algorithm is running
     */
    public final void setProblem(P problem) {
        if (state == State.SOLVING) {
            throw new IllegalStateException("The algorithm is currently "
                    + "running! Changing the underlying instance could lead to "
                    + "undefined behaviour!");
        }
        if (this.problem != problem) {
            this.problem = problem;
            runtime = 0;
            solution = null;
            startTime = 0;
            state = State.WAITING;
        }
    }

    /**
     * Returns the current accuracy in progress events. The accuracy equals the
     * change in progress that has to be done until an event is actually fired.
     *
     * @return	the progress accuracy
     */
    public double getAccuracy() {
        return accuracy;
    }

    /**
     * Sets a new progress accuracy. The accuracy describes the value by that
     * the progress has to be changed until an {@link AlgorithmProgressEvent} is
     * fired.
     *
     * @param accuracy the new accuracy value. must be in the interval [0,1]
     * @throws IllegalArgumentException if accuracy is not within the allowed
     * range
     */
    public void setAccuracy(double accuracy) {
        if (accuracy < 0 || accuracy > 1) {
            throw new IllegalArgumentException("Invalid value for accuracy: "
                    + accuracy);
        }
        this.accuracy = accuracy;
    }

    /**
     * Determines the accuracy in such a way that at most most
     * {@code possibleChanges} many events are fired.
     *
     * @param possibleChanges the maximal number of progress events
     */
    public void setAccuracy(int possibleChanges) {
        setAccuracy(1. / possibleChanges);
    }

    /**
     * Returns the time between the start of the algorithm and its termination
     * in milliseconds.
     *
     * @return the runtime of the algorithm in milliseconds.
     * @throws IllegalStateException if the algorithm has not terminated yet.
     */
    public final long getRuntime() {
        if (state == State.SOLVED || state == State.SOLVING_FAILED) {
            return runtime;
        }
        throw new IllegalStateException("The algorithm has not terminated yet."
                + " Please call run() first and wait for its termination.");
    }

    /**
     * Returns the runtime of the algorithm as a string formatted with regard to
     * human readability. The formatting is done according to
     * {@link Formatter#formatUnit(double, org.zetool.common.util.units.UnitScale, int)}.
     *
     * @return the runtime of the algorithm formatted as a string.
     * @throws IllegalStateException if the algorithm has not terminated yet.
     */
    public final String getRuntimeAsString() {
        if (state == State.SOLVED || state == State.SOLVING_FAILED) {
            return "" + runtime;
        }
        throw new IllegalStateException("The algorithm has not terminated yet. "
                + "Please call run() first and wait for its termination.");
    }

    /**
     * Returns the solution computed by the algorithm.
     *
     * @return the solution to the algorithm.
     * @throws IllegalStateException if the problem has not been solved yet.
     */
    public final S getSolution() {
        if (isProblemSolved()) {
            return solution;
        }
        throw new IllegalStateException("The problem has not been solved yet. "
                + "Please call run() first and wait for its termination.");
    }

    /**
     * Returns the start time of the algorithm. The start time is measured in
     * the number of milliseconds elapsed since midnight, January 1, 1970 UTC.
     *
     * @return the start time of the algorithm.
     * @throws IllegalStateException if the execution of the algorithm has not
     * yet begun.
     */
    public final long getStartTime() {
        if (state != State.WAITING) {
            return startTime;
        }
        throw new IllegalStateException("The execution of the algorithm has "
                + "not started yet. Please call run() first.");
    }

    /**
     * Returns the current state of the algorithm.
     *
     * @return the current state of the algorithm.
     */
    public final State getState() {
        return state;
    }

    /**
     * Returns whether LOG messages of this algorithm are written to System.out
     * or not.
     *
     * @return {@code true}, if LOG messages are written to System.out,
     * {@code false} if otherwise.
     */
    public final Level getLogLevel() {
        return LOG.getLevel();
    }

    /**
     * Sets the level from that messages are logged by the {@link Logger} of
     * this algorithm. By default, messages starting from
     * {@link java.util.logging.Level#CONFIG} are given out using
     * {@link System#out} and warnings and errors are sent to
     * {@link System#err}.
     *
     * @param level the LOG level that is used by the algorithm
     */
    public final void setLogLevel(Level level) {
        LOG.setLevel(level);
    }

    /**
     * Returns the logger currently used by the {@link Algorithm}.
     *
     * @return the logger currently used by the {@link Algorithm}
     */
    public Logger getLogger() {
        return LOG;
    }

    /**
     * Sets the logger to a default logger using the class name as name.
     */
    public void setLogger() {
        this.LOG = Logger.getLogger(this.getClass().getName());
    }

    /**
     * Sets a specific logger for the {@link Algorithm}.
     *
     * @param logger the logger
     */
    public void setLogger(Logger logger) {
        this.LOG = logger;
    }

    /**
     * Returns whether events are also treated as LOG messages or not.
     *
     * @return {@code true}, if events are also treated as LOG messages,
     * {@code false} if otherwise.
     */
    public final boolean isLoggingEvents() {
        return loggingEvents;
    }

    /**
     * Sets whether events are also treated as LOG messages.
     *
     * @param loggingEvents whether events are also treated as LOG messages.
     */
    public final void setLoggingEvents(boolean loggingEvents) {
        this.loggingEvents = loggingEvents;
        if (loggingEvents) {
            addAlgorithmListener(new EventLogger());
        } else { // remove all existing logger
            List<AlgorithmListener> logger = new LinkedList<>();
            for (AlgorithmListener listener : algorithmListeners) {
                if (listener instanceof Algorithm.EventLogger) {
                    logger.add(listener);
                }
            }
            algorithmListeners.removeAll(logger);
        }
    }

    /**
     * Returns whether a problem instance has been specified for the algorithm.
     * This is the prerequisite for beginning the execution of the algorithm.
     *
     * @return {@code true} if a problem instance has been specified,
     * {@code false} otherwise.
     */
    public final boolean isProblemInitialized() {
        return state != State.UNINITIALIZED;
    }

    /**
     * Returns whether this algorithm has successfully run and solved the
     * instance of the problem given to it. If this is {@code true}, then the
     * solution to the instance of the problem can be obtained by {@code
     * getSolution}.
     *
     * @return {@code true} if the algorithm'Seconds instance of the problem has
     * been solved successfully and {@code false} otherwise.
     */
    public final boolean isProblemSolved() {
        return state == State.SOLVED;
    }

    /**
     * Returns whether the algorithm is currently begin executed.
     *
     * @return {@code true} if this algorithm is currently running and
     * {@code false} otherwise.
     */
    public final boolean isRunning() {
        return state == State.SOLVING;
    }

    /**
     * Writes the specified message to System.out, if the LOG level of the
     * current LOG level of the logger is below
     * {@link java.util.logging.Level#INFO}. Does nothing otherwise.
     *
     * @param message the message that it to be logged.
     */
    protected final void log(String message) {
        LOG.info(message);
    }

    /**
     * Formats the specified message and parameters using String.format() and
     * logs it.
     *
     * @param message the format string of the message.
     * @param params the parameters for formatting the message.
     */
    protected final void log(String message, Object... params) {
        log(String.format(message, params));
    }

    public final void run() {
        runAlgorithm();
    }

    /**
     * A framework method for executing the algorithm and returns the result.
     * <p>
     * Calling the method solves the problem and returns the solution. The
     * solution is stored and can be accessed again using {@link #getSolution()
     * }.</p>
     *
     * @return the solution to the algorithm.
     */
    public final S call() {
        runAlgorithm();
        return getSolution();
    }

    /**
     * <p>
     * The framework method for executing the algorithm. It is responsible for
     * recording the runtime of the actual algorithm in addition to handling
     * exceptions and recording the solution to the problem instance.</p>
     * <p>
     * Calling the method solves the problem, afterwards it can be accessed
     * using {@link #getSolution() }.</p>
     *
     * @throws IllegalStateException if the instance of the problem has not been
     * specified yet.
     */
    public final void runAlgorithm() {
        if (!isProblemInitialized()) {
            throw new IllegalStateException("The instance of the problem has been specified yet. Please call setProblem() first.");
        } else {
            try {
                startTime = System.currentTimeMillis();
                state = State.SOLVING;
                progress = 0;
                fireEvent(new AlgorithmStartedEvent(this));
                solution = runAlgorithm(problem);
                state = State.SOLVED;
            } catch (AssertionError e) {
                this.cause = e;
                state = State.SOLVING_FAILED;
                LOG.log(Level.SEVERE, "An assertion error has occured: ", e);
            } catch (RuntimeException ex) {
                this.cause = ex;
                state = State.SOLVING_FAILED;
                handleException(ex);
            } catch (OutOfMemoryError ex) {
                this.cause = ex;
                state = State.SOLVING_FAILED;
                LOG.log(Level.SEVERE, "No more memory. Execution stopped: ", ex);
            } finally {
                runtime = System.currentTimeMillis() - startTime;
                fireEvent(new AlgorithmTerminatedEvent(this));
            }
        }
    }

    /**
     * The default exception handling method. It logs that the algorithm failed
     * to solve the instance using the {@link java.util.logging.Level#SEVERE}
     * level and re-throws the runtime exception that caused the premature
     * termination of the algorithm. Subclasses can override this method to
     * change this behavior.
     *
     * @param exception the exception that caused the termination of the
     * algorithm.
     */
    protected void handleException(RuntimeException exception) {
        LOG.log(java.util.logging.Level.SEVERE, "Exception in Algorithm " + this.name, exception);
        this.cause = exception;
    }

    /**
     * Returns the {@link Error} or {
     *
     * @RuntimeException} that occured during the execution of the algorithm.
     * @return the exception that occured while executing the algorithm
     */
    public Throwable getCause() {
        return cause;
    }

    /**
     * The abstract method that needs to be implemented by sub-classes in order
     * to implement the actual algorithm.
     *
     * @param problem an instance of the problem.
     * @return a solution to the specified problem.
     */
    protected abstract S runAlgorithm(P problem);

    /**
     * A private listener class for receiving events and logging them.
     */
    private class EventLogger implements AlgorithmListener {

        /**
         * This method is called when an event occurred in an algorithm that is
         * being listened to.
         *
         * @param event the event which occurred.
         */
        @Override
        public void eventOccurred(AlgorithmEvent event) {
            String message = "";
            /*
            if (event instanceof AlgorithmStartedEvent) {
                message = String.format("%1$s: %2$s gestartet...",
                        event.getFormattedEventTime(), Algorithm.this.getClass().getSimpleName());
            } else if (event instanceof AlgorithmDetailedProgressEvent) {
                message = String.format("%1$s: %2$s running... %3$s%",
                        event.getFormattedEventTime(), Algorithm.this.getClass().getSimpleName(),
                        ((AlgorithmProgressEvent) event).getProgressAsInteger());
            } else if (event instanceof AlgorithmProgressEvent) {
                message = String.format("%1$s: %2$s running... %3$s%",
                        event.getFormattedEventTime(), Algorithm.this.getClass().getSimpleName(),
                        ((AlgorithmProgressEvent) event).getProgressAsInteger());
            } else if (event instanceof AlgorithmTerminatedEvent) {
                message = String.format("%1$s: %2$s beendet nach %3$s.",
                        event.getFormattedEventTime(), Algorithm.this.getClass().getSimpleName(), getRuntimeAsString());
            }
            if (!message.isEmpty()) {
                log(message);
            }*/
        }
    }

    public final boolean isPaused() {
        return paused;
    }

    public final void setPaused(boolean paused) {
        this.paused = paused;
    }

}
