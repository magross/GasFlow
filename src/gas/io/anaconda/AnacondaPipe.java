/*
 * AnacondaPipe.java
 * 
 * 
 */
package gas.io.anaconda;

import gas.io.Pipe;
import gas.io.XMLProperty;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Length;
import javax.measure.quantity.VolumetricFlowRate;
import javax.measure.unit.Unit;
import org.jscience.physics.amount.Amount;

/**
 *
 * @author Martin Groß
 */
public class AnacondaPipe extends AnacondaConnection implements Pipe {

    private Amount<Length> diameter;
    private Amount<VolumetricFlowRate> flowInInit;
    private Amount<VolumetricFlowRate> flowOutInit;    
    private Amount<Length> length;
    private Amount<Length> roughness;

    public Amount<Dimensionless> computeSlope() {
        return (Amount<Dimensionless>) getTo().getHeight().minus(getFrom().getHeight()).divide(getLength());
    }

    public Amount<Length> getDiameter() {
        return diameter;
    }

    public Amount<VolumetricFlowRate> getFlowInInit() {
        return flowInInit;
    }

    public Amount<VolumetricFlowRate> getFlowOutInit() {
        return flowOutInit;
    }

    public Amount<Length> getLength() {
        return length;
    }

    public Amount<Length> getRoughness() {
        return roughness;
    }
    
    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("pipe");
    }

    @Override
    protected void parseProperties() {
        super.parseProperties();
        diameter = getProperties().get("diameter").getAmount();
        XMLProperty fMax = getProperties().get("flowInInit");
        if (fMax.getUnit().equals("1000m_cube_per_hour")) {
            flowInInit = Amount.valueOf(Double.parseDouble(fMax.getValue()) * 1000, (Unit<VolumetricFlowRate>) Unit.valueOf("m^3 / h"));
        } else {
            throw new AssertionError("Volumetric flow rate unit unknown: " + fMax.getUnit());
        }
        XMLProperty fMin = getProperties().get("flowOutInit");
        if (fMin.getUnit().equals("1000m_cube_per_hour")) {
            flowOutInit = Amount.valueOf(Double.parseDouble(fMax.getValue()) * 1000, (Unit<VolumetricFlowRate>) Unit.valueOf("m^3 / h"));
        } else {
            throw new AssertionError("Volumetric flow rate unit unknown: " + fMin.getUnit());
        }
        length = getProperties().get("length").getAmount();
        roughness = getProperties().get("roughness").getAmount();
    }
}
