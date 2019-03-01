/**
 * GasLibSetting.java
 *
 */

package gas.io.gaslib;

import gas.io.ConnectionType;
import gas.io.XMLElementWithID;

/**
 *
 * @author Martin Groß
 */
public class GasLibSetting extends XMLElementWithID {

    private int flowDirection;
    private int value;
    private ConnectionType type;

    @Override
    protected boolean parseAttribute(String name, String value) {
        if (!super.parseAttribute(name, value)) {
            switch (name) {
                case "flowDirection":
                    flowDirection = Integer.parseInt(value);
                    return true;
                case "value":
                    this.value = Integer.parseInt(value);
                    return true;
                default:
                    return false;
            }
        } else {
            return true;
        }
    }

    @Override
    protected void parseNodeName(String nodeName) {
        type = ConnectionType.getType(nodeName);
    }

    @Override
    public String toString() {
        return "Setting {id = " + getId() + ", flowDirection=" + flowDirection + ", value=" + value + ", type=" + type + '}';
    }
}
