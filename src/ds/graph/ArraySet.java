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
 * ArraySet.java
 *
 */
package ds.graph;

import java.lang.reflect.Array;
import java.util.Iterator;

/**
 * The {@code ArraySet} class represents a set of
 * {@code Identifiable} objects. They are internally stored in an
 * array by their IDs. Therefore they are ordered by the IDs.
 * The class implements the interface {@code IdentifiableCollection}
 * and thus provides all specified methods. Especially the methods that
 * can be implemented using the IDs are very efficient as storing an element
 * or looking for an element with a specified ID.
 */
public class ArraySet<E extends Identifiable> implements IdentifiableCollection<E> {
	/** The concrete type replacing the generics. Is needed because Java does not support generic arrays. */
	private Class<? extends Identifiable> elementType;
	/** The intern array to store the elements by their ID. */
	private E[] elements;
	/**
	 * A variable to store the current size of the data structure,
	 * where size means the number of stored elements.
	 */
	private int size;

	/**
	 * Constructs an {@code ArraySet} containing the elements in the
	 * given array. The elements must be stored in the field corresponding
	 * to their ID, elsewise an {@code IllegalArgumentException}
	 * is thrown.
	 * @param elements an array with elements that shall be contained in this {@code ArraySet}.
	 */
	public ArraySet( E[] elements ) {
		this.elementType = elements[0].getClass();
		this.elements = elements;
		for( int i = 0; i < elements.length; i++ )
			if( elements[i] == null || elements[i].id() != i )
				throw new IllegalArgumentException();
		size = elements.length;
	}

	/**
	 * Constructs an {@code ArraySet},
	 * typed to {@code elementType}, but containing no elements and
	 * with zero capacity.
	 * The capacity must be set by {@code public void setCapacity(int capacity)}
	 * before storing elements in the {@code ArraySet}.
	 * @param elementType the type the elements in this {@code ArraySet}
	 *                    will have.
	 */
	public ArraySet( Class<E> elementType ) {
		this( elementType, 0 );
	}

	/**
	 * Constructs an {@code ArraySet} containing no elements, but typed
	 * to {@code elementType} and with a capacity to store elements
	 * with IDs from zero to {@code capacity-1}.
	 * @param elementType the type the elements in this {@code ArraySet}
	 *                    will have.
	 * @param capacity the highest possible ID for elements plus one.
	 */
	@SuppressWarnings( "unchecked" )
	public ArraySet( Class<E> elementType, int capacity ) {
		this.elementType = elementType;
		this.elements = (E[]) Array.newInstance( elementType, capacity );
	}

	/**
	 * Adds an element to the {@code ArraySet} and returns
	 * whether the insertion was successful. The insertion fails
	 * if the ID of the element is negative outside the range of this
	 * {@code ArraySet}.
	 * Elsewise the element will be stored at the appropriate array position.
	 * @param element element to be add.
	 */
	public boolean add( E element ) {
		if( element.id() < 0 || element.id() >= this.getCapacity() )
			return false;
		if( elements[element.id()] == null )
			size++;
		elements[element.id()] = element;
		return true;
	}

	/**
	 * Removes the element from the {@code ArraySet} having the
	 * same ID as the element {@code element}.
	 * If there is no such element in the {@code ArraySet},
	 * nothing happens.
	 * Due to the array based implementation this operation is efficient.
	 * Runtime O(1).
	 * @param element element element to be removed.
	 */
	public void remove( E element ) {
		if( element.id() >= 0 && element.id() <= elements.length - 1 ) {
			if( elements[element.id()] != null )
				size--;
			elements[element.id()] = null;
		}
	}

	/**
	 * Removes and returns the last element of this {@code ListSequence}.
	 * The last element is the element with the highest ID.
	 * If the {@code ArraySet</ArraySet> is empty, nothing happens.
	 * @return the last element of this {@code ArraySet}.
	 */
	public E removeLast() {
		final E e = last();
		if( e == null )
			return null;
		else {
			remove( e );
			return e;
		}
	}

	/**
	 * Returns whether the element is contained in this {@code ArraySet}.
	 * The test checks for containedness of the specified element (not for
	 * containedness of an element having the same ID).
	 * The test is efficient because of the array based implementation.
	 * Runtime O(1).
	 * @param element the element that shall be checked for containedness.
	 * @return whether the element {@code element} contained in this {@code ArraySet}.
	 */
	public boolean contains( E element ) {
		return element.id() < 0 || element.id() >= elements.length ? false : elements[element.id()] == element;
	}

