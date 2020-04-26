/**
 * GasLibTerminal.java
 *
 */

package gas.io.gaslib;

import gas.io.XMLProperty;


import units.UnitsTools;

/**
 *
 * @author Martin Groß
 */
public abstract class GasLibTerminalNode extends GasLibIntersection {

    private double flowMax;
    private double flowMin;

    public GasLibTerminalNode() {
        super();
        flowMin = 0 * UnitsTools.m3/UnitsTools.hr;
        flowMax = 10000000 * UnitsTools.m3/UnitsTools.hr;
        
        properties.put("flowMin", new XMLProperty("flowMin", "1000m_cube_per_hour", "0"));
        properties.put("flowMax", new XMLProperty("flowMax", "1000m_cube_per_hour", "10000"));
    }
    
    public double getFlowMax() {
        return flowMax;
    }

    public void setFlowMax(double flowMax) {
        this.flowMax = flowMax;
    }

    public double getFlowMin() {
        return flowMin;
    }

    public void setFlowMin(double flowMin) {
        this.flowMin = flowMin;
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
