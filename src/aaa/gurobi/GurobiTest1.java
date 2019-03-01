/**
 * 
 */
package aaa.gurobi;

import ds.graph.Edge;
import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Martin
 */
public class GurobiTest1 {

    public static void main(String[] args) {
        int k = 5;
        CaterpillarGraph caterpillarGraph = new CaterpillarGraph(k);
        List<Edge> links = caterpillarGraph.createLinkSet();

        try {
            GRBEnv env = new GRBEnv("mip1.log");
            GRBModel model = new GRBModel(env);

            // Create variables
            GRBVar[] x = new GRBVar[caterpillarGraph.numberOfEdges()];
            Map<GRBVar, String> varNames = new HashMap<>();
            
            GRBLinExpr objectiveFunction = new GRBLinExpr();
            
            int index = 0;
            for (Edge edge : caterpillarGraph.edges()) {
                System.out.println("x" + index + ": " + edge);
                x[index] = model.addVar(0.0, 1.0, 1.0, GRB.CONTINUOUS, "x" + index);
                varNames.put(x[index], "x" + index);
                objectiveFunction.addTerm(1.0, x[index]);
                ++index;
            }
            model.setObjective(objectiveFunction, GRB.MAXIMIZE);

            // Cut Constraints
            for (Edge link : links) {
                GRBLinExpr constraint = new GRBLinExpr();
                for (Edge edge : caterpillarGraph.getPath(link.start(), link.end())) {
                    constraint.addTerm(1.0, x[edge.id()]);
                }
                model.addConstr(constraint, GRB.LESS_EQUAL, 1.0, "cut for " + link + "");
            }

            model.write("c:\\data\\test.lp");
            // Optimize model
            model.optimize();

            for (int i = 0; i < x.length; i++) {
                System.out.println(x[i].get(GRB.StringAttr.VarName)
                        + " " + x[i].get(GRB.DoubleAttr.X));
            }

            System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));

            System.out.println((k + 1) / model.get(GRB.DoubleAttr.ObjVal));
            // Dispose of model and environment
            model.dispose();
            env.dispose();

        } catch (GRBException e) {
            System.out.println("Error code: " + e.getErrorCode() + ". "
                    + e.getMessage());
        }
    }
}