	/**
	 * Returns whether this {@code ArraySet} is empty. Runtime O(1).
	 * @return whether this {@code ArraySet} is empty.
	 */
	public boolean empty() {
		return size() == 0;
	}

	/**
	 * <p>Returns the size of this {@code ArraySet}.
	 * The size means the number of stored elements, not the capacity of the
	 * internal array.</p>
	 * <p>Runtime O(1).</p>
	 * @return the size of this {@code ArraySet}.
	 */
	public int size() {
		return size;
	}

	/**
	 * Returns the element with the ID {@code id} that is stored in this
	 * {@code ArraySet} or null if no element with this ID is stored.
	 * An {@code ArraySet} is especially a set, i.e. the returned
	 * element is uniquely defined.
	 * The test is efficient because of the array based implementation.
	 * Runtime O(1).
	 * @param id the ID that shall be checked
	 * @return the element with the ID {@code id} that is stored in this
	 * {@code ArraySet}, {@code null} if no element with this
	 * ID is stored.
	 */
	public E get( int id ) {
		return elements[id];
	}

	/**
	 * Returns the first element stored in this {@code ArraySet}.
	 * The order in {@code ArraySet} depends on the IDs,
	 * thus the first element is the element with the smallest ID.
	 * If the {@code ArraySet} is empty, {@code null} is returned.
	 * Runtime O(ID_first), where ID_first is the ID of the first element.
	 * @return the first element stored in this {@code ArraySet},
	 *         null if no element is stored.
	 */
	public E first() {
		int index = 0;
		while( index < elements.length && elements[index] == null )
			index++;
		if( index < elements.length )
			return elements[index];
		else
			return null;
	}

	/**
	 * Returns the last element stored in this {@code ArraySet}.
	 * The order in {@code ArraySet} depends on the IDs,
	 * thus the last element is the element with the highest ID.
	 * If the {@code ArraySet} is empty, {@code null} is returned.
	 * Runtime O(ID_last), where ID_first is the ID of the last element.
	 * @return the last element stored in this {@code ArraySet}, null if no element is stored.
	 */
	public E last() {
		int index = elements.length - 1;
		while( index > -1 && elements[index] == null )
			index--;
		return index > -1 ? elements[index] : null;
	}

	/**
	 * <p>Returns the predecessor of the element {@code element}.
	 * Returns null if the  {@code element} is the first in the
	 * {@code ArraySet} or if it is not stored in the
	 * {@code ArraySet}.</p>
	 * <p>The order in {@code ArraySet} depends on the IDs,
	 * thus the predecessor of the element {@code element} is the element
	 * with the highest ID smaller than the ID of {@code element}.
	 * Runtime O(ID_element) where ID_element is the ID of {@code element}.</p>
	 * @param element the element which predecessor is wanted
	 * @return the predecessor of {@code element} or null if the element
	 * is the first in the {@code ArraySet} or is not contained in the {@code ArraySet}.
	 */
	public E predecessor( E element ) {
		if( contains( element ) ) {
			int index = element.id() - 1;
			while( index > -1 && elements[index] == null )
				index--;
			return index > -1 ? elements[index] : null;
		}
		return null;
	}

	/**
	 * Returns the successor of the element {@code element}.
	 * Returns null if the  {@code element} is the last in the
	 * {@code ArraySet} or if it is not stored in the
	 * {@code ArraySet}.
	 * The order in {@code ArraySet} depends on the IDs,
	 * thus the successor of the element {@code element} ist the element
	 * with the smallest ID higher than the ID of {@code element}.
	 * Runtime O(n-ID_element) where ID_element is the ID of {@code element}
	 * and n is the number of possible IDs.
	 * @param element the element which successor is wanted
	 * @return the successor of {@code element<\code> or null if the element
	 * is the first in the {@code ArraySet} or is not contained
	 * in the {@code ArraySet}.
	 */
	public E successor( E element ) {
		if( contains( element ) ) {
			int index = element.id();
			while( index < elements.length && elements[index] == null )
				index++;
			return index < elements.length ? elements[index] : null;
		}
		return null;
	}

