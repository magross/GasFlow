/**
 * AnacondaControlSettingsFile.java
 *
 */

package gas.io.anaconda;

import gas.io.XMLFile;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import org.w3c.dom.Node;

/**
 *
 * @author Martin Groß
 */
public class AnacondaControlSettingsFile extends XMLFile {

    private final Map<String, AnacondaControlSetting> settings;

    public AnacondaControlSettingsFile() {
        settings = new LinkedHashMap<>();
    }

    public Map<String, AnacondaControlSetting> getSettings() {
        return settings;
    }

    @Override
    protected void parseChild(Node domXMLNode) {
        AnacondaControlSetting setting = new AnacondaControlSetting();
        setting.initializeFrom(domXMLNode);
        settings.put(setting.getId(), setting);
    }

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("controlSettings");
    }
}

