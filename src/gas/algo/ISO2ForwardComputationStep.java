/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gas.algo;

import algorithm.Algorithm;
import ds.graph.GasEdge;
import ds.graph.GasNode;
import ds.graph.Graph;
import ds.graph.IdentifiableAmountMapping;
import gas.ds.GasFlow;
import gas.ds.ForwardComputationStep;
import gas.problem.SourceSinkForwardComputationProblem;
import gas.quantity.EdgeConstant;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Mass;
import javax.measure.quantity.MassFlowRate;
import javax.measure.quantity.Pressure;
import static javax.measure.unit.NonSI.BAR;
import javax.measure.unit.SI;
import static javax.measure.unit.SI.KILOGRAM;
import javax.measure.unit.Unit;
import javolution.lang.MathLib;
import org.jscience.physics.amount.Amount;

/**
 *
 * @author Martin
 */
public class ISO2ForwardComputationStep extends Algorithm<SourceSinkForwardComputationProblem, ForwardComputationStep> {

    static final double DOUBLE_RELATIVE_ERROR = MathLib.pow(2, -53);

    static final double DECREMENT = (1.0 - DOUBLE_RELATIVE_ERROR);

    static final double INCREMENT = (1.0 + DOUBLE_RELATIVE_ERROR);
    
    

    protected Amount sgnSqrt(Amount amount) {
        Unit unit = amount.getUnit();
        double oldMax = amount.getMaximumValue();
        double oldMin = amount.getMinimumValue();

        if (oldMin < 0) {
            oldMin = -oldMin;
        }
        double min = MathLib.sqrt(oldMin);
        if (oldMin < 0) {
            min = -min;
        }
        min = (min < 0) ? min * INCREMENT : min * DECREMENT;

        if (oldMax < 0) {
            oldMax = -oldMax;
        }
        double max = MathLib.sqrt(oldMax);
        if (oldMax < 0) {
            max = -max;
        }
        max = (max < 0) ? max * DECREMENT : max * INCREMENT;

        if (max < min) {
            double b = min;
            min = max;
            max = b;
        }
        
        return Amount.valueOf((max + min) / 2.0 , (max - min) / 2.0, unit.root(2));
    }
    
    @Override
    protected ForwardComputationStep runAlgorithm(SourceSinkForwardComputationProblem problem) {
        Graph<GasNode, GasEdge> network = problem.getNetwork();
        ForwardComputationStep flow = new ForwardComputationStep(problem.getNetwork());
        GasFlow currentFlow = flow.getNextGasFlow();    

        IdentifiableAmountMapping<GasNode, Pressure> pressures = problem.getInitialPressures();
        IdentifiableAmountMapping<GasEdge, EdgeConstant> edgeConstants = problem.getEdgeConstants();
        IdentifiableAmountMapping<GasEdge, MassFlowRate> massFlowRates = currentFlow.getMassFlowRates();
        IdentifiableAmountMapping<GasNode, MassFlowRate> massFlowRateDeltas = new IdentifiableAmountMapping<>(network.nodes());
        IdentifiableAmountMapping<GasNode, Pressure> newPressures = currentFlow.getPressures();

        for (GasEdge edge : network.edges()) {
            Amount edgeConstant = edgeConstants.get(edge);
            Amount<Pressure> pressureStart = pressures.get(edge.start());
            Amount<Pressure> pressureEnd = pressures.get(edge.end());

            Amount pressureStartSq = pressureStart.pow(2);

            Amount pressureEndSq = pressureEnd.pow(2);
            Amount massFlowRateSq = pressureStartSq.minus(pressureEndSq).times(edgeConstant);
            Amount<MassFlowRate> massFlowRate = sgnSqrt(massFlowRateSq);
            massFlowRates.set(edge, massFlowRate);
        }

        for (GasNode node : network.nodes()) {
            //System.out.println("Step: Processing node " + node.id());
            Amount<MassFlowRate> massFlowRateDelta = Amount.valueOf(0, MassFlowRate.UNIT);
            for (GasEdge edge : network.incomingEdges(node)) {
                massFlowRateDelta = massFlowRateDelta.plus(massFlowRates.get(edge));
            }
            for (GasEdge edge : network.outgoingEdges(node)) {
                massFlowRateDelta = massFlowRateDelta.minus(massFlowRates.get(edge));
            }
            massFlowRateDeltas.set(node, massFlowRateDelta);
        }

        Amount<Duration> timeStep = problem.getTimeStep();

        for (GasNode node : network.nodes()) {
            //System.out.println(problem.getSpeedOfSound().getRelativeError());
            Amount<Mass> gasMass = pressures.get(node).divide(problem.getSpeedOfSound().pow(2)).times(node.getVolume()).to(KILOGRAM);
            //System.out.println("m O: " + gasMass.getRelativeError());
            Amount<MassFlowRate> massFlowRateDelta = massFlowRateDeltas.get(node);
            Amount newDensity = gasMass.plus(massFlowRateDelta.times(timeStep)).divide(node.getVolume());

            Amount newPressure = newDensity.times(problem.getSpeedOfSound().pow(2)).to(BAR);
            //System.out.println("p^2 N: " + newPressure.getRelativeError());
            newPressures.set(node, newPressure);
        }

        return flow;
    }
}
