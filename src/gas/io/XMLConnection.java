/*
 * GasLibConnection.java
 * 
 * 
 */
package gas.io;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.measure.quantity.VolumetricFlowRate;
import static javax.measure.unit.NonSI.HOUR;
import static javax.measure.unit.SI.METER;
import javax.measure.unit.Unit;
import org.jscience.physics.amount.Amount;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author Martin Groß
 */
public abstract class XMLConnection extends XMLElementWithID {

    protected  Amount<VolumetricFlowRate> flowMax;
    protected  Amount<VolumetricFlowRate> flowMin;
    protected  XMLIntersection from;
    protected  volatile String fromId;
    protected  final Map<String, XMLProperty> properties;
    protected  XMLIntersection to;
    protected  volatile String toId;

    public XMLConnection() {
        properties = new LinkedHashMap<>();
        flowMax = Amount.valueOf(10000000, (Unit<VolumetricFlowRate>) METER.pow(3).divide(HOUR));
        flowMin = Amount.valueOf(-10000000, (Unit<VolumetricFlowRate>) METER.pow(3).divide(HOUR));
    }

    public void connectToIntersections(Map<String, XMLIntersection> intersections) {
        if (intersections.containsKey(fromId) && intersections.containsKey(toId)) {
            from = intersections.get(fromId);
            from.getConnections().add(this);
            to = intersections.get(toId);
            to.getConnections().add(this);
        } else if (!intersections.containsKey(fromId)) {
            throw new AssertionError("Unknown intersection ID: " + fromId);
        } else if (!intersections.containsKey(toId)) {
            throw new AssertionError("Unknown intersection ID: " + toId);
        }
    }

    public Amount<VolumetricFlowRate> getFlowMax() {
        return flowMax;
    }

    public Amount<VolumetricFlowRate> getFlowMin() {
        return flowMin;
    }

    public ConnectionType getType() {
        return ConnectionType.getType(this);
    }

    public XMLIntersection getFrom() {
        if (from == null) {
            throw new AssertionError("From is not initialized - has connectToIntersections been called previously?");
        }
        return from;
    }

    public XMLIntersection getTo() {
        if (to == null) {
            throw new AssertionError("To is not initialized - has connectToIntersections been called previously?");
        }
        return to;
    }

    protected Map<String, XMLProperty> getProperties() {
        return properties;
    }

    @Override
    public void initializeFrom(Node domXMLNode) {
        super.initializeFrom(domXMLNode);
        parseProperties();
    }

    @Override
    protected boolean parseAttribute(String name, String value) {
        if (!super.parseAttribute(name, value)) {
            switch (name) {
                case "from":
                    fromId = value;
                    return true;
                case "to":
                    toId = value;
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
        XMLProperty property = new XMLProperty();
        property.initializeFrom(domXMLNode);
        properties.put(property.getName(), property);
    }

    protected void parseProperties() {
        XMLProperty fMax = properties.get("flowMax");
        if (fMax.getUnit().equals("1000m_cube_per_hour") || fMax.getUnit().equals("m/m")) {
            flowMax = Amount.valueOf(Double.parseDouble(fMax.getValue()) * 1000, (Unit<VolumetricFlowRate>) Unit.valueOf("m^3 / h"));
        } else {
            throw new AssertionError("Volumetric flow rate unit unknown: " + fMax.getUnit());
        }
        XMLProperty fMin = properties.get("flowMin");
        if (fMin.getUnit().equals("1000m_cube_per_hour") || fMin.getUnit().equals("m/m")) {
            flowMin = Amount.valueOf(Double.parseDouble(fMax.getValue()) * 1000, (Unit<VolumetricFlowRate>) Unit.valueOf("m^3 / h"));
        } else {
            throw new AssertionError("Volumetric flow rate unit unknown: " + fMin.getUnit());
        }
    }

    @Override
    protected void writeAttributes(Element element) {
        super.writeAttributes(element);
        element.setAttribute("from", fromId);
        element.setAttribute("to", toId);
    }

    @Override
    protected void writeChildren(Document document, Element parent) {
        super.writeChildren(document, parent);
        for (XMLProperty property : properties.values()) {
            Element e;
            e = document.createElement(property.getName());
            e.setAttribute("value", property.getValue());
            switch (property.getUnit()) {
                case "m/m":
                    break;
                case "°C":
                    e.setAttribute("unit", "Celsius");
                    break;
                case "MJ/m^3":
                    e.setAttribute("unit", "MJ_per_m_cube");
                    break;
                case "kg/m^3":
                    e.setAttribute("unit", "kg_per_m_cube");
                    break;
                case "g/mol":
                    e.setAttribute("unit", "kg_per_kmol");
                    break;
                case "W/m^2/K":
                    e.setAttribute("unit", "W_per_m_square_per_K");
                    break;
                default:
                    e.setAttribute("unit", property.getUnit());
            }
            parent.appendChild(e);
        }
    }

    @Override
    public  void writeTo(Document document, Element parent) {
        writeAttributes(parent);
        writeChildren(document, parent);
    }
}
