/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gas.algo.propagation;

import ds.graph.GasNode;
import gas.io.IntersectionType;
import gas.io.gaslib.GasLibIntersection;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author Martin
 */
public class NodeStyleMapping implements Function<GasNode, String> {
    
    private final Map<Object, GasLibIntersection> nodeIntersections;
    private final Map<Object, IntersectionType> typeOverrides;

    public NodeStyleMapping(Map<Object, GasLibIntersection> nodeIntersections, Map<Object, IntersectionType> typeOverrides) {
        this.nodeIntersections = nodeIntersections;
        this.typeOverrides = typeOverrides;
    }
    
    @Override
    public String apply(GasNode n) {
        String style;
        if (typeOverrides.containsKey(n)) {
            switch (typeOverrides.get(n)) {
                case SOURCE:
                    style = "sourceStyle";
                    break;
                case SINK:
                    style = "sinkStyle";
                    break;
                case TERMINAL:
                    style = "terminalStyle";
                    break;
                default:
                    style = "nodeStyle";
                    break;
            }
        } else {
            switch (IntersectionType.getType(nodeIntersections.get(n))) {
                case SOURCE:
                    style = "sourceStyle";
                    break;
                case SINK:
                    style = "sinkStyle";
                    break;
                case TERMINAL:
                    style = "terminalStyle";
                    break;
                default:
                    style = "nodeStyle";
                    break;
            }
        }
        return style;
    }

}
