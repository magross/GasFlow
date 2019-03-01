/**
 * AnacondaTimeValueData.java
 *
 */

package gas.io.anaconda;

import gas.io.XMLElement;
import java.util.LinkedList;
import java.util.List;
import org.w3c.dom.Node;

/**
 *
 * @author Martin Groß
 */
public class AnacondaTimeValuesData extends XMLElement {

    private double time;
    private List<Double> values;

    public AnacondaTimeValuesData() {
        values = new LinkedList<>();
    }

    public double getTime() {
        return time;
    }

    public List<Double> getValues() {
        return values;
    }

    @Override
    protected void parseChild(Node domXMLNode) {
        switch (domXMLNode.getNodeName()) {
            case "t":
                time = Double.parseDouble(domXMLNode.getTextContent());
                return;
            case "value":
                values.add(Double.parseDouble(domXMLNode.getTextContent()));
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
