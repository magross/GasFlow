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
package ds.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Martin Groß
 */
public class MinHeap<O, P extends Number> {

    private List<Element> elements;
    private Map<O, Integer> objectToIndex;

    public MinHeap() {
        this(10);
    }

    public MinHeap(int initialCapacity) {
        elements = new ArrayList<Element>(initialCapacity);
        objectToIndex = new HashMap<O, Integer>(initialCapacity);
    }

    public boolean contains(O object) {
        return objectToIndex.containsKey(object);
    }

    public void decreasePriority(O object, P newPriority) {
        Element element = elements.get(objectToIndex.get(object));
        element.setPriority(newPriority);
        moveUp(objectToIndex.get(object));
    }

    public Element extractMin() {
        Element minimum = elements.get(0);
        elements.set(0, elements.get(elements.size() - 1));
        elements.remove(elements.size() - 1);
        objectToIndex.remove(minimum.getObject());
        if (!elements.isEmpty()) {
            objectToIndex.put(elements.get(0).getObject(), 0);
            moveDown(0);
        }
        return minimum;
    }

    public Element insert(O object, P priority) {
        return insert(new Element(object, priority));
    }

    private Element insert(Element data) {
        elements.add(data);
        objectToIndex.put(data.getObject(), elements.size() - 1);
        moveUp(elements.size() - 1);
        return data;
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

    private void moveDown(int v) {
        int l = 2 * (v + 1) - 1;
        int r = 2 * (v + 1);
        int smallest;
        while (l < elements.size()) {
            if (r < elements.size() && smallerThan(elements.get(r).getPriority(), elements.get(l).getPriority())) {
                smallest = r;
            } else {
                smallest = l;
            }
            if (smallerThan(elements.get(smallest).getPriority(), elements.get(v).getPriority())) {
                swap(smallest, v);
                v = smallest;
                l = 2 * (v + 1) - 1;
                r = 2 * (v + 1);
            } else {
                break;
            }
        }
    }

    private void moveUp(int v) {
        int parent = (v - 1) / 2;
        while (v > 0 && smallerThan(elements.get(v).getPriority(), elements.get(parent).getPriority())) {
            swap(parent, v);
            v = parent;
            parent = (v - 1) / 2;
        }
    }

    private boolean smallerThan(P x, P y) {
        return x.doubleValue() < y.doubleValue();
    }

    private void swap(int i, int j) {
        Element e = elements.get(i);
        Element f = elements.get(j);
        elements.set(i, f);
        elements.set(j, e);
        objectToIndex.put(e.getObject(), j);
        objectToIndex.put(f.getObject(), i);
    }

    public final class Element {

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
            return "(" + object + "," + priority + ")";
        }
    }
}
