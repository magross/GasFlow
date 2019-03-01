/**
 * GasLibValve.java
 *
 */

package gas.io.gaslib;

import javax.measure.quantity.Pressure;
import org.jscience.physics.amount.Amount;

/**
 *
 * @author Martin Groß
 */
public class GasLibValve extends GasLibConnection {

    private Amount<Pressure> pressureDifferentialMax;

    public Amount<Pressure> getPressureDifferentialMax() {
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
