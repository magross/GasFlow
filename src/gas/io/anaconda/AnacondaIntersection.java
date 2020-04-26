/*
 * AnacondaIntersection.java
 * 
 * 
 */
package gas.io.anaconda;

import gas.io.XMLIntersection;
import gas.io.IntersectionType;
import static gas.io.IntersectionType.NODE;
import static gas.io.IntersectionType.SINK;
import static gas.io.IntersectionType.SOURCE;
import gas.io.XMLProperty;


import units.UnitsTools;

/**
 *
 * @author Martin Groß
 */
public abstract class AnacondaIntersection extends XMLIntersection {

    public static AnacondaIntersection createNewIntersection(IntersectionType type) {
        switch (type) {
            case NODE:
                return new AnacondaInnerNode();
            case SINK:
                return new AnacondaSink();
            case SOURCE:
                return new AnacondaSource();
            default:
                throw new AssertionError("Unknown type: " + type);
        }
    }
    
    private boolean controllable;
    private String idPos;
    private double maxCtrl;
    private double minCtrl;
    private double pressureInit;
    private double scalingOfControl; 
    private double temperature;

    public boolean isControllable() {
        return controllable;
    }

    public String getIdPos() {
        return idPos;
    }

    public double getMaxCtrl() {
        return maxCtrl;
    }

    public double getMinCtrl() {
        return minCtrl;
    }

    public double getPressureInit() {
        return pressureInit;
    }

    public double getScalingOfControl() {
        return scalingOfControl;
    }

    public double getTemperature() {
        return temperature; 
    }

    @Override
    protected boolean parseAttribute(String name, String value) {
        if (!super.parseAttribute(name, value)) {
            switch (name) {
                case "controllable":
                    controllable = (Integer.parseInt(value) != 0);
                    return true;
                case "idPos":
                    idPos = value;
                    return true;
                case "maxCtrl":
                    maxCtrl = Double.parseDouble(value);
                    return true;
                case "minCtrl":
                    minCtrl = Double.parseDouble(value);
                    return true;                    
                case "scalingOfControl":
                    scalingOfControl = Double.parseDouble(value);
                    return true;                                        
                default:
                    return false;
            }
        } else {
            return true;
        }
    }      
    
    @Override
    protected void parseProperties() {
        super.parseProperties();
        pressureInit = getProperties().get("pressureInit").getAmount();
        XMLProperty pTemperature = getProperties().get("temperature");     
        if (pTemperature.getUnit().isEmpty() || pTemperature.getUnit().equals("Celsius")) {
            pTemperature.setUnit("C");
        }
        temperature = pTemperature.getAmount(); 
    }
}
