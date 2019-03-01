/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aaa.gurobi;

/**
 *
 * @author Martin
 */
public class PowerSetStream {
    /*
            // Create odd-cut constraints: For every edge...
            for (List<Node> node : caterpillarGraph.nodes().powerset()) {
                // Create a new linear constraint
                GRBLinExpr cutConstraint = new GRBLinExpr();  
                links.stream()
                        // Find the links covering the current edge by filtering all links whose path contains the current edge
                        .filter(link -> caterpillarGraph.getPath(link.start(), link.end()).contains(edge))
                        // ... then converting links to LP variables
                        .map(link -> linkToVariable.get(link))
                        // ... and adding those variables to the left side of the cut constraint
                        .forEach(var -> cutConstraint.addTerm(1.0, var));
                model.addConstr(cutConstraint, GRB.GREATER_EQUAL, 1.0, "cut for " + edge + "");
            }            

            // 3-Oddset Constraints
            List<List<Edge>> oddsets = new LinkedList<>();
            oddsets.addAll(caterpillarGraph.createOddsets(3));
            oddsets.addAll(caterpillarGraph.createOddsets(5));
            /*oddsets.addAll(caterpillarGraph.createOddsets(7));
            oddsets.addAll(caterpillarGraph.createOddsets(9));
            oddsets.addAll(caterpillarGraph.createOddsets(11));
            oddsets.addAll(caterpillarGraph.createOddsets(13));
            for (List<Edge> oddset : oddsets) {
                //System.out.println("Oddset: " + oddset);
                Set<GRBVar> coveringLinks = new HashSet<GRBVar>();
                for (Edge edge : oddset) {
                    index = 0;
                    for (Edge link : links) {
                        //System.out.println(link + " " + caterpillarGraph.getPath(link.start(), link.end()) + " " + edge + " " + caterpillarGraph.getPath(link.start(), link.end()).contains(edge));
                        if (caterpillarGraph.getPath(link.start(), link.end()).contains(edge)) {
                            coveringLinks.add(x[index]);
                        }
                        ++index;
                    }
                }
                objectiveFunction = new GRBLinExpr();
                StringBuilder builder = new StringBuilder();
                boolean first = true;
                for (GRBVar var : coveringLinks) {
                    objectiveFunction.addTerm(1.0, var);
                    if (first) {
                        first = false;
                    } else {
                        builder.append(" + ");
                    }
                    builder.append(varNames.get(var));
                    builder.append(" >= ");
                    builder.append((oddset.size()+1)/2.0);
                }
                model.addConstr(objectiveFunction, GRB.GREATER_EQUAL, 2.0, "odd-set " + oddset);
            }*/
}
