/*
 * GasLibCombinedDecisionFile.java
 * 
 * 
 */
package gas.io.gaslib;

import gas.io.XMLFile;
import java.util.LinkedHashMap;
import java.util.Map;
import org.w3c.dom.Node;

/**
 *
 * @author Martin Groß
 */
public class GasLibCombinedDecisionFile extends XMLFile {

    /**
     * Stores the
     */
    private final Map<String, GasLibDecisionGroup> decisionGroups;

    public GasLibCombinedDecisionFile() {
        decisionGroups = new LinkedHashMap<>();
    }

    public Map<String, GasLibDecisionGroup> getDecisionGroups() {
        return decisionGroups;
    }

    @Override
    protected void parseChild(Node domXMLNode) {
        GasLibDecisionGroup decisionGroup = new GasLibDecisionGroup();
        decisionGroup.initializeFrom(domXMLNode);
        decisionGroups.put(decisionGroup.getId(), decisionGroup);
    }

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("combinedDecisions");
    }
}
