/**
 * GasLibDecision.java
 *
 */

package gas.io.gaslib;

import gas.io.XMLElementWithID;
import java.util.LinkedList;
import java.util.List;
import org.w3c.dom.Node;

/**
 *
 * @author Martin Groß
 */
public class GasLibDecision extends XMLElementWithID {    

    private final List<GasLibSetting> settings;

    public GasLibDecision() {
        settings = new LinkedList<>();
    }

    public List<GasLibSetting> getSettings() {
        return settings;
    }

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("decision");
    }

    @Override
    protected void parseChild(Node domXMLNode) {
        GasLibSetting setting = new GasLibSetting();
        setting.initializeFrom(domXMLNode);
        settings.add(setting);
    }

    @Override
    public String toString() {
        return String.format("Decision %1$s %2$s", getId(), settings);
    }
}
