/**
 * Information.java
 *
 */
package gas.io;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 *
 * @author Martin Groß
 */
public class XMLInformation extends XMLElement {

    private final List<String> authors;
    private Date date;
    private String dateText;
    private String documentation;
    private String title;
    private String type;

    public XMLInformation() {
        authors = new LinkedList<>();
    }

    public List<String> getAuthors() {
        return authors;
    }

    public Date getDate() {
        return date;
    }

    public String getDocumentation() {
        return documentation;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    @Override
    protected void parseChild(Node domXMLNode) {
        switch (domXMLNode.getNodeName()) {
            case "framework:title":
                title = domXMLNode.getTextContent();
                return;
            case "framework:type":
                type = domXMLNode.getTextContent();
                return;
            case "framework:author":
                authors.add(domXMLNode.getTextContent());
                return;
            case "framework:date":
                dateText = domXMLNode.getTextContent();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
                try {
                    date = sdf.parse(domXMLNode.getTextContent());
                } catch (ParseException ex) {
                    Logger.getLogger(XMLInformation.class.getName()).log(Level.SEVERE, null, ex);
                }
                return;
            case "framework:documentation":
                documentation = domXMLNode.getTextContent();
                return;
            default:
                throw new AssertionError("Unknown node type: " + domXMLNode.getNodeName());
        }
    }

    @Override
    public  void writeTo(Document document, Element parent) {
        Element e;
        Text text;
        e = document.createElement("framework:title");
        text = document.createTextNode(title.replace("_", "").replace("-", ""));
        e.appendChild(text);
        parent.appendChild(e);
        e = document.createElement("framework:type");
        text = document.createTextNode(type);
        e.appendChild(text);
        parent.appendChild(e);
        for (String author : authors) {
            e = document.createElement("framework:author");
            text = document.createTextNode(author);
            e.appendChild(text);
            document.appendChild(e);
        }
        e = document.createElement("framework:date");
        text = document.createTextNode(dateText);
        e.appendChild(text);
        parent.appendChild(e);
        e = document.createElement("framework:documentation");
        text = document.createTextNode(documentation);
        e.appendChild(text);
        parent.appendChild(e);
    }
}
