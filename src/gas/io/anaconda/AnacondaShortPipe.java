/**
 * ShortPipe.java
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
public class AnacondaShortPipe extends AnacondaConnection {

    private Amount<VolumetricFlowRate> flowInit;

    public Amount<VolumetricFlowRate> getFlowInit() {
        return flowInit;
    }
    
    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("shortPipe");
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
    }        
}
