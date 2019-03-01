/*
 * GasLibPipe.java
 * 
 * 
 */
package gas.io.gaslib;

import gas.io.Pipe;
import gas.io.XMLProperty;
import gas.quantity.HeatTransferCoefficient;
import javax.measure.quantity.Area;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Length;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Velocity;
import static javax.measure.unit.NonSI.BAR;
import javax.measure.unit.SI;
import static javax.measure.unit.SI.KELVIN;
import static javax.measure.unit.SI.METER;
import static javax.measure.unit.SI.SECOND;
import static javax.measure.unit.SI.WATT;
import static javax.measure.unit.Unit.ONE;
import org.jscience.physics.amount.Amount;
import org.jscience.physics.amount.Constants;

/**
 *
 * @author Martin Groß
 */
public class GasLibPipe extends GasLibConnection implements Pipe {

    private Amount<Length> diameter;
    private Amount<HeatTransferCoefficient> heatTransferCoefficient;
    private Amount<Length> length;
    private Amount<Pressure> pressureMax;
    private Amount<Length> roughness;

    public GasLibPipe() {
        super();
    }

    public GasLibPipe(String id, GasLibIntersection start, GasLibIntersection end, String startId, String endId,
            Amount length) {
        super();
        this.id = id;
        from = start;
        fromId = startId;
        to = end;
        toId = endId;
        this.diameter = Amount.valueOf(500, SI.MILLIMETER);
        this.roughness = Amount.valueOf(0.05, SI.MILLIMETER);
        this.length = length;
        heatTransferCoefficient = (Amount<HeatTransferCoefficient>) Amount.valueOf(2, WATT.divide(METER).divide(METER).divide(KELVIN));
        pressureMax = Amount.valueOf(200, BAR);
    }

    public static void main(String[] args) {
        System.out.println(Amount.valueOf(2, WATT.divide(METER).divide(METER).divide(KELVIN)));
    }

    public Amount<Area> computeCrossSection() {
        return (Amount<Area>) diameter.times(diameter).times(Math.PI / 4);
    }

    public Amount<Dimensionless> computeSiamCoefficient(GasLibNetworkFile networkFile) {
        Amount lambda = getLength()
                .times(-1.0)
                .times(computeNikuradseFrictionFactor())
                .times(networkFile.getMeanSpecificGasConstant())
                .times(computePapayCompressibilityFactor(networkFile))
                .times(networkFile.getMeanTemperature())
                .divide(computeCrossSection())
                .divide(computeCrossSection())
                .divide(getDiameter());
        /*
        Amount s = Constants.g
                .times(2.0)
                .times(computeSlope())
                .times(getLength())
                .divide(networkFile.getMeanSpecificGasConstant())
                .divide(computePapayCompressibilityFactor(networkFile))
                .divide(networkFile.getMeanTemperature());*/
        return lambda;
    }

    public Amount<Dimensionless> computeNikuradseFrictionFactor() {
        double diameterM = diameter.doubleValue(METER);
        double roughnessM = roughness.doubleValue(METER);
        double relativeRoughness = roughnessM / diameterM;
        double frictionFactor = Math.pow(1.138 - 2 * Math.log10(relativeRoughness), -2);
        return Amount.valueOf(frictionFactor, ONE);
    }

    public Amount<Pressure> getMeanPressure() {
        double minOfMax = Math.min(getTo().getPressureMax().doubleValue(BAR), getFrom().getPressureMax().doubleValue(BAR));
        double maxOfMin = Math.max(getFrom().getPressureMin().doubleValue(BAR), getTo().getPressureMin().doubleValue(BAR));
        return Amount.valueOf(0.5 * (minOfMax + maxOfMin), BAR);
    }

    public Amount<Dimensionless> computePapayCompressibilityFactor(GasLibNetworkFile constants) {
        double tr = constants.getMeanReducedTemperature().doubleValue(Dimensionless.UNIT);
        double pr = ((Amount<Dimensionless>) getMeanPressure().divide(constants.getMeanPseudocriticalPressure())).doubleValue(Dimensionless.UNIT);
        double z = 1 - 3.52 * pr * Math.exp(-2.26 * tr) + 0.247 * pr * pr * Math.exp(-1.878 * tr);
        return Amount.valueOf(z, Dimensionless.UNIT);
    }

    public Amount<Dimensionless> computeSlope() {
        return (Amount<Dimensionless>) getTo().getHeight().minus(getFrom().getHeight()).divide(getLength());
    }

