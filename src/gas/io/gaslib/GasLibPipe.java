/*
 * GasLibPipe.java
 * 
 * 
 */
package gas.io.gaslib;

import gas.io.Pipe;
import gas.io.XMLProperty;
import gas.quantity.HeatTransferCoefficient;
import units.UnitsTools;
import units.qual.*;

/**
 *
 * @author Martin Groß
 */
public class GasLibPipe extends GasLibConnection implements Pipe {

    private double diameter;
    private double heatTransferCoefficient;
    private double length;
    private double pressureMax;
    private double roughness;

    public GasLibPipe() {
        super();
    }

    public GasLibPipe(String id, GasLibIntersection start, GasLibIntersection end, String startId, String endId,
            double length) {
        super();
        this.id = id;
        from = start;
        fromId = startId;
        to = end;
        toId = endId;
        this.diameter = 500 * UnitsTools.mm;
        this.roughness = 0.05 * UnitsTools.mm;
        this.length = length;
        heatTransferCoefficient = 2 * UnitsTools.W/UnitsTools.m/UnitsTools.m/UnitsTools.K;
        pressureMax = 200 * UnitsTools.bar;
    }

    public double computeCrossSection() {
        return diameter * diameter * (Math.PI / 4);
    }

    public double computeSiamCoefficient(GasLibNetworkFile networkFile) {
        double lambda = getLength()*-1.0*computeNikuradseFrictionFactor()*networkFile.getMeanSpecificGasConstant()
                *computePapayCompressibilityFactor(networkFile)*networkFile.getMeanTemperature()
                /computeCrossSection()/computeCrossSection()/getDiameter();
        return lambda;
    }

    public double computeNikuradseFrictionFactor() {
        double diameterM = diameter;
        double roughnessM = roughness;
        double relativeRoughness = roughnessM / diameterM;
        double frictionFactor = Math.pow(1.138 - 2 * Math.log10(relativeRoughness), -2);
        return frictionFactor;
    }

    public @bar double getMeanPressure() {
        double minOfMax = Math.min(getTo().getPressureMax(), getFrom().getPressureMax());
        double maxOfMin = Math.max(getFrom().getPressureMin(), getTo().getPressureMin());
        return 0.5 * (minOfMax + maxOfMin) * UnitsTools.bar;
    }

    public double computePapayCompressibilityFactor(GasLibNetworkFile constants) {
        double tr = constants.getMeanReducedTemperature();
        double pr = getMeanPressure()/constants.getMeanPseudocriticalPressure();
        double z = 1 - 3.52 * pr * Math.exp(-2.26 * tr) + 0.247 * pr * pr * Math.exp(-1.878 * tr);
        return z;
    }

    public double computeSlope() {
        return (getTo().getHeight() - getFrom().getHeight())/getLength();
    }

    public double computeTimelessCoefficient(@mPERs double c) {
        double crossSection = computeCrossSection();
        // lambda
        double friction = computeNikuradseFrictionFactor();
        // c^2
        double c2 = c*c;
        // c^4
        double c4 = c2*c2;
        // D / c^2 * length * friction factor
        double Dc2ll = UnitsTools.mm_to_m(diameter)/(c2*friction*UnitsTools.mm_to_m(getLength()));
        // gh'D / (c^4 * friction)
        double ghDc4l = computeSlope()*UnitsTools.gravity*UnitsTools.mm_to_m(diameter)/c4/friction;
        double result = crossSection/crossSection*Dc2ll + ghDc4l;
        return result;
    }

    public double computeStartpointPressure(double c, double start, double flow) {
        double beta = computeTimelessCoefficient(c);
        beta = beta * (3600 * UnitsTools.s) * (3600 * UnitsTools.s);
        double pi = start*start;
        return ((flow*flow/beta) + Math.sqrt(pi));
    }

    public double computeEndpointPressure(double c, double start, double flow) {
        double beta = computeTimelessCoefficient(c);
        beta = beta * (3600 * UnitsTools.s) * (3600 * UnitsTools.s);
        double pi = start*start;
        return ((flow*flow/beta) - Math.sqrt(pi));
    }

    public double computeMassFlow(double start, double end, double duration) {
        return 0;
    }

    public double computePiDifference(double massFlow) {
        return 0;
    }

    public double getDiameter() {
        return diameter;
    }

    public double getHeatTransferCoefficient() {
        return heatTransferCoefficient;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getPressureMax() {
        return pressureMax;
    }

    public double getRoughness() {
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
            pressureMax = 0;
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
            XMLProperty property = new XMLProperty("length", "meter", length + "");
            properties.put(property.getName(), property);
        }
        if (!getProperties().containsKey("diameter")) {
            XMLProperty property = new XMLProperty("diameter", "meter", diameter + "");
            properties.put(property.getName(), property);
        }
        if (!getProperties().containsKey("roughness")) {
            XMLProperty property = new XMLProperty("roughness", "meter", roughness + "");
            properties.put(property.getName(), property);
        }
        if (!getProperties().containsKey("pressureMax")) {
            XMLProperty property = new XMLProperty("pressureMax", "bar", pressureMax + "");
            properties.put(property.getName(), property);
        }
        if (!getProperties().containsKey("heatTransferCoefficient")) {
            XMLProperty property = new XMLProperty("heatTransferCoefficient", "W_per_m_square_per_K", heatTransferCoefficient + "");
            properties.put(property.getName(), property);
        }
    }
}
