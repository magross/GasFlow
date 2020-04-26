/*
 * GasLibScenario.java
 * 
 * 
 */
package gas.io.gaslib;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import static gas.algo.propagation.BoundPropagation.DEBUG;
import gas.io.GraphConversion;
import gas.io.XMLElementWithID;
import gas.io.XMLIntersection;
import gas.io.gaslib.GasLibScenarioNode.Type;
import static gas.io.gaslib.GasLibScenarioNode.Type.ENTRY;
import java.util.HashSet;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import units.UnitsTools;

/**
 *
 * @author Martin Groß
 */
public class GasLibScenario extends XMLElementWithID {

    private double probability;
    
    private BiMap<String, GasLibScenarioNode> scenarioNodes;

    public GasLibScenario() {
        scenarioNodes = HashBiMap.create();
    }

    public BiMap<String, GasLibScenarioNode> getNodes() {
        return scenarioNodes;
    }

    public double getProbability() {
        return probability;
    }

    @Override
    protected boolean checkNodeName(String name) {
        return name.equals("scenario");
    }

    @Override
    protected void parseChild(Node domXMLNode) {
        if (domXMLNode.getNodeName().equals("scenarioProbability")) {
            probability = Double.parseDouble(domXMLNode.getAttributes().getNamedItem("value").getNodeValue());
        } else if (domXMLNode.getNodeName().equals("temperatureMax")) {
            //temperatureMax = Double.parseDouble(domXMLNode.getAttributes().getNamedItem("value").getNodeValue());            
        } else if (domXMLNode.getNodeName().equals("temperatureMin")) {
            //temperatureMin = Double.parseDouble(domXMLNode.getAttributes().getNamedItem("value").getNodeValue());                        
        } else {
            GasLibScenarioNode scenarioNode = new GasLibScenarioNode();
            scenarioNode.initializeFrom(domXMLNode);
            scenarioNodes.put(scenarioNode.getId(), scenarioNode);
        }
    }

    public double getLowerPressureBound(XMLIntersection i) {
        return scenarioNodes.get(i.getId()).getLowerPressureBound();
    }

    public double getUpperPressureBound(XMLIntersection i) {
        return scenarioNodes.get(i.getId()).getLowerPressureBound();
    }

    public GasLibScenarioNode getNode(XMLIntersection i) {
        if (!scenarioNodes.containsKey(i.getId())) {
            GasLibScenarioNode node = new GasLibScenarioNode();
            node.setId(i.getId());
            scenarioNodes.put(i.getId(), node);
        }
        return scenarioNodes.get(i.getId());
    }

    public double getBalance(XMLIntersection i) {
        GasLibScenarioNode n = scenarioNodes.get(i.getId());
        if (n == null) {
            return 0 * UnitsTools.m3/UnitsTools.hr;
        }
        //assert n.getLowerFlowRateBound().equals(n.getUpperFlowRateBound());
        if (getNode(i).getType() == ENTRY) {
            return n.getLowerFlowRateBound();
        } else {
            return n.getLowerFlowRateBound()*-1.0;
        }

    }

    @Override
    public void writeTo(Document document, Element parent) {
        //super.writeTo(document, parent);
        //System.out.println("GasLibScenario: " + parent);
        parent.setAttribute("defaultPowerAndFlowZero", "1");
        writeAttributes(parent);

        //parent.appendChild(scenarioElement);
        for (GasLibScenarioNode node : scenarioNodes.values()) {
            if (DEBUG) {
                System.out.println(node);
                System.out.println(node.hasExactFlowBound());
                System.out.println(node.getFlowRateBound());
                System.out.println(node.getFlowRateBound() == (0 * UnitsTools.m3/UnitsTools.hr));
            }
            if (node.hasExactFlowBound() && node.getFlowRateBound() == (0 * UnitsTools.m3/UnitsTools.hr)) {
                continue;
            }
            Element scenarioNodeElement = document.createElement("node");
            parent.appendChild(scenarioNodeElement);
            node.writeTo(document, scenarioNodeElement);
        }
    }

    public void removeNode(GasLibIntersection ci) {
        scenarioNodes.remove(ci.getId());
    }

    public void removeNodes(GasLibNetworkFile network) {
        Set<String> nodeIDs = scenarioNodes.keySet();
        GraphConversion<GasLibIntersection, GasLibConnection> conv = network.getDynamicNetwork();
        HashSet<String> remove = new HashSet<>();
        for (String nodeID : nodeIDs) {
            GasLibIntersection intersection = network.getIntersections().getMap().get(nodeID);
            if (intersection == null) {
                System.out.println(nodeID);
                remove.add(nodeID);
            }
        }
        for (String string : remove) {
            scenarioNodes.remove(string);
        }
    }

