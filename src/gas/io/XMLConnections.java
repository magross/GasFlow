/**
 * Connections.java
 *
 */
package gas.io;

import com.google.common.collect.BiMap;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author Martin Groß
 */
public abstract class XMLConnections<T extends XMLConnection> extends XMLElement {

    protected int[] connectionsNumbers;
    protected BiMap<String, T> connections;
    private final XMLIntersections intersections;

    public XMLConnections(XMLIntersections intersections) {     
        this.intersections = intersections;
    }

    public BiMap<String, T> getMap() {
        return connections;
    }
    
    public int numberOf(ConnectionType type) {
        if (connectionsNumbers == null) {
            connectionsNumbers = new int[ConnectionType.values().length];               
            for (T connection : connections.values()) {
                connectionsNumbers[connection.getType().ordinal()]++;
            }
        }
        return connectionsNumbers[type.ordinal()];
    }
    
    public int numberOfConnections() {
        return connections.size();
    }

    @Override
    protected void parseChild(Node domXMLNode) {
        T connection = createConnection(domXMLNode.getNodeName());
        connection.initializeFrom(domXMLNode);
        connection.connectToIntersections(intersections.getMap());
        connections.put(connection.getId(), connection);
    }

    protected abstract T createConnection(String connectionType);

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("framework:connections");
    }

    @Override
    public  void writeTo(Document document, Element parent) {
        
        for (T connection : connections.values()) {
            //System.out.println("XMLConnections: " + connection.getId());
            Element e = document.createElement(connection.getType().toString());
            parent.appendChild(e);
            connection.writeTo(document, e);
        }
    }
}
