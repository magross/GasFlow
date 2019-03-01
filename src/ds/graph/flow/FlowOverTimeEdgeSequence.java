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
 * FlowOverTimePath.java
 *
 */
package ds.graph.flow;

import ds.graph.DynamicPath;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Node;
import ds.graph.Path;
import java.util.LinkedList;

/**
 * The <code>@link FlowOverTimePath</code> class represents the flow on one 
 * {@link Path} in a network. The delay time in the first node of the path
 * implies the time when the represented flow would start to leave the first node.
 * The flow is send with a constant rate of <code>rate</code>. It sends a
 * total quantity of <code>amount<code> units of flow. Together with the rate
 * and the starting time this implies the point in time where the flow
 * will stop sending.
 * <code>DynamicPathFlows</code> are needed to represent dynamic flows path based.
 */
public class FlowOverTimeEdgeSequence extends LinkedList<FlowOverTimeEdge> {

    private int amount;
    private int rate;

    public FlowOverTimeEdgeSequence() {
        super();
        amount = 1;
        rate = 1;
    }

    public FlowOverTimeEdgeSequence(FlowOverTimePath path) {
        rate = path.getRate();
        for (FlowOverTimeEdge edge : path) {
            add(edge);
        }
    }

    public FlowOverTimeEdgeSequence(FlowOverTimeEdgeSequence edgeSequence) {
        rate = edgeSequence.getRate();
        for (FlowOverTimeEdge edge : edgeSequence) {
            add(edge);
        }
    }

    public void append(FlowOverTimeEdgeSequence sequence) {
        addAll(sequence);
    }

    public void append(FlowOverTimeEdgeSequence sequence, int time) {
        append(sequence);
    }

    @Deprecated
    public void append(FlowOverTimePath path, int time) {
        append(path);
    }

    public FlowOverTimeEdge getFirstEdge() {
        return getFirst();
    }

    public FlowOverTimeEdge getLastEdge() {
        return getLast();
    }

    @Deprecated
    public int delay(FlowOverTimeEdge edge) {
        return edge.getDelay();
    }

    public FlowOverTimeEdge get(Edge edge) {
        for (FlowOverTimeEdge e : this) {
            if (e.getEdge().equals(edge)) {
                return e;
            }
        }
        return null;
    }

    public FlowOverTimeEdge get(Edge edge, int time) {
        for (FlowOverTimeEdge e : this) {
            if (e.getEdge().equals(edge) && e.getTime() == time) {
                return e;
            }
        }
        return null;
    }

    public FlowOverTimeEdge get(IdentifiableIntegerMapping<Edge> transitTimes, Node node, int time) {
        int lastArrival = 0;
        for (FlowOverTimeEdge e : this) {
            if (e.getEdge().start().equals(node) && lastArrival <= time && time <= e.getTime()) {
                return e;
            }
            lastArrival = e.getTime() + transitTimes.get(e.getEdge());
        }
        return null;
    }

    public int length(IdentifiableIntegerMapping<Edge> transitTimes) {
        return getLast().getTime() + transitTimes.get(getLast().getEdge());
    }

    public int lengthUntil(IdentifiableIntegerMapping<Edge> transitTimes, FlowOverTimeEdge edge) {
        return edge.getTime();
    }

    public int lengthUpTo(IdentifiableIntegerMapping<Edge> transitTimes, FlowOverTimeEdge edge) {
        return edge.getTime() + transitTimes.get(edge.getEdge());
    }

    public FlowOverTimeEdgeSequence subsequence(FlowOverTimeEdge from, FlowOverTimeEdge to) {
        return subsequence(from, to, false, false);
    }

    public FlowOverTimeEdgeSequence subsequence(FlowOverTimeEdge from, FlowOverTimeEdge to, boolean fromInclusive, boolean toInclusive) {
        FlowOverTimeEdgeSequence result = new FlowOverTimeEdgeSequence();
        result.setAmount(amount);
        result.setRate(rate);
        boolean copying = (from == null);
        for (FlowOverTimeEdge edge : this) {
            if (copying && edge != to) {
                result.add(edge);
            } else if (copying && edge == to) {
                break;
            } else if (!copying && edge == from) {
                copying = true;
            }
        }
        if (fromInclusive && from != null) {
            result.addFirst(from);
        }
        if (toInclusive && to != null) {
            result.addLast(to);
        }
        return result;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    /**
     * Returns a String consisting of the rate, the amount and a description
     * of the path that is described in {@link DynamicPath}.
     * @return a String consisting of the rate, the amount and a description
     * of the path that is described in {@link DynamicPath}.
     */
    @Override
    public String toString() {
        return String.format("{%1$s, %2$s}", rate, super.toString());
    }

    public String toText(IdentifiableIntegerMapping transitTimes) {
        StringBuilder result = new StringBuilder();
        result.append(toString() + "\n");
        int lastArrival = 0;
        for (FlowOverTimeEdge edge : this) {
            result.append(" Reaching node " + edge.getEdge().start() + " at time " + lastArrival + ".\n");
            if (edge.getTime() - lastArrival != 0) {
                result.append(" Waiting for " + (edge.getTime() - lastArrival) + ".\n");
            }
            result.append(" Entering edge " + edge.getEdge().id() + " at " + edge.getTime() + ".\n");
            lastArrival = edge.getTime() + transitTimes.get(edge.getEdge());
        }
        return result.toString();
    }
}
