/**
 * AnacondaObjectCondition.java
 *
 */

package gas.io.anaconda;

import gas.io.XMLElementWithID;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Node;

/**
 *
 * @author Martin Groß
 */
public class AnacondaObjectCondition extends XMLElementWithID {

    private final List<AnacondaFlowPressureData> dataPoints;

    public AnacondaObjectCondition() {
        dataPoints = new LinkedList<>();
    }

    public List<AnacondaFlowPressureData> getDataPoints() {
        return dataPoints;
    }

    @Override
    protected void parseChild(Node domXMLNode) {
        AnacondaFlowPressureData data = new AnacondaFlowPressureData();
        data.initializeFrom(domXMLNode);
        dataPoints.add(data);
    }

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("objectCondition");
    }
}
