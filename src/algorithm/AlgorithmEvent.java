/*
 * AlgorithmEvent.java
 *
 */
package algorithm;


/**
 * The abstract base class for algorithm events. It records the algorithm the
 * event occurred in and the time at which the event occured. The time should be
 * given in milliseconds elapsed since midnight, January 1, 1970 UTC.
 *
 * @author Martin Groß
 */
public abstract class AlgorithmEvent {

  /**
   * The algorithm the event occurred in.
   */
  private Algorithm<?, ?> algorithm;
  /**
   * The time at which the event occurred in milliseconds elapsed since
   * midnight, January 1, 1970 UTC.
   */
  private long eventTime;

  /**
   * A protected constructor for subclasses to initialize the basic fields of
   * this class.
   *
   * @param algorithm the algorithm the event occurred in.
   * @param eventTime the time at which the event occurred in milliseconds
   * elapsed since midnight, January 1, 1970 UTC.
   */
  protected AlgorithmEvent( Algorithm<?, ?> algorithm, long eventTime ) {
    this.algorithm = algorithm;
    this.eventTime = eventTime;
  }

  /**
   * Returns the algorithm the event occurred in.
   *
   * @return the algorithm the event occurred in.
   */
  public final Algorithm<?, ?> getAlgorithm() {
    return algorithm;
  }

  /**
   * Returns the time at which the event occurred in milliseconds elapsed since
   * midnight, January 1, 1970 UTC.
   *
   * @return the time at which the event occurred.
   */
  public final long getEventTime() {
    return eventTime;
  }

  /**
   * Returns the event time formatted as a string by using the class
   * java.util.DateFormat.
   *
   * @return the event time formatted as a string.
   */
  public final String getFormattedEventTime() {
    return Long.toString(eventTime);
  }
}