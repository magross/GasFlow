/*
 * GasLibConnection.java
 * 
 * 
 */
package gas.io.gaslib;

import gas.io.XMLConnection;
import gas.io.ConnectionType;
import static gas.io.ConnectionType.COMPRESSOR_STATION;
import static gas.io.ConnectionType.CONTROL_VALVE;
import static gas.io.ConnectionType.PIPE;
import static gas.io.ConnectionType.RESISTOR;
import static gas.io.ConnectionType.SHORT_PIPE;
import static gas.io.ConnectionType.VALVE;
import org.w3c.dom.Element;

/**
 *
 * @author Martin Groß
 */
public abstract class GasLibConnection extends XMLConnection {

    public static GasLibConnection createNewConnection(ConnectionType type) {
        switch (type) {
            case COMPRESSOR_STATION:
                return new GasLibCompressorStation();
            case CONTROL_VALVE:
                return new GasLibControlValve();
            case PIPE: 
                return new GasLibPipe();
            case RESISTOR:
                return new GasLibResistor();
            case SHORT_PIPE:
                return new GasLibShortPipe();
            case VALVE:
                return new GasLibValve();
            default:
                throw new AssertionError("Unknown connection type: " + type);
        }
    }
    
    
    
    protected  String alias;



    
    
    public String getAlias() {
        return alias;
    }    
    
    @Override
    protected boolean parseAttribute(String name, String value) {
        if (!super.parseAttribute(name, value)) {
            switch (name) {
                case "alias":
                    alias = value;
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
    }    

    void createProperties() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
