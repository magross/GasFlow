/**
 * XMLIntersections.java
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
public abstract class XMLIntersections<T extends XMLIntersection> extends XMLElement {

    protected BiMap<String, T> intersections;

    @Override
    protected void parseChild(Node domXMLNode) {
        T intersection = createIntersection(domXMLNode.getNodeName());
        intersection.initializeFrom(domXMLNode);
        intersections.put(intersection.getId(), intersection);
    }

    protected abstract T createIntersection(String intersectionType);

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("framework:nodes");
    }
    
    public int numberOfIntersections() {
        return intersections.size();
    }

    public BiMap<String, T> getMap() {
        return intersections;
    }

    @Override
    public void writeTo(Document document, Element parent) {
        for (T intersection : intersections.values()) {
            intersection.writeTo(document, parent);
        }
    }
}
