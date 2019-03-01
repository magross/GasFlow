/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gas.io;

import javax.measure.quantity.Length;
import org.jscience.physics.amount.Amount;

/**
 *
 * @author Martin
 */
public interface Pipe {
    
    public Amount<Length> getDiameter();
    public Amount<Length> getLength();
    public Amount<Length> getRoughness();
    
}
