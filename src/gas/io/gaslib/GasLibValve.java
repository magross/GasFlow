/**
 * GasLibValve.java
 *
 */

package gas.io.gaslib;


import units.UnitsTools;

/**
 *
 * @author Martin Groß
 */
public class GasLibValve extends GasLibConnection {

    private double pressureDifferentialMax;

    public double getPressureDifferentialMax() {
        return pressureDifferentialMax;
    }

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("valve");
    }

    @Override
    protected void parseProperties() {
        super.parseProperties();
        pressureDifferentialMax = getProperties().get("pressureDifferentialMax").getAmount();
    }
}
