/**
 * GasLibCompressorStation.java
 *
 */

package gas.io.gaslib;

import gas.io.XMLIntersection;
import java.util.Map;



import units.UnitsTools;
import org.w3c.dom.Element;

/**
 *
 * @author Martin Groß
 */
public class GasLibCompressorStation extends GasLibConnection {
    
    private double diameterIn;
    private double diameterOut;
    private double dragFactorIn;
    private double dragFactorOut;
    private XMLIntersection fuelGasVertex;
    private String fuelGasVertexId;
    private boolean gasCoolerExisting;
    private boolean internalBypassRequired;
    private double pressureInMin;
    private double pressureOutMax;

    public double getDiameterIn() {
        return diameterIn;
    }

    public double getDiameterOut() {
        return diameterOut;
    }

    public double getDragFactorIn() {
        return dragFactorIn;
    }

    public double getDragFactorOut() {
        return dragFactorOut;
    }

    public XMLIntersection getFuelGasVertex() {
        return fuelGasVertex;
    }

    public boolean isGasCoolerExisting() {
        return gasCoolerExisting;
    }

    public boolean isInternalBypassRequired() {
        return internalBypassRequired;
    }

    public double getPressureInMin() {
        return pressureInMin;
    }

    public double getPressureOutMax() {
        return pressureOutMax;
    }

    @Override
    public void connectToIntersections(Map<String, XMLIntersection> intersections) {
        super.connectToIntersections(intersections);
        fuelGasVertex = intersections.get(fuelGasVertexId);
    }

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("compressorStation");
    }

    @Override
    protected boolean parseAttribute(String name, String value) {
        if (!super.parseAttribute(name, value)) {
            switch (name) {
                case "fuelGasVertex":
                    fuelGasVertexId = value;
                    return true;
                case "gasCoolerExisting":
                    gasCoolerExisting = parseBoolean(value);
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
    protected void writeAttributes(Element element) {
        super.writeAttributes(element);
        element.setAttribute("fuelGasVertex", fuelGasVertexId);
        element.setAttribute("gasCoolerExisting", writeBoolean(gasCoolerExisting));
        element.setAttribute("internalBypassRequired", writeBoolean(internalBypassRequired));
    }    

    @Override
    protected void parseProperties() {
        super.parseProperties();
        if (getProperties().containsKey("diameterIn")) {
            diameterIn = getProperties().get("diameterIn").getAmount();
        }
        if (getProperties().containsKey("diameterOut")) {
            diameterOut = getProperties().get("diameterOut").getAmount();
        }
        if (getProperties().containsKey("dragFactorIn")) {
            dragFactorIn = getProperties().get("dragFactorIn").getAmount();
        }
        if (getProperties().containsKey("dragFactorOut")) {
            dragFactorOut = getProperties().get("dragFactorOut").getAmount();
        }
        pressureInMin = getProperties().get("pressureInMin").getAmount();
        pressureOutMax = getProperties().get("pressureOutMax").getAmount();
    }
}
