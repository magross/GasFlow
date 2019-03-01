/*
 * DoubleMap.java
 *
 */

package ds.graph;

/**
 *
 * @author Martin Gro�
 */

public class DoubleMap<T extends Identifiable> {
    
    private double[] map;
    
    public DoubleMap(DynamicNetwork graph) {
        map = new double[graph.numberOfEdges()];
    }
    
    public DoubleMap(int numberOfEdges) {
        map = new double[numberOfEdges];
    }
    
    public DoubleMap(Iterable<T> domain) {
        int maxId = -1;
        for (T x : domain) {
            if (maxId < x.id()) maxId = x.id();
        }
        map = new double[maxId+1];
    }
    
    public DoubleMap(T[] domain) {
        int maxId = -1;
        for (T x : domain) {
            if (maxId < x.id()) maxId = x.id();
        }
        map = new double[maxId+1];
    }    
    
    public DoubleMap(DoubleMap<T> doubleMap) {
        map = new double[doubleMap.map.length];
        for (int i=0; i<map.length; i++) {
            map[i] = doubleMap.map[i];
        }
    }
    
    public DoubleMap(DoubleMap<T> doubleMap, double d) {
        map = new double[doubleMap.map.length + 1];
        for (int i=0; i<doubleMap.map.length;i++) {
            map[i] = doubleMap.map[i];
        }
        map[doubleMap.map.length] = d;
    }
    
    public double get(T x) {
        return map[x.id()];
    }

    public void decrease(T x, double amount) {
        map[x.id()] -= amount;
    }    
    
    public void increase(T x, double amount) {
        map[x.id()] += amount;
    }
    
    public void set(T x, double value) {
        if (x.id() >= map.length) {
            double[] n = new double[x.id()+1];
            System.arraycopy(map,0,n,0,map.length);
            map = n;
        }
        map[x.id()] = value;
    }
    
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("(");
        for (int i=0; i<map.length; i++) {
            buffer.append(map[i]);
            if (i<map.length-1) buffer.append(", ");
        }
        buffer.append(")");
        return buffer.toString();
    }
    
}
