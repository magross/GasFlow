/**
 * AnacondaTerminalNode.java
 *
 */

package gas.io.anaconda;

import gas.io.XMLProperty;
import javax.measure.quantity.VolumetricFlowRate;
import javax.measure.unit.Unit;
import org.jscience.physics.amount.Amount;

/**
 *
 * @author Martin Groß
 */
public abstract class AnacondaTerminalNode extends AnacondaIntersection {

    private Amount<VolumetricFlowRate> flowMax;
    private Amount<VolumetricFlowRate> flowMin;

    public Amount<VolumetricFlowRate> getFlowMax() {
        return flowMax;
    }

    public Amount<VolumetricFlowRate> getFlowMin() {
        return flowMin;
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
