/**
 * GasLibControlValve.java
 *
 */

package gas.io.gaslib;


import units.UnitsTools;
import org.w3c.dom.Element;

/**
 *
 * @author Martin Groß
 */
public class GasLibControlValve extends GasLibConnection {

    private boolean gasPreheaterExisting;
    private boolean internalBypassRequired;
    private double pressureDifferentialMax;
    private double pressureDifferentialMin;
    private double pressureInMin;
    private double pressureLossIn;
    private double pressureLossOut;
    private double pressureOutMax;

    public boolean isGasPreheaterExisting() {
        return gasPreheaterExisting;
    }

    public boolean isInternalBypassRequired() {
        return internalBypassRequired;
    }

    public double getPressureDifferentialMax() {
        return pressureDifferentialMax;
    }

    public double getPressureDifferentialMin() {
        return pressureDifferentialMin;
    }

    public double getPressureInMin() {
        return pressureInMin;
    }

    public double getPressureLossIn() {
        return pressureLossIn;
    }

    public double getPressureLossOut() {
        return pressureLossOut;
    }

    public double getPressureOutMax() {
        return pressureOutMax;
    }

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("controlValve");
    }

    @Override
    protected boolean parseAttribute(String name, String value) {
        if (!super.parseAttribute(name, value)) {
            switch (name) {
                case "gasPreheaterExisting":
                    gasPreheaterExisting = parseBoolean(value);
                    return true;
                case "internalBypassRequired":
                    internalBypassRequired = parseBoolean(value);
                    return true;
                default:
                    return false;
            }
        } else {
            return true;
        }
    }

    @Override
    protected void parseProperties() {
        super.parseProperties();
        if (getProperties().containsKey("pressureDifferentialMax")) {
            pressureDifferentialMax = getProperties().get("pressureDifferentialMax").getAmount();
        }
        if (getProperties().containsKey("pressureDifferentialMin")) {
            pressureDifferentialMin = getProperties().get("pressureDifferentialMin").getAmount();
        }
        pressureInMin = getProperties().get("pressureInMin").getAmount();
        pressureOutMax = getProperties().get("pressureOutMax").getAmount();
        if (getProperties().containsKey("pressureLossIn")) {
            pressureLossIn = getProperties().get("pressureLossIn").getAmount();
        }
        if (getProperties().containsKey("pressureLossOut")) {
            pressureLossOut = getProperties().get("pressureLossOut").getAmount();
        }
    }
    
    @Override
    protected void writeAttributes(Element element) {
        super.writeAttributes(element);
        element.setAttribute("gasPreheaterExisting", writeBoolean(gasPreheaterExisting));
        element.setAttribute("internalBypassRequired", writeBoolean(internalBypassRequired));
    }        
}
