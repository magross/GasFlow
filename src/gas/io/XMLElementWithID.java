/**
 * XMLElementWithID.java
 *
 */

package gas.io;

import org.w3c.dom.Element;

/**
 *
 * @author Martin Groß
 */
public abstract class XMLElementWithID extends XMLElement {

    protected String id;

    public String getId() {
        return id;
    }

    @Override
    protected boolean parseAttribute(String name, String value) {
        switch (name) {
            case "id":
                id = value;
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void writeAttributes(Element element) {
        super.writeAttributes(element);
        element.setAttribute("id", id);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" + "id=" + id + '}';
    }
    
    
}
