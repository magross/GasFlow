/**
 * AnacondaLinearBoundaryCondition.java
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
public class AnacondaLinearBoundaryCondition extends XMLElementWithID {

    private final List<Double> coefficients;
    private final List<AnacondaTimeValueData> dataPoints;

    public AnacondaLinearBoundaryCondition() {
        coefficients = new LinkedList<>();
        dataPoints = new LinkedList<>();
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
        return name.equals("linearBoundaryCondition");
    }
}