    public boolean validate() {
        if (!DEBUG) {
            return true;
        }
        double supplyL = 0 * UnitsTools.m3/UnitsTools.hr;
        double supplyU = 0 * UnitsTools.m3/UnitsTools.hr;
        double demandL = 0 * UnitsTools.m3/UnitsTools.hr;
        double demandU = 0 * UnitsTools.m3/UnitsTools.hr;
        double sum = 0 * UnitsTools.m3/UnitsTools.hr;
        double sumD = 0.0;
        double sumB = 0.0;
        for (GasLibScenarioNode node : scenarioNodes.values()) {
            if (node.hasExactFlowBound() && node.getFlowRateBound() == (0 * UnitsTools.m3/UnitsTools.hr)) {
                continue;
            }
            double lowerFlowRateBound = node.getLowerFlowRateBound();
            double upperFlowRateBound = node.getUpperFlowRateBound();
            if (node.getType() == Type.ENTRY) {
                //System.out.println("ENTRY " + node.getId() + " " + lowerFlowRateBound + " " + upperFlowRateBound);
                supplyL = supplyL + lowerFlowRateBound;
                supplyU = supplyU + upperFlowRateBound;
                sum = sum + lowerFlowRateBound;
                sumD += lowerFlowRateBound;
                sumB += node.getFlowRateBound();
            } else if (node.getType() == Type.EXIT) {
                //System.out.println("EXIT " + node.getId() + " "  + lowerFlowRateBound + " " + upperFlowRateBound);
                demandL = demandL + lowerFlowRateBound;
                demandU = demandU + upperFlowRateBound;
                sum = sum - lowerFlowRateBound;
                sumD -= lowerFlowRateBound;
                sumB -= node.getFlowRateBound();
            }
        }
        if (DEBUG) {
            System.out.println("Validation: " + supplyL + " " + supplyU + " " + demandL + " " + demandU + " ");
        }
        if (DEBUG) {
            System.out.println(sum + " " + (supplyL - demandL) + " " + sumD + " " + sumB);
        }
        return true;
    }

    public double getLowerFlowRateBound(GasLibIntersection i) {
        if (scenarioNodes.get(i.getId()) == null) {
            return 0 * UnitsTools.m3/UnitsTools.hr;
        }
        if (getNode(i).getType() == ENTRY) {
            return scenarioNodes.get(i.getId()).getLowerFlowRateBound();
        } else {
            return scenarioNodes.get(i.getId()).getLowerFlowRateBound()*-1.0;
        }

    }

    public double getUpperFlowRateBound(GasLibIntersection i) {
        if (scenarioNodes.get(i.getId()) == null) {
            return 0 * UnitsTools.m3/UnitsTools.hr;
        }
        if (getNode(i).getType() == ENTRY) {
            return scenarioNodes.get(i.getId()).getUpperFlowRateBound();
        } else {
            return scenarioNodes.get(i.getId()).getUpperFlowRateBound()*-1.0;
        }
    }

    public boolean hasFlowRateBound(GasLibIntersection i) {
        GasLibScenarioNode n = scenarioNodes.get(i.getId());
        if (n == null) {
            return false;
        }
        boolean b = !(n.getUpperFlowRateBound() == (0 * UnitsTools.m3/UnitsTools.hr)) && !(n.getLowerFlowRateBound() == (0 * UnitsTools.m3/UnitsTools.hr));
        return (n != null) && ((n.hasExactFlowBound() && !(n.getFlowRateBound() == (0 * UnitsTools.m3/UnitsTools.hr)))
                || b);
    }

    public void printBalance() {
        double supplyL = 0 * UnitsTools.m3/UnitsTools.hr;
        double supplyU = 0 * UnitsTools.m3/UnitsTools.hr;
        double demandL = 0 * UnitsTools.m3/UnitsTools.hr;
        double demandU = 0 * UnitsTools.m3/UnitsTools.hr;
        double sum = 0 * UnitsTools.m3/UnitsTools.hr;
        double sumD = 0.0;
        double sumB = 0.0;
        for (GasLibScenarioNode node : scenarioNodes.values()) {
            if (node.hasExactFlowBound() && node.getFlowRateBound() == (0 * UnitsTools.m3/UnitsTools.hr)) {
                continue;
            }
            if (node.getType() == Type.ENTRY) {
                System.out.println(String.valueOf(node.getFlowRateBound() / 1000).replace(".", ","));
            } else {
                System.out.println("-" + String.valueOf(node.getFlowRateBound() / 1000).replace(".", ","));
            }
            
        }
    }

}
