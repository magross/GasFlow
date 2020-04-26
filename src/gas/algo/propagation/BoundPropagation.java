/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gas.algo.propagation;

import ds.graph.AbstractEdge;
import ds.graph.DynamicNetwork;
import ds.graph.GasEdge;
import ds.graph.GasNode;
import gas.io.ConnectionType;
import gas.io.GraphConversion;
import gas.io.IntersectionType;
import gas.io.gaslib.GasLibConnection;
import gas.io.gaslib.GasLibIntersection;
import gas.io.gaslib.GasLibIntersections;
import gas.io.gaslib.GasLibNetworkFile;
import gas.io.gaslib.GasLibPipe;
import gas.io.gaslib.GasLibScenario;
import gas.io.gaslib.GasLibScenarioFile;
import gas.io.gaslib.GasLibScenarioNode;
import static gas.io.gaslib.GasLibScenarioNode.Type.ENTRY;
import static gas.io.gaslib.GasLibScenarioNode.Type.EXIT;
import gas.io.gaslib.GasLibSink;
import gas.io.gaslib.GasLibSource;
import gas.io.tikz.TikZ;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import units.UnitsTools;

/**
 *
 * @author Martin
 */
public class BoundPropagation<N, E extends AbstractEdge<N>> {

    public static final boolean DEBUG = false;
    public static final boolean DEBUG_FINE = false;

    private transient GraphConversion<GasLibIntersection, GasLibConnection> conversion;
    private int serialReductions;
    private int leafReductions;
    private int parallelReductions;
    private DynamicNetwork<N, E> network;
    private Map<Object, IntersectionType> typeOverrides;
    private transient Map<Object, GasLibConnection> edgeConnections;
    private transient Map<Object, GasLibIntersection> nodeIntersections;
    private transient GasLibNetworkFile networkFile;
    private Map<E, Double> edgeParameters;

    public BoundPropagation() {
        edgeParameters = new HashMap<>();
        typeOverrides = new HashMap<>();
    }

    public void setGasNetworkFile(GasLibNetworkFile gasNetworkFile) {
        conversion = gasNetworkFile.getDynamicNetwork();
        network = (DynamicNetwork) conversion.getGraph();
        networkFile = gasNetworkFile;
        edgeConnections = (Map) conversion.getEdgeConnections();
        nodeIntersections = (Map) conversion.getNodeIntersections();
        for (E edge : network.edges()) {
            edgeParameters.put(edge, computeParameter(edge));
        }
    }

    protected GasLibScenarioFile scenarioFile;
    protected GasLibScenario scenario;

    public void setGasScenarioFile(GasLibScenarioFile scenarioFile) {
        this.scenarioFile = scenarioFile;
        scenario = scenarioFile.getScenarios().values().iterator().next();

    }

    public DynamicNetwork<N, E> getNetwork() {
        return network;
    }

    protected double computeParameter(E edge) {
        GasLibConnection connection = edgeConnections.get(edge);
        if (connection instanceof GasLibPipe) {
            GasLibPipe pipe = (GasLibPipe) connection;
            return pipe.computeSiamCoefficient(networkFile);
        } else {
            return 0 * UnitsTools.kg*UnitsTools.J/UnitsTools.g/(UnitsTools.mm*UnitsTools.mm*UnitsTools.mm*UnitsTools.mm*UnitsTools.mm);
        }
    }

    protected double serialComposition(E edge1, E edge2) {
        return edgeParameters.get(edge1) + edgeParameters.get(edge2);
    }

    protected double parallelComposition(E edge1, E edge2) {
        double denom = Math.sqrt(edgeParameters.get(edge1)) + Math.sqrt(edgeParameters.get(edge2));
        return edgeParameters.get(edge1)*edgeParameters.get(edge2)/(denom*denom);
    }

    public void setNetwork(DynamicNetwork<N, E> network) {
        this.network = network;
    }

    private final Predicate<E> combineableParallels = (e -> edgeConnections == null
            || ConnectionType.getType(edgeConnections.get(e)) == ConnectionType.PIPE
            || ConnectionType.getType(edgeConnections.get(e)) == ConnectionType.SHORT_PIPE
            || ConnectionType.getType(edgeConnections.get(e)) == ConnectionType.UNKNOWN);

