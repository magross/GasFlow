/**
 * GurobiPrimalOddcutLP.java
 *
 */
package aaa.gurobi;

import ds.graph.Edge;
import ds.graph.Node;
import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Martin Groß
 */
public class GurobiPrimalOddcutLP {
    
    public static boolean DEBUG = false;

    public static void main(String[] args) {
        // The number of spikes of the caterpillar graph (the resulting tree will have numberOfSpikes+2 leaves).
        int numberOfSpikes = 7;
        // The integral optimum value
        int integerOptimum = numberOfSpikes + 1;
        if (args.length == 1) {
            numberOfSpikes = Integer.parseInt(args[0]);
        }
        // Create the tree.
        CaterpillarGraph caterpillarGraph = new CaterpillarGraph(numberOfSpikes);
        // Create the link set.
        List<Edge> links = caterpillarGraph.createLinkSet();
        try {
            // Create the interface to Gurobi.
            GRBEnv env = new GRBEnv("oddcut.log");
            GRBModel model = new GRBModel(env);

            // Create variables for every link.
            GRBVar[] x = new GRBVar[links.size()];
            // Create a mapping of Gurobi variables to strings for debug purposes.
            Map<Edge, GRBVar> linkToVariable = new HashMap<>();
            int index = 0;
            for (Edge link : links) {
                // Lower Bound, Upper Bound, Objective Coefficient, Type, Name
                x[index] = model.addVar(0.0, 1.0, 1.0, GRB.CONTINUOUS, "x" + index);
                System.out.println("x" + index + ": " + link);
                linkToVariable.put(link, x[index]);
                ++index;
            }

            // A map to store the covering links for each edge - we will use those again for the oddcut constraints
            Map<Edge, List<Edge>> coveringLinkMap = new HashMap<>();
            // Create cut constraints: For every edge...
            for (Edge edge : caterpillarGraph.edges()) {
                // Create a new linear constraint
                GRBLinExpr cutConstraint = new GRBLinExpr();
                List<Edge> coveringLinks = links.stream()
                        // Find the links covering the current edge by filtering all links whose path contains the current edge
                        .filter(link -> caterpillarGraph.getPath(link.start(), link.end()).contains(edge))
                        .collect(Collectors.toList());
                coveringLinkMap.put(edge, coveringLinks);
                coveringLinks.stream()
                        // ... then converting links to LP variables
                        .map(link -> linkToVariable.get(link))
                        // ... and adding those variables to the left side of the cut constraint
                        .forEach(var -> cutConstraint.addTerm(1.0, var));
                // Finally, we add this left side with >= 1.0 to the model
                model.addConstr(cutConstraint, GRB.GREATER_EQUAL, 1.0, "cut_for_" + edge.toString().replaceAll(" ", ""));
            }

            LinkedList<Edge> edges = new LinkedList<>();
            for (Edge edge : caterpillarGraph.edges()) {
                edges.add(edge);
            }

            // Create a characteristic vector that will be used as a representation of a subset of the node set
            CharacteristicVector<Node> subset = new CharacteristicVector<>(0);
            // Iterate over all subsets of the node sets (not optimized right now!)
            for (int i = 1; i <= (1 << (caterpillarGraph.numberOfNodes())); i++) {
                // The cuts for a node set and its complement are the same, so we do not need these here
                if (Integer.bitCount(i) > caterpillarGraph.numberOfNodes() / 2.0) {
                    continue;
                }
                subset.setNumber(i);
                // Get all edges in the current cut
                List<Edge> delta = edges.stream()
                        .filter(edge -> subset.contains(edge.start()) != subset.contains(edge.end()))
                        // ... and store them in a list for later use
                        .collect(Collectors.toList());
                // Count the size of the cut
                long cutSize = delta.size();                
                if (DEBUG) {
                    System.out.println("Oddset " + i + ": " + delta);
                }
                // If we have an odd cut with more than a single edge, add a constraint
                if (cutSize > 2 && cutSize % 2 == 1) {
                    GRBLinExpr oddcutConstraint = new GRBLinExpr();                    
                    List<Edge> coveringLinks = new LinkedList<>();
                    // For each edge in the cut, get the links covering it and put them all into one list
                    delta.stream().map(edge -> coveringLinkMap.get(edge)).forEach(coveringLinks::addAll);
                    // Eliminate duplicates from the list
                    Set<Edge> distinctLinks = new HashSet<>();
                    distinctLinks.addAll(coveringLinks);
                    //List<Edge> distinctLinks = coveringLinks.stream().distinct().collect(Collectors.toList());
                    
                    // For each link, count the occurences in the multiset ...
                    for (Edge link : distinctLinks) {
                        long occurences = coveringLinks.stream().filter(l -> l.equals(link)).count();
                        if (DEBUG) {
                            System.out.println(" " + link+ " " + occurences);
                        }
                        // ... and add the link to the constraint with the correct coefficient
                        oddcutConstraint.addTerm((occurences % 2 == 0)? occurences / 2 : (occurences+1)/2, linkToVariable.get(link));
                    }
                    // The right side of the odd cut constraint
                    long rightSide = (cutSize + 1) / 2;
                    // Add the constraint to the model
                    model.addConstr(oddcutConstraint, GRB.GREATER_EQUAL, rightSide, "oddcut_for_" + subset.toString());
                }
            }

            // Write the LP into a text file
            model.write("c:\\data\\oddcut.lp");
            // Optimize model
            System.out.println("");
            model.optimize();

            // Query result
            double lpOptimum = model.get(GRB.DoubleAttr.ObjVal);
            // Print the solution vector
            System.out.println("\nOptimal Solution:");
            for (int i = 0; i < x.length; i++) {
                System.out.printf(" %1$s = %2$s\n", x[i].get(GRB.StringAttr.VarName), x[i].get(GRB.DoubleAttr.X));
            }
            // Print the solution value
            System.out.println("LP Value: " + lpOptimum);
            System.out.println("IP Gap for this instance: " + integerOptimum / lpOptimum);

            // Dispose of model and environment
            model.dispose();
            env.dispose();

        } catch (GRBException e) {
            System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
        }
    }
}
