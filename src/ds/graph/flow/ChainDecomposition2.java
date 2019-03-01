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
 * ChainDecomposition2.java
 *
 */
package ds.graph.flow;

import ds.graph.DynamicResidualNetwork;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.Node;
//import ds.graph.flow.FlowOverTimeEdgeSequence;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

/**
 *
 * @author Martin Groß
 */
public class ChainDecomposition2 {

    private static final boolean DEBUG = false;
    private static final boolean DEBUG_FINE = false;
    public PathBasedFlowOverTime pathBased;
    
    IdentifiableObjectMapping<Edge, Queue[]> pathsUsingEdge;
    IdentifiableObjectMapping<Node, Queue[]> pathsUsingNode;
    DynamicResidualNetwork network;
    Queue<FlowOverTimeEdge> reverseEdges;
    Queue<FlowOverTimeEdge> reverseNodes;
    LinkedList<FlowOverTimeEdgeSequence> paths;

    Vector<FlowOverTimeEdgeSequence> pathBased2 = new Vector<FlowOverTimeEdgeSequence>();
    
    
    public void uncrossPaths(DynamicResidualNetwork network, LinkedList<FlowOverTimeEdgeSequence> edgeSequences) {
        this.network = network;
        this.paths = edgeSequences;
        pathsUsingEdge = new IdentifiableObjectMapping<Edge, Queue[]>(network.edges(), Queue[].class);
        pathsUsingNode = new IdentifiableObjectMapping<Node, Queue[]>(network.nodes(), Queue[].class);
        reverseEdges = new LinkedList<FlowOverTimeEdge>();
        reverseNodes = new LinkedList<FlowOverTimeEdge>();
        if (DEBUG) System.out.println("Supersource: " + network.getSuperSource());
        if (DEBUG) System.out.println("Source: " + network.successorNodes(network.getSuperSource()));
        //if (DEBUG) System.out.println("Capacities: " + network.capacities());
        if (DEBUG) System.out.println("TransitTimes: " + network.transitTimes());
        int index = 0;
        while (!edgeSequences.isEmpty()) {
            reverseEdges.clear();
            reverseNodes.clear();
            if (DEBUG_FINE) System.out.println(" Paths: " + paths);            
            FlowOverTimeEdgeSequence edgeSequence = edgeSequences.poll();
            if (DEBUG) {
                System.out.println("Processing Edge Sequence: " + (index++) + ": " + edgeSequence);
            }
            //test(edgeSequence);
            int time = 0;
            for (FlowOverTimeEdge edge : edgeSequence) {
                time += edgeSequence.delay(edge);
                if (edgeSequence.delay(edge) < 0) {
                    for (int t = time; t > time + edgeSequence.delay(edge); t--) {
                        if (DEBUG_FINE) System.out.println(" Cancel waiting in " + edge.getEdge().start() + " at time " + t + ": " + edgeSequence);
                    }
                    reverseNodes.add(edge);
                } else if (edgeSequence.delay(edge) > 0) {
                    for (int i = time - edgeSequence.delay(edge); i < time; i++) {
                        if (DEBUG_FINE) System.out.println(" Waiting in " + edge.getEdge().start() + " at time " + i + ": " + edgeSequence);                        
                    }
                }
                time += network.transitTimes().get(edge.getEdge());
                if (network.isReverseEdge(edge.getEdge())) {
                    reverseEdges.add(edge);
                }
            }
            if (DEBUG) System.out.println(" Reverse edges: " + reverseEdges);
            if (DEBUG) System.out.println(" Reverse nodes: " + reverseNodes);
            if (reverseEdges.isEmpty() && reverseNodes.isEmpty()) {
                if (DEBUG) System.out.println(" Add path to path based flow.");
                FlowOverTimeEdgeSequence path = new FlowOverTimeEdgeSequence(edgeSequence);
                addPathToUsageLists(path);
                pathBased2.add(path);
            } else if (reverseEdges.isEmpty() && !reverseNodes.isEmpty()) {
                FlowOverTimeEdge reverseNodeEdge = reverseNodes.poll();
                int t = getArrivalTimeStart(edgeSequence, reverseNodeEdge);
                if (DEBUG) System.out.println(" Try to uncross reverse node " + reverseNodeEdge.getEdge().start() + " at time " + t);
                if (DEBUG_FINE) System.out.println(" Uncrossing partners: " + Arrays.deepToString(pathsUsingNode.get(reverseNodeEdge.getEdge().start())));
                Queue<FlowOverTimeEdgeSequence> uncrossingPartners = pathsUsingNode.get(reverseNodeEdge.getEdge().start())[t - 1];
                if (uncrossingPartners == null && DEBUG) {
                    System.out.println(Arrays.deepToString(pathsUsingNode.get(reverseNodeEdge.getEdge().start())));
                }
                do {
                    FlowOverTimeEdgeSequence partner = uncrossingPartners.peek();
                    if (DEBUG) System.out.println(" Using path for uncrossing: " + partner);
                    uncrossPaths(edgeSequence, partner, reverseNodeEdge.getEdge().start(), reverseNodeEdge, t);
                } while (edgeSequence.getRate() > 0);
            } else {
                FlowOverTimeEdge reverseEdge = reverseEdges.poll();
                Edge edge = network.reverseEdge(reverseEdge.getEdge());
                int t = getArrivalTime(edgeSequence, reverseEdge);
                int t2 = getArrivalTimeEnd(edgeSequence, reverseEdge);
                if (DEBUG) System.out.println(" Try to uncross reverse edge " + reverseEdge + " at time " + t + " with the normal edge " + edge + " at time " + t2);
                if (DEBUG_FINE) System.out.println(" Uncrossing partners: " + Arrays.deepToString(pathsUsingEdge.get(edge)));
                Queue<FlowOverTimeEdgeSequence> uncrossingPartners = pathsUsingEdge.get(edge)[t2];
                do {
                    FlowOverTimeEdgeSequence partner = uncrossingPartners.peek(); 
                    if (DEBUG) System.out.println(" Using path for uncrossing: " + partner);
                    uncrossPaths(edgeSequence, partner, reverseEdge, t, t2);
                } while (edgeSequence.getRate() > 0);
            }
            if (DEBUG) System.out.println("");
        }
        
        if (DEBUG) System.out.println("Converting: ");
        index = 0;
        for (FlowOverTimeEdgeSequence es : pathBased2) {
            if (DEBUG) System.out.println(index + ": " + es);
            pathBased.addPathFlow(new FlowOverTimePath(es));
            index++;
        }
    }
    
