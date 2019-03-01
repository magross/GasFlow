/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gas.common;

import gas.io.ConnectionType;
import javax.measure.quantity.VolumetricFlowRate;
import org.jscience.physics.amount.Amount;

/**
 *
 * @author gross
 */
public interface Connection<I extends Intersection> {

    Amount<VolumetricFlowRate> getFlowMax();

    Amount<VolumetricFlowRate> getFlowMin();

    I getFrom();

    I getTo();

    ConnectionType getType();
}
