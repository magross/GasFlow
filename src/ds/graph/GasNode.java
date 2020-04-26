/**
 * GasNode.java
 *
 */

package ds.graph;



import units.UnitsTools;

/**
 *
 * @author Martin Groß
 */
public class GasNode implements Identifiable {

    private int id;
    private double height;
    private double volume;

    public GasNode() {
        
    }

    public GasNode(int i) {
        id = i;
    }

    public GasNode(int id, double height) {
        this.id = id;
        this.height = height;
    }    
    
    public GasNode(int id, double height, double volume) {
        this.id = id;
        this.height = height;
        this.volume = volume;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
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
