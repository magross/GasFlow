/**
 * GasLibSource.java
 *
 */
package gas.io.gaslib;

import gas.io.XMLProperty;
import gas.quantity.CalorificValue;
import gas.quantity.MolarMass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import units.UnitsTools;
import units.qual.*;

/**
 *
 * @author Martin Groß
 */
public class GasLibSource extends GasLibTerminalNode {

    private double calorificValue;
    private double gasTemperature;
    private double heatCapacityA;
    private double heatCapacityB;
    private double heatCapacityC;
    private double molarMass;
    private double normDensity;
    private double pseudocriticalPressure;
    private double pseudocriticalTemperature;

    public GasLibSource() {
        super();
        calorificValue = 36.4543670654 * UnitsTools.MJ/UnitsTools.m3;
        gasTemperature = 0 * UnitsTools.C;
        heatCapacityA = 31.8251781464;
        heatCapacityB = -0.00846800766885;
        heatCapacityC = 7.44647331885e-05;
        molarMass = 18.5674 * UnitsTools.g/UnitsTools.mol;
        normDensity = 0.785 * UnitsTools.kg/UnitsTools.m3;
        pseudocriticalPressure = 45.9293457336 * UnitsTools.bar;
        pseudocriticalTemperature = 188.549758911 * UnitsTools.K;
        
        
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

    
    
    public double getCalorificValue() {
        return calorificValue;
    }

    public @C double getGasTemperature() {
        return gasTemperature;
    }

    public double getHeatCapacityA() {
        return heatCapacityA;
    }

    public double getHeatCapacityB() {
        return heatCapacityB;
    }

    public double getHeatCapacityC() {
        return heatCapacityC;
    }

    public @gPERmol double getMolarMass() {
        return molarMass;
    }

    public double getNormDensity() {
        return normDensity;
    }

    public @bar double getPseudocriticalPressure() {
        return pseudocriticalPressure;
    }

    public @K double getPseudocriticalTemperature() {
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
