/**
 * GasLibDecisionGroup.java
 *
 */
package gas.io.gaslib;

import gas.io.XMLElementWithID;
import java.util.LinkedHashMap;
import org.w3c.dom.Node;

/**
 *
 * @author Martin Groß
 */
public class GasLibDecisionGroup extends XMLElementWithID {

    private final LinkedHashMap<String, GasLibDecision> decisions;

    public GasLibDecisionGroup() {
        decisions = new LinkedHashMap<>();
    }

    public LinkedHashMap<String, GasLibDecision> getDecisions() {
        return decisions;
    }

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("decisionGroup");
    } 

    @Override
    protected void parseChild(Node domXMLNode) {
        GasLibDecision decision = new GasLibDecision();
        decision.initializeFrom(domXMLNode);
        decisions.put(decision.getId(), decision);
    }

    @Override
    public String toString() {
        return String.format("DecisionGroup %1$s %2$s", getId(), decisions);
    }
}
