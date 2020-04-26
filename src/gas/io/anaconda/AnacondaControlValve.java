/**
 * AnacondaControlValve.java
 *
 */

package gas.io.anaconda;

import gas.io.XMLProperty;



import units.UnitsTools;

/**
 *
 * @author Martin Groß
 */
public class AnacondaControlValve extends AnacondaConnection {

    private double flowInit;    
    private double pressureDifferentialMax;
    private double pressureDifferentialMin;

    public double getFlowInit() {
        return flowInit;
    }
    
    public double getPressureDifferentialMax() {
        return pressureDifferentialMax;
    }

    public double getPressureDifferentialMin() {
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
            flowInit = Double.parseDouble(fMax.getValue()) * 1000 * UnitsTools.m3/UnitsTools.hr;
        } else {
            throw new AssertionError("Volumetric flow rate unit unknown: " + fMax.getUnit());
        }        
        pressureDifferentialMax = getProperties().get("pressureDifferentialMax").getAmount();
        pressureDifferentialMin = getProperties().get("pressureDifferentialMin").getAmount();
    }
}