    /*
    public Amount<Dimensionless> computeReynoldsNumber(Amount volumetricFlow) {
        Amount rho = Amount.valueOf(0.82, KILOGRAM.divide(METER.pow(3)));
        Amount eta = Amount.valueOf(0.0000119, KILOGRAM.divide(METER).divide(SECOND));
        return rho.divide(eta.times(roughness).times(PI)).times(volumetricFlow).times(4);
    } */
 /*
    // in m^3 / s
    public Amount computeHoeferFrictionFactor(Amount volumetricFlow) {
        Amount R = computeReynoldsNumber(volumetricFlow);
        double d2 = Math.log10(R.doubleValue(ONE) / 7.0);
        Amount d12 = Amount.valueOf(4.518, ONE).divide(R).times(d2);        
        Amount d3 = d12.plus(roughness.divide(3.71).divide(diameter));
        double denom = Math.pow(2*Math.log10(d3.doubleValue(ONE)), -2);
        return Amount.valueOf(denom, ONE);
    } */
    public Amount computeTimelessCoefficient(Amount<Velocity> c) {
        Amount<Area> crossSection = computeCrossSection();
        // lambda
        Amount friction = computeNikuradseFrictionFactor();
        // c^2
        Amount c2 = c.times(c);
        // c^4
        Amount c4 = c2.times(c2);
        // D / c^2 * length * friction factor
        Amount Dc2ll = diameter.divide(c2.times(friction).times(getLength()));
        // gh'D / (c^4 * friction)
        Amount ghDc4l = computeSlope().times(Constants.g).times(diameter).divide(c4).divide(friction);
        Amount result = crossSection.times(crossSection).times(Dc2ll.plus(ghDc4l)).to(METER.pow(2).times(SECOND.pow(2)));
        return result;
    }

    /*
    // in m^3 / s
    public Amount computeTimelessHoeferCoefficient(Amount<Velocity> c, Amount volumetricFlow) {
        Amount<Area> crossSection = computeCrossSection();
        // lambda
        Amount friction = computeHoeferFrictionFactor(volumetricFlow);
        // c^2
        Amount c2 = c.times(c);
        // c^4
        Amount c4 = c2.times(c2);
        // D / c^2 * length * friction factor
        Amount Dc2ll = diameter.divide(c2.times(friction).times(getLength()));
        // gh'D / (c^4 * friction)
        Amount ghDc4l = computeSlope().times(Constants.g).times(diameter).divide(c4).divide(friction);
        Amount result = crossSection.times(crossSection).times(Dc2ll.plus(ghDc4l)).to(METER.pow(2).times(SECOND.pow(2)));
        return result;
    } */
    public Amount computeStartpointPressure(Amount<Velocity> c, Amount<Pressure> start, Amount flow) {
        Amount beta = computeTimelessCoefficient(c);
        beta = beta.times(Amount.valueOf("3600 s")).times(Amount.valueOf("3600 s"));
        Amount pi = start.pow(2);
        //System.out.println(beta);
        //System.out.println(flow.pow(2).divide(beta));
        //System.out.println(pi);
        //System.out.println(flow.pow(2).divide(beta).minus(pi).abs().sqrt().to(BAR));
        return flow.pow(2).divide(beta).plus(pi).abs().sqrt().to(BAR);
    }

    /*
    public Amount computeStartpointPressureH(Amount<Velocity> c, Amount<Pressure> start, Amount flow) {        
        Amount beta = computeTimelessHoeferCoefficient(c, flow.divide(Amount.valueOf(1, HOUR)).divide(Amount.valueOf(0.82, KILOGRAM.divide(METER.pow(3)))));
        beta = beta.times(Amount.valueOf("3600 s")).times(Amount.valueOf("3600 s"));
        Amount pi = start.pow(2);
        //System.out.println(beta);
        //System.out.println(flow.pow(2).divide(beta));
        //System.out.println(pi);
        //System.out.println(flow.pow(2).divide(beta).minus(pi).abs().sqrt().to(BAR));
        return flow.pow(2).divide(beta).plus(pi).abs().sqrt().to(BAR);
    } */
    public Amount computeEndpointPressure(Amount<Velocity> c, Amount<Pressure> start, Amount flow) {
        Amount beta = computeTimelessCoefficient(c);
        beta = beta.times(Amount.valueOf("3600 s")).times(Amount.valueOf("3600 s"));
        Amount pi = start.pow(2);
        //System.out.println(beta);
        //System.out.println(flow.pow(2).divide(beta));
        //System.out.println(pi);
        //System.out.println(flow.pow(2).divide(beta).minus(pi).abs().sqrt().to(BAR));
        return flow.pow(2).divide(beta).minus(pi).abs().sqrt().to(BAR);
    }

