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
import javax.measure.quantity.Pressure;
import javax.measure.quantity.VolumetricFlowRate;
import javax.measure.unit.Unit;
import org.jscience.physics.amount.Amount;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author Martin Groß
 */
public class GasLibScenario extends XMLElementWithID {

    private double probability;
    //private Amount<Temperature> temperatureMax;
    //private Amount<Temperature> temperatureMin;

    //private Map<GasLibIntersection, Pressure> lowerPressureBounds;
    //private Map<GasLibIntersection, Pressure> upperPressureBounds;
    //private Map<GasLibIntersection, MassFlowRate> balance;
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

    public Amount<Pressure> getLowerPressureBound(XMLIntersection i) {
        return scenarioNodes.get(i.getId()).getLowerPressureBound();
    }

    public Amount<Pressure> getUpperPressureBound(XMLIntersection i) {
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

    public Amount<VolumetricFlowRate> getBalance(XMLIntersection i) {
        GasLibScenarioNode n = scenarioNodes.get(i.getId());
        if (n == null) {
            return (Amount<VolumetricFlowRate>) Amount.valueOf("0 m^3/h");
        }
        //assert n.getLowerFlowRateBound().equals(n.getUpperFlowRateBound());
        if (getNode(i).getType() == ENTRY) {
            return n.getLowerFlowRateBound();
        } else {
            return n.getLowerFlowRateBound().times(-1.0);
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
                System.out.println(node.getFlowRateBound().approximates(Amount.valueOf("0 m^3/h")));
            }
            if (node.hasExactFlowBound() && node.getFlowRateBound().approximates(Amount.valueOf("0 m^3/h"))) {
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
        Amount<VolumetricFlowRate> supplyL = (Amount<VolumetricFlowRate>) Amount.valueOf("0 m^3/h");
        Amount<VolumetricFlowRate> supplyU = (Amount<VolumetricFlowRate>) Amount.valueOf("0 m^3/h");
        Amount<VolumetricFlowRate> demandL = (Amount<VolumetricFlowRate>) Amount.valueOf("0 m^3/h");
        Amount<VolumetricFlowRate> demandU = (Amount<VolumetricFlowRate>) Amount.valueOf("0 m^3/h");
        Amount<VolumetricFlowRate> sum = (Amount<VolumetricFlowRate>) Amount.valueOf("0 m^3/h");
        double sumD = 0.0;
        double sumB = 0.0;
        for (GasLibScenarioNode node : scenarioNodes.values()) {
            if (node.hasExactFlowBound() && node.getFlowRateBound().approximates(Amount.valueOf("0 m^3/h"))) {
                continue;
            }
            Amount<VolumetricFlowRate> lowerFlowRateBound = node.getLowerFlowRateBound();
            Amount<VolumetricFlowRate> upperFlowRateBound = node.getUpperFlowRateBound();
            if (node.getType() == Type.ENTRY) {
                //System.out.println("ENTRY " + node.getId() + " " + lowerFlowRateBound + " " + upperFlowRateBound);
                supplyL = supplyL.plus(lowerFlowRateBound);
                supplyU = supplyU.plus(upperFlowRateBound);
                sum = sum.plus(lowerFlowRateBound);
                sumD += lowerFlowRateBound.doubleValue((Unit<VolumetricFlowRate>) Unit.valueOf("m^3/h"));
                sumB += node.getFlowRateBound().doubleValue((Unit<VolumetricFlowRate>) Unit.valueOf("m^3/h"));
            } else if (node.getType() == Type.EXIT) {
                //System.out.println("EXIT " + node.getId() + " "  + lowerFlowRateBound + " " + upperFlowRateBound);
                demandL = demandL.plus(lowerFlowRateBound);
                demandU = demandU.plus(upperFlowRateBound);
                sum = sum.minus(lowerFlowRateBound);
                sumD -= lowerFlowRateBound.doubleValue((Unit<VolumetricFlowRate>) Unit.valueOf("m^3/h"));
                sumB -= node.getFlowRateBound().doubleValue((Unit<VolumetricFlowRate>) Unit.valueOf("m^3/h"));
            }
        }
        if (DEBUG) {
            System.out.println("Validation: " + supplyL + " " + supplyU + " " + demandL + " " + demandU + " ");
        }
        if (DEBUG) {
            System.out.println(sum + " " + supplyL.minus(demandL) + " " + sumD + " " + sumB);
        }
        return true;
    }

    public Amount<VolumetricFlowRate> getLowerFlowRateBound(GasLibIntersection i) {
        if (scenarioNodes.get(i.getId()) == null) {
            return (Amount<VolumetricFlowRate>) Amount.valueOf("0 m^3/h");
        }
        if (getNode(i).getType() == ENTRY) {
            return scenarioNodes.get(i.getId()).getLowerFlowRateBound();
        } else {
            return scenarioNodes.get(i.getId()).getLowerFlowRateBound().times(-1.0);
        }

    }

    public Amount<VolumetricFlowRate> getUpperFlowRateBound(GasLibIntersection i) {
        if (scenarioNodes.get(i.getId()) == null) {
            return (Amount<VolumetricFlowRate>) Amount.valueOf("0 m^3/h");
        }
        if (getNode(i).getType() == ENTRY) {
            return scenarioNodes.get(i.getId()).getUpperFlowRateBound();
        } else {
            return scenarioNodes.get(i.getId()).getUpperFlowRateBound().times(-1.0);
        }
    }

    public boolean hasFlowRateBound(GasLibIntersection i) {
        GasLibScenarioNode n = scenarioNodes.get(i.getId());
        if (n == null) {
            return false;
        }
        boolean b = !n.getUpperFlowRateBound().approximates(Amount.valueOf("0 m^3/h")) && !n.getLowerFlowRateBound().approximates(Amount.valueOf("0 m^3/h"));
        return (n != null) && ((n.hasExactFlowBound() && !n.getFlowRateBound().approximates(Amount.valueOf("0 m^3/h")))
                || b);
    }

    public void printBalance() {
        Amount<VolumetricFlowRate> supplyL = (Amount<VolumetricFlowRate>) Amount.valueOf("0 m^3/h");
        Amount<VolumetricFlowRate> supplyU = (Amount<VolumetricFlowRate>) Amount.valueOf("0 m^3/h");
        Amount<VolumetricFlowRate> demandL = (Amount<VolumetricFlowRate>) Amount.valueOf("0 m^3/h");
        Amount<VolumetricFlowRate> demandU = (Amount<VolumetricFlowRate>) Amount.valueOf("0 m^3/h");
        Amount<VolumetricFlowRate> sum = (Amount<VolumetricFlowRate>) Amount.valueOf("0 m^3/h");
        double sumD = 0.0;
        double sumB = 0.0;
        for (GasLibScenarioNode node : scenarioNodes.values()) {
            if (node.hasExactFlowBound() && node.getFlowRateBound().approximates(Amount.valueOf("0 m^3/h"))) {
                continue;
            }
            if (node.getType() == Type.ENTRY) {
                System.out.println(String.valueOf(node.getFlowRateBound().doubleValue((Unit<VolumetricFlowRate>) Unit.valueOf("m^3/h")) / 1000).replace(".", ","));
            } else {
                System.out.println("-" + String.valueOf(node.getFlowRateBound().doubleValue((Unit<VolumetricFlowRate>) Unit.valueOf("m^3/h")) / 1000).replace(".", ","));
            }
            
        }
    }

}