	/**
	 * Returns an iterator for the elements of this {@code ArraySet}.
	 * With the iterator one can iterate comfortable through all elements.
	 * @return an iterator for the elements of this {@code ArraySet}.
	 */
	public Iterator<E> iterator() {
		return new ArrayIterator<E>( elements );
	}

	/**
	 * Returns the capacity of this {@code ArraySet}.
	 * The capacity is one higher than highest accepted ID.
	 * @return the capacity of this {@code ArraySet}.
	 */
	public int getCapacity() {
		return elements.length;
	}

	/**
	 * Sets the the capacity of this {@code ArraySet}.
	 * The capacity is one higher than the highest accepted ID.
	 * Should only be used if the constructor
	 * {@code public ArraySet(Class<E> elementType)}
	 * was used. Dynamic resizing is not recommended!
	 * Elements with IDs greater or equal to {@code capacity} will be
	 * cut off.
	 * @param capacity the capacity to be set.
	 */
	@SuppressWarnings( "unchecked" )
	public void setCapacity( int capacity ) {
		E[] newElements = (E[]) Array.newInstance( elementType, capacity );
		for( int i = elements.length - 1; i >= capacity; i-- )
			if( elements[i] != null )
				size--;
		System.arraycopy( elements, 0, newElements, 0, Math.min( elements.length, capacity ) );
		elements = newElements;
	}

	/**
	 * Returns a String describing the {@code ArraySet}.
	 * The String contains the indices of the internal array that have
	 * a stored element. As the elements have the same ID as the position
	 * they are stored in, die output String is also a list of the IDs
	 * of all elements stored in the {@code ArraySet}.
	 * @return a String containing the indices of the internal array that have a stored element.
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder( "[" );
		final Iterator<E> it = this.iterator();
		if( it.hasNext() )
			sb.append( it.next().id() );
		while( it.hasNext() ) {
			sb.append( " " );
			sb.append( it.next().id() );
		}
		sb.append( "]" );
		return sb.toString();
	}

	/**
	 * Returns the hash code of this array set.
	 * The hash code is calculated by computing the arithmetic mean
	 * of the hash codes of the contained elements.
	 * Therefore the hash code is equal for array sets equal according to
	 * the {@code equals}-method, but not necessarily different
	 * for array sets different according to the {@code equals}-method
	 * If hashing of array sets is heavily used,
	 * the implementation of this method should be reconsidered.
	 * @return the hash code of this node.
	 */
	@Override
	public int hashCode() {
		int h = 0;
		for( E e : this )
			h += Math.floor( e.hashCode() / this.size() );
		return h;
	}

	/**
	 * Returns whether an object is equal to this array set.
	 * The result is true if and only if the argument is not null and is a
	 * {@code ArraySet} object including the same number of
	 * elements where all the elements are pairwise equal according
	 * to their {@code equals}-Method.
	 * @param o object to compare.
	 * @return {@code true} if the given object represents a
	 * {@code ArraySet} equivalent to this object, {@code false} otherwise.
	 */
	@Override
	@SuppressWarnings( "unchecked" )
	public boolean equals( Object o ) {
		boolean equal;
		if( o == null || !(o instanceof ArraySet) )
			equal = false;
		else {
			ArraySet arraysetu = (ArraySet) o;
			ArraySet<E> arrayset = null;
			if( arraysetu.getClass() != this.getClass() )
				return false;
			else
				arrayset = (ArraySet<E>) arraysetu;
			equal = (this.size() == arrayset.size());
			if( equal ) {
				Iterator<E> i1 = this.iterator();
				Iterator<E> i2 = arrayset.iterator();
				while( i1.hasNext() )
					equal &= (i1.next().equals( i2.next() ));
			}
		}
		return equal;
	}

	/**
	 * Clones this array set by cloning the elements and creating a new
	 * {@code ArraySet} object with the clones.
	 * @return a {@code ArraySet} object with clones of the elements of this object.
	 *//*
	@Override
	@SuppressWarnings( "unchecked" )
	public ArraySet<E> clone() {
		E[] c = (E[]) Array.newInstance( elementType, elements.length );
		for( int i = 0; i < elements.length; i++ )
			c[i] = elements[i] == null ? null : (E) elements[i].clone();
		return new ArraySet<E>( c );
	}*/
}
