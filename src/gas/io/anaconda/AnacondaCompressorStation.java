/**
 * AnacondaCompressorStation.java
 *
 */

package gas.io.anaconda;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Power;
import javax.measure.quantity.Volume;
import javax.measure.quantity.VolumetricFlowRate;
import org.jscience.physics.amount.Amount;

/**
 *
 * @author Martin Groß
 */
public class AnacondaCompressorStation extends AnacondaConnection {
    
    private String bypassValveId;
    private Amount<Dimensionless> efficiency;
    private Amount<VolumetricFlowRate> flowInit;
    private Amount<Duration> minDownTime;
    private Amount<Duration> minRunTime;
    private Amount<Power> powerMax;
    private Amount<Power> powerMin;
    private double scalingOfControl;
    private Amount<Volume> shutdownCosts;
    private Amount<Volume> startupCosts;
    private Amount<Dimensionless> specificFuelConsumption;
    private String typeOfControl;

    public String getBypassValveId() {
        return bypassValveId;
    }

    public Amount<Dimensionless> getEfficiency() {
        return efficiency;
    }

    public Amount<VolumetricFlowRate> getFlowInit() {
        return flowInit;
    }

    public Amount<Duration> getMinDownTime() {
        return minDownTime;
    }

    public Amount<Duration> getMinRunTime() {
        return minRunTime;
    }

    public Amount<Power> getPowerMax() {
        return powerMax;
    }

    public Amount<Power> getPowerMin() {
        return powerMin;
    }

    public double getScalingOfControl() {
        return scalingOfControl;
    }

    public Amount<Volume> getShutdownCosts() {
        return shutdownCosts;
    }

    public Amount<Volume> getStartupCosts() {
        return startupCosts;
    }

    public Amount<Dimensionless> getSpecificFuelConsumption() {
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
