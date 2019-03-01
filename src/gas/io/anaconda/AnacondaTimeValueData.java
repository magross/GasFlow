/**
 * AnacondaTimeValueData.java
 *
 */

package gas.io.anaconda;

import gas.io.XMLElement;
import org.w3c.dom.Node;

/**
 *
 * @author Martin Groß
 */
public class AnacondaTimeValueData  extends XMLElement {

    private double time;
    private double value;

    public double getTime() {
        return time;
    }

    public double getValue() {
        return value;
    }

    @Override
    protected void parseChild(Node domXMLNode) {
        switch (domXMLNode.getNodeName()) {
            case "time":
                time = Double.parseDouble(domXMLNode.getTextContent());
                return;
            case "value":
                value = Double.parseDouble(domXMLNode.getTextContent());
                return;
            default:
                throw new AssertionError("Unexpected node name: " + domXMLNode.getNodeName());
        }
    }

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("data");
    }
}
