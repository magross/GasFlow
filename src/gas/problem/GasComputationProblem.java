/**
 * GasComputationProblem.java
 *
 */

package gas.problem;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Length;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Velocity;
import javax.measure.unit.SI;
import static javax.measure.unit.SI.GRAM;
import static javax.measure.unit.SI.KELVIN;
import static javax.measure.unit.SI.MOLE;
import javax.measure.unit.Unit;
import static javax.measure.unit.Unit.ONE;
import org.jscience.physics.amount.Amount;
import static org.jscience.physics.amount.Constants.R;

/**
 *
 * @author Martin Groß
 */
public class GasComputationProblem {

    /**
     * Molar mass of the gas mixture.
     */
    public static final Amount M = Amount.valueOf(18.3, GRAM.divide(MOLE));
    /**
     * Constant Temperature (T = 288.15K).
     */
    public static final Amount<Temperature> T = Amount.valueOf(288.15, KELVIN);
    /**
     * Constant Compressibility Factor (z = 0.9).
     */
    public static final Amount<Dimensionless> z = Amount.valueOf(0.9, ONE);

    /**
     * Specific gas constant.
     */
    public static final Amount Rs = R.divide(M);
    /**
     * Speed of sound.
     */
    public static final Amount<Velocity> c = (Amount<Velocity>) z.times(Rs).times(T).to(SI.METERS_PER_SECOND.pow(2)).sqrt();
    /**
     * Speed of sound squared.
     */
    public static final Amount c2 = z.times(Rs).times(T).to(SI.METERS_PER_SECOND.pow(2));

    public static Amount<Dimensionless> computeFrictionFactor(Amount<Length> roughness, Amount<Length> diameter) {
        Unit unit = Unit.ONE;
        double d = roughness.divide(diameter).doubleValue(unit);
        return (Amount<Dimensionless>) Amount.ONE.divide(Amount.valueOf(1.14 - 2*Math.log10(d), Unit.ONE).pow(2));
    }
}
