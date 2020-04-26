/*
 * AnacondaPipe.java
 * 
 * 
 */
package gas.io.anaconda;

import gas.io.Pipe;
import gas.io.XMLProperty;
import units.UnitsTools;

/**
 *
 * @author Martin Groß
 */
public class AnacondaPipe extends AnacondaConnection implements Pipe {

    private double diameter;
    private double flowInInit;
    private double flowOutInit;    
    private double length;
    private double roughness;

    public double computeSlope() {
        return (getTo().getHeight() - getFrom().getHeight())/getLength();
    }

    public double getDiameter() {
        return diameter;
    }

    public double getFlowInInit() {
        return flowInInit;
    }

    public double getFlowOutInit() {
        return flowOutInit;
    }

    public double getLength() {
        return length;
    }

    public double getRoughness() {
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
            flowInInit = Double.parseDouble(fMax.getValue()) * 1000 * UnitsTools.m3/UnitsTools.hr;
        } else {
            throw new AssertionError("Volumetric flow rate unit unknown: " + fMax.getUnit());
        }
        XMLProperty fMin = getProperties().get("flowOutInit");
        if (fMin.getUnit().equals("1000m_cube_per_hour")) {
            flowOutInit = Double.parseDouble(fMax.getValue()) * 1000 * UnitsTools.m3/UnitsTools.hr;
        } else {
            throw new AssertionError("Volumetric flow rate unit unknown: " + fMin.getUnit());
        }
        length = getProperties().get("length").getAmount();
        roughness = getProperties().get("roughness").getAmount();
    }
}
