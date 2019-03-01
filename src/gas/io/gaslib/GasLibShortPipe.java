/**
 * GasLibShortPipe.java
 *
 */

package gas.io.gaslib;

/**
 *
 * @author Martin Groß
 */
public class GasLibShortPipe extends GasLibConnection {

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("shortPipe");
    }
}
