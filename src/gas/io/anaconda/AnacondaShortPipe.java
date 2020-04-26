/**
 * ShortPipe.java
 *
 */

package gas.io.anaconda;

import gas.io.XMLProperty;


import units.UnitsTools;

/**
 *
 * @author Martin Groß
 */
public class AnacondaShortPipe extends AnacondaConnection {

    private double flowInit;

    public double getFlowInit() {
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
            flowInit = Double.parseDouble(fMax.getValue()) * 1000 * UnitsTools.m3/UnitsTools.hr;
        } else {
            throw new AssertionError("Volumetric flow rate unit unknown: " + fMax.getUnit());
        }
    }        
}
