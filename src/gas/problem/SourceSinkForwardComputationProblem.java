/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gas.problem;

import ds.graph.GasEdge;
import ds.graph.GasNode;
import ds.graph.Graph;
import ds.graph.IdentifiableAmountMapping;
import gas.quantity.EdgeConstant;
import static java.lang.Math.PI;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Length;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Velocity;
import javax.measure.quantity.Volume;
import static javax.measure.unit.NonSI.BAR;
import javax.measure.unit.SI;
import static javax.measure.unit.SI.CUBIC_METRE;
import static javax.measure.unit.SI.GRAM;
import static javax.measure.unit.SI.KELVIN;
import static javax.measure.unit.SI.METER;
import static javax.measure.unit.SI.MOLE;
import static javax.measure.unit.SI.PASCAL;
import static javax.measure.unit.SI.SECOND;
import javax.measure.unit.Unit;
import static javax.measure.unit.Unit.ONE;
import org.jscience.physics.amount.Amount;
import static org.jscience.physics.amount.Constants.R;
import static org.jscience.physics.amount.Constants.g;

/**
 *
 * @author Martin
 */
public class SourceSinkForwardComputationProblem {

    /**
     * Compressibility Factor (z)
     */
    private Amount<Dimensionless> compressibilityFactor;
    /**
     * Dynamic Viscosity (eta in Pa s)
     */
    private Amount<?> dynamicViscosity;
    
    private IdentifiableAmountMapping<GasEdge, EdgeConstant> edgeConstants;
    
       
    private IdentifiableAmountMapping<GasNode, Pressure> initialPressures;
    /**
     * Molar mass of the gas mixture. (M)
     */
    private Amount<?> molarMass;
    /**
     * The network
     */
    private Graph<GasNode, GasEdge> network;
    
    private GasNode sink;
    private GasNode source;
    
    private Amount<Pressure> sourcePressure;
    private Amount<Pressure> sinkPressure;    
    /**
     * Specific gas constant (R_s)
     */
    private volatile Amount<?> specificGasConstant;
    /**
     * Speed of sound (c)
     */
    private Amount<Velocity> speedOfSound;
    /**
     * Temperature (T)
     */
    private Amount<Temperature> temperature;
    /**
     *
     */
    private Amount<Duration> timeStep;    

    public SourceSinkForwardComputationProblem() {
        sourcePressure = Amount.valueOf(10, BAR);
        sinkPressure = Amount.valueOf(1, BAR);        
        compressibilityFactor = Amount.valueOf(0.9, ONE);
        dynamicViscosity = Amount.valueOf(0.000011, PASCAL.times(SECOND));
        molarMass = Amount.valueOf(18.3, GRAM.divide(MOLE));
        temperature = Amount.valueOf(288.15, KELVIN);
        specificGasConstant = R.divide(molarMass);
        speedOfSound = (Amount<Velocity>) compressibilityFactor
                .times(specificGasConstant)
                .times(temperature)
                .to(SI.METERS_PER_SECOND.pow(2)).sqrt();
        /****/
        speedOfSound = Amount.valueOf(speedOfSound.getEstimatedValue(), 0.0, speedOfSound.getUnit());
        timeStep = Amount.valueOf(1, SECOND);
    }

    protected Amount<Dimensionless> computeFrictionFactor(Amount<Length> roughness, Amount<Length> diameter) {
        Unit unit = Unit.ONE;
        double d = roughness.divide(diameter).doubleValue(unit);
        return (Amount<Dimensionless>) Amount.ONE.divide(Amount.valueOf(1.14 - 2*Math.log10(d), Unit.ONE).pow(2));
    }    

    protected Amount<EdgeConstant> computeEdgeCoefficient(GasEdge edge) {
        Amount<Length> diameter = edge.getDiameter();
        Amount<Length> length = edge.getLength();
        Amount<Dimensionless> slope = edge.getSlope();
        Amount<Length> roughness = edge.getRoughness();
        Amount<Dimensionless> frictionFactor = computeFrictionFactor(roughness, diameter);
        Amount term1 = g
                .times(slope)
                .times(diameter)
                .divide(getSpeedOfSound().pow(4))
                .divide(frictionFactor);
        Amount term2 = diameter
                .divide(getSpeedOfSound().pow(2))
                .divide(frictionFactor)
                .divide(length);        
        Amount term3 = term1.plus(term2).to(SECOND.pow(2).divide(METER.pow(2)));
        Amount term4 = term3.times(diameter.pow(4)).times(PI*PI/16.0);        
        return term4;
    };
    
