/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gas.io;

import gas.io.anaconda.AnacondaCompressorStation;
import gas.io.anaconda.AnacondaControlValve;
import gas.io.anaconda.AnacondaPipe;
import gas.io.anaconda.AnacondaShortPipe;
import gas.io.anaconda.AnacondaValve;
import gas.io.gaslib.GasLibCompressorStation;
import gas.io.gaslib.GasLibControlValve;
import gas.io.gaslib.GasLibPipe;
import gas.io.gaslib.GasLibResistor;
import gas.io.gaslib.GasLibShortPipe;
import gas.io.gaslib.GasLibValve;

/**
 *
 * @author Martin Groß
 */
public enum ConnectionType {
    PIPE("pipe", "Pipe"),
    SHORT_PIPE("shortPipe", "Short Pipe"),
    RESISTOR("resistor", "Resistor"),
    VALVE("valve", "Valve"),
    CONTROL_VALVE("controlValve", "Control Valve"),
    COMPRESSOR_STATION("compressorStation", "Compressor Station"),
    UNKNOWN("unknown","Unknown");

    private final String textString;
    private final String xmlString;


    private ConnectionType(String string, String text) {
        this.xmlString = string;
        this.textString = text;
    }

    public static ConnectionType getType(XMLConnection connection) {
        if (connection instanceof AnacondaPipe || connection instanceof GasLibPipe) {
            return PIPE;
        } else if (connection instanceof AnacondaShortPipe || connection instanceof GasLibShortPipe) {
            return SHORT_PIPE;
        } else if (connection instanceof AnacondaValve || connection instanceof GasLibValve) {
            return VALVE;
        } else if (connection instanceof AnacondaControlValve || connection instanceof GasLibControlValve) {
            return CONTROL_VALVE;
        } else if (connection instanceof AnacondaCompressorStation || connection instanceof GasLibCompressorStation) {
            return COMPRESSOR_STATION;
        } else if (connection instanceof GasLibResistor) {
            return RESISTOR;
        } else if (connection == null) {
            return UNKNOWN;
        } else {
            throw new AssertionError("Unknown Pipe Type: " + connection);
        }
    }

    public static ConnectionType getType(String xmlString) {
        switch (xmlString) {
            case "pipe":
                return ConnectionType.PIPE;
            case "shortPipe":
                return ConnectionType.SHORT_PIPE;
            case "valve":
                return ConnectionType.VALVE;
            case "controlValve":
                return ConnectionType.CONTROL_VALVE;
            case "compressor":
            case "compressorStation":
                return ConnectionType.COMPRESSOR_STATION;
            case "resistor":
                return ConnectionType.RESISTOR;
            default:
                throw new AssertionError("Unknown Pipe Type: " + xmlString);
        }
    }

    @Override
    public String toString() {
        return xmlString;
    }

    /**
     * Returns a human-readable representation of the connection type.
     * @return a human-readable representation of the connection type.
     */
    public String toText() {
        return textString;
    }
}
