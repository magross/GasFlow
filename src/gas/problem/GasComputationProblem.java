/**
 * GasComputationProblem.java
 *
 */

package gas.problem;

import units.UnitsTools;

/**
 *
 * @author Martin Groß
 */
public class GasComputationProblem {

    /**
     * Molar mass of the gas mixture.
     */
    public static final double M = 0.0183 * UnitsTools.kg/UnitsTools.mol;
    /**
     * Constant Temperature (T = 288.15K).
     */
    public static final double T = 288.15 * UnitsTools.K;
    /**
     * Constant Compressibility Factor (z = 0.9).
     */
    public static final double z = 0.9;

    /**
     * Specific gas constant.
     */
    public static final double Rs = UnitsTools.R/M;
    /**
     * Speed of sound.
     */
    public static final double c = Math.sqrt(z*Rs*T);
    /**
     * Speed of sound squared.
     */
    public static final double c2 = z*Rs*T;

    public static double computeFrictionFactor(double roughness, double diameter) {
        double d = roughness/diameter;
        return 1/((1.14 - 2*Math.log10(d))*(1.14 - 2*Math.log10(d)));
    }
}
