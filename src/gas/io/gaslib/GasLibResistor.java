/**
 * GasLibResistor.java
 *
 */

package gas.io.gaslib;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Length;
import static javax.measure.unit.SI.METER;
import org.jscience.physics.amount.Amount;

/**
 *
 * @author Martin Groß
 */
public class GasLibResistor extends GasLibConnection {

    private Amount<Length> diameter;
    private Amount<Dimensionless> dragFactor;

    public Amount<Length> getDiameter() {
        return diameter;
    }

    public Amount<Dimensionless> getDragFactor() {
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
