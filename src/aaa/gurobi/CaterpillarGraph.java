/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aaa.gurobi;

import ds.graph.Edge;
import ds.graph.Network;
import ds.graph.Node;
import ds.graph.Path;
import ds.graph.StaticPath;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin
 */
public class CaterpillarGraph extends Network {

    /**
     * The number of segments.
     */
    private final int numberOfSegments;

    /**
     * Creates a new caterpillar graph with the specified number of segments.
     *
     * @param numberOfSegments the number of segments; has to be at least two.
     * @throws IllegalArgumentException if the number of segments is less then
     * two.
     */
    public CaterpillarGraph(int numberOfSegments) {
        super(2 * numberOfSegments + 2, 2 * numberOfSegments + 1);
        if (numberOfSegments < 2) {
            throw new IllegalArgumentException("Number of segments has to be at least 2.");
        }
        this.numberOfSegments = numberOfSegments;
        for (int i = 0; i < numberOfSegments; i++) {
            Node previousLowerNode = getNode(0 + 2 * i);
            Node upperNode = getNode(1 + 2 * i);
            Node lowerNode = getNode(2 + 2 * i);
            createAndSetEdge(upperNode, lowerNode);
            createAndSetEdge(previousLowerNode, lowerNode);
        }
        createAndSetEdge(getNode(2 * numberOfSegments), getNode(2 * numberOfSegments + 1));
    }

    /**
     * Creates the canonical link set for this caterpillar graph and returns it.
     *
     * @return the canonical link set for this caterpillar graph.
     */
    public List<Edge> createLinkSet() {
        int index = numberOfEdges();
        List<Edge> links = new LinkedList<>();
        for (int i = 0; i < numberOfSegments - 2; i++) {
            Edge link = new Edge(index++, getNode(1 + 2 * i), getNode(3 + 2 * i));
            links.add(link);
            Edge link2 = new Edge(index++, getNode(1 + 2 * i), getNode(6 + 2 * i));
            links.add(link2);
        }
        links.add(new Edge(index++, getNode(0), getNode(1)));
        links.add(new Edge(index++, getNode(0), getNode(4)));
        links.add(new Edge(index++, getNode(-3 + 2 * numberOfSegments), getNode(-1 + 2 * numberOfSegments)));
        links.add(new Edge(index++, getNode(-3 + 2 * numberOfSegments), getNode(1 + 2 * numberOfSegments)));
        links.add(new Edge(index++, getNode(-1 + 2 * numberOfSegments), getNode(1 + 2 * numberOfSegments)));
        return links;
    }

    /**
     * Creates the independent odd edge sets for the specified cardinality.
     *
     * @param cardinality the cardinality of the odd edge sets
     * @return a set of all indepedent edge sets of the specified cardinality.
     */
    public List<List<Edge>> createOddsets(int cardinality) {
        List<List<Edge>> oddsets = new LinkedList<>();
        oddsets.addAll(createTypeZeroSubsets(cardinality));
        oddsets.addAll(createTypeOneSubsets(cardinality));
        oddsets.addAll(createTypeTwoSubsets(cardinality));
        return oddsets;
    }

    protected List<List<Edge>> createTypeZeroSubsets(int numberOfPicks) {
        return createTypeZeroSubsets(numberOfPicks, 0, numberOfSegments - 1);
    }

    /**
     * Creates all odd-sets of the specified cardinality that contain no lower
     * edges.
     *
     * @param numberOfPicks
     * @param firstSegment
     * @param lastSegment
     * @return all odd-sets of the specified cardinality that contain no lower
     * edges.
     */
    protected List<List<Edge>> createTypeZeroSubsets(int numberOfPicks, int firstSegment, int lastSegment) {
        List<List<Edge>> oddsets = new LinkedList<>();
        if (numberOfPicks > lastSegment - firstSegment + 1) {
            return oddsets;
        }
        int[][] vectors = listPickVectors(numberOfPicks, lastSegment - firstSegment + 1);
        //System.out.println(numberOfPicks + " " + firstSegment + " " + lastSegment);
        //System.out.println(Arrays.deepToString(vectors));
        for (int[] vector : vectors) {
            List<Edge> oddset = new LinkedList<>();
            for (int segmentIndex = 0; segmentIndex < vector.length; segmentIndex++) {
                if (vector[segmentIndex] == 1) {
                    Node start = getNode(1 + 2 * (segmentIndex + firstSegment));
                    Node end = getNode(2 + 2 * (segmentIndex + firstSegment));
                    oddset.add(getFirstEdge(start, end));
                }
            }
            oddsets.add(oddset);
        }
        return oddsets;
    }

