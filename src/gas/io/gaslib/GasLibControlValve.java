/**
 * GasLibControlValve.java
 *
 */

package gas.io.gaslib;

import javax.measure.quantity.Pressure;
import org.jscience.physics.amount.Amount;
import org.w3c.dom.Element;

/**
 *
 * @author Martin Groß
 */
public class GasLibControlValve extends GasLibConnection {

    private boolean gasPreheaterExisting;
    private boolean internalBypassRequired;
    private Amount<Pressure> pressureDifferentialMax;
    private Amount<Pressure> pressureDifferentialMin;
    private Amount<Pressure> pressureInMin;
    private Amount<Pressure> pressureLossIn;
    private Amount<Pressure> pressureLossOut;
    private Amount<Pressure> pressureOutMax;

    public boolean isGasPreheaterExisting() {
        return gasPreheaterExisting;
    }

    public boolean isInternalBypassRequired() {
        return internalBypassRequired;
    }

    public Amount<Pressure> getPressureDifferentialMax() {
        return pressureDifferentialMax;
    }

    public Amount<Pressure> getPressureDifferentialMin() {
        return pressureDifferentialMin;
    }

    public Amount<Pressure> getPressureInMin() {
        return pressureInMin;
    }

    public Amount<Pressure> getPressureLossIn() {
        return pressureLossIn;
    }

    public Amount<Pressure> getPressureLossOut() {
        return pressureLossOut;
    }

    public Amount<Pressure> getPressureOutMax() {
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
