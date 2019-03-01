/**
 * GasLibConnections.java
 *
 */

package gas.io.gaslib;

import com.google.common.collect.HashBiMap;
import gas.io.ConnectionType;
import gas.io.XMLConnections;
import gas.io.XMLIntersections;
import java.util.LinkedHashMap;

/**
 *
 * @author Martin Groß
 */
public class GasLibConnections extends XMLConnections<GasLibConnection> {

    public GasLibConnections(XMLIntersections intersections) {
        super(intersections);
        connections = HashBiMap.create();
    }

    @Override
    protected GasLibConnection createConnection(String connectionType) {
        return GasLibConnection.createNewConnection(ConnectionType.getType(connectionType));
    }
}
