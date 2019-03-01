/**
 * AnacondaFPData.java
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
public class AnacondaFlowPressureData extends XMLElement {

    private double coordinate;
    private double flow;
    private double pressure;
    private final List<Double> values;

    public AnacondaFlowPressureData() {
        values = new LinkedList<>();
    }

    public double getCoordinate() {
        return coordinate;
    }

    public double getFlow() {
        return flow;
    }

    public double getPressure() {
        return pressure;
    }

    @Override
    public void initializeFrom(Node domXMLNode) {
        super.initializeFrom(domXMLNode);
        parseProperties();
    }

    @Override
    protected void parseChild(Node domXMLNode) {
        switch (domXMLNode.getNodeName()) {
            case "x":
                coordinate = Double.parseDouble(domXMLNode.getTextContent());
                return;
            case "value":
                values.add(Double.parseDouble(domXMLNode.getTextContent()));
                return;
            default:
                throw new AssertionError("Unexpected node name: " + domXMLNode.getNodeName());
        }
    }

    protected void parseProperties() {
        pressure = values.get(0);
        flow = values.get(1);
    }

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("data");
    }
}
