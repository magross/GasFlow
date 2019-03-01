/**
 * AnacondaBoundaryConstraintsFile.java
 *
 */

package gas.io.anaconda;

import gas.io.XMLFile;
import java.util.LinkedHashMap;
import java.util.Map;
import org.w3c.dom.Node;

/**
 *
 * @author Martin Groß
 */
public class AnacondaBoundaryConstraintsFile extends XMLFile {

    private final Map<String, AnacondaLinearBoundaryCondition> boundaryConditions;
    private final Map<String, AnacondaLinearMinMaxCondition> minMaxConditions;
    private final Map<String, AnacondaLinearTerminalCondition> terminalConditions;

    public AnacondaBoundaryConstraintsFile() {
        boundaryConditions = new LinkedHashMap<>();
        minMaxConditions = new LinkedHashMap<>();
        terminalConditions = new LinkedHashMap<>();
    }

    public Map<String, AnacondaLinearBoundaryCondition> getBoundaryConditions() {
        return boundaryConditions;
    }

    public Map<String, AnacondaLinearMinMaxCondition> getMinMaxConditions() {
        return minMaxConditions;
    }

    public Map<String, AnacondaLinearTerminalCondition> getTerminalConditions() {
        return terminalConditions;
    }

    @Override
    protected void parseChild(Node domXMLNode) {
        switch (domXMLNode.getNodeName()) {
            case "linearBoundaryCondition":
                AnacondaLinearBoundaryCondition condition = new AnacondaLinearBoundaryCondition();
                condition.initializeFrom(domXMLNode);
                boundaryConditions.put(condition.getId(), condition);
                break;
            case "linearMinMaxCondition":
                AnacondaLinearMinMaxCondition condition1 = new AnacondaLinearMinMaxCondition();
                condition1.initializeFrom(domXMLNode);
                minMaxConditions.put(condition1.getId(), condition1);
                break;
            case "linearTerminalCondition":
                AnacondaLinearTerminalCondition condition2 = new AnacondaLinearTerminalCondition();
                condition2.initializeFrom(domXMLNode);
                terminalConditions.put(condition2.getId(), condition2);
                break;
            default:
                throw new AssertionError("");
        }
    }

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("boundaryConstraints");
    }
}
