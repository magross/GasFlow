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
 * MaximumFlowProblem.java
 *
 */

package ds.graph.problem;

import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;
import ds.graph.Node;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Groß
 */
public class MaximumFlowProblem {
    
    private Network network;
    private IdentifiableIntegerMapping<Edge> capacities;
    private List<Node> sources;
    private List<Node> sinks;

    public MaximumFlowProblem(Network network, IdentifiableIntegerMapping<Edge> capacities, List<Node> sources, List<Node> sinks) {
        this.network = network;
        this.capacities = capacities;
        this.sources = sources;
        this.sinks = sinks;
    }

    public MaximumFlowProblem(Network network, IdentifiableIntegerMapping<Edge> capacities, Node source, List<Node> sinks) {
        this(network, capacities, new LinkedList<Node>(), sinks);
        sources.add(source);
    }

    public MaximumFlowProblem(Network network, IdentifiableIntegerMapping<Edge> capacities, List<Node> sources, Node sink) {
        this(network, capacities, sources, new LinkedList<Node>());
        sinks.add(sink);
    }
    
    public MaximumFlowProblem(Network network, IdentifiableIntegerMapping<Edge> capacities, Node source, Node sink) {
        this(network, capacities, new LinkedList<Node>(), new LinkedList<Node>());
        sources.add(source);
        sinks.add(sink);
    }

    public IdentifiableIntegerMapping<Edge> getCapacities() {
        return capacities;
    }

    public void setCapacities(IdentifiableIntegerMapping<Edge> capacities) {
        this.capacities = capacities;
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }
    
    public Node getSink() {
        if (sinks.size() == 1) {
            return sinks.get(0);
        } else {
            throw new IllegalStateException("There are multiple sinks.");
        }
    }
    
    public void setSink(Node sink) {
        sinks.clear();
        sinks.add(sink);
    }

    public List<Node> getSinks() {
        return sinks;
    }

    public void setSinks(List<Node> sinks) {
        this.sinks = sinks;
    }

    public Node getSource() {
        if (sources.size() == 1) {
            return sources.get(0);
        } else {
            throw new IllegalStateException("There are multiple sources.");
        }
    }
    
    public void setSource(Node source) {
        sources.clear();
        sources.add(source);
    }    
    
    public List<Node> getSources() {
        return sources;
    }

    public void setSources(List<Node> sources) {
        this.sources = sources;
    }    
}