    private final Predicate<N> contractableNode = (n -> network.degree(n) == 2
            && (nodeIntersections != null && (!nodeIntersections.containsKey(n) || IntersectionType.getType(nodeIntersections.get(n)) == IntersectionType.NODE))
            && (combineableParallels.test(network.incidentEdges(n).getFirst()) && combineableParallels.test(network.incidentEdges(n).getLast())));

    private final Predicate<E> removableLeafEdge = (e -> edgeConnections == null
            || !edgeConnections.containsKey(e)
            || ConnectionType.getType(edgeConnections.get(e)) == ConnectionType.PIPE
            || ConnectionType.getType(edgeConnections.get(e)) == ConnectionType.SHORT_PIPE
            || ConnectionType.getType(edgeConnections.get(e)) == ConnectionType.RESISTOR
            || ConnectionType.getType(edgeConnections.get(e)) == ConnectionType.VALVE);

    protected List<List<E>> findParallelPassiveEdges() {
        List<List<E>> result = new LinkedList<>();
        Set<E> processed = new HashSet<>();
        for (E edge : network.edges()) {
            if (processed.contains(edge)) {
                continue;
            }
            List<E> candidates = new LinkedList<>();
            candidates.addAll(network.getEdges(edge.start(), edge.end()));
            candidates.addAll(network.getEdges(edge.end(), edge.start()));
            processed.addAll(candidates);
            List<E> list = candidates.stream().filter(combineableParallels).collect(Collectors.toList());
            if (list.size() > 1) {
                result.add(list);
            }
        }
        return result;
    }

    public boolean validateDegrees() {
        if (!DEBUG) {
            return true;
        }
        for (N node : network.nodes()) {
            GasLibIntersection i = nodeIntersections.get(node);
            int deg = network.degree(node);
            for (GasLibConnection c : edgeConnections.values()) {
                if (c.getFrom().getId().equals(i.getId()) || c.getTo().getId().equals(i.getId())) {
                    deg = deg - 1;
                }
            }
            if (deg != 0) {
                if (DEBUG) {
                    System.out.println("validateDegrees: " + node + " inters=" + i.getId() + " " + deg + " " + network.incidentEdges(node));
                }

                for (GasLibConnection c : edgeConnections.values()) {
                    if (c.getFrom().getId().equals(i.getId()) || c.getTo().getId().equals(i.getId())) {
                        if (DEBUG) {
                            System.out.println("  " + c.getId());
                        }
                    }

                }
                return false;
            }
        }
        return true;
    }

