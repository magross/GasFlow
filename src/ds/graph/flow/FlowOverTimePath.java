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

import ds.graph.Localization;
import ds.graph.DynamicPath;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Path;
import ds.graph.StaticPath;
import java.util.Iterator;

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
public class FlowOverTimePath extends FlowOverTimeEdgeSequence /*extends StaticPathFlow*/ {

    /**
     * The underlying path.
     */
    //private DynamicPath dynamicPath;
    /**
     * The constant rate flow is flowing through the path.
     */
    //private int rate;

    /**
     * Creates a <code>FlowOverTimePath</code> with unit rate, amount zero
     * and an empty path.
     */
    public FlowOverTimePath() {
        super();
    }

    public FlowOverTimePath(FlowOverTimeEdgeSequence edgeSequence) {
        super(edgeSequence);
    }

    @Deprecated
    public FlowOverTimePath(DynamicPath path, int rate, int amount) {
        // super(path, rate);
        //if (path == null) {
        //    throw new NullPointerException(Localization.getInstance().getString("ds.graph.PathIsNullException"));
        //}
        //setPath(path);
        for (Edge edge : path) {
            addLast(new FlowOverTimeEdge(edge, path.getDelay(edge)));
        }
        setRate(rate);
        setAmount(amount);
    }

    public Iterable<Edge> edges() {
        return new Iterable<Edge>() {

            public Iterator<Edge> iterator() {
                return new Iterator<Edge>() {

                    private Iterator<FlowOverTimeEdge> internal = FlowOverTimePath.this.iterator();

                    public boolean hasNext() {
                        return internal.hasNext();
                    }

                    public Edge next() {
                        return internal.next().getEdge();
                    }

                    public void remove() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                };
            }
        };
    }

    public Edge firstEdge() {
        return getFirstEdge().getEdge();
    }

    public Edge lastEdge() {
        return getFirstEdge().getEdge();
    }

    @Deprecated
    public void addLastEdge(Edge edge, int delay) {
        addLast(new FlowOverTimeEdge(edge, delay));
    }

    /**
     * Creates a <code>FlowOverTimePath</code> with rate <code>rate</code>, 
     * amount <code>amount</code> and underlying path <code>path</code>.
     * @param path the path along which flow is to be sent.
     * @param rate the number of flow units sent per time unit.
     * @param amount the total number of flow units to be sent.
     * @exception NullPointerException if <code>path</code> is null.
     */
/*    public FlowOverTimePath(StaticPath path, int rate, int amount) {
        super(path, rate);
        if (path == null) {
            throw new NullPointerException(Localization.getInstance().getString("ds.graph.PathIsNullException"));
        }
        setPath(path);
        setRate(rate);
        setAmount(amount);
    }

    public FlowOverTimePath(StaticPathFlow staticPathFlow, int amount) {
        super(staticPathFlow.getPath(), amount);
        if (staticPathFlow == null) {
            throw new NullPointerException(Localization.getInstance().getString("ds.graph.StaticPathFlowIsNullException"));
        }
        setPath(staticPathFlow.getPath());
        setRate(staticPathFlow.getAmount());
        setAmount(amount);
    }*/
/*
    public void append(FlowOverTimeEdgeSequence path) {
        for (FlowOverTimeEdge edge : path) {
            dynamicPath.addLastEdge(edge.getEdge(), edge.getDelay());
        }
    }

    public void append(FlowOverTimePath path) {
        for (Edge edge : path.getDynamicPath()) {
            dynamicPath.addLastEdge(edge, path.delay(edge));
        }
    }*/
/*
    public Edge getFirstEdge() {
        return dynamicPath.first();
    }

    public Edge getLastEdge() {
        return dynamicPath.last();
    }

    public boolean isEmpty() {
        return dynamicPath.getEdges().empty();
    }
*/
    /**
     * Returns the delay on the startnode of edge <code>edge</code>.
     * If the edge is not returned in the path of this <code>FlowOverTimePath</code>,
     * -1 will be returned.
     * @param edge the edge with the startnode which the delay is wanted of.
     * @return the delay of the startnode of edge <code>edge</code>.
     *//*
    public int delay(Edge edge) {
        return dynamicPath.getDelay(edge);
    }*/

