/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gas.common;

import java.util.List;


import units.UnitsTools;

/**
 *
 * @author Martin Groß
 * @param <C> the type of connection;
 */
public interface Intersection<C extends Connection> {

    List<C> getConnections();

    double getHeight();

    String getId();

    double getPressureMax();

    double getPressureMin();

    double getX();

    double getY();

}
