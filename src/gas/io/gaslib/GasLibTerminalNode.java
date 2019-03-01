/**
 * GasLibTerminal.java
 *
 */

package gas.io.gaslib;

import gas.io.XMLProperty;
import javax.measure.quantity.VolumetricFlowRate;
import javax.measure.unit.Unit;
import org.jscience.physics.amount.Amount;

/**
 *
 * @author Martin Groß
 */
public abstract class GasLibTerminalNode extends GasLibIntersection {

    private Amount<VolumetricFlowRate> flowMax;
    private Amount<VolumetricFlowRate> flowMin;

    public GasLibTerminalNode() {
        super();
        flowMin = (Amount<VolumetricFlowRate>) Amount.valueOf("0 m^3/h");
        flowMax = (Amount<VolumetricFlowRate>) Amount.valueOf("10000000 m^3/h");
        
        properties.put("flowMin", new XMLProperty("flowMin", "1000m_cube_per_hour", "0"));
        properties.put("flowMax", new XMLProperty("flowMax", "1000m_cube_per_hour", "10000"));
    }
    
    public Amount<VolumetricFlowRate> getFlowMax() {
        return flowMax;
    }

    public void setFlowMax(Amount<VolumetricFlowRate> flowMax) {
        this.flowMax = flowMax;
    }

    public Amount<VolumetricFlowRate> getFlowMin() {
        return flowMin;
    }

    public void setFlowMin(Amount<VolumetricFlowRate> flowMin) {
        this.flowMin = flowMin;
    }

    @Override
    protected void parseProperties() {
        super.parseProperties();
        XMLProperty fMax = getProperties().get("flowMax");
        if (fMax.getUnit().equals("1000m_cube_per_hour")) {
            flowMax = Amount.valueOf(Double.parseDouble(fMax.getValue()) * 1000, (Unit<VolumetricFlowRate>) Unit.valueOf("m^3 / h"));
        } else {
            throw new AssertionError("Volumetric flow rate unit unknown: " + fMax.getUnit());
        }
        XMLProperty fMin = getProperties().get("flowMin");
        if (fMin.getUnit().equals("1000m_cube_per_hour")) {
            flowMin = Amount.valueOf(Double.parseDouble(fMax.getValue()) * 1000, (Unit<VolumetricFlowRate>) Unit.valueOf("m^3 / h"));
        } else {
            throw new AssertionError("Volumetric flow rate unit unknown: " + fMin.getUnit());
        }
    }
}
