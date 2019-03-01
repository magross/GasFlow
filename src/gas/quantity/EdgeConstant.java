/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gas.quantity;

import javax.measure.quantity.Quantity;
import static javax.measure.unit.SI.METER;
import static javax.measure.unit.SI.SECOND;
import javax.measure.unit.Unit;

/**
 *
 * @author gross
 */
public interface EdgeConstant extends Quantity {

    public static final Unit<EdgeConstant> UNIT = (Unit<EdgeConstant>) SECOND.pow(2).times(METER.pow(2));

}
