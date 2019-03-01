/**
 * AnacondaControlValve.java
 *
 */

package gas.io.anaconda;

import gas.io.XMLProperty;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.VolumetricFlowRate;
import javax.measure.unit.Unit;
import org.jscience.physics.amount.Amount;

/**
 *
 * @author Martin Groß
 */
public class AnacondaControlValve extends AnacondaConnection {

    private Amount<VolumetricFlowRate> flowInit;    
    private Amount<Pressure> pressureDifferentialMax;
    private Amount<Pressure> pressureDifferentialMin;

    public Amount<VolumetricFlowRate> getFlowInit() {
        return flowInit;
    }
    
    public Amount<Pressure> getPressureDifferentialMax() {
        return pressureDifferentialMax;
    }

    public Amount<Pressure> getPressureDifferentialMin() {
        return pressureDifferentialMin;
    }

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("controlValve");
    }

    @Override
    protected void parseProperties() {
        super.parseProperties();
        XMLProperty fMax = getProperties().get("flowInit");
        if (fMax.getUnit().equals("1000m_cube_per_hour")) {
            flowInit = Amount.valueOf(Double.parseDouble(fMax.getValue()) * 1000, (Unit<VolumetricFlowRate>) Unit.valueOf("m^3 / h"));
        } else {
            throw new AssertionError("Volumetric flow rate unit unknown: " + fMax.getUnit());
        }        
        pressureDifferentialMax = getProperties().get("pressureDifferentialMax").getAmount();
        pressureDifferentialMin = getProperties().get("pressureDifferentialMin").getAmount();
    }
}
