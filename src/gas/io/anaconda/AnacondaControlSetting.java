/**
 * AnacondaControlSetting.java
 *
 */

package gas.io.anaconda;

import gas.io.XMLElementWithID;
import java.util.LinkedList;
import java.util.List;
import org.w3c.dom.Node;

/**
 *
 * @author Martin Groß
 */
public class AnacondaControlSetting extends XMLElementWithID {

    private final List<AnacondaTimeValuesData> dataPoints;

    public AnacondaControlSetting() {
        dataPoints = new LinkedList<>();
    }

    public List<AnacondaTimeValuesData> getDataPoints() {
        return dataPoints;
    }

    @Override
    protected void parseChild(Node domXMLNode) {
        AnacondaTimeValuesData data = new AnacondaTimeValuesData();
        data.initializeFrom(domXMLNode);
        dataPoints.add(data);
    }

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("controlSetting");
    }
}
