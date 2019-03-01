/**
 * Sink.java
 *
 */

package gas.io.anaconda;

/**
 *
 * @author Martin Groß
 */
public class AnacondaSink extends AnacondaTerminalNode {

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("sink");
    }
}
