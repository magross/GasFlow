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
 * ArrayIterator.java
 * 
 */
package ds.graph;

import java.util.Iterator;

/**
 * An iterator to comfortably iterate through the elements of an 
 * <code>ArraySet</code>. The elements in an <code>ArraySet</code>
 * are ordered by their IDs.
 */
public class ArrayIterator<E> implements Iterator<E> {

    /**
     * The stored elements.
     */
    private E[] data;
    /**
     * The current index.
     */
    private int index;

    /**
     * Constructs an iterator for a given dataarray. 
     * The ArraySet uses this constructor with its internal array.
     * @param data the dataarray of the <code>ArraySet</code>.
     */
    public ArrayIterator(E[] data) {
        this.data = data;
        this.index = 0;
    }

    /**
     * Returns whether there is one more element in the <code>ArraySet</code>.
     * @return <code>true</code> if there is one more element in the
     *         <code>ArraySet</code>, <code>false</code> else.
     */
    public boolean hasNext() {
        while (index < data.length && data[index] == null) {
            index++;
        }
        return index < data.length;
    }

    /**
     * Returns the next element of the <code>ArraySet</code>.
     * Returns <code>null</code> if there is no more element.
     * @return the next element of the <code>ArraySet</code> if there is one more,
     *        <code>null</code> else.
     */
    public E next() {
        if (index < data.length) {
            index++;
            return data[index - 1];
        } else {
            return null;
        }
    }

    /**
     * Removes the element that was last returned by <code>public E next()</code>.
     * Method is not yet implemented.
     */
    public void remove() {
        throw new UnsupportedOperationException(Localization.getInstance().getString("ds.graph.NotSupportedException"));
    }
}