    private void split(FlowOverTimeEdgeSequence sequence, Edge edge, int time, FlowOverTimeEdgeSequence head, FlowOverTimeEdgeSequence tail) {
        head.setRate(sequence.getRate());
        tail.setRate(sequence.getRate());
        boolean found = false;
        int t = 0;
        for (FlowOverTimeEdge e : sequence) {
            t += e.getDelay();
            if (e.getEdge().equals(edge) && t == time && !found) {
                found = true;
                continue;
            }
            t += network.transitTimes().get(e.getEdge());
            if (found) {
                tail.add(e.clone());
            } else {
                head.add(e.clone());
            }
        }
        assert !tail.isEmpty();
        tail.getFirstEdge().setDelay(time + network.transitTimes().get(edge) + tail.getFirstEdge().getDelay());
    }
    
    protected void clean(FlowOverTimeEdgeSequence start1, FlowOverTimeEdgeSequence start2, FlowOverTimeEdgeSequence end1, FlowOverTimeEdge reverseEdge) {
        IdentifiableIntegerMapping<Edge> transitTimes = network.transitTimes();        
        if (start2.isEmpty() || end1.isEmpty()) {
            return; 
        }        
        int start2Time = start2.length(transitTimes) - transitTimes.get(start2.getLastEdge().getEdge());
        //int end1Time = start1.length(transitTimes) + reverseEdge.length(transitTimes) + end1.getFirstEdge().length(transitTimes);
        int end1Time = end1.getFirstEdge().length(transitTimes);
        while (!start2.isEmpty() && !end1.isEmpty() 
                && network.reverseEdge(start2.getLastEdge().getEdge()).equals(end1.getFirstEdge().getEdge())
                && end1Time == start2Time) {
            start2.removeLast();
            FlowOverTimeEdge old = end1.removeFirst();
            if (!start2.isEmpty()) {
                start2Time = start2.length(transitTimes) - transitTimes.get(start2.getLastEdge().getEdge()); 
            }
            if (!end1.isEmpty()) {
                end1Time += end1.getFirstEdge().length(transitTimes);
                end1.getFirstEdge().setDelay(old.length(transitTimes) + end1.getFirstEdge().getDelay());
            }
        }        
    }

