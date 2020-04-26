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
import units.UnitsTools;

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

    private static final double c = 351.521085 * UnitsTools.m/UnitsTools.s;
    private static final double pMinEntry1 = 60.0 * UnitsTools.bar;
    private static final double pMaxEntry1 = 64.0 * UnitsTools.bar;
    private static final double pMinExit1 = 59.98 * UnitsTools.bar;
    private static final double pMaxExit1 = 59.992 * UnitsTools.bar;
    private static final double pMinExit2 = 59.96 * UnitsTools.bar;
    private static final double pMaxExit2 = 59.999 * UnitsTools.bar;
    private static final double f1 = flow1[index] * 820 * UnitsTools.kg;
    private static final double f2 = flow2[index] * 820 * UnitsTools.kg;
    private static final double f3 = flow3[index] * 820 * UnitsTools.kg;
}
