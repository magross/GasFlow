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
 * IdentifiableCollection.java
 * 
 */

package ds.graph;

/**
 * Represents a datastructure to store {@link Identifiable} objects. 
 * The ID of the objects can be used to provide a faster implementation,
 * but a listbased implementation is also possible.
 * The <code>IdentifiableCollection</code> interface does not demand a 
 * specified ordering of the contained elements. The order depends on
 * the implementation of the implementing classes.
 * Generics are used to make the datastructure usable for all classes
 * implementing the interface {@link Identifiable}. 
 * @param E the type of the elements that shall be stored in this IdentifiableCollection. 
 *          <code>E</code>ust implement {@link Identifiable}.
 */
public interface IdentifiableCollection<E extends Identifiable> extends Iterable<E> {
    
    /**
     * Adds an element to the <code>IdentifiableCollection</code> and returns
     * whether the insertion was successful.
     * The implementation differs depending on the implementing class.
     * @param element element to be add.
     */
    boolean add(E element);
    
    /**
     * Removes the element <code>element</code> from the 
     * <code>IdentifiableCollection</code>.
     * The efficiency depends on the implementation in the
     * implementing classis. 
     * @param element element to be removed.
     */
    void remove(E element);
    
    /**
     * Removes the last element from the 
     * <code>IdentifiableCollection</code>.
     * The efficiency of this operation and the ordering of elements (i.e. 
     * which element is deleted) depends on the implementation in the
     * implementing classis. 
     */
    E removeLast();
    
    /**
     * Returns whether the element is contained in this <code>IdentifiableCollection</code>.
     * @param element the element that shall be checked for containedness.
     * @return whether the element <code>element</code> contained in this 
     *         <code>IdentifiableCollection</code>.
     */
    boolean contains(E element);
    
    /**
     * Returns whether this <code>IdentifiableCollection</code> is empty.
     * @return whether this <code>IdentifiableCollection</code> is empty.
     */
    boolean empty();
    
    /**
     * Returns the size of this <code>IdentifiableCollection</code>, 
     * i.e. the number of stored elements.
     * @return the size of this <code>IdentifiableCollection</code>, 
     *         i.e. the number of stored elements.
     */
    int size();
    
    /**
     * Returns an element with the ID <code>id</code> that is stored in this 
     * <code>IdentifiableCollection</code>.
     * If the implementation of the interface is a set, the returned element
     * is unique, elsewise the method returns one of the contained elements
     * with the given ID.
     * @param id the ID that shall be checked
     * @return the element with the ID <code>id</code> that is stored in this 
     * <code>IdentifiableCollection</code>, <code>null</code> if no element with this
     * ID is stored.
     */
    E get(int id);
    
    /**
     * Returns the first element stored in this <code>IdentifiableCollection</code>.
     * Which element is considered to be first depends on the order the concret 
     * implementation of the interface uses!
     * @return the first element stored in this <code>IdentifiableCollection</code>,
     *         null if no element is stored.
     */
    E first();
    
    /**
     * Returns the last element stored in this <code>IdentifiableCollection</code>.
     * Which element is considered to be last depends on the order the concret 
     * implementation of the interface uses!
     * @return the last element stored in this <code>IdentifiableCollection</code>,
     *        null if no element is stored.
     */
    E last();
    
    /**
     * Returns the predecessor of the element <code>element</code>.
     * Returns null if the  <code>element</code> is the first in the 
     * <code>IdentifiableCollection</code> or if it is not stored in the 
     * <code>IdentifiableCollection</code>.
     * Which element is considered to be the predecessor depends on the order the concret 
     * implementation of the interface uses!
     * @param element the element which predecessor is wanted
     * @return the predecessor of <code>element<\code> or null if the element 
     * is the first in the <code>IdentifiableCollection</code> or is not contained 
     * in the <code>IdentifiableCollection</code>.
     */
    E predecessor(E element);
    
    /**
     * Returns the successor of the element <code>element</code>.
     * Returns null if the  <code>element</code> is the last in the 
     * <code>IdentifiableCollection</code> or if it is not stored in the 
     * <code>IdentifiableCollection</code>.
     * Which element is considered to be the successor depends on the order 
     * the concret implementation of the interface uses!
     * @param element the element which successor is wanted
     * @return the successor of <code>element<\code> or null if the element 
     * is the first in the <code>IdentifiableCollection</code> or is not contained 
     * in the <code>IdentifiableCollection</code>.
     */
    E successor(E element);    

}
