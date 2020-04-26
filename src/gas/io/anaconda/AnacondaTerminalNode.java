/**
 * AnacondaTerminalNode.java
 *
 */

package gas.io.anaconda;

import gas.io.XMLProperty;


import units.UnitsTools;

/**
 *
 * @author Martin Groß
 */
public abstract class AnacondaTerminalNode extends AnacondaIntersection {

    private double flowMax;
    private double flowMin;

    public double getFlowMax() {
        return flowMax;
    }

    public double getFlowMin() {
        return flowMin;
    }
    
    @Override
    protected void parseProperties() {
        super.parseProperties();
        XMLProperty fMax = getProperties().get("flowMax");
        if (fMax.getUnit().equals("1000m_cube_per_hour")) {
            flowMax = Double.parseDouble(fMax.getValue()) * 1000 * UnitsTools.m3/UnitsTools.hr;
        } else {
            throw new AssertionError("Volumetric flow rate unit unknown: " + fMax.getUnit());
        }
        XMLProperty fMin = getProperties().get("flowMin");
        if (fMin.getUnit().equals("1000m_cube_per_hour")) {
            flowMin = Double.parseDouble(fMax.getValue()) * 1000 * UnitsTools.m3/UnitsTools.hr;
        } else {
            throw new AssertionError("Volumetric flow rate unit unknown: " + fMin.getUnit());
        }        
    }
}