    protected boolean contractDegreeTwoNodes() {
        boolean rerun = false;
        List<N> degreeTwo = network.nodes().stream()
                .filter(contractableNode)
                .collect(Collectors.toList());
        if (DEBUG) {
            System.out.println("BP Serial: Start ");
        }
        while (!degreeTwo.isEmpty()) {

            N node = degreeTwo.remove(0);
            GasLibIntersection i = nodeIntersections.get(node);
            if (DEBUG) {
                System.out.println(scenario.getNode(i) + " " + scenario.getLowerFlowRateBound(i) + " " + scenario.getUpperFlowRateBound(i));
            }
            if (network.degree(node) != 2 || scenario.hasFlowRateBound(i)) {
                continue;
            }
            if (DEBUG) {
                System.out.println("BP Serial: Removing " + i);
            }
            rerun = true;
            E edge = network.incidentEdges(node).getFirst();
            E edge2 = network.incidentEdges(node).getLast();
            N other = edge.opposite(node);
            N other2 = edge2.opposite(node);
            network.removeNode(node);

            scenario.removeNode(i);
            //scenarioFile.removeNodes(networkFile);
            network.removeEdge(edge);
            network.removeEdge(edge2);
            if (node instanceof GasNode) {
                networkFile.removeNodeAndIntersection((GasNode) node);
                nodeIntersections.remove(node);
            } else {
                System.out.println("WARNING NODE " + node);
            }
            GasLibConnection edgeC = edgeConnections.get(edge);
            GasLibConnection edge2C = edgeConnections.get(edge2);
            if (edge instanceof GasEdge) {
                networkFile.removeEdgeAndConnection((GasEdge) edge);
                networkFile.removeEdgeAndConnection((GasEdge) edge2);
                edgeConnections.remove(edge);
                edgeConnections.remove(edge2);
            } else {
                System.out.println("WARNING EDGE " + edge);
            }
            serialReductions++;

            if (network.getEdges(other, other2).isEmpty() && other != other2) {
                // Create new serial edge!

                E newEdge = network.createEdge(other, other2);
                edgeParameters.put(newEdge, serialComposition(edge, edge2));
                if (newEdge instanceof GasEdge) {
                    if (DEBUG) {
                        System.out.println("Serial Edge");
                    }
                    GasLibPipe newC = (GasLibPipe) networkFile.addEdgeAndConnection((GasEdge) newEdge, serialComposition(edge, edge2));
                    if (newC.getLength() == 0 * UnitsTools.mm) {
                        System.err.println(edgeC + " " + edge2C);

                    }
                    edgeConnections.put((GasEdge) newEdge, newC);
                }
            } else if (!network.getEdges(other, other2).isEmpty()) {
                if (DEBUG) {
                    System.out.println("O-O2 Edge");
                }
                // Triggered parallel reduction - edge removal
                parallelReductions++;
                switch (network.degree(other)) {
                    case 0:
                        if (true) {
                            System.out.println("Other, Case 0");
                        }
                        network.removeNode(other);

                        degreeTwo.remove(other);
                        break;
                    case 1:
                        if (DEBUG) {
                            System.out.println("Other, Case 1");
                        }
                        rerun = true;
                        break;
                    case 2:
                        if (DEBUG) {
                            System.out.println("Other, Case 2");
                        }
                        degreeTwo.add(other);
                        break;
                    default:
                        if (DEBUG) {
                            System.out.println("Other, Case 3+");
                        }
                }
                switch (network.degree(other2)) {
                    case 0:
                        if (true) {
                            System.out.println("Other2, Case 0");
                        }
                        network.removeNode(other2);
                        degreeTwo.remove(other2);
                        break;
                    case 1:
                        if (DEBUG) {
                            System.out.println("Other2, Case 1");
                        }
                        rerun = true;
                        break;
                    case 2:
                        if (DEBUG) {
                            System.out.println("Other2, Case 2");
                        }
                        degreeTwo.add(other2);
                        break;
                    default:
                        if (DEBUG) {
                            System.out.println("Other, Case 3+");
                        }
                }

            } else if (other == other2) {

                if (DEBUG) {
                    System.out.println("O=O2");
                }
                // Triggered parallel reduction - edge removal
                parallelReductions++;
                switch (network.degree(other)) {
                    case 0:
                        if (true) {
                            System.out.println("Other, Case 0");
                        }
                        network.removeNode(other);
                        degreeTwo.remove(other);
                        break;
                    case 1:
                        if (true) {
                            System.out.println("Other, Case 1");
                        }
                        E edge3 = network.incidentEdges(other).getFirst();
                        network.removeEdge(edge3);
                        network.removeNode(other);
                        degreeTwo.remove(other);
                        rerun = true;
                        break;
                    case 2:
                        if (DEBUG) {
                            System.out.println("Other, Case 2");
                        }
                        degreeTwo.add(other);
                        break;
                    default:
                        if (DEBUG) {
                            System.out.println("Other, Case 3+");
                        }
                }
                switch (network.degree(other2)) {
                    case 0:
                        if (true) {
                            System.out.println("Other2, Case 0");
                        }
                        network.removeNode(other2);
                        degreeTwo.remove(other2);
                        break;
                    case 1:
                        if (true) {
                            System.out.println("Other2, Case 1");
                        }
                        E edge3 = network.incidentEdges(other2).getFirst();
                        network.removeEdge(edge3);
                        network.removeNode(other2);
                        degreeTwo.remove(other2);
                        rerun = true;
                        break;
                    case 2:
                        if (DEBUG) {
                            System.out.println("Other2, Case 2");
                        }
                        degreeTwo.add(other2);
                        break;
                    default:
                        if (DEBUG) {
                            System.out.println("Other, Case 3+");
                        }
                }

            } else {
                System.out.println("EA!");
                assert false;
            }
            if (!networkFile.validate()) {
                System.out.printf("BP Serial Removal caused an inconcistency:\n  node=%s\n  edge=%s\n  edge2=%s\n  other=%s\n  other2=%s\n  node-i=%s\n"
                        + "  other-i=%s\n  other2-i=%s\n  edge-c=%s\n  edge2-c=%s\n", node, edge, edge2, other, other2,
                        nodeIntersections.get(node), nodeIntersections.get(other), nodeIntersections.get(other2),
                        edgeConnections.get(edge), edgeConnections.get(edge2));
                System.exit(0);
            } else if (!validateDegrees()) {
                System.out.printf("BP Serial Removal caused a degree inconcistency:\n  node=%s\n  edge=%s\n  edge2=%s\n  other=%s\n  other2=%s\n  node-i=%s\n"
                        + "  other-i=%s\n  other2-i=%s\n  edge-c=%s\n  edge2-c=%s\n", node, edge, edge2, other, other2,
                        nodeIntersections.get(node), nodeIntersections.get(other), nodeIntersections.get(other2),
                        edgeConnections.get(edge), edgeConnections.get(edge2));
                System.exit(20);
            } else {
                if (DEBUG) {
                    System.out.printf("BP Serial Removal:\n  node=%s\n  edge=%s\n  edge2=%s\n  other=%s\n  other2=%s\n  node-i=%s\n"
                            + "  other-i=%s\n  other2-i=%s\n  edge-c=%s\n  edge2-c=%s\n", node, edge, edge2, other, other2,
                            nodeIntersections.get(node), nodeIntersections.get(other), nodeIntersections.get(other2),
                            edgeConnections.get(edge), edgeConnections.get(edge2));
                }
            }
        }
        return rerun;
    }