    //protected void join(FlowOverTimeEdgeSequence start1, FlowOverTimeEdgeSequence start2, FlowOverTimeEdgeSequence end1, FlowOverTimeEdgeSequence end2) {
    protected void join(FlowOverTimeEdgeSequence start1, FlowOverTimeEdgeSequence end2) {
        int secondStart = end2.delay(end2.getFirstEdge());
        int firstEnd = start1.length(network.transitTimes());
        start1.append(end2, secondStart - firstEnd);
        start1.setRate(Math.min(start1.getRate(), end2.getRate()));
        //if (DEBUG) System.out.println(" Partner is append to path with " + secondStart + " - " + firstEnd);
        /*
        secondStart = end1.delay(end1.getFirstEdge());
        firstEnd = start2.length(network.transitTimes());        
        start2.append(end1, secondStart - firstEnd);            
        if (DEBUG) System.out.println(" Path is append to partner with " + secondStart + " - " + firstEnd);*/
    }    
    
    private void uncrossPaths(FlowOverTimeEdgeSequence pathWithReverseEdge, FlowOverTimeEdgeSequence partner, FlowOverTimeEdge reverseEdge, int reverseTime, int normalTime) {       
        FlowOverTimeEdgeSequence start1 = new FlowOverTimeEdgeSequence();
        FlowOverTimeEdgeSequence end1 = new FlowOverTimeEdgeSequence();
        split(pathWithReverseEdge, reverseEdge.getEdge(), reverseTime, start1, end1);
        FlowOverTimeEdgeSequence start2 = new FlowOverTimeEdgeSequence();
        FlowOverTimeEdgeSequence end2 = new FlowOverTimeEdgeSequence();
        split(partner, network.reverseEdge(reverseEdge.getEdge()), normalTime, start2, end2);        
        if (DEBUG) System.out.println(" Path is split to:\n  " + start1 + "\n  " + end1);
        if (DEBUG) System.out.println(" Partner is split to:\n  " + start2 + "\n  " + end2);
        
        clean(start1, start2, end1, reverseEdge);
        if (DEBUG) System.out.println(" Path is cleaned to:\n  " + start1 + "\n  " + end1);
        if (DEBUG) System.out.println(" Partner is cleaned to:\n  " + start2 + "\n  " + end2);        

        //join(start1, start2, end1, end2);
        
        join(start1, end2);
        join(start2, end1);
         
        int rate = Math.min(pathWithReverseEdge.getRate(), partner.getRate());
        //start1.setRate(rate);
        //start2.setRate(rate);        
        if (DEBUG) System.out.println(" The new paths are:\n  " + start1.getRate() + ": " + start1 + "\n  " + start2.getRate() + ": " + start2);
        
        pathWithReverseEdge.setRate(pathWithReverseEdge.getRate() - rate);
        partner.setRate(partner.getRate() - rate);
        if (partner.getRate() == 0) {
            if (DEBUG) System.out.println(" Partner is removed from usage lists: " + partner);
            removePathFromUsageLists(partner);
            pathBased2.remove(partner);
        }
        FlowOverTimeEdgeSequence newPath = new FlowOverTimeEdgeSequence(start1);
        if (DEBUG) System.out.println(" New path is added from usage lists: " + newPath);
        addPathToUsageLists(newPath);
        pathBased2.add(newPath);
        paths.addFirst(start2);        
        if (DEBUG_FINE) System.out.println(" Paths: " + paths);                
    }

