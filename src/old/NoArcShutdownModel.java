package old;

import ds.graph.Edge;
import ds.graph.Node;/*
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.cplex.IloCplex;*/
import old.GasNetwork;

/*  Represents the following (quadratic) program as a CPLEX model object. The
 *  model depends on a GasNetwork instance (G=(V,A), beta, gamma, demands, aout).
 *
 *  min \sum_{a \in A} beta_a/3 |q_a| + \sum_{a \in aout} gamma_a/3 q_a
 *  s.t.
 *        \sum_{a \in out(v)} q_a - \sum_{a \in in(v)} q_a = d_u
 *        q_a^2 - pi_v + pi_w = 0
 *        q \in \R^A
 *        pi \in R_{>=0}^V
 *
 * Notice that |q_a| is a non-linear term in the objective function. We 
 * substitute: 
 * 
 *    q_a  = q^+_a - q^-
 *   |q_a| = q^+_a + q^-
 * 
 * in order to linearize the problem. We currently cannot deal with the 
 * q_a^2 - pi_v + pi_w = 0 constraint.
 *
 * For the documentation of the CPLEX interface, see here:
 * 
 * http://www-01.ibm.com/support/knowledgecenter/SSSA5P_12.6.0/ilog.odms.cplex.help/CPLEX/UsrMan/topics/APIs/Java/03_archi_overview.html?lang=en
 * http://www-01.ibm.com/support/knowledgecenter/SSSA5P_12.6.0/ilog.odms.cplex.help/CPLEX/UsrMan/topics/APIs/Java/07_modeling_overview.html?lang=en
 * http://www-01.ibm.com/support/knowledgecenter/SSSA5P_12.6.0/ilog.odms.cplex.help/CPLEX/UsrMan/topics/APIs/Java/08_class_IloModeler.html?lang=en
 * http://www-01.ibm.com/support/knowledgecenter/SSSA5P_12.6.0/ilog.odms.cplex.help/CPLEX/UsrMan/topics/APIs/Java/10_build_model.html?lang=en 
 */
