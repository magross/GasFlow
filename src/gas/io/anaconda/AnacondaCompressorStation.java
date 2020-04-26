/**
 * AnacondaCompressorStation.java
 *
 */

package gas.io.anaconda;

/**
 *
 * @author Martin Groß
 */
public class AnacondaCompressorStation extends AnacondaConnection {
    
    private String bypassValveId;
    private double efficiency;
    private double flowInit;
    private double minDownTime;
    private double minRunTime;
    private double powerMax;
    private double powerMin;
    private double scalingOfControl;
    private double shutdownCosts;
    private double startupCosts;
    private double specificFuelConsumption;
    private String typeOfControl;

    public String getBypassValveId() {
        return bypassValveId;
    }

    public double getEfficiency() {
        return efficiency;
    }

    public double getFlowInit() {
        return flowInit;
    }

    public double getMinDownTime() {
        return minDownTime;
    }

    public double getMinRunTime() {
        return minRunTime;
    }

    public double getPowerMax() {
        return powerMax;
    }

    public double getPowerMin() {
        return powerMin;
    }

    public double getScalingOfControl() {
        return scalingOfControl;
    }

    public double getShutdownCosts() {
        return shutdownCosts;
    }

    public double getStartupCosts() {
        return startupCosts;
    }

    public double getSpecificFuelConsumption() {
        return specificFuelConsumption;
    }

    public String getTypeOfControl() {
        return typeOfControl;
    }

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("compressor");
    }

    @Override
    protected boolean parseAttribute(String name, String value) {
        if (!super.parseAttribute(name, value)) {
            switch (name) {
                case "bypassValve":
                    bypassValveId = value;
                    return true;
                case "scalingOfControl":
                    scalingOfControl = Double.parseDouble(value);
                    return true;
                case "typeOfControl":
                    // "pressure" or "power"
                    typeOfControl = value; 
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

    }
}
