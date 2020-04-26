/**
 * GasLibResistor.java
 *
 */

package gas.io.gaslib;

import units.UnitsTools;

/**
 *
 * @author Martin Groß
 */
public class GasLibResistor extends GasLibConnection {

    private double diameter;
    private double dragFactor;

    public double getDiameter() {
        return diameter;
    }

    public double getDragFactor() {
        return dragFactor;
    }
    
    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("resistor");
    }

    @Override
    protected void parseProperties() {
        //System.out.println(super.getClass());
        super.parseProperties();
        if (getProperties().containsKey("diameter")) {
            diameter = getProperties().get("diameter").getAmount();
        }        
        if (getProperties().containsKey("dragFactor")) {
            dragFactor = getProperties().get("dragFactor").getAmount();
        }        
    }
}
