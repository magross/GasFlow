/**
 * XMLFile.java
 *
 */
package gas.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author Martin Groß
 */
public abstract class XMLFile extends XMLElement {

    private boolean valid;
    private String xmlns;
    private String xmlnsFramework;
    private String xmlnsXsi;
    private String xsiSchemaLocation;
    protected String topLevelName = "network";

    public void readFromFile(String fileName) {
        try {
            readFromFile(new FileInputStream(fileName));            
        } catch (FileNotFoundException ex) {
            valid = false;
            Logger.getLogger(XMLNetworkFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void readFromFile(InputStream stream) {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(stream);
            initializeFrom(document.getDocumentElement());
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            valid = false;
            Logger.getLogger(XMLNetworkFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeToFile(String filename) {
        writeToFile(new StreamResult(filename));
    }

    public void writeToFile(StreamResult result) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.newDocument();
            Element element = document.createElement(topLevelName);
            writeAttributes(element);
            document.appendChild(element);
            writeTo(document, element);
            DOMSource source = new DOMSource(document);
            transformer.transform(source, result);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(XMLFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException | TransformerException ex) {
            Logger.getLogger(XMLFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected boolean parseAttribute(String name, String value) {
        if (!super.parseAttribute(name, value)) {
            switch (name) {
                case "xmlns":
                    xmlns = value;
                    return true;
                case "xmlns:framework":
                    xmlnsFramework = value;
                    return true;
                case "xmlns:xsi":
                    xmlnsXsi = value;
                    return true;
                case "xsi:schemaLocation":
                    xsiSchemaLocation = value;
                    return true;
                default:
                    return false;
            }
        } else {
            return true;
        }
    }

    @Override
    protected void writeAttributes(Element element) {
        element.setAttribute("xmlns:xsi", xmlnsXsi);
        element.setAttribute("xmlns", xmlns);
        element.setAttribute("xsi:SchemaLocation", xsiSchemaLocation);
        element.setAttribute("xmlns:framework", xmlnsFramework);       
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
