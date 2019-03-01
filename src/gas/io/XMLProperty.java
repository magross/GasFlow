/**
 * Property.java
 *
 */
package gas.io;

import javax.measure.quantity.Temperature;
import javax.measure.quantity.VolumetricFlowRate;
import javax.measure.unit.SI;
import static javax.measure.unit.SI.CELSIUS;
import static javax.measure.unit.SI.KELVIN;
import javax.measure.unit.Unit;
import org.jscience.physics.amount.Amount;

/**
 *
 * @author Martin Groß
 */
public class XMLProperty extends XMLElement {

    private Amount amount;
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

    public Amount getAmount() {
        if (amount != null) {
            return amount;
        } else {
            switch (unit) {
                case "barg":
                    amount = Amount.valueOf((Double.parseDouble(value)+1) + "bar");
                    break;
                case "Celsius":
                    amount = Amount.valueOf(value + "C");
                    break;
                case "1000m_cube_per_hour":
                    amount = Amount.valueOf(Double.parseDouble(value) * 1000, (Unit<VolumetricFlowRate>) Unit.valueOf("m^3 / h"));
                    break;
                case "kg_per_kmol":
                    amount = Amount.valueOf(value + "g/mol");
                    break;
                case "meter":
                    amount = Amount.valueOf(value + "m");
                    break;
                default:
                    try {
                        amount = Amount.valueOf(value + unit);
                    } catch (Exception e) {
                        System.err.println("XMLProperty.getAmount(): " + e.getMessage());
                        System.err.println(name + ": " + value + " '" + unit + "'");
                        return null;
                    }
            }
            return amount;
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