    /*
    public Amount computeEndpointPressureH(Amount<Velocity> c, Amount<Pressure> start, Amount flow) {        
        Amount beta = computeTimelessHoeferCoefficient(c, flow.divide(Amount.valueOf(1, HOUR)).divide(Amount.valueOf(0.82, KILOGRAM.divide(METER.pow(3)))));
        beta = beta.times(Amount.valueOf("3600 s")).times(Amount.valueOf("3600 s"));
        Amount pi = start.pow(2);
        //System.out.println(beta);
        //System.out.println(flow.pow(2).divide(beta));
        //System.out.println(pi);
        //System.out.println(flow.pow(2).divide(beta).minus(pi).abs().sqrt().to(BAR));
        return flow.pow(2).divide(beta).minus(pi).abs().sqrt().to(BAR);
    } */
    public Amount computeMassFlow(Amount<Pressure> start, Amount<Pressure> end, Amount<Duration> duration) {
        return null;
    }

    public Amount computePiDifference(Amount massFlow) {
        return null;
    }

    public Amount<Length> getDiameter() {
        return diameter;
    }

    public Amount<HeatTransferCoefficient> getHeatTransferCoefficient() {
        return heatTransferCoefficient;
    }

    public Amount<Length> getLength() {
        return length;
    }

    public void setLength(Amount<Length> length) {
        this.length = length;
    }

    public Amount<Pressure> getPressureMax() {
        return pressureMax;
    }

    public Amount<Length> getRoughness() {
        return roughness;
    }

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("pipe");
    }

    @Override
    protected void parseProperties() {
        super.parseProperties();
        diameter = getProperties().get("diameter").getAmount();
        XMLProperty pHeatTransferCoefficient = getProperties().get("heatTransferCoefficient");
        if (pHeatTransferCoefficient.getUnit().equals("W_per_m_square_per_K")
                || pHeatTransferCoefficient.getUnit().equals("m/m")) {
            pHeatTransferCoefficient.setUnit("W/m^2/K");
        } else {
            throw new AssertionError("Heat transfer coefficient unit unknown: " + pHeatTransferCoefficient.getUnit());
        }
        heatTransferCoefficient = pHeatTransferCoefficient.getAmount();
        length = getProperties().get("length").getAmount();
        if (getProperties().containsKey("pressureMax")) {
            pressureMax = getProperties().get("pressureMax").getAmount();
        } else {
            pressureMax = null;
        }
        roughness = getProperties().get("roughness").getAmount();
    }

    public void createProperties() {
        if (!getProperties().containsKey("flowMin")) {
            XMLProperty property = new XMLProperty("flowMin", "1000m_cube_per_hour", "-10000");
            properties.put(property.getName(), property);
        }
        if (!getProperties().containsKey("flowMax")) {
            XMLProperty property = new XMLProperty("flowMax", "1000m_cube_per_hour", "10000");
            properties.put(property.getName(), property);
        }
        if (!getProperties().containsKey("length")) {
            XMLProperty property = new XMLProperty("length", length.getUnit().toString(), length.doubleValue(length.getUnit()) + "");
            properties.put(property.getName(), property);
        }
        if (!getProperties().containsKey("diameter")) {
            XMLProperty property = new XMLProperty("diameter", diameter.getUnit().toString(), diameter.doubleValue(diameter.getUnit()) + "");
            properties.put(property.getName(), property);
        }
        if (!getProperties().containsKey("roughness")) {
            XMLProperty property = new XMLProperty("roughness", roughness.getUnit().toString(), roughness.doubleValue(roughness.getUnit()) + "");
            properties.put(property.getName(), property);
        }
        if (!getProperties().containsKey("pressureMax")) {
            XMLProperty property = new XMLProperty("pressureMax", pressureMax.getUnit().toString(), pressureMax.doubleValue(pressureMax.getUnit()) + "");
            properties.put(property.getName(), property);
        }
        if (!getProperties().containsKey("heatTransferCoefficient")) {
            XMLProperty property = new XMLProperty("heatTransferCoefficient", "W_per_m_square_per_K", heatTransferCoefficient.doubleValue(heatTransferCoefficient.getUnit()) + "");
            properties.put(property.getName(), property);
        }
    }
}
