/**
 * GasLibInnerNode.java
 *
 */

package gas.io.gaslib;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Martin Groß
 */
public class GasLibInnerNode extends GasLibIntersection {

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("innode");
    }

    @Override
    public  void writeTo(Document document, Element parent) {
        Element element = document.createElement("innode");
        writeAttributes(element);
        writeChildren(document, element);
        parent.appendChild(element);
    }
}