    private void split(FlowOverTimeEdgeSequence sequence, Node node, int time, FlowOverTimeEdgeSequence head, FlowOverTimeEdgeSequence tail) {
        head.setRate(sequence.getRate());
        tail.setRate(sequence.getRate());
        boolean found = false;
        int t = 0;
        for (FlowOverTimeEdge e : sequence) {
            if (e.getEdge().start().equals(node) && isIn(time, t, t + sequence.delay(e))) {
                found = true;
            }            
            t += e.getDelay();
            t += network.transitTimes().get(e.getEdge());
            if (found) {
                tail.add(e.clone());
            } else {
                head.add(e.clone());
            }
        }
        assert !tail.isEmpty();
        //tail.getFirstEdge().setDelay(time + network.transitTimes().get(edge) + tail.getFirstEdge().getDelay());
        tail.getFirstEdge().setDelay(head.length(network.transitTimes()) + tail.getFirstEdge().getDelay());
    }        
    
    private void uncrossPaths(FlowOverTimeEdgeSequence pathWithReverseNode, FlowOverTimeEdgeSequence partner, Node reverseNode, FlowOverTimeEdge reverseNodeEdge, int t) {
        //FlowOverTimeEdgeSequence start1 = getPathUntil(pathWithReverseNode, reverseNode, t);
        //FlowOverTimeEdgeSequence start2 = new FlowOverTimeEdgeSequence(getPathUntil(partner, reverseNode, t - 1));
        //FlowOverTimeEdgeSequence end1 = getPathFrom(pathWithReverseNode, reverseNode, t);
        //FlowOverTimeEdgeSequence end2 = getPathFrom(partner, reverseNode, t - 1);

        FlowOverTimeEdgeSequence start1 = new FlowOverTimeEdgeSequence();
        FlowOverTimeEdgeSequence end1 = new FlowOverTimeEdgeSequence();
        split(pathWithReverseNode, reverseNode, t, start1, end1);
        FlowOverTimeEdgeSequence start2 = new FlowOverTimeEdgeSequence();
        FlowOverTimeEdgeSequence end2 = new FlowOverTimeEdgeSequence();
        split(partner, reverseNode, t-1, start2, end2);                
        if (DEBUG) System.out.println(" Path is split to:\n  " + start1 + "\n  " + end1);
        if (DEBUG) System.out.println(" Partner is split to:\n  " + start2 + "\n  " + end2);
        /*
        //if (!end2.isEmpty()) {
            int time = getArrivalTime(partner, end2.getFirstEdge()) - getArrivalTimeStart(pathWithReverseNode, reverseNodeEdge);
            start1.append(end2, time);
        //}
        //if (!end1.isEmpty()) {
            time = 0;
            if (!start2.isEmpty()) {
                time = getArrivalTime(pathWithReverseNode, end1.getFirstEdge()) - getArrivalTimeEnd(partner, start2.getLastEdge().getFirstEdge());
                if (DEBUG) System.out.println(" Delay: " + time + " " + getArrivalTime(pathWithReverseNode, end1.getFirstEdge()) + " " + getArrivalTimeEnd(partner, start2.getLastEdge().getFirstEdge()));
            } else {
                time = getArrivalTime(pathWithReverseNode, end1.getFirstEdge()) - getArrivalTime(partner, reverseNode);
            }
            start2.append(end1, time);
        //}    
            */
        
        join(start1, end2);
        join(start2, end1);        
        int rate = Math.min(pathWithReverseNode.getRate(), partner.getRate());
        start1.setRate(rate);
        start2.setRate(rate);        
        if (DEBUG) System.out.println(" The new paths are:\n  " + start1.getRate() + ": " + start1 + "\n  " + start2.getRate() + ": " + start2);
        pathWithReverseNode.setRate(pathWithReverseNode.getRate() - rate);
        partner.setRate(partner.getRate() - rate);
        if (partner.getRate() == 0) {
            if (DEBUG) System.out.println(" Removing partner (no rate left)");
            removePathFromUsageLists(partner);
            pathBased2.remove(partner);
        }
        FlowOverTimeEdgeSequence newPath = new FlowOverTimeEdgeSequence(start1);
        addPathToUsageLists(newPath);
        pathBased2.add(newPath);
        paths.addFirst(start2);
    }    

