/**
 * AnacondaConnections.java
 *
 */

package gas.io.anaconda;

import com.google.common.collect.HashBiMap;
import gas.io.ConnectionType;
import gas.io.XMLConnections;
import gas.io.XMLIntersections;
import java.util.LinkedHashMap;

/**
 *
 * @author Martin Groß
 */
public class AnacondaConnections extends XMLConnections<AnacondaConnection> {

    public AnacondaConnections(XMLIntersections intersections) {
        super(intersections);
        connections = HashBiMap.create();
    }

    @Override
    protected AnacondaConnection createConnection(String connectionType) {
        return AnacondaConnection.createNewConnection(ConnectionType.getType(connectionType));
    }
}
