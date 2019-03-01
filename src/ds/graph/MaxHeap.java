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
 * MaxHeap.java
 *
 */

package ds.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Martin Groß
 */
public class MaxHeap<O,P extends Number> {
    
    private List<Element<O,P>> elements;
    private Map<O,Integer> objectToIndex;
    
    public MaxHeap() {
        this(10);
    }
    
    public MaxHeap(int initialCapacity) {
        elements = new ArrayList<Element<O,P>>(initialCapacity);
        objectToIndex = new HashMap<O,Integer>(initialCapacity);
    }
    
    public boolean contains(O object) {
        return objectToIndex.containsKey(object);
    }
    
    public void increasePriority(O object, P newPriority) {
       Element<O,P> element = elements.get(objectToIndex.get(object));
       element.setPriority(newPriority);
       moveUp(objectToIndex.get(object));
    }
    
    public Element<O,P> extractMax() {
        Element<O,P> maximum = elements.get(0);
        elements.set(0,elements.get(elements.size()-1));
        elements.remove(elements.size()-1);
        objectToIndex.remove(maximum.getObject());
        if (!elements.isEmpty()) {
            objectToIndex.put(elements.get(0).getObject(),0);
            moveDown(0);
        }
        return maximum;
    }
    
    public O extractMaxObject() {
        return extractMax().getObject();
    }
    
    public O getMaximumObject() {
        return elements.get(0).getObject();
    }
    
    public void insert(O object, P priority) {
        insert(new Element<O,P>(object,priority));
    }
    
    private void insert(Element<O,P> data) {
        elements.add(data);
        objectToIndex.put(data.getObject(),elements.size()-1);
        moveUp(elements.size()-1);
    }
    
    public boolean isEmpty() {
        return elements.isEmpty();
    }
    
    public P priority(O object) {
        if (contains(object)) {
            return elements.get(objectToIndex.get(object)).getPriority();
        } else {
            return null;
        }
    }    
    
    private void moveDown(int v){
        int l = 2*(v+1)-1;
        int r = 2*(v+1);
        int smallest;
        while (l < elements.size()) {
            if (r < elements.size() && greaterThan(elements.get(r).getPriority(),elements.get(l).getPriority())) {
                smallest = r;
            } else {
                smallest = l;
            }
            if (greaterThan(elements.get(smallest).getPriority(),elements.get(v).getPriority())) {
                swap(smallest,v);
                v = smallest;
                l = 2*(v+1)-1;
                r = 2*(v+1);
            } else {
                break;
            }
        }
    }
    
    private void moveUp(int v){
        int parent = (v-1)/2;
        while (v > 0 && greaterThan(elements.get(v).getPriority(),elements.get(parent).getPriority())) {
            swap(parent,v);
            v = parent;
            parent = (v-1)/2;
        }
    }   
    
    private boolean greaterThan(P x, P y) {
        return x.doubleValue() > y.doubleValue();
    }
    
    private void swap(int i, int j) {
        Element<O,P> e = elements.get(i);
        Element<O,P> f = elements.get(j);
        elements.set(i,f);
        elements.set(j,e);
        objectToIndex.put(e.getObject(),j);
        objectToIndex.put(f.getObject(),i);
    }    
    
    public final class Element<O,P extends Number> {
        
        private P priority;
        private O object;
        
        private Element(O object, P priority) {
            this.priority = priority;
            this.object = object;
        }
        
        public P getPriority() {
            return priority;
        }
        
        public void setPriority(P priority) {
            this.priority = priority;
        }
        
        public O getObject() {
            return object;
        }
        
        public void setObject(O object) {
            this.object = object;
        }
        
        public String toString() {
            return "("+object+","+priority+")";
        }
        
    }
    
}