    protected Amount<EdgeConstant> computeExactEdgeCoefficient(GasEdge edge, double precision) {
        Amount<Length> diameter = edge.getDiameter();
        Amount<Length> length = edge.getLength();
        Amount<Dimensionless> slope = edge.getSlope();
        Amount<Length> roughness = edge.getRoughness();
        Amount<Dimensionless> frictionFactor = computeFrictionFactor(roughness, diameter);
        Amount term1 = g
                .times(slope)
                .times(diameter)
                .divide(getSpeedOfSound().pow(4))
                .divide(frictionFactor);
        Amount term2 = diameter
                .divide(getSpeedOfSound().pow(2))
                .divide(frictionFactor)
                .divide(length);        
        Amount term3 = term1.plus(term2).to(SECOND.pow(2).divide(METER.pow(2)));
        Amount term4 = term3.times(diameter.pow(4)).times(PI*PI/16.0);        
        /****/
        return Amount.valueOf(term4.getEstimatedValue(), precision, term4.getUnit());
    };    
    
    protected void updateConstants() {
        specificGasConstant = R.divide(molarMass);
        speedOfSound = (Amount<Velocity>) compressibilityFactor
                .times(specificGasConstant)
                .times(temperature)
                .to(SI.METERS_PER_SECOND.pow(2)).sqrt();
        /****/
        speedOfSound = Amount.valueOf(speedOfSound.getEstimatedValue(), 0.0, speedOfSound.getUnit());
    }

    /****/
    public void assumeExactPressures() {
        for (GasNode node : network.nodes()) {
            initialPressures.set(node, Amount.valueOf(initialPressures.get(node).getEstimatedValue(), initialPressures.get(node).getUnit()));
            //System.out.println(initialPressures.get(node));
        }
    }
    
    public Amount<Dimensionless> getCompressibilityFactor() {
        return compressibilityFactor;
    }

    public void setCompressibilityFactor(Amount<Dimensionless> compressibilityFactor) {
        this.compressibilityFactor = compressibilityFactor;
        updateConstants();
    }

    public Amount<?> getDynamicViscosity() {
        return dynamicViscosity;
    }

    @Deprecated
    public void setDynamicViscosity(Amount<?> dynamicViscosity) {
        this.dynamicViscosity = dynamicViscosity;
    }    
    
    public IdentifiableAmountMapping<GasEdge, EdgeConstant> getEdgeConstants() {
        return edgeConstants;
    }    
    
    public IdentifiableAmountMapping<GasNode, Pressure> getInitialPressures() {
        return initialPressures;
    }

    public void setInitialPressures(IdentifiableAmountMapping<GasNode, Pressure> initialPressures) {
        this.initialPressures = initialPressures;
    }    
    
    public Amount<?> getMolarMass() {
        return molarMass;
    }

    public void setMolarMass(Amount<?> molarMass) {
        this.molarMass = molarMass;
        updateConstants();
    }

    public Graph<GasNode, GasEdge> getNetwork() {
        return network;
    }
    
    public void setNetwork(Graph<GasNode, GasEdge> network) {
        this.network = network;
        edgeConstants = new IdentifiableAmountMapping<>(network.edges());
        initialPressures = new IdentifiableAmountMapping<>(network.nodes());
        for (GasNode node : network.nodes()) {
            initialPressures.set(node, Amount.valueOf(1, BAR));
            Amount<Volume> volume = Amount.valueOf(0, CUBIC_METRE);
            for (GasEdge edge : network.incidentEdges(node)) {
                volume = volume.plus(edge.getHalfVolume());
            }
            node.setVolume(volume);
        }
        for (GasEdge edge : network.edges()) {
            edgeConstants.set(edge, computeExactEdgeCoefficient(edge, 0.0));            
        }
    }    
    
    @Deprecated
    public Amount<?> getReynoldsNumber(Amount<Length> diameter, Amount<Velocity> velocity) {
        return diameter.times(velocity).divide(dynamicViscosity);
    }

    public Amount<?> getSpecificGasConstant() {
        return specificGasConstant;
    }

    public Amount<Velocity> getSpeedOfSound() {
        return speedOfSound;
    }    
    
    public Amount<Temperature> getTemperature() {
        return temperature;
    }

    public void setTemperature(Amount<Temperature> temperature) {
        this.temperature = temperature;
        updateConstants();
    }

    public Amount<Duration> getTimeStep() {
        return timeStep;
    }

    public void setTimeStep(Amount<Duration> timeStep) {
        this.timeStep = timeStep;
    }

    public GasNode getSink() {
        return sink;
    }

    public void setSink(GasNode sink) {
        this.sink = sink;
    }

    public GasNode getSource() {
        return source;
    }

    public void setSource(GasNode source) {
        this.source = source;
    }

    public Amount<Pressure> getSourcePressure() {
        return sourcePressure;
    }

    public void setSourcePressure(Amount<Pressure> sourcePressure) {
        this.sourcePressure = sourcePressure;
    }

    public Amount<Pressure> getSinkPressure() {
        return sinkPressure;
    }

    public void setSinkPressure(Amount<Pressure> sinkPressure) {
        this.sinkPressure = sinkPressure;
    }
    
    
}
