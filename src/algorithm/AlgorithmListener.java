/**
 * AlgorithmListener.java
 *
 */
package algorithm;

import java.util.EventListener;

/**
 * The interface for classes that want to receive events from algorithms.
 * @author Martin Groß
 */
@FunctionalInterface
public interface AlgorithmListener extends EventListener {

    /**
     * This method is called when an event occurred in an algorithm that is
     * being listened to.
     * @param event the event which occurred.
     */
    void eventOccurred(AlgorithmEvent event);
}