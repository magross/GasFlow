/*
 * AnacondaConnection.java
 * 
 * 
 */
package gas.io.anaconda;

import gas.io.XMLConnection;
import gas.io.ConnectionType;
import static gas.io.ConnectionType.COMPRESSOR_STATION;
import static gas.io.ConnectionType.CONTROL_VALVE;
import static gas.io.ConnectionType.PIPE;
import static gas.io.ConnectionType.RESISTOR;
import static gas.io.ConnectionType.SHORT_PIPE;
import static gas.io.ConnectionType.VALVE;

/**
 *
 * @author Martin Groß
 */
public abstract class AnacondaConnection extends XMLConnection {

    public static AnacondaConnection createNewConnection(ConnectionType type) {
        switch (type) {
            case COMPRESSOR_STATION:
                return new AnacondaCompressorStation();
            case CONTROL_VALVE:
                return new AnacondaControlValve();
            case PIPE: 
                return new AnacondaPipe();
            case RESISTOR:
                throw new AssertionError("Anaconda does not support resistors.");
            case SHORT_PIPE:
                return new AnacondaShortPipe();
            case VALVE:
                return new AnacondaValve();
            default:
                throw new AssertionError("Unknown connection type: " + type);
        }
    }  
}
