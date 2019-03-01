/*
 * GasLibIntersection.java
 * 
 * 
 */
package gas.io.gaslib;

import gas.io.XMLIntersection;
import gas.io.IntersectionType;
import static gas.io.IntersectionType.NODE;
import static gas.io.IntersectionType.SINK;
import static gas.io.IntersectionType.SOURCE;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author Martin Groß
 */
public abstract class GasLibIntersection extends XMLIntersection {

    public static GasLibIntersection createNewIntersection(IntersectionType type) {
        switch (type) {
            case NODE:
                return new GasLibInnerNode();
            case SINK:
                return new GasLibSink();
            case SOURCE:
                return new GasLibSource();
            default:
                throw new AssertionError("Unknown type: " + type);
        }
    }
    
    private String alias;
    private double latitude;
    private double longitude;

    public GasLibIntersection() {
        super();
        alias = "";
        latitude = 0.0;
        longitude = 0.0;
    }
    
    

    @Override
    public void initializeFrom(Node domXMLNode) {
        super.initializeFrom(domXMLNode);
        parseProperties();
    }

    public String getAlias() {
        return alias;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    protected boolean parseAttribute(String name, String value) {
        if (!super.parseAttribute(name, value)) {
            switch (name) {
                case "alias":
                    alias = value;
                    return true;
                case "geoWGS84Lat":
                    latitude = Double.parseDouble(value);
                    return true;
                case "geoWGS84Long":
                    longitude = Double.parseDouble(value);
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
        super.writeAttributes(element);
        element.setAttribute("alias", alias);
        element.setAttribute("geoWGS84Lat", Double.toString(latitude));
        element.setAttribute("geoWGS84Long", Double.toString(longitude));
    }

    public void setId(String id) {
        this.id = id;
    }
}
