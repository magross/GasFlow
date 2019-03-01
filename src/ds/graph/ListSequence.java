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
 * ListSequence.java
 *
 */

package ds.graph;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * The <code>ListSequence</code> class represents a sequence of 
 * <code>Identifiable</code> objects. They are ordered by the order of their
 * adding to the list sequence. 
 * The class implements the interface <code>IdentifiableCollection</code> 
 * and thus provides all specified methods. Anyway, the class is best used
 * by only adding and deleting elements without asking for containedness of 
 * elements with specified IDs or removing arbitrary elements.
 */
public class ListSequence<E extends Identifiable> extends LinkedList<E> implements IdentifiableCollection<E> {

    /**
     * Creates a <code>ListSequence</code> object without elements.
     */
    public ListSequence() {
        super();
    }

    /**
     * Creates a <code>ListSequence</code> object containing the elements
     * contained in <code>c</code>. The contained elements are not cloned.
     * @param c
     */
    public ListSequence(Collection<? extends E> c) {
	super(c); 
    }    
    
    /**
     * Adds an element to the <code>ListSequence</code> and returns
     * whether the insertion was successful.
     * The element will be added at the end of the <code>ListSequence</code>. 
     * @param element element to be add.
     */
    @Override
    public boolean add(E element){
        return super.add(element);
    }
    
    /**
     * Removes and returns the last element of this <code>ListSequence</code>.
     * The order of the elements depends on the order of their addings.
     * If the <code>ArraySet</ArraySet> is empty, nothing happens.
     * @return the last element of this <code>ListSequence</code>.
     */
    @Override
    public E removeLast(){
        return super.removeLast();
    }
    
    /**
     * Removes the first element in this <code>ListSequence</code>
     * having the same ID as <code>element</code>.
     * @param element the element to be removed.
     */
    public void remove(E element){
        Iterator<E> i = this.iterator();
        E e;
        if (i.hasNext())
            e = i.next();
        else
            return;
        while (!e.equals(element) && i.hasNext())
            e = i.next();
        if (e.equals(element)){
            i.remove();
        }
    }
    
    /**
     * Returns whether the element is contained in this <code>ListSequence</code>.
     * @param element the element that shall be checked for containedness.
     * @return whether the element <code>element</code> contained in this 
     *         <code>ListSequence</code>.
     */
    public boolean contains(E element) {
        return super.contains(element);
    }
    
    /**
     * Returns whether this <code>ListSequence</code> is empty. Runtime O(1).
     * @return whether this <code>ListSequence</code> is empty
     */
    public boolean empty(){
        return (super.isEmpty());
    }

    /**
     * Returns the predecessor of the element <code>element</code>.
     * Returns null if the  <code>element</code> is the first in the 
     * <code>ListSequence</code> or if it is not stored in the 
     * <code>ListSequence</code>.
     * The order of the elements does not regard the IDs but the 
     * order of their addings.
     * @param element the element which predecessor is wanted
     * @return the predecessor of <code>element<\code> or null if the element 
     * is the first in the <code>ListSequence</code> or is not contained 
     * in the <code>ListSequence</code>.
     */
    public E predecessor(E element) {
        int i = (super.indexOf(element));
        if (i == -1 || i == 0) {
            return null;
        }
        return super.get(i - 1);
    }
    
        
    /**
     * Returns the successor of the element <code>element</code>.
     * Returns null if the  <code>element</code> is the last in the 
     * <code>ListSequence</code> or if it is not stored in the 
     * <code>ListSequence</code>.
     * The order of the elements does not regard the IDs but the 
     * order of their addings.
     * @param element the element which successor is wanted
     * @return the successor of <code>element<\code> or null if the element 
     * is the last in the <code>ListSequence</code> or is not contained 
     * in the <code>ListSequence</code>.
     */
    public E successor(E element) {
        int i = (super.indexOf(element));
        if (i == -1 || i == super.size() - 1) {
            return null;
        }
        return super.get(i + 1);
    }     
    
    /**
     * Returns the first element in the sequence and null if the sequence is empty.
     * The order of the elements does not regard the IDs but the 
     * order of their addings.
     * @return the first element in the sequence and null if the sequence is empty.
     */
    public E first() {
        if (this.empty()) {
            return null;
        } else {
            return super.getFirst();
        }
    }

    /**
     * Returns the last element in the sequence and null if the sequence is empty.
     * The order of the elements does not regard the IDs but the 
     * order of their addings.
     * @return the last element in the sequence and null if the sequence is empty.
     */
    public E last() {
        if (this.empty()) {
            return null;
        } else {
            return super.getLast();
        }
    }

    /**
     * Returns a String containing the elements of this list sequence
     * in the order the iterator uses.
     * @return a String containing the elements of this sequence in the order of their adding.
     */
    @Override
    public String toString() {
        Iterator<E> it = this.iterator();
        String result = "[";
        if (it.hasNext()){
            E e = it.next();
            result += e.id();
        }
            
        while (it.hasNext()) {
            E e = it.next();
            result += " ";
            result += e.id();
        }
        result += "]";
        return result;     
    }

    /**
     * Clones this list sequence by cloning the elements and creating a new 
     * <code>ListSequence</code> object with the clones.
     * @return a <code>ListSequence</code> object with clones of the elements
     *         of this object.
     *//*
    @Override
    public ListSequence<E> clone() {
        ListSequence<E> copy = new ListSequence<E>();
        for (E e : this) {
            copy.add((E) e.clone());
        }
        return copy;
    }*/

    /**
     * Returns whether an object is equal to this list sequence.
     * The result is true if and only if the argument is not null and is a
     * <code>ListSequence</code> object including the same number of
     * elements where all the elements are pairwise equal according
     * to their <code>equals</code>-Method.
     * @param o object to compare.
     * @return <code>true</code> if the given object represents a
     * <code>ListSequence</code> equivalent to this object, <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean eq;
        if (o == null || !(o instanceof ListSequence)) {
            eq = false;
        } else {
            ListSequence ls = (ListSequence) o;
            eq = (this.size() == ls.size());
            if (eq) {
                Iterator<E> i1 = this.iterator();
                Iterator<E> i2 = ls.iterator();
                while (i1.hasNext()) {
                    eq &= (i1.next().equals(i2.next()));
                }
            }
        }
        return eq;
    }

    /**
     * Returns the hash code of this list sequence.
     * The hash code is calculated by computing the arithmetic mean
     * of the hash codes of the contained elements.
     * Therefore the hash code is equal for list sequences equal according to
     * the <code>equals</code>-method, but not necessarily different
     * for list sequences different according to the <code>equals</code>-method.
     * If hashing of list sequences is heavily used,
     * the implementation of this method should be reconsidered.
     * @return the hash code of this node.
     */
    @Override
    public int hashCode() {
        int h = 0;
        for (E e : this) {
            h += Math.floor(e.hashCode() / this.size());
        }
        return h;
    }
}
