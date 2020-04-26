/**
 * Property.java
 *
 */
package gas.io;

import units.UnitsTools;

/**
 *
 * @author Martin Groß
 */
public class XMLProperty extends XMLElement {

    private Double amount;
    private String name;
    private String unit;
    private String value;

    public XMLProperty() {
        amount = null;
        name = "";
        unit = "m/m";
        value = "";
    }

    public XMLProperty(String name, String unit, String value) {
        amount = null;
        this.name = name;
        this.unit = unit;
        this.value = value;
    }

    public double getAmount() {
        if (amount != null) {
            return amount.doubleValue();
        } else {
            switch (unit) {
                case "bar":
                    amount = (Double.parseDouble(value)+1) * UnitsTools.bar;
                    break;
                case "Celsius":
                    amount = Double.parseDouble(value) * UnitsTools.C;
                    break;
                case "1000m_cube_per_hour":
                    amount = (Double.parseDouble(value) * 1000) * UnitsTools.m3/UnitsTools.hr;
                    break;
                case "kg_per_kmol":
                    amount = Double.parseDouble(value) * UnitsTools.g/UnitsTools.mol;
                    break;
                case "meter":
                    amount = Double.parseDouble(value) * UnitsTools.m;
                    break;
                default:
                    try {
                        amount = Double.parseDouble(value);
                    } catch (Exception e) {
                        System.err.println("XMLProperty.getAmount(): " + e.getMessage());
                        System.err.println(name + ": " + value + " '" + unit + "'");
                        return 0;
                    }
            }
            return amount.doubleValue();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
        amount = null;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        amount = null;
    }

    @Override
    protected boolean parseAttribute(String name, String value) {
        if (!super.parseAttribute(name, value)) {
            amount = null;
            switch (name) {
                case "unit":
                    unit = value;
                    return true;
                case "value":
                    this.value = value;
                    return true;
                default:
                    return false;
            }
        } else {
            return true;
        }
    }

    @Override
    protected void parseNodeName(String nodeName) {
        name = nodeName;
    }

    @Override
    public String toString() {
        return "Property {" + "name=" + name + ", unit=" + unit + ", value=" + value + '}';
    }
}
