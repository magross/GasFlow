/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gas;

import ds.graph.Edge;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.Node;
import gas.io.XMLConnection;
import gas.io.XMLIntersection;

/**
 *
 * @author gross
 */
public class GasNetwork {

    static IdentifiableObjectMapping<Node, XMLIntersection> nodeIntersections;
    static IdentifiableObjectMapping<Edge, XMLConnection> edgeConnections;
}
