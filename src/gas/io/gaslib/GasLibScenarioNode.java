/*
 * GasLibScenarioNode.java
 * 
 * 
 */
package gas.io.gaslib;

import gas.io.XMLElementWithID;
import gas.io.gaslib.GasLibScenarioProperty.Bound;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.measure.quantity.MassFlowRate;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.VolumetricFlowRate;
import org.jscience.physics.amount.Amount;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author Martin Groß
 */
public class GasLibScenarioNode extends XMLElementWithID {
    
    public static final boolean DEBUG = false;



    public enum Type {
        ENTRY, EXIT;
    }

    private final Map<String, GasLibScenarioProperty> properties;
    private Type type;

    public GasLibScenarioNode() {
        this.properties = new LinkedHashMap<>();
    }

    public Map<String, GasLibScenarioProperty> getProperties() {
        return properties;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("node");
    }

    @Override
    protected boolean parseAttribute(String name, String value) {
        if (!super.parseAttribute(name, value)) {
            switch (name) {
                case "type":
                    type = Type.valueOf(value.toUpperCase());
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
        GasLibScenarioProperty property = new GasLibScenarioProperty();
        property.initializeFrom(domXMLNode);
        if (property.getUnit().equals("1000m_cube_per_hour") || property.getUnit().equals("m/m")) {
            property.setUnit("m^3/h");
            property.setValue(Double.parseDouble(property.getValue()) * 1000.0 + "");
            if (DEBUG && Double.parseDouble(property.getValue()) != 0.0) {
                System.out.println("GLSN.parseChild: " + property.getValue() + " " + Double.parseDouble(property.getValue()));
            }            
        }
        properties.put(property.getName() + "_" + property.getBoundType(), property);
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean hasExactFlowBound() {
        return properties.get("flow_BOTH") != null;
    }

    public Amount<VolumetricFlowRate> getFlowRateBound() {
        if (properties.get("flow_BOTH") != null) {
            return properties.get("flow_BOTH").getAmount();
        } else if (properties.get("flow_LOWER") != null && properties.get("flow_UPPER") != null) {
            if (properties.get("flow_LOWER").getAmount().approximates(properties.get("flow_UPPER").getAmount())) {
                return properties.get("flow_LOWER").getAmount();
            }
        }
        throw new AssertionError("");
    }

    public Amount<VolumetricFlowRate> getLowerFlowRateBound() {
        if (properties.get("flow_LOWER") != null) {
            return properties.get("flow_LOWER").getAmount();
        } else if (properties.get("flow_BOTH") != null) {
            return properties.get("flow_BOTH").getAmount();
        } else {
            return (Amount<VolumetricFlowRate>) Amount.valueOf("0 m^3/h");
        }

    }

    public Amount<VolumetricFlowRate> getUpperFlowRateBound() {
        if (properties.get("flow_UPPER") != null) {
            return properties.get("flow_UPPER").getAmount();
        } else if (properties.get("flow_BOTH") != null) {
            return properties.get("flow_BOTH").getAmount();
        } else {
            return (Amount<VolumetricFlowRate>) Amount.valueOf("0 m^3/h");
        }
    }

    public void setFlowBound(Amount<MassFlowRate> amount) {
        if (properties.get("flow_BOTH") == null) {
            GasLibScenarioProperty property = new GasLibScenarioProperty("flow_BOTH", "m^3/h", "0", Bound.BOTH);
            properties.put("flow_BOTH", property);
        }
        properties.get("flow_BOTH").setValue("" + amount.doubleValue(amount.getUnit()));
    }

    public void setLowerFlowBound(Amount amount) {
        if (properties.get("flow_LOWER") == null) {
            GasLibScenarioProperty property = new GasLibScenarioProperty("flow_LOWER", "m^3/h", "0", Bound.LOWER);
            properties.put("flow_LOWER", property);
        }
        properties.get("flow_LOWER").setValue("" + amount.doubleValue(amount.getUnit()));
    }

    public void setUpperFlowBound(Amount amount) {
        if (properties.get("flow_UPPER") == null) {
            GasLibScenarioProperty property = new GasLibScenarioProperty("flow_UPPER", "m^3/h", "0", Bound.UPPER);
            properties.put("flow_UPPER", property);
        }
        properties.get("flow_UPPER").setValue("" + amount.doubleValue(amount.getUnit()));
    }
    
    public void setUpperPressureBound(Amount amount) {
        if (properties.get("pressure_UPPER") == null) {
            GasLibScenarioProperty property = new GasLibScenarioProperty("pressure_UPPER", "bar", "0", Bound.UPPER);
            properties.put("pressure_UPPER", property);
        }
        properties.get("pressure_UPPER").setValue("" + amount.doubleValue(amount.getUnit()));
    }

    public void setLowerPressureBound(Amount amount) {
        if (properties.get("pressure_LOWER") == null) {
            GasLibScenarioProperty property = new GasLibScenarioProperty("pressure_LOWER", "bar", "0", Bound.LOWER);
            properties.put("pressure_LOWER", property);
        }
        properties.get("pressure_LOWER").setValue("" + amount.doubleValue(amount.getUnit()));
    }    
    
    public boolean hasLowerPressureBound() {
        return properties.get("pressure_LOWER") != null;
    }

    public boolean hasUpperPressureBound() {
        return properties.get("pressure_UPPER") != null;
    }
    
    public Amount<Pressure> getLowerPressureBound() {
        return properties.get("pressure_LOWER").getAmount();
    }

    public Amount<Pressure> getUpperPressureBound() {
        return properties.get("pressure_UPPER").getAmount();
    }

    @Override
    protected void writeAttributes(Element element) {
        super.writeAttributes(element);
        if (type == null) {
            System.out.println(getId());
        }

        element.setAttribute("type", type.name().toLowerCase());
    }

    @Override
    public void writeTo(Document document, Element parent) {
        writeAttributes(parent);
        //Element pressureEL = document.createElement("pressure");
        //pressureEL.setAttribute("bound", "lower");
        //Element pressureEU = document.createElement("pressure");
        //pressureEL.setAttribute("bound", "lower");
        writeChildren(document, parent);
    }

    @Override
    protected void writeChildren(Document document, Element parent) {
        super.writeChildren(document, parent);
        //System.out.println(properties.values());
        for (GasLibScenarioProperty property : properties.values()) {
            if (property.getBoundType() == null) {
                continue;
            }
            Element e;
            if (property.getName().contains("_")) {
                e = document.createElement(property.getName().substring(0, property.getName().indexOf("_")));
            } else {
                e = document.createElement(property.getName());
            }
            e.setAttribute("value", property.getValue());

            e.setAttribute("bound", property.getBoundType().name().toLowerCase());
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
                case "m^3/h":
                    e.setAttribute("unit", "1000m_cube_per_hour");
                    e.setAttribute("value", (Double.parseDouble(property.getValue()) / 1000) + "");
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
