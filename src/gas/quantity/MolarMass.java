/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gas.quantity;

import javax.measure.quantity.Quantity;
import static javax.measure.unit.SI.GRAM;
import static javax.measure.unit.SI.MOLE;
import javax.measure.unit.Unit;

/**
 *
 * @author gross
 */
public interface MolarMass extends Quantity {

    public static final Unit<MolarMass> UNIT = (Unit<MolarMass>) GRAM.divide(MOLE);
    
}
