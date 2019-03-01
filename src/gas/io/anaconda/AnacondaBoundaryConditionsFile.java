/**
 * AnacondaBoundaryConditionsFile.java
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
public class AnacondaBoundaryConditionsFile extends XMLFile {

    private final Map<String, AnacondaLinearBoundaryCondition> boundaryConditions;

    public AnacondaBoundaryConditionsFile() {
        boundaryConditions = new LinkedHashMap<>();
    }

    public Map<String, AnacondaLinearBoundaryCondition> getBoundaryConditions() {
        return boundaryConditions;
    }

    @Override
    protected void parseChild(Node domXMLNode) {
        AnacondaLinearBoundaryCondition condition = new AnacondaLinearBoundaryCondition();
        condition.initializeFrom(domXMLNode);
        boundaryConditions.put(condition.getId(), condition);
    }

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("boundaryConstraints");
    }
}
