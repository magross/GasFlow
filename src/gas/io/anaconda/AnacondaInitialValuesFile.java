/**
 * AnacondaInitialValuesFile.java
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
public class AnacondaInitialValuesFile extends XMLFile {
    
    private final Map<String, AnacondaObjectCondition> objectConditions;

    public AnacondaInitialValuesFile() {
        objectConditions = new LinkedHashMap<>();
    }

    public Map<String, AnacondaObjectCondition> getObjectConditions() {
        return objectConditions;
    }

    @Override
    protected void parseChild(Node domXMLNode) {
        AnacondaObjectCondition objectCondition = new AnacondaObjectCondition();
        objectCondition.initializeFrom(domXMLNode);
        objectConditions.put(objectCondition.getId(), objectCondition);
    }

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("initialConditions");
    }
}