    public void run() {
        typeOverrides = new HashMap<>();
        boolean propagated;
        int initialNumberOfEdges = network.numberOfEdges();

        if (DEBUG) {
            System.out.println("");
            scenario.validate();
        }

        do {
            if (DEBUG) {
                System.out.println("BP: Starting Main Interation:");
            }
            propagated = false;
            N candidate;

            List<List<E>> candidateLists = findParallelPassiveEdges();
            while (!candidateLists.isEmpty()) {
                // Parallel reduction
                if (DEBUG) {
                    System.out.println("BP Parallel Removal");
                }
                List<E> candidates = candidateLists.remove(0);
                parallelReductions += candidates.size() - 1;
                while (candidates.size() > 1) {
                    E edge0 = candidates.get(0);
                    E edge1 = candidates.get(1);
                    edgeParameters.put(edge1, parallelComposition(edge0, edge1));

                    network.removeEdge(edge0);
                    if (edge0 instanceof GasEdge) {
                        networkFile.removeEdgeAndConnection((GasEdge) edge0);
                        edgeConnections.remove(edge0);
                    }
                    if (edge1 instanceof GasEdge) {
                        networkFile.updateEdgeConnection((GasEdge) edge1, parallelComposition(edge0, edge1));
                    }
                    candidates.remove(0);
                }
                propagated = true;
            }

            if (DEBUG && !networkFile.validate()) {
                System.out.println("BP: Parallel edge removal caused inconsistency.");
                System.exit(0);
            }

            if (propagated) {
                continue;
            }
            /*
            if (!networkFile.validate()) {
                System.out.println("BP: Serial edge removal caused inconsistency.");
                System.exit(0);
            }*/
            List<N> leafCandidates = network.nodes().stream()
                    .filter(n -> network.degree(n) == 1 && removableLeafEdge.test(network.incidentEdges(n).getFirst()))
                    .collect(Collectors.toList());

            while (!leafCandidates.isEmpty()) {
                // Leaf remocal
                candidate = leafCandidates.remove(0);
                if (DEBUG) {
                    System.out.println("");
                    System.out.println("BP Remove Leaf " + candidate + " " + network.degree(candidate) + " " + network.incidentEdges(candidate));
                }
                if (network.degree(candidate) == 0) {
                    network.removeNode(candidate);
                    continue;
                }

                GasLibIntersection i = nodeIntersections.get(candidate);
                for (GasLibConnection c : networkFile.getConnections().getMap().values()) {
                    if (c.getFrom().getId().equals(i.getId())) {
                        if (DEBUG_FINE) {
                            System.out.println("  incident in file: " + c.getId());
                        }
                    }
                    if (c.getTo().getId().equals(i.getId())) {
                        if (DEBUG_FINE) {
                            System.out.println("  incident in file: " + c.getId());
                        }
                    }
                }
                E leafEdge = network.incidentEdges(candidate).getFirst();
                N neighbor = leafEdge.opposite(candidate);
                if (candidate instanceof GasNode) {
                    GasLibIntersection candidateI = conversion.getIntersection((GasNode) candidate);
                    double balanceC = scenario.getBalance(candidateI);
                    double lowerFlowRateBoundC = scenario.getLowerFlowRateBound(candidateI);
                    double upperFlowRateBoundC = scenario.getUpperFlowRateBound(candidateI);

                    GasLibIntersection neighborI = conversion.getIntersection((GasNode) neighbor);
                    double balanceN = scenario.getBalance(neighborI);
                    double lowerFlowRateBoundN = scenario.getLowerFlowRateBound(neighborI);
                    double upperFlowRateBoundN = scenario.getUpperFlowRateBound(neighborI);
                    double sum = balanceN + balanceC;
                    double sumL = lowerFlowRateBoundC + lowerFlowRateBoundN;
                    double sumU = upperFlowRateBoundC + upperFlowRateBoundN;

                    if (DEBUG) {
                        System.out.println(lowerFlowRateBoundC + " " + lowerFlowRateBoundN + " " + upperFlowRateBoundC + " " + upperFlowRateBoundN + " " + sumL + " " + sumU);
                    }

                    if (sumU < (0 * UnitsTools.m3/UnitsTools.hr)) {
                        if (balanceN == (0 * UnitsTools.m3/UnitsTools.hr)) {
                            networkFile.changeNodeTo(neighborI, candidateI);
                        }
                        if (balanceN > (0 * UnitsTools.m3/UnitsTools.hr)) {
                            networkFile.changeNodeTo(neighborI, candidateI);
                        }
                        GasLibScenarioNode scenarioNode = scenario.getNode(neighborI);
                        scenarioNode.setType(EXIT);

                        if (sumL == sumU) {
                            scenarioNode.setFlowBound(sumL*-1.0);
                        } else {
                            scenarioNode.setUpperFlowBound(sumU*-1.0);
                            scenarioNode.setLowerFlowBound(sumL*-1.0);
                        }

                        //scenario.updateFlowBounds(neighborI, sumL, sumU);
                        if (sumL == sumU) {
                            //
                        }
                    } else if (sumL > (0 * UnitsTools.m3/UnitsTools.hr)) {
                        if (balanceN == (0 * UnitsTools.m3/UnitsTools.hr)) {
                            networkFile.changeNodeTo(neighborI, candidateI);
                        }
                        if (balanceN > (0 * UnitsTools.m3/UnitsTools.hr)) {
                            networkFile.changeNodeTo(neighborI, candidateI);
                        }
                        GasLibScenarioNode scenarioNode = scenario.getNode(neighborI);
                        scenarioNode.setType(ENTRY);

                        if (sumL == sumU) {
                            scenarioNode.setFlowBound(sumL);
                        } else {
                            scenarioNode.setUpperFlowBound(sumU);
                            scenarioNode.setLowerFlowBound(sumL);
                        }
                    } else {
                        if (balanceN == (0 * UnitsTools.m3/UnitsTools.hr)) {
                            networkFile.changeNodeTo(neighborI, candidateI);
                        }
                        if (balanceN < (0 * UnitsTools.m3/UnitsTools.hr)) {
                            networkFile.changeNodeTo(neighborI, candidateI);
                        }
                        GasLibScenarioNode scenarioNode = scenario.getNode(neighborI);
                        scenarioNode.setType(ENTRY);

                        //System.err.println("Warning: " + sumL);
                        if (sumL == sumU) {
                            scenarioNode.setFlowBound(sumL);
                        } else {
                            scenarioNode.setUpperFlowBound(sumU);
                            scenarioNode.setLowerFlowBound(sumL);
                        }
                    }
                    
                    
                    GasLibScenarioNode candidateSN = scenario.getNode(candidateI);
                    GasLibScenarioNode neighborSN = scenario.getNode(neighborI);
                    double beta = edgeParameters.get(leafEdge);
                    if (candidateSN.hasLowerPressureBound()) {
                        double oldLBPSq = candidateSN.getLowerPressureBound();
                        oldLBPSq = oldLBPSq*oldLBPSq;
                        double lowerFlowSq = candidateSN.getLowerFlowRateBound()*(0.82 * UnitsTools.kg/UnitsTools.m3);
                        lowerFlowSq = lowerFlowSq * lowerFlowSq;
                        double newLBPSq;
                        if (leafEdge.start().equals(candidate)) {
                            newLBPSq = oldLBPSq - (beta*lowerFlowSq);
                        } else {
                            newLBPSq = oldLBPSq + (beta*lowerFlowSq);
                        }
                        if (newLBPSq < (1.0267 * UnitsTools.bar*UnitsTools.bar)) {
                            newLBPSq = 1.0267 * UnitsTools.bar*UnitsTools.bar;
                        }
                        double newLBP = Math.sqrt(newLBPSq);
                        
                        if (!neighborSN.hasLowerPressureBound() || newLBP > neighborSN.getLowerPressureBound()) {
                            neighborSN.setLowerPressureBound(newLBP);
                            //System.out.println(newLBP);
                            if (newLBP == 0) {
                                System.out.println(beta + " " + candidateSN.getLowerPressureBound() + " " + newLBP + " " + newLBPSq);
                            }
                        }
                    }
                    if (candidateSN.hasUpperPressureBound()) {
                        double oldUBPSq = candidateSN.getUpperPressureBound();
                        oldUBPSq = oldUBPSq*oldUBPSq;
                        double upperFlowSq = candidateSN.getUpperFlowRateBound() * (0.82 * UnitsTools.kg/UnitsTools.m3);
                        upperFlowSq = upperFlowSq * upperFlowSq;
                        double newUBPSq;
                        if (leafEdge.start().equals(candidate)) {
                            newUBPSq = oldUBPSq - (beta*upperFlowSq);
                        } else {
                            newUBPSq = oldUBPSq + (beta*upperFlowSq);
                        }
                        if (newUBPSq < 0 * UnitsTools.bar*UnitsTools.bar) {
                            newUBPSq = 0 * UnitsTools.bar*UnitsTools.bar;
                        }
                        if (newUBPSq < 1.01325 * UnitsTools.bar*UnitsTools.bar) {
                            newUBPSq = 1.01325 * UnitsTools.bar*UnitsTools.bar;
                        } 
                        double newUBP = Math.sqrt(newUBPSq);
                       
                        if (!neighborSN.hasUpperPressureBound() || newUBP < neighborSN.getUpperPressureBound()) {
                            neighborSN.setUpperPressureBound(newUBP);
                            //System.out.println(newUBP);
                        }                        
                    }
                    
                    scenario.removeNode(candidateI);
                    //System.out.println("Validate: ");
                    if (DEBUG) {
                        scenario.validate();
                    }
                    
                }

                network.removeNode(candidate);
                if (candidate instanceof GasNode) {
                    networkFile.removeNodeAndIntersection((GasNode) candidate);
                }
                if (leafEdge instanceof GasEdge) {
                    networkFile.removeEdgeAndConnection((GasEdge) leafEdge);
                }
                if (network.degree(neighbor) == 1 && removableLeafEdge.test(network.incidentEdges(neighbor).getFirst())) {
                    leafCandidates.add(neighbor);
                }
                if (nodeIntersections != null) {
                    IntersectionType first, second;
                    if (typeOverrides.containsKey(candidate)) {
                        first = typeOverrides.get(candidate);
                    } else {
                        first = IntersectionType.getType(nodeIntersections.get(candidate));
                    }
                    if (typeOverrides.containsKey(neighbor)) {
                        second = typeOverrides.get(neighbor);
                    } else {
                        second = IntersectionType.getType(nodeIntersections.get(neighbor));
                    }
                    typeOverrides.put(neighbor, IntersectionType.combine(first, second));
                }
                leafReductions++;
                propagated = true;

                if (DEBUG && !networkFile.validate()) {
                    System.out.printf("BP: Leaf edge removal caused inconsistency:\n  candidate=%s\n  leafEdge=%s\n  neighbor=%s\n"
                            + "  candidate-i=%s\n  neighbor-i=%s\n  leafEdge-c=%s\n", candidate, leafEdge, neighbor,
                            nodeIntersections.get(candidate), nodeIntersections.get(neighbor),
                            edgeConnections.get(leafEdge));
                    System.exit(0);
                }

                if (candidate instanceof GasNode) {
                    nodeIntersections.remove(candidate);
                }
                if (leafEdge instanceof GasEdge) {
                    edgeConnections.remove(leafEdge);
                }

                if (DEBUG && !validateDegrees()) {
                    System.out.printf("BP: Leaf edge removal caused degree inconsistency:\n  candidate=%s\n  leafEdge=%s\n  neighbor=%s\n"
                            + "  candidate-i=%s\n  neighbor-i=%s\n  leafEdge-c=%s\n", candidate, leafEdge, neighbor,
                            nodeIntersections.get(candidate), nodeIntersections.get(neighbor),
                            edgeConnections.get(leafEdge));
                    System.exit(2);
                }

            }

            if (propagated) {
                continue;
            }

            propagated = contractDegreeTwoNodes();// || propagated;            
        } while (propagated);

        LinkedList<GasLibIntersection> makeSink = new LinkedList<>();
        LinkedList<GasLibIntersection> makeSource = new LinkedList<>();
        if (networkFile.getIntersections() instanceof GasLibIntersections) {
            for (GasLibIntersection i : networkFile.getIntersections().getMap().values()) {
                double balance = scenario.getLowerFlowRateBound(i);
                if (balance < (0 * UnitsTools.m3/UnitsTools.hr) && !(i instanceof GasLibSink)) {
                    makeSink.add(i);
                }
                if (balance == (0 * UnitsTools.m3/UnitsTools.hr)) {

                }
                if (balance > (0 * UnitsTools.m3/UnitsTools.hr) && !(i instanceof GasLibSource)) {
                    makeSource.add(i);
                }
            }
        }

        for (GasLibIntersection i : makeSink) {
            GasLibSink newSink = new GasLibSink();
            newSink.setId(i.getId());
            networkFile.getIntersections().getMap().put(i.getId(), newSink);
        }

        for (GasLibIntersection i : makeSource) {
            GasLibSource newSource = new GasLibSource();
            newSource.setId(i.getId());
            networkFile.getIntersections().getMap().put(i.getId(), newSource);
        }

        System.out.printf(" Propagation finished. %1$s nodes and %2$s edges remaining. This is a %3$s%4$s reduction in the number of edges.\n", network.numberOfNodes(), network.numberOfEdges(), Math.round(100 - 100.0 * network.numberOfEdges() / initialNumberOfEdges), "%");

        if (DEBUG) {
            scenario.printBalance();
        }

    }

    public int getSerialReductions() {
        return serialReductions;
    }

    public int getLeafReductions() {
        return leafReductions;
    }

    public int getParallelReductions() {
        return parallelReductions;
    }

    public void write(File tikzFile) {
        Function<GasNode, String> nodeTrans = new NodeStyleMapping(nodeIntersections, typeOverrides);
        conversion.setNodeTransformation(nodeTrans);
        TikZ tikzed = conversion.toTikZ();
        tikzed.writeToFile(tikzFile.getPath());
        tikzed.compileTeX(tikzFile);
    }

    public void update() {
        networkFile.update((Map<GasEdge, Double>) edgeParameters);
    }

}
