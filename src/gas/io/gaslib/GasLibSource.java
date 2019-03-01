/**
 * GasLibSource.java
 *
 */
package gas.io.gaslib;

import gas.io.XMLProperty;
import gas.quantity.CalorificValue;
import gas.quantity.MolarMass;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.VolumetricDensity;
import javax.measure.unit.SI;
import org.jscience.physics.amount.Amount;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Martin Groß
 */
public class GasLibSource extends GasLibTerminalNode {

    private Amount<CalorificValue> calorificValue;
    private Amount<Temperature> gasTemperature;
    private Amount<Dimensionless> heatCapacityA;
    private Amount<Dimensionless> heatCapacityB;
    private Amount<Dimensionless> heatCapacityC;
    private Amount<MolarMass> molarMass;
    private Amount<VolumetricDensity> normDensity;
    private Amount<Pressure> pseudocriticalPressure;
    private Amount<Temperature> pseudocriticalTemperature;

    public GasLibSource() {
        super();
        calorificValue = (Amount<CalorificValue>) Amount.valueOf("36.4543670654 MJ/m^3");
        gasTemperature = (Amount<Temperature>) Amount.valueOf("0 C");
        heatCapacityA = (Amount<Dimensionless>) Amount.valueOf(31.8251781464, Dimensionless.UNIT);
        heatCapacityB = (Amount<Dimensionless>) Amount.valueOf(-0.00846800766885, Dimensionless.UNIT);
        heatCapacityC = (Amount<Dimensionless>) Amount.valueOf(7.44647331885e-05, Dimensionless.UNIT);
        molarMass = (Amount<MolarMass>) Amount.valueOf("18.5674 g/mol");
        normDensity = (Amount<VolumetricDensity>) Amount.valueOf("0.785 kg/m^3");
        pseudocriticalPressure = (Amount<Pressure>) Amount.valueOf("45.9293457336 bar");
        pseudocriticalTemperature = (Amount<Temperature>) Amount.valueOf("188.549758911 K");
        
        
        createProperty("gasTemperature", gasTemperature);
        createProperty("calorificValue", calorificValue);
        createProperty("normDensity", normDensity);
        createProperty("coefficient-A-heatCapacity", heatCapacityA);
        createProperty("coefficient-B-heatCapacity", heatCapacityB);
        createProperty("coefficient-C-heatCapacity", heatCapacityC);
        createProperty("molarMass", molarMass);        
        createProperty("pseudocriticalPressure", pseudocriticalPressure);
        createProperty("pseudocriticalTemperature", pseudocriticalTemperature);
    }

    
    
    public Amount<CalorificValue> getCalorificValue() {
        return calorificValue;
    }

    public Amount<Temperature> getGasTemperature() {
        return gasTemperature;
    }

    public Amount<Dimensionless> getHeatCapacityA() {
        return heatCapacityA;
    }

    public Amount<Dimensionless> getHeatCapacityB() {
        return heatCapacityB;
    }

    public Amount<Dimensionless> getHeatCapacityC() {
        return heatCapacityC;
    }

    public Amount<MolarMass> getMolarMass() {
        return molarMass;
    }

    public Amount<VolumetricDensity> getNormDensity() {
        return normDensity;
    }

    public Amount<Pressure> getPseudocriticalPressure() {
        return pseudocriticalPressure;
    }

    public Amount<Temperature> getPseudocriticalTemperature() {
        return pseudocriticalTemperature;
    }

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("source");
    }

    @Override
    protected void parseProperties() {
        super.parseProperties();
        XMLProperty pCalorificValue = getProperties().get("calorificValue");
        if (pCalorificValue.getUnit().equals("MJ_per_m_cube")) {
            pCalorificValue.setUnit("MJ/m^3");
        }
        calorificValue = pCalorificValue.getAmount();
        XMLProperty pGasTemperature;
        if (getProperties().containsKey("gasTemperature")) {
            pGasTemperature = getProperties().get("gasTemperature");
        } else {
            pGasTemperature = getProperties().get("temperature");
        }
        if (pGasTemperature.getUnit().isEmpty() || pGasTemperature.getUnit().equals("C") || pGasTemperature.getUnit().equals("Celsius")) {
            pGasTemperature.setUnit("°C");
        }
        gasTemperature = pGasTemperature.getAmount();
        heatCapacityA = getProperties().get("coefficient-A-heatCapacity").getAmount();
        heatCapacityB = getProperties().get("coefficient-B-heatCapacity").getAmount();
        heatCapacityC = getProperties().get("coefficient-C-heatCapacity").getAmount();
        XMLProperty pMolarMass = getProperties().get("molarMass");
        if (pMolarMass.getUnit().equals("kg_per_kmol")) {
            pMolarMass.setUnit("g/mol");
        }
        molarMass = getProperties().get("molarMass").getAmount();
        calorificValue = pMolarMass.getAmount();
        XMLProperty pNormDensity = getProperties().get("normDensity");
        if (pNormDensity.getUnit().equals("kg_per_m_cube")) {
            pNormDensity.setUnit("kg/m^3");
        }
        calorificValue = pNormDensity.getAmount();      
        pseudocriticalPressure = getProperties().get("pseudocriticalPressure").getAmount();
        pseudocriticalTemperature = getProperties().get("pseudocriticalTemperature").getAmount();
    }

    @Override
    public void writeTo(Document document, Element parent) {
        Element element = document.createElement("source");
        writeAttributes(element);
        writeChildren(document, element);
        parent.appendChild(element);
    }
}
