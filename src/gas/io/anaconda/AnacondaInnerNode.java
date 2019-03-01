/**
 * AnacondaInnerNode.java
 *
 */

package gas.io.anaconda;

/**
 *
 * @author Martin Groß
 */
public class AnacondaInnerNode extends AnacondaIntersection {

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("innode");
    }
}
