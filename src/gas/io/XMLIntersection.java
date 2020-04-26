/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gas.io;

import gas.common.Intersection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import units.UnitsTools;
import units.qual.*;

/**
 *
 * @author gross
 */
public abstract class XMLIntersection extends XMLElementWithID implements Intersection {

    private final List<XMLConnection> connections;
    private double height;
    private double pressureMax;
    private double pressureMin;
    protected final Map<String, XMLProperty> properties;
    private double x;
    private double y;

    public XMLIntersection() {
        super();
        connections = new LinkedList<>();
        properties = new LinkedHashMap<>();
        height = 0 * UnitsTools.m;
        pressureMin = 1.01325 * UnitsTools.bar;
        pressureMax = 81.01325 * UnitsTools.bar;

        createProperty("height", height);
        createProperty("pressureMin", pressureMin);
        createProperty("pressureMax", pressureMax);
    }

    protected void createProperty(String name, double amount) {
        properties.put(name, new XMLProperty(name, "" + amount, "" + amount));
    }

    @Override
    public void initializeFrom(Node domXMLNode) {
        super.initializeFrom(domXMLNode);
        parseProperties();
    }

    public List<XMLConnection> getConnections() {
        return connections;
    }

    public double getHeight() {
        return height;
    }

    public @bar double getPressureMax() {
        return pressureMax;
    }

    public @bar double getPressureMin() {
        return pressureMin;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    protected Map<String, XMLProperty> getProperties() {
        return properties;
    }

    @Override
    protected boolean parseAttribute(String name, String value) {
        if (!super.parseAttribute(name, value)) {
            switch (name) {
                case "x":
                    x = Double.parseDouble(value);
                    return true;
                case "y":
                    y = Double.parseDouble(value);
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
        XMLProperty pHeight = getProperties().get("height");
        if (pHeight.getUnit().isEmpty() || pHeight.getUnit().equals("m/m")) {
            pHeight.setUnit("m");
        }
        height = pHeight.getAmount();
        pressureMax = getProperties().get("pressureMax").getAmount();
        pressureMin = getProperties().get("pressureMin").getAmount();
    }

    @Override
    protected void writeAttributes(Element element) {
        super.writeAttributes(element);
        element.setAttribute("x", Double.toString(x));
        element.setAttribute("y", Double.toString(y));
    }

    @Override
    protected void writeChildren(Document document, Element parent) {
        super.writeChildren(document, parent);
        for (XMLProperty property : properties.values()) {
            Element e;
            e = document.createElement(property.getName());
            e.setAttribute("value", property.getValue());
            switch (property.getUnit()) {
                case "":
                case "m/m":
                    break;
                case "C":
                case "°C":
                    e.setAttribute("unit", "Celsius");
                    break;
                case "MJ/m³":
                case "MJ/m^3":
                    e.setAttribute("unit", "MJ_per_m_cube");
                    break;
                case "kg/m³":
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
}
