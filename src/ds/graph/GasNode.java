/**
 * GasNode.java
 *
 */

package ds.graph;

import javax.measure.quantity.Length;
import javax.measure.quantity.Volume;
import org.jscience.physics.amount.Amount;

/**
 *
 * @author Martin Groß
 */
public class GasNode implements Identifiable {

    private int id;
    private Amount<Length> height;
    private Amount<Volume> volume;

    public GasNode() {
        
    }

    public GasNode(int i) {
        id = i;
    }

    public GasNode(int id, Amount<Length> height) {
        this.id = id;
        this.height = height;
    }    
    
    public GasNode(int id, Amount<Length> height, Amount<Volume> volume) {
        this.id = id;
        this.height = height;
        this.volume = volume;
    }

    public Amount<Length> getHeight() {
        return height;
    }

    public void setHeight(Amount<Length> height) {
        this.height = height;
    }

    public Amount<Volume> getVolume() {
        return volume;
    }

    public void setVolume(Amount<Volume> volume) {
        this.volume = volume;
    }

    @Override
    public int id() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "GasNode{" + "id=" + id + ", height=" + height + ", volume=" + volume + '}';
    }

}
