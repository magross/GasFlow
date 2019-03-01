/**
 * GasEdge.java
 *
 */

package ds.graph;

import static java.lang.Math.PI;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Length;
import javax.measure.quantity.Volume;
import javax.measure.quantity.VolumetricFlowRate;
import static javax.measure.unit.SI.METER;
import org.jscience.physics.amount.Amount;

/**
 *
 * @author Martin Groß
 */
public class GasEdge extends AbstractEdge<GasNode> implements Identifiable {

    private Amount<Length> diameter;
    private Amount<VolumetricFlowRate> flowMax;
    private Amount<VolumetricFlowRate> flowMin;
    private int id;
    private Amount<Length> length;
    private Amount<Length> roughness;

    public GasEdge(GasNode start, GasNode end) {
        this(start, end, -1);
    }

    public GasEdge(GasNode start, GasNode end, int id) {
        super(start, end);
        this.id = id;        
        this.diameter = Amount.valueOf(0, METER);
        this.length = Amount.valueOf(0, METER);
        this.roughness = Amount.valueOf(0, METER);
    }

    public GasEdge(GasNode start, GasNode end, int id, Amount<Length> diameter, Amount<Length> length, Amount<Length> roughness) {
        this(start, end, id);
        this.diameter = diameter;
        this.length = length;
        this.roughness = roughness;
    }

    public static GasEdge createEdge(GasNode start, GasNode end) {
        return new GasEdge(start, end);
    }

    @Override
    public int id() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Amount<Length> getDiameter() {
        return diameter;
    }

    public void setDiameter(Amount<Length> diameter) {
        this.diameter = diameter;
    }

    public Amount<VolumetricFlowRate> getFlowMax() {
        return flowMax;
    }

    public void setFlowMax(Amount<VolumetricFlowRate> flowMax) {
        this.flowMax = flowMax;
    }

    public Amount<VolumetricFlowRate> getFlowMin() {
        return flowMin;
    }

    public void setFlowMin(Amount<VolumetricFlowRate> flowMin) {
        this.flowMin = flowMin;
    }

    public Amount<Length> getLength() {
        return length;
    }

    public void setLength(Amount<Length> length) {
        this.length = length;
    }

    public Amount<Dimensionless> getSlope() {
        return (Amount<Dimensionless>) (end().getHeight().minus(start().getHeight()).divide(length));
    }
    
    public Amount<Length> getRoughness() {
        return roughness;
    }

    public void setRoughness(Amount<Length> roughness) {
        this.roughness = roughness;
    }
    
    public Amount<Volume> getHalfVolume() {
        return (Amount<Volume>) diameter.pow(2).times(PI / 8.0).times(length);
    }

    @Override
    public String toString() {
        return String.format("(%1$s,%2$s)", start().id(), end().id(), id());
    }
    
  
}
