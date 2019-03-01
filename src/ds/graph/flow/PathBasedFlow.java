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
 * PathBasedFlow.java
 * 
 */

package ds.graph.flow;

import java.util.Iterator;
import java.util.Vector;

/**
 * The <code>PathBasedFlow</code> class represents a static flow in a path based representation.
 * The static flow is stored as a <code>Vector</code> of {@link StaticPathFlow} objects.
 */
public class PathBasedFlow implements Iterable<StaticPathFlow>{
    
	/**
	 * The static path flows belonging to this <code>PathBasedFlow</code>.
	 */
    Vector<StaticPathFlow> staticPathFlows;
    
    /**
     * Creates a new <code>DynamicFlow</code> object without any path flows.
     */
    public PathBasedFlow(){
    	staticPathFlows = new Vector<StaticPathFlow>();
    }
    
    /**
     * Adds a path flow to this dynamic flow.
     * @param staticPathFlow the path flow to be add.
     */
    public void addPathFlow(StaticPathFlow staticPathFlow){
        if (staticPathFlow != null)
            staticPathFlows.add(staticPathFlow);
    }
    
    /**
     * Returns an iterator to iterate over the <code>StaticPathFlows</code> 
     * contained in this <code>PathBasedFlow</Code>.
     * @return an iterator to iterate over the <code>StaticPathFlows</code> 
     * contained in this <code>PathBasedFlow</Code>.

     */
    @Override
    public Iterator<StaticPathFlow> iterator(){
    	return staticPathFlows.iterator();
    }
    
    /**
     * Returns a String containing a description of all 
     * contained <code>StaticPathFlows</code>.
     * @return a String containing a description of all 
     * contained <code>StaticPathFlows</code>.
     */
    @Override
    public String toString(){
    	String result = "[\n";
    	for (StaticPathFlow pathFlow : staticPathFlows){
    		result += " " + pathFlow.toString() + "\n";
    	}
    	result += "]";
    	return result;
    }
    
}