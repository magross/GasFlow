/*
 * GasLibScenarioFile.java
 * 
 * 
 */
package gas.io.gaslib;

import gas.io.XMLFile;
import java.util.LinkedHashMap;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author Martin Groß
 */
public class GasLibScenarioFile extends XMLFile {

    private final Map<String, GasLibScenario> scenarios;

    public GasLibScenarioFile() {
        scenarios = new LinkedHashMap<>();
        topLevelName = "boundaryValue";
    }
    
    public GasLibScenarioFile(String filename) {
        this();
        readFromFile(filename);
    }

    public Map<String, GasLibScenario> getScenarios() {
        return scenarios;
    }

    @Override
    protected void parseChild(Node domXMLNode) {
        GasLibScenario scenario = new GasLibScenario();
        scenario.initializeFrom(domXMLNode);
        scenarios.put(scenario.getId(), scenario); 
    }

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("boundaryValue");
    }
    

    @Override
    public void writeTo(Document document, Element parent) {
        for (GasLibScenario scen : scenarios.values()) {
            //System.out.println("Write: " + scen + " " + parent);
            //Element scenarioElement = document.createElement("scenario");
            Element scenarioElement = document.createElement("scenario");
            parent.appendChild(scenarioElement);
            scen.writeTo(document, scenarioElement);
        }
    }    
    
    public void removeNodes(GasLibNetworkFile network) {
        for (GasLibScenario scen : scenarios.values()) {
            scen.removeNodes(network);
        }
    }
    
    public boolean validateBalance() {
        for (GasLibScenario scen : scenarios.values()) {
            if (!scen.validate()) {
                return false;
            }
        }
        return true;
    }
    
}
