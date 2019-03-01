/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * MaximumFlow.java
 *
 */

package ds.graph.flow;

import ds.graph.problem.MaximumFlowProblem;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Node;

/**
 *
 * @author Martin Groß
 */
public class MaximumFlow extends Flow {
    
    private MaximumFlowProblem problem;
    
    public MaximumFlow(MaximumFlowProblem problem, IdentifiableIntegerMapping<Edge> flow) {
        super(flow);
        this.problem = problem;
    }

    public MaximumFlowProblem getProblem() {
        return problem;
    }
    
    public int getFlowValue() {
        int result = 0;
        for (Node source : problem.getSources()) {
            for (Edge edge : problem.getNetwork().outgoingEdges(source)) {
                result += get(edge);
            }
            for (Edge edge : problem.getNetwork().incomingEdges(source)) {
                result -= get(edge);
            }
        }
        return result;        
    }
}
