/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gas.algo;

import gas.io.gaslib.GasLibConnection;
import gas.io.gaslib.GasLibNetworkFile;
import gas.io.gaslib.GasLibPipe;
import gas.io.gaslib.GasLibScenario;
import gas.io.gaslib.GasLibScenarioFile;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Velocity;
import static javax.measure.unit.NonSI.BAR;
import static javax.measure.unit.NonSI.HOUR;
import javax.measure.unit.SI;
import static javax.measure.unit.SI.KILOGRAM;
import static javax.measure.unit.SI.METER;
import static javax.measure.unit.SI.SECOND;
import org.jscience.physics.amount.Amount;

/**
 *
 * @author Martin
 */
public class Hoefer {

    private static final int index = 6;

    private static final double[] flow1 = {
        54, 46.32552272, 34.81896375, 28.66, 46.435, 41.992, 32.386
    };

    private static final double[] flow2 = {
        2, 10.07839714, 22.19056448, 28.66, 9.963, 14.640, 24.752
    };

    private static final double[] flow3 = {
        52, 36.24712558, 12.62839927, 0, 36.472, 27.352, 7.634
    };

    private static final Amount<Velocity> c = (Amount<Velocity>) Amount.valueOf("351.521085 m/s");
    private static final Amount<Pressure> pMinEntry1 = (Amount<Pressure>) Amount.valueOf(60.0, BAR);
    private static final Amount<Pressure> pMaxEntry1 = (Amount<Pressure>) Amount.valueOf(64.0, BAR);
    private static final Amount<Pressure> pMinExit1 = (Amount<Pressure>) Amount.valueOf(59.98, BAR);
    private static final Amount<Pressure> pMaxExit1 = (Amount<Pressure>) Amount.valueOf(59.992, BAR);
    private static final Amount<Pressure> pMinExit2 = (Amount<Pressure>) Amount.valueOf(59.96, BAR);
    private static final Amount<Pressure> pMaxExit2 = (Amount<Pressure>) Amount.valueOf(59.999, BAR);
    private static final Amount f1 = Amount.valueOf(flow1[index] * 820, KILOGRAM);
    private static final Amount f2 = Amount.valueOf(flow2[index] * 820, KILOGRAM);
    private static final Amount f3 = Amount.valueOf(flow3[index] * 820, KILOGRAM);

    /*
    public static void main(String[] args) {
        boolean HOEFER = false;
        GasLibNetworkFile networkFile = new GasLibNetworkFile("D://git/TRR154-dev/data/d1/stationaryFeasTest/treeOneEntryTwoExits.net");
            Amount<Pressure> middleMin1 = null;
            Amount<Pressure> middleMax1 = null;
        for (GasLibConnection con : networkFile.getConnections().getMap().values()) {
            GasLibPipe pipe = ((GasLibPipe) con);
            Amount beta = pipe.computeTimelessCoefficient(c);
            Amount betaH = pipe.computeTimelessHoeferCoefficient(c, Amount.valueOf(15, METER.pow(3).divide(SECOND)));

            //System.out.println(pipe.getId());       
            //System.out.println("Friction");
            Amount lambda = pipe.computeNikuradseFrictionFactor();
            Amount lambdaH1 = pipe.computeHoeferFrictionFactor(Amount.valueOf(15, METER.pow(3).divide(SECOND)));
            Amount lambdaH2 = pipe.computeHoeferFrictionFactor(Amount.valueOf(0.1, METER.pow(3).divide(SECOND)));
            //System.out.println(lambda.minus(lambdaH1));
            //System.out.println(lambda.minus(lambdaH2));

            if (!HOEFER) {
                
            if (pipe.getId().equals("Entry1_Innode")) {
                middleMin1 = pipe.computeEndpointPressure(c, pMinEntry1, f1);
                middleMax1 = pipe.computeEndpointPressure(c, pMaxEntry1, f1);
                System.out.println("Middle Node: [" + middleMin1.doubleValue(BAR) + "," + middleMax1.doubleValue(BAR) + "] bar");
            }

            if (pipe.getId().equals("Innode_Exit1")) {
                Amount<Pressure> middleMin2 = pipe.computeStartpointPressure(c, pMinExit1, f2);
                Amount<Pressure> middleMax2 = pipe.computeStartpointPressure(c, pMaxExit1, f2);
                System.out.println("Middle Node: [" + middleMin2.doubleValue(BAR) + "," + middleMax2.doubleValue(BAR) + "] bar");
                System.out.println(middleMax2.minus(middleMin1));
            }

            if (pipe.getId().equals("Innode_Exit2")) {
                Amount<Pressure> middleMin3 = pipe.computeStartpointPressure(c, pMinExit2, f3);
                Amount<Pressure> middleMax3 = pipe.computeStartpointPressure(c, pMaxExit2, f3);
                System.out.println("Middle Node: [" + middleMin3.doubleValue(BAR) + "," + middleMax3.doubleValue(BAR) + "] bar");
                System.out.println(middleMax3.minus(middleMin1));
            }

            } else {
            if (pipe.getId().equals("Entry1_Innode")) {
                middleMin1 = pipe.computeEndpointPressureH(c, pMinEntry1, f1);
                middleMax1 = pipe.computeEndpointPressureH(c, pMaxEntry1, f1);
                System.out.println("Middle Node: [" + middleMin1.doubleValue(BAR) + "," + middleMax1.doubleValue(BAR) + "] bar");
            }

            if (pipe.getId().equals("Innode_Exit1")) {
                Amount<Pressure> middleMin2 = pipe.computeStartpointPressureH(c, pMinExit1, f2);
                Amount<Pressure> middleMax2 = pipe.computeStartpointPressureH(c, pMaxExit1, f2);
                System.out.println("Middle Node: [" + middleMin2.doubleValue(BAR) + "," + middleMax2.doubleValue(BAR) + "] bar");
                System.out.println(middleMax2.minus(middleMin1));
            }

            if (pipe.getId().equals("Innode_Exit2")) {
                Amount<Pressure> middleMin3 = pipe.computeStartpointPressureH(c, pMinExit2, f3);
                Amount<Pressure> middleMax3 = pipe.computeStartpointPressureH(c, pMaxExit2, f3);
                System.out.println("Middle Node: [" + middleMin3.doubleValue(BAR) + "," + middleMax3.doubleValue(BAR) + "] bar");
                System.out.println(middleMax3.minus(middleMin1));
            }            
            }
            //System.out.println("s");
            //System.out.println(beta);
            //System.out.println(betaH);
            //System.out.println(beta.minus(betaH));            
            //beta = beta.times(Amount.valueOf("3600 s")).times(Amount.valueOf("3600 s"));
            //betaH = betaH.times(Amount.valueOf("3600 s")).times(Amount.valueOf("3600 s"));
            //System.out.println("h");
            //System.out.println(beta);
            //System.out.println(betaH);
            //System.out.println(beta.minus(betaH));

            //System.out.println();
            //beta = beta.to(METER.pow(2).times(SECOND.pow(4)));
            //System.out.println(beta);
        }

        GasLibScenarioFile scenarioFile = new GasLibScenarioFile("D://git/TRR154-dev/data/d1/stationaryFeasTest/twoExits1.scn");
        GasLibScenario scenario = scenarioFile.getScenarios().values().iterator().next();
    }
*/
}
