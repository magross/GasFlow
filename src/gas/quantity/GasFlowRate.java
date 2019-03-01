/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gas.quantity;

import javax.measure.quantity.Quantity;
import javax.measure.unit.SI;
import static javax.measure.unit.SI.KILOGRAM;
import static javax.measure.unit.SI.METER;
import static javax.measure.unit.SI.SECOND;
import javax.measure.unit.Unit;

/**
 *
 * @author gross
 */
public interface GasFlowRate extends Quantity {

    public static final Unit<GasFlowRate> UNIT = (Unit<GasFlowRate>) KILOGRAM.divide(SECOND).divide(METER.pow(2));

}
