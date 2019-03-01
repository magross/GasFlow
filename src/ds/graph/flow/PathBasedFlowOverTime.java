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
 * PathBasedFlowOverTime.java
 * 
 */

package ds.graph.flow;

import java.util.Iterator;
import java.util.Vector;

/**
 * The <code>PathBasedFlowOverTime</code> class represents a dynamic flow in a path based representation.
 * The dynamic flow is stored as a <code>Vector</code> of {@link FlowOverTimePath} objects.
 */
public class PathBasedFlowOverTime implements Iterable<FlowOverTimePath>{
    
	/**
	 * The path flows belonging to this <code>PathBasedFlowOverTime</code>.
	 */
    Vector<FlowOverTimePath> pathFlows;
    
    /**
     * Creates a new <code>PathBasedFlowOverTime</code> object without any path flows.
     */
    public PathBasedFlowOverTime(){
        pathFlows = new Vector<FlowOverTimePath>();
    }
    
    /**
     * Adds a path flow to this dynamic flow.
     * @param pathFlow the path flow to be add.
     */
    public void addPathFlow(FlowOverTimePath pathFlow){
        if (pathFlow != null)
            pathFlows.add(pathFlow);
    }

    public void remove(FlowOverTimePath pathFlow){
            pathFlows.remove(pathFlow);
    }    
    
    /**
     * Returns an iterator to iterate over the <code>DynamicPathFlows</code> 
     * contained in this <code>PathBasedFlowOverTime</Code>.
     * @return an iterator to iterate over the <code>DynamicPathFlows</code> 
     * contained in this <code>PathBasedFlowOverTime</Code>.
     */
    @Override
    public Iterator<FlowOverTimePath> iterator(){
    	return pathFlows.iterator();
    }
    
    /**
     * Returns a String containing a description of all 
     * contained <code>DynamicPathFlows</code>.
     * @return a String containing a description of all 
     * contained <code>DynamicPathFlows</code>.
     */
    @Override
    public String toString(){
    	String result = "[\n";
    	for (FlowOverTimePath pathFlow : pathFlows){
    		result += " " + pathFlow.toString() + "\n";
    	}
    	result += "]";
    	return result;
    }
    
}