    protected List<List<Edge>> createTypeTwoSubsets(int numberOfPicks) {
        List<List<Edge>> result = new LinkedList<>();
        for (int first = 0; first < numberOfSegments; first++) {
            for (int second = first + Math.max(1, numberOfPicks - 2); second < numberOfSegments + 1; second++) {
                List<List<Edge>> createTypeZeroSubsets = createTypeZeroSubsets(numberOfPicks - 2, first, second - 1);
                for (List<Edge> subset : createTypeZeroSubsets) {
                    subset.add(getFirstEdge(getNode(2 * first), getNode(2 + 2 * first)));
                    if (second < numberOfSegments) {
                        subset.add(getFirstEdge(getNode(2 * second), getNode(2 + 2 * second)));
                    } else {
                        subset.add(getFirstEdge(getNode(2 * second), getNode(1 + 2 * second)));
                    }
                }
                result.addAll(createTypeZeroSubsets);
            }
        }
        return result;
    }

    private static int[][] listPickVectors(int numberOfPicks, int numberOfElements) {
        return listPickVectors(numberOfPicks, 0, new int[numberOfElements]);
    }

    private static int[][] listPickVectors(int numberOfPicks, int firstIndex, int[] pickVector) {
        if (numberOfPicks == 0) {
            int[][] result = new int[1][];
            result[0] = pickVector;
            return result;
        }
        if (firstIndex == pickVector.length) {
            return new int[0][];
        }
        int[][] firstPart = listPickVectors(numberOfPicks, firstIndex + 1, pickVector);
        int[] pickVectorNew = new int[pickVector.length];
        System.arraycopy(pickVector, 0, pickVectorNew, 0, pickVector.length);
        pickVectorNew[firstIndex] = 1;
        int[][] secondPart = listPickVectors(numberOfPicks - 1, firstIndex + 1, pickVectorNew);
        int[][] result = new int[firstPart.length + secondPart.length][];
        System.arraycopy(firstPart, 0, result, 0, firstPart.length);
        System.arraycopy(secondPart, 0, result, firstPart.length, secondPart.length);
        return result;
    }

    public List<List<Edge>> createTypeOneSubsets(int numberOfPicks) {
        List<List<Edge>> oddsets = new LinkedList<>();
        for (int split = 0; split < numberOfSegments + 1; split++) {
            // System.out.println("Split " + split);
            List<List<Edge>> firstHalfOddsets = createTypeZeroSubsets(numberOfPicks - 1, 0, split - 1);
            List<List<Edge>> secondHalfOddsets = createTypeZeroSubsets(numberOfPicks - 1, split, numberOfSegments - 1);
            // System.out.println("F " + firstHalfOddsets);
            // System.out.println("S " + secondHalfOddsets);
            for (List<Edge> first : firstHalfOddsets) {
                List<Edge> oddset = new LinkedList<>();
                oddset.addAll(first);
                if (split < numberOfSegments) {
                    oddset.add(getFirstEdge(getNode(2 * split), getNode(2 + 2 * split)));
                } else {
                    oddset.add(getFirstEdge(getNode(2 * split), getNode(1 + 2 * split)));
                }
                oddsets.add(oddset);
            }
            for (List<Edge> second : secondHalfOddsets) {
                List<Edge> oddset = new LinkedList<>();
                oddset.addAll(second);
                if (split < numberOfSegments) {
                    oddset.add(getFirstEdge(getNode(2 * split), getNode(2 + 2 * split)));
                } else {
                    oddset.add(getFirstEdge(getNode(2 * split), getNode(1 + 2 * split)));
                }
                oddsets.add(oddset);
            }
        }
        return oddsets;
    }

    public Path getPath(Node start, Node end) {
        if (end.id() < start.id()) {
            return getPath(end, start);
        } else if (start.id() == end.id()) {
            return new StaticPath();
        } else {
            Path path = new StaticPath(false);
            int s = start.id();
            if (s % 2 == 1) {
                path.addLastEdge(getFirstEdge(getNode(s), getNode(s + 1)));
                ++s;
            }
            int last;
            if (end.id() % 2 == 0) {
                last = end.id();
            } else if (end.id() % 2 == 1 && end.id() == numberOfNodes() - 1) {
                last = end.id() - 1;
            } else {
                last = end.id() + 1;
            }
            for (int i = s; i < last; i += 2) {
                path.addLastEdge(getFirstEdge(getNode(i), getNode(i + 2)));
            }
            if (end.id() % 2 == 0) {
            } else if (end.id() % 2 == 1 && end.id() == numberOfNodes() - 1) {
                path.addLastEdge(getFirstEdge(getNode(end.id() - 1), getNode(end.id())));
            } else {
                path.addLastEdge(getFirstEdge(getNode(end.id() + 1), getNode(end.id())));
            }
            return path;
        }
    }

    public static void main(String[] args) {
        System.out.println("Test");
        String s = Arrays.deepToString(listPickVectors(2, 0, new int[5]));
        System.out.println(s);
    }
}
