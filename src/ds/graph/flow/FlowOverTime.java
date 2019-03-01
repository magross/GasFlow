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
 * FlowOverTime.java
 *
 */
package ds.graph.flow;

import ds.graph.DynamicResidualNetwork;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Martin Groß
 */
public class FlowOverTime {

    private static final boolean DEBUG = false;
    
    private EdgeBasedFlowOverTime edgeBased;
    private PathBasedFlowOverTime pathBased;
    private int flowAmount;
    private int timeHorizon;
		private int totalCost;

    public FlowOverTime(DynamicResidualNetwork network, Queue<EarliestArrivalAugmentingPath> eaaPaths) {
        edgeBased = new EdgeBasedFlowOverTime(network.flow());
        pathBased = new PathBasedFlowOverTime();
        LinkedList<FlowOverTimeEdgeSequence> paths = new LinkedList<FlowOverTimeEdgeSequence>();
        int index = 0;
				totalCost = 0;
        for (EarliestArrivalAugmentingPath eaaPath : eaaPaths) {
            if (DEBUG) System.out.println( (index++) + ":\n " + eaaPath + "\n " + eaaPath.getFlowOverTimeEdgeSequence(network).toText(network.transitTimes()));
            paths.add(eaaPath.getFlowOverTimeEdgeSequence(network));
            flowAmount += eaaPath.getCapacity();
            timeHorizon = Math.max(timeHorizon, eaaPath.getArrivalTime() + 1);
						totalCost += eaaPath.getCapacity() * eaaPath.getArrivalTime();
        }
        ChainDecomposition2 pd = new ChainDecomposition2();
        pd.pathBased = pathBased;
        pd.uncrossPaths(network, paths);
    }
    
    public EdgeBasedFlowOverTime getEdgeBased() {
        return edgeBased;
    }

    public int getFlowAmount() {
        return flowAmount;
    }

    public PathBasedFlowOverTime getPathBased() {
        return pathBased;
    }

    public int getTimeHorizon() {
        return timeHorizon;
    }

		/**
		 * Returns the total costs for the flow.
		 * @return
		 */
		public int getTotalCost() {
			return totalCost;
		}
}