    /**
     * Sets the path belonging to this <code>FlowOverTimePath</code>.
     * If the object is a <code>DynamicPath</code>, it is set directly.
     * Else a <code>DynamicPath</code> will be created from
     * the edges of <code>path</code> 
     * that has zero delay times before edges and this path will be set.
     * @param path The path to be set.
     */
/*    @Override
    public void setPath(StaticPath path) {
        if (path == null) {
            throw new NullPointerException(Localization.getInstance().getString("ds.graph.PathIsNullException"));
        }
        if (path instanceof DynamicPath) {
            setPath((DynamicPath) path);
        } else {
            this.dynamicPath = new DynamicPath(path.getEdges());
        }
    }
*//*
    public DynamicPath getDynamicPath() {
        return dynamicPath;
    }*/

    /**
     * Sets the path belonging to this <code>FlowOverTimePath</code>.
     * @param path the path to be set
     * @exception NullPointerException if <code>path</code> is null.
     */
/*    public void setPath(DynamicPath path) {
        if (path == null) {
            throw new NullPointerException(Localization.getInstance().getString("ds.graph.PathIsNullException"));
        }
        this.dynamicPath = path;
        this.path = dynamicPath;
    }
*/
    /**
     * Returns the rate flow in this <code>FlowOverTimePath</code> is flowing with.
     * @return the rate flow in this <code>FlowOverTimePath</code> is flowing with.
     *//*
    public int getRate() {
        return rate;
    }

    public int getAmount() {
        return rate;
    }*/

    /**
     * Sets the rate flow in this <code>FlowOverTimePath</code> shall flow with.
     * @param rate the rate flow in this <code>FlowOverTimePath</code> shall flow with.
     */
    /*public void setRate(int rate) {
        this.rate = rate;
    }*/
/*
    public String toText(IdentifiableIntegerMapping transitTimes) {
        StringBuilder result = new StringBuilder();
        result.append(toString() + "\n");
        int time = 0;
        for (Edge edge : dynamicPath) {
            result.append(" Reaching node " + edge.start() + " at time " + time + ".\n");
            if (delay(edge) > 0) {
                result.append(" Waiting for " + delay(edge) + ".\n");
            }
            time += delay(edge);
            result.append(" Entering edge " + edge.id() + " at " + time);
            time += transitTimes.get(edge);
        }
        return result.toString();
    }*/

    /**
     * Returns a String consisting of the rate, the amount and a description
     * of the path that is described in {@link DynamicPath}.
     * @return a String consisting of the rate, the amount and a description
     * of the path that is described in {@link DynamicPath}.
     */
/*    @Override
    public String toString() {
        return String.format("{%1$s, %2$s, %3$s}", rate, amount, dynamicPath);
    }
*/
    /**
     * Returns whether an object is equal to this <code>FlowOverTimePath</code>.
     * The result is <code>true</code> if and only if the argument is not null
     * and is a <code>FlowOverTimePath</code> object having the same rate and amount
     * and containing a path equal to the path in this <code>FlowOverTimePath</code>.
     * @param o object to compare.
     * @return <code>true</code> if the given object represents a
     * <code>FlowOverTimePath</code> equivalent to this node, <code>false</code> otherwise.
     */
/*    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof FlowOverTimePath)) {
            return false;
        } else {
            FlowOverTimePath pathFlow = (FlowOverTimePath) o;
            return (pathFlow.getRate() == this.rate && pathFlow.getAmount() == this.amount && pathFlow.getDynamicPath().equals(this.dynamicPath));
        }
    }*/

    /**
     * Returns the hash code of this <code>FlowOverTimePath</code>.
     * The hash code is calculated by computing the arithmetic mean
     * of the rate, the amount and the hashcode of the underlying
     * {@link DynamicPath} of this <code>FlowOverTimePath</code>.
     * Therefore the hash code is equal for path flows that are equal 
     * according to the <code>equals</code>-method, but not necessarily
     * different for path flows different to the <code>equals</code>-method.
     * If hashing of path flows is heavily used,
     * the implementation of this method should be reconsidered.
     * @return the hash code of this object
     */
/*    @Override
    public int hashCode() {
        return (dynamicPath.hashCode() / 3 + rate / 3 + amount / 3);
    }
*/
    /**
     * Clones this <code>FlowOverTimePath</code> by cloning the underlying {@link DynamicPath}
     * and creating a new <code>FlowOverTimePath</code> with the clone and the same
     * <code>rate</code> and <code>amount</code> as this <code>FlowOverTimePath</code>.
     * @return a <code>FlowOverTimePath</code> object with a clone of the path of this
     * path flow and the same rate and amount as this path flow.
     */
/*    @Override
    public FlowOverTimePath clone() {
        return new FlowOverTimePath((DynamicPath) this.dynamicPath.clone(), this.rate, this.amount);
    }*/
}