    private void addPathToUsageLists(FlowOverTimeEdgeSequence path) {
        int time = 0;
        for (FlowOverTimeEdge edge : path) {
            for (int t = time; t < time + edge.getDelay(); t++) {
                if (!pathsUsingNode.isDefinedFor(edge.getEdge().start())) {
                    pathsUsingNode.set(edge.getEdge().start(), new LinkedList[network.getTimeHorizon()]);
                }                
                if (pathsUsingNode.get(edge.getEdge().start())[t] == null) {
                    pathsUsingNode.get(edge.getEdge().start())[t] = new LinkedList();
                }
                if (DEBUG_FINE) System.out.println(" Waiting in " + edge.getEdge().start() + " at time " + t + ": " + path);                
                pathsUsingNode.get(edge.getEdge().start())[t].add(path);
            }            
            time += edge.getDelay();
            if (!pathsUsingEdge.isDefinedFor(edge.getEdge())) {
                pathsUsingEdge.set(edge.getEdge(), new LinkedList[network.getTimeHorizon()]);
            }
            if (pathsUsingEdge.get(edge.getEdge())[time] == null) {
                pathsUsingEdge.get(edge.getEdge())[time] = new LinkedList();
            }
            pathsUsingEdge.get(edge.getEdge())[time].add(path);
            if (DEBUG) {
                System.out.println(" " + edge.getEdge().id() + "@" + time + ": " + path);
            }
            time += network.transitTimes().get(edge.getEdge());
        }
    }         
    
    private void removePathFromUsageLists(FlowOverTimeEdgeSequence path) {
        int time = 0;
        for (FlowOverTimeEdge e : path) {
            Edge edge = e.getEdge();
            for (int t = time; t < time + e.getDelay(); t++) {
                pathsUsingNode.get(edge.start())[t].remove(path);
            }
            time += e.getDelay();      
            pathsUsingEdge.get(edge)[time].remove(path);
            time += network.transitTimes().get(edge);
        }
    }
    
    private int getArrivalTime(FlowOverTimeEdgeSequence path, Edge edge) {
        int time = 0;
        for (FlowOverTimeEdge e : path) {
            time += e.getDelay();
            if (e.getEdge() == edge) {
                return time;
            }
            time += network.transitTimes().get(e.getEdge());
        }       
        throw new AssertionError("This should not happen.");
    }
    
    private int getArrivalTime(FlowOverTimeEdgeSequence path, FlowOverTimeEdge edge) {
        int time = 0;
        for (FlowOverTimeEdge e : path) {
            time += e.getDelay();
            if (e.equals(edge)) {
                return time;
            }
            time += network.transitTimes().get(e.getEdge());
        }       
        throw new AssertionError("This should not happen.");
    }    
    
    private int getArrivalTimeEnd(FlowOverTimeEdgeSequence path, Edge edge) {
        int time = 0;
        for (FlowOverTimeEdge e : path) {
            time += e.getDelay();
            time += network.transitTimes().get(e.getEdge());
            if (e.getEdge() == edge) {
                return time;
            }            
        }       
        throw new AssertionError("This should not happen.");
    }            

    private int getArrivalTimeStart(FlowOverTimeEdgeSequence path, FlowOverTimeEdge edge) {
        int time = 0;
        for (FlowOverTimeEdge e : path) {
            if (e.equals(edge)) {
                return time;
            }                        
            time += path.delay(e);
            time += network.transitTimes().get(e.getEdge());
        }       
        throw new AssertionError("This should not happen.");
    }    
    
    private int getArrivalTimeEnd(FlowOverTimeEdgeSequence path, FlowOverTimeEdge edge) {
        int time = 0;
        for (FlowOverTimeEdge e : path) {
            time += path.delay(e);
            time += network.transitTimes().get(e.getEdge());
            if (e.equals(edge)) {
                return time;
            }            
        }       
        throw new AssertionError("This should not happen.");
    }        

    private int getArrivalTime(FlowOverTimeEdgeSequence path, Node node) {
        int time = 0;
        for (FlowOverTimeEdge e : path) {
            if (e.getEdge().start().equals(node)) {
                return time;
            }
            time += path.delay(e);
            time += network.transitTimes().get(e.getEdge());
        }       
        throw new AssertionError("This should not happen.");
    }        
    
