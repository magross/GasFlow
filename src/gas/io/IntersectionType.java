/*
 * IntersectionType.java
 * 
 * 
 */
package gas.io;

import gas.io.anaconda.AnacondaInnerNode;
import gas.io.anaconda.AnacondaSink;
import gas.io.anaconda.AnacondaSource;
import gas.io.gaslib.GasLibInnerNode;
import gas.io.gaslib.GasLibSink;
import gas.io.gaslib.GasLibSource;

/**
 *
 * @author Martin Groß
 */
public enum IntersectionType {
    NODE, SINK, SOURCE, TERMINAL;

    public static IntersectionType getType(XMLIntersection intersection) {
        if (intersection instanceof AnacondaInnerNode || intersection instanceof GasLibInnerNode) {
            return NODE;
        } else if (intersection instanceof AnacondaSink || intersection instanceof GasLibSink) {
            return SINK;
        } else if (intersection instanceof AnacondaSource || intersection instanceof GasLibSource) {
            return SOURCE;
        } else {
            throw new AssertionError("Unknown Pipe Type: " + intersection);
        }
    }

    public static IntersectionType getType(String string) {
        switch (string) {
            case "innode":
                return IntersectionType.NODE;
            case "sink":
                return IntersectionType.SINK;
            case "source":
                return IntersectionType.SOURCE;
            default:
                throw new AssertionError("Unknown Pipe Type: " + string);
        }
    }

    private static IntersectionType[][] RESULTS = {
        { NODE,     SINK,     SOURCE,   TERMINAL },
        { SINK,     SINK,     TERMINAL, TERMINAL },
        { SOURCE,   TERMINAL, SOURCE,   TERMINAL},
        { TERMINAL, TERMINAL, TERMINAL, TERMINAL }
    };
    
    public static IntersectionType combine(IntersectionType first, IntersectionType second) {        
        return RESULTS[first.ordinal()][second.ordinal()];
    }
}