public class NoArcShutdownModel {/*extends IloCplex {

    private IloNumVar[] qPlus = null;
    private IloNumVar[] qMinus = null;
    private IloNumVar[] pi = null;
    private GasNetwork net = null;
    
    public NoArcShutdownModel(GasNetwork net) throws IloException 
    {
        this.net = net;
    }
    
    // Populates the model with all variables, constraints and the 
    // objective function.
    public void build() throws IloException{
        addVariables();
        addObjective();
        addFlowBalanceConstraints();
        addGasPressureConstraints();
    }
    
    // Can only be called after the lp has been solved and only if a feasible
    // has been found. 
    // Returns the value of the q variable associated with arc a in the 
    // solution found by CPLEX. This method reverts the variable transformation
    // q_a = q^+_a - q^-_a and returns the value that q_a would have had in the
    // original formulation.
    public double getQValue(Edge a) throws IloException{
        return getValue(qPlus[a.id()]) - getValue(qMinus[a.id()]);
    }

    // Can only be called after the lp has been solved and only if a feasible
    // has been found. 
    // Returns the value of the q^+ variable associated with arc a in the 
    // solution found by CPLEX.
    public double getQPlusValue(Edge a) throws IloException {
        return getValue(qPlus[a.id()]);
    }

    // Can only be called after the lp has been solved and only if a feasible
    // has been found. 
    // Returns the value of the q^- variable associated with arc a in the 
    // solution found by CPLEX.
    public double getQMinusValue(Edge a) throws IloException {
        return getValue(qMinus[a.id()]);
    }
    
    // Can only be called after the lp has been solved and only if a feasible
    // has been found. 
    // Returns the value of the q^- variable associated with arc a in the 
    // solution found by CPLEX.
    public double getPiValue(Node v) throws IloException {
        return getValue(pi[v.id()]);
    }
    
    // Creates the objects for all variables. Important: The variables are 
    // not yet added to the model; each variable is added (by CPLEX) once 
    // a constraint that contains it is added to the model.
    protected void addVariables() throws IloException{
        // Initialize the variable arrays
        this.qPlus = new IloNumVar[net.g.numberOfEdges()];
        this.qMinus = new IloNumVar[net.g.numberOfEdges()];
        this.pi = new IloNumVar[net.g.numberOfNodes()];

        // Add two q-variables for each arc: qplus[a] and qminus[a]. The variable for the 
        // arc with id X will be stored in position X of the array.
        for( Edge a : net.g.edges() ){
            // Add a continuous variable with lower bound 0, upper bound infinity and name q[p|m]{srcId}t{tgtId}            
            qPlus[a.id()] = numVar(0, Double.MAX_VALUE, IloNumVarType.Float, "qp" + a.start().id() + "t" + a.end().id());
            qMinus[a.id()] = numVar(0, Double.MAX_VALUE, IloNumVarType.Float, "qm" + a.start().id() + "t" + a.end().id());
        }
        
        // Add a pi-variable for each node. The variable for the arc with id X
        // will be stored in position X of the piVariables array
        for( Edge a : net.g.edges() ){
            // Add a continuous variable with lower bound 0, upper bound infinity and name pi{nodeId}
            pi[a.id()] = numVar(0, Double.MAX_VALUE, IloNumVarType.Float, "pi" + a.start().id() + "t" + a.end().id() );
        }
    }
    
    // Adds the objective function to the model.
    protected void addObjective() throws IloException {
        // Adds the objective function
        // 
        //   \sum_{a \in A} beta/3(q^+_a + q^-_a) + \sum_{a \in A_out} gamma/3 (q^+_a - q^-_a)
        //        
        IloLinearNumExpr obj = linearNumExpr();
        for(Edge a : net.g.edges()){
            obj.addTerm(net.beta.get(a) / 3.0, qPlus[a.id()]);
            obj.addTerm(net.beta.get(a) / 3.0, qMinus[a.id()]);
        }
        
        for(Edge a : net.aOut){
            obj.addTerm(net.gamma.get(a) / 3.0, qPlus[a.id()]);
            obj.addTerm((-1.0)*net.gamma.get(a) / 3.0, qMinus[a.id()]);
        }
        
        addMinimize(obj);
    }
    
    // Adds all flow balance constraints to the model
    protected void addFlowBalanceConstraints() throws IloException{
        // Adds the constraint
        //
        //     \sum_{a \in outedges(v)} (q^+_a - q^-_a) - \sum_{a \in inedges(v) (q^+_a - q^-_a) = d_v
        //
        // for all nodes v. In CPLEX notation, this constraint is written as
        //   
        //     d_v <= \sum_{a \in outedges(v)} (q^+_a - q^-_a) - \sum_{a \in inedges(v) (q^+_a - q^-_a) <= d_v
        // 
        for(Node v : net.g.nodes()){
            // Build the left hand side of the constraint
            IloLinearNumExpr lhs = linearNumExpr();
            for(Edge a : net.g.outgoingEdges(v)){
                lhs.addTerm(1, qPlus[a.id()]);
                lhs.addTerm(-1, qMinus[a.id()]);
            }
            
            for(Edge a : net.g.incomingEdges(v)){
                lhs.addTerm(-1, qPlus[a.id()]);
                lhs.addTerm( 1, qMinus[a.id()]);
            }
            
            // Add constraint of type lb <= lhs <= ub, where lb = ub = demand[v]
            addRange(net.demand.get(v), lhs, net.demand.get(v), "BAL" + v.id());
        }
    }
    
    // Adds all gas pressure constraints to the model.
    protected void addGasPressureConstraints() throws IloException{
        // Adds the constraint 
        // 
        //    (q^+_a - q^-_a)^2 - pi_v + pi_w = 0
        // 
        // for all arcs a = (v,w). In CPLEX normal form, this equation becomes
        //
        //   0 <= (q^+_a)^2 - 2*q^+_a*q^-_a + (q^-_a)^2 - pi_v + pi_w <= 0
        //
        for(Edge a : net.g.edges()){
            // operator overloading ftw... Build every term of the above 
            // sum separately and then glue them together.
          
            // (q^+_a)^2
            final IloNumExpr qpSquare = prod(qPlus[a.id()], qPlus[a.id()]);
            
            // -2 * q^+_a * q^-_a
            final IloNumExpr qpTimesQm = prod(-2, qPlus[a.id()], qMinus[a.id()] );
            
            // (q^-_a)^2
            final IloNumExpr qmSquare = prod(qMinus[a.id()], qMinus[a.id()]);
            
            // (q^+_a)^2 - 2*q^+_a*q^-_a + (q^-_a)^2
            final IloNumExpr pressSquare = sum(qpSquare, qpTimesQm, qmSquare);
            
            // The complete left-hand side
            IloNumExpr lhs = sum(pressSquare, prod(-1, pi[a.start().id()]), pi[a.end().id()]);
            addRange(0.0, lhs, 0.0, "PRESS" + a.start().id() + "T" + a.end().id());
        }
    }*/
}
 