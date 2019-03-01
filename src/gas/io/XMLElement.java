/**
 * GasLIbElement.java
 *
 */

package gas.io;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Martin Groß
 */
public abstract class XMLElement {
   
    public void initializeFrom(Node domXMLNode) {
        if (!checkNodeName(domXMLNode.getNodeName())) {
            throw new AssertionError("Node " + domXMLNode.getNodeName() + " is not of the expected type.");
        }
        parseNodeName(domXMLNode.getNodeName());
        parseAttributes(domXMLNode);
        parseChildren(domXMLNode);
    }

    protected boolean checkNodeName(String name) {
        return true;
    }

    protected final boolean parseBoolean(String string) {
        switch (string) {
            case "0":
                return false;
            case "1":
                return true;
            default:
                throw new AssertionError("Unexpected value: " + string);
        }
    }
    
    protected final String writeBoolean(boolean bool) {
        if (bool) {
            return "1";
        } else {
            return "0";
        }
    }

    protected void parseNodeName(String nodeName) {
    }

    private void parseAttributes(Node domXMLNode) {
        NamedNodeMap attributes = domXMLNode.getAttributes();
        for (int j = 0; j < attributes.getLength(); j++) {
            Node attribute = attributes.item(j);
            if (attribute.getNodeName().equals("#text")) {
                continue;
            }
            String name = attribute.getNodeName();
            String value = attribute.getNodeValue();
            if (!parseAttribute(name, value)) {
                throw new AssertionError("Unknown attribute: " + name + " = " + value + ".");
            }
        }
    }

    protected boolean parseAttribute(String name, String value) {
        return false;
    }

    private void parseChildren(Node domXMLNode) {
        NodeList childNodes = domXMLNode.getChildNodes();
        for (int j = 0; j < childNodes.getLength(); j++) {
            Node childNode = childNodes.item(j);
            if (childNode.getNodeName().equals("#text") || childNode.getNodeName().equals("#comment")) {
                continue;
            }
            parseChild(childNode);
        }
    }

    protected void parseChild(Node domXMLNode) {
    }

    public void writeTo(Document document, Element parent) {
    }
    
    protected void writeAttributes(Element element) {
    }
    
    protected void writeChildren(Document document, Element parent) {
    }
}
