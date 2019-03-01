/**
 * GasLibSink.java
 *
 */

package gas.io.gaslib;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Martin Groß
 */
public class GasLibSink extends GasLibTerminalNode {

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("sink");
    }

    @Override
    public  void writeTo(Document document, Element parent) {
        Element element = document.createElement("sink");
        writeAttributes(element);
        writeChildren(document, element);
        parent.appendChild(element);
    }
}
