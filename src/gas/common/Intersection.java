/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gas.common;

import java.util.List;
import javax.measure.quantity.Length;
import javax.measure.quantity.Pressure;
import org.jscience.physics.amount.Amount;

/**
 *
 * @author Martin Groß
 * @param <C> the type of connection;
 */
public interface Intersection<C extends Connection> {

    List<C> getConnections();

    Amount<Length> getHeight();

    String getId();

    Amount<Pressure> getPressureMax();

    Amount<Pressure> getPressureMin();

    double getX();

    double getY();

}