    public FlowOverTimeEdgeSequence getPathFrom(FlowOverTimeEdgeSequence path, Edge edge, int time) {
        FlowOverTimeEdgeSequence result = new FlowOverTimeEdgeSequence();
        //result.setAmount(path.getAmount());
        result.setRate(path.getRate());
        boolean edgeReached = false;
        int t = 0;
        for (FlowOverTimeEdge e : path) {
            t += e.getDelay();
            if (e.getEdge().equals(edge) && t == time) {
                edgeReached = true;
                continue;
            }
            if (edgeReached) {
                result.add(new FlowOverTimeEdge(e.getEdge(), e.getDelay(), t));
            }
            t += network.transitTimes().get(e.getEdge());
        }
        return result;
    }

    public FlowOverTimeEdgeSequence getPathFrom(FlowOverTimeEdgeSequence path, FlowOverTimeEdge edge, int time) {
        FlowOverTimeEdgeSequence result = new FlowOverTimeEdgeSequence();
        result.setRate(path.getRate());
        boolean edgeReached = false;
        int t = 0;
        for (FlowOverTimeEdge e : path) {
            t += path.delay(e);
            if (e.equals(edge) && t == time) {
                edgeReached = true;
                continue;
            }
            t += network.transitTimes().get(e.getEdge());
            if (edgeReached) {
                result.addLast(e);
            }
        }
        return result;
    }        
    
    private FlowOverTimeEdgeSequence getPathUntil(FlowOverTimeEdgeSequence path, FlowOverTimeEdge edge, int time) {
        FlowOverTimeEdgeSequence result = new FlowOverTimeEdgeSequence();
        result.setRate(path.getRate());
        int t = 0;
        for (FlowOverTimeEdge e : path) {
            t += path.delay(e);
            if (e.equals(edge) && t == time) {
                break;
            }
            t += network.transitTimes().get(e.getEdge());
            result.addLast(e);
        }
        return result;        
    }        
   
    private FlowOverTimeEdgeSequence getPathUntil(FlowOverTimeEdgeSequence path, Edge edge, int time) {
        FlowOverTimeEdgeSequence result = new FlowOverTimeEdgeSequence();
        //result.setAmount(path.getAmount());
        result.setRate(path.getRate());
        int t = 0;
        for (FlowOverTimeEdge e : path) {
            t += e.getDelay();
            if (e.getEdge().equals(edge) && t == time) {
                break;
            }
            result.add(new FlowOverTimeEdge(e.getEdge(), e.getDelay(), t));
            t += network.transitTimes().get(e.getEdge());
            
            //result.getDynamicPath().addLastEdge(e, path.delay(e));
        }
        return result;        
    }
    
    public FlowOverTimeEdgeSequence getPathFrom(FlowOverTimeEdgeSequence path, Node node, int time) {
        FlowOverTimeEdgeSequence result = new FlowOverTimeEdgeSequence();
        result.setRate(path.getRate());
        boolean nodeReached = false;
        int t = 0;
        for (FlowOverTimeEdge e : path) {
            if (e.getEdge().start().equals(node) && isIn(time, t, t + path.delay(e))) {
                nodeReached = true;
                //e.setDelay(0);
                result.addLast(e);
                //result.getDynamicPath().addLastEdge(e, 0);
                continue;
            }
            t += path.delay(e);
            t += network.transitTimes().get(e.getEdge());
            if (nodeReached) {
                result.addLast(e);
            }
        }
        return result;
    }        
    
    public FlowOverTimeEdgeSequence getPathUntil(FlowOverTimeEdgeSequence path, Node node, int time) {
        FlowOverTimeEdgeSequence result = new FlowOverTimeEdgeSequence();
        result.setRate(path.getRate());
        int t = 0;
        for (FlowOverTimeEdge e : path) {            
            if (e.getEdge().start().equals(node) && isIn(time, t, t + path.delay(e))) {
                break;
            }
            t += path.delay(e);
            t += network.transitTimes().get(e.getEdge());
            result.addLast(e);//getDynamicPath().addLastEdge(e, path.delay(e));
        }
        return result;
    }        
    
    private boolean isIn(int i, int v1, int v2)  {
       int min, max;
       if (v1 < v2) {
           min = v1;
           max = v2;
       } else {
           min = v2;
           max = v1;
       }
       return min <= i && i <= max;
    }
}
