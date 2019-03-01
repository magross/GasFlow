/*
 * GasLibScenarioProperty.java
 * 
 * 
 */

package gas.io.gaslib;

import gas.io.XMLProperty;

/**
 *
 * @author Martin
 */
public class GasLibScenarioProperty extends XMLProperty {
    
    public enum Bound {
        BOTH, LOWER, UPPER;
    }
    
    private Bound bound;

    public GasLibScenarioProperty() {
        super();
    }

    public GasLibScenarioProperty(String name, String unit, String value, Bound bound) {
        super(name, unit, value);
        this.bound = bound;
    }
    
    public Bound getBoundType() {
        return bound;
    }

    @Override
    protected boolean parseAttribute(String name, String value) {
        if (!super.parseAttribute(name, value)) {
            switch (name) {
                case "bound":
                    bound = Bound.valueOf(value.toUpperCase());
                    return true;
                default:
                    return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public String toString() {
        return "ScenarioProperty {" + "name=" + getName() + ", bound=" + bound + ", unit=" + getUnit() + ", value=" + getValue() + '}';
    }  
}
