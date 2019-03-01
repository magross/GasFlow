/**
 * AnacondaSource.java
 *
 */
package gas.io.anaconda;

/**
 *
 * @author Martin Groß
 */
public class AnacondaSource extends AnacondaTerminalNode {

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("source");
    }    
}