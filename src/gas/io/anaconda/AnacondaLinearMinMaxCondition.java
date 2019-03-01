/**
 * AnacondaLinearMinMaxCondition.java
 *
 */

package gas.io.anaconda;

import gas.io.XMLElementWithID;
import java.util.LinkedList;
import java.util.List;
import org.w3c.dom.Node;

/**
 *
 * @author Martin Groß
 */
public class AnacondaLinearMinMaxCondition extends XMLElementWithID {

    public enum Type {
        ALWAYS_ABOVE("alwaysAbove"),
        ALWAYS_BELOW("alwaysBelow"),
        MAX_BETWEEN("maxBetween"),
        MIN_BETWEEN("minBetween");

        private String name;

        private Type(String string) {
            name = string;
        }
    }

    private final List<Double> coefficients;
    private final List<AnacondaTimeValueData> dataPoints;

    public AnacondaLinearMinMaxCondition() {
        coefficients = new LinkedList<>();
        dataPoints = new LinkedList<>();
    }

    @Override
    protected boolean parseAttribute(String name, String value) {
        if (!super.parseAttribute(name, value)) {
            switch (name) {
                case "type":
                    return true;
                case "eps":
                    return true;
                default:
                    return false;
            }
        } else {
            return true;
        }
    }


    @Override
    protected void parseChild(Node domXMLNode) {
        switch (domXMLNode.getNodeName()) {
            case "lin":
                coefficients.add(Double.parseDouble(domXMLNode.getTextContent()));
                return;
            case "data":
                AnacondaTimeValueData data = new AnacondaTimeValueData();
                data.initializeFrom(domXMLNode);
                dataPoints.add(data);
                return;
            default:
                throw new AssertionError("Unknown node type: " + domXMLNode.getNodeName());
        }
    }

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("linearMinMaxCondition");
    }
}
