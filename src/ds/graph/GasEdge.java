/**
 * GasEdge.java
 *
 */

package ds.graph;

import static java.lang.Math.PI;
import units.UnitsTools;

/**
 *
 * @author Martin Groß
 */
public class GasEdge extends AbstractEdge<GasNode> implements Identifiable {

    private double diameter;
    private double flowMax;
    private double flowMin;
    private int id;
    private double length;
    private double roughness;

    public GasEdge(GasNode start, GasNode end) {
        this(start, end, -1);
    }

    public GasEdge(GasNode start, GasNode end, int id) {
        super(start, end);
        this.id = id;        
        this.diameter = 0 * UnitsTools.m;
        this.length = 0 * UnitsTools.m;
        this.roughness = 0 * UnitsTools.m;
    }

    public GasEdge(GasNode start, GasNode end, int id, double diameter, double length, double roughness) {
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

    public double getDiameter() {
        return diameter;
    }

    public void setDiameter(double diameter) {
        this.diameter = diameter;
    }

    public double getFlowMax() {
        return flowMax;
    }

    public void setFlowMax(double flowMax) {
        this.flowMax = flowMax;
    }

    public double getFlowMin() {
        return flowMin;
    }

    public void setFlowMin(double flowMin) {
        this.flowMin = flowMin;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getSlope() {
        return (end().getHeight() - start().getHeight())/length;
    }
    
    public double getRoughness() {
        return roughness;
    }

    public void setRoughness(double roughness) {
        this.roughness = roughness;
    }
    
    public double getHalfVolume() {
        return diameter*diameter*(PI / 8.0)*length;
    }

    @Override
    public String toString() {
        return String.format("(%1$s,%2$s)", start().id(), end().id(), id());
    }
    
  
}
