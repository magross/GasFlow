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
 * IntegerObjectMapping.java
 *
 */

package ds.graph;

import java.util.Iterator;
import java.util.TreeSet;
import ds.graph.IntegerObjectMapping.TimeObjectPair;

/**
 * The <code>IntegerObjectMapping</code> class represents a mapping from 
 * integers to arbitrary. Values of this mapping's domain are referred
 * to as time henceforth, since time being the domain will be the primary
 * application for such mappings.
 * Internally, the <code>IntegerObjectMapping</code> is considered as a step
 * function. Consequently, the mapping is stored as a sorted collection of step
 * starts which is obviously sufficient to encode the mapping. 
 * The size needed to encode an <code>IntegerObjectMapping</code> is therefore
 * linear in the number of steps required.
 * In order to access steps efficiently, a TreeSet is used which in turn is 
 * based on a red-black tree. This allows the addition, removal and search for
 * steps in O(log (number of steps)) time.
 * For mappings of integers to integers see
 * {@link IntegerIntegerMapping}.
 */
public class IntegerObjectMapping<R> implements Cloneable, Iterable<IntegerObjectMapping<R>.TimeObjectPair> {
    
    /**
     * Stores the mapping internally. Must not be null.
     */
    protected TreeSet<TimeObjectPair> mapping;
    
    /**
     * Creates a new <code>IntegerObjectMapping</code> that is defined for all
     * integer values. Initially, all integers are mapped to null. Runtime O(1).
     */
    public IntegerObjectMapping() {
        mapping = new TreeSet<TimeObjectPair>();
        set(Integer.MIN_VALUE, null);
    }

    /**
     * Returns the value associated with the specified integer. 
     * Runtime O(log (number of steps)).
     * @param time the integer for which the associated value is to be returned.
     * @return the value associated with the specified integer.
     */
    public R get(int time) {
        TimeObjectPair test = new TimeObjectPair(time, null); 
        if (mapping.contains(test)) {
            return mapping.floor(test).value();
        } else {
            return null;
        }
    }

    public int getLastTime() {
        return mapping.last().time();
    }
    
    /**
     * Maps the integer <code>time</code> to the object <code>value</code>.
     * Runtime O(log (number of steps)).
     * @param time the integer for which an association is to be made.
     * @param value the value to be associated with the integer.
     */
    public void set(int time, R value) {
        TimeObjectPair tip = new TimeObjectPair(time, value);
        TimeObjectPair floor = mapping.floor(tip);
        if (floor != null && floor.equals(tip)) {
            floor.set(value);
        } else {
            mapping.add(tip);
        }
    }
    
    /**
     * Returns an iterator over the time - value mappings in this
     * <code>IntegerObjectMapping</code>. Runtime O(1).
     * @return an iterator over the time - value mappings in this
     * <code>IntegerObjectMapping</code>.
     */    
    @Override
    public Iterator<TimeObjectPair> iterator() {
        return mapping.iterator();
    }
    
    /**
     * Returns a copy of this mapping. Runtime O(number of steps).
     * @return a copy of this mapping.
     */
    @Override
    public IntegerObjectMapping<R> clone() {
        IntegerObjectMapping<R> clone = new IntegerObjectMapping<R>();
        for (TimeObjectPair tip : mapping) {
            clone.set(tip.time(), tip.value());
        }
        return clone;
    }
    
    /**
     * Checks whether the specified object is equivalent to this mapping. This
     * is the case if and only if the specified object is not <code>null</code,
     * of type <code>IntegerObjectMapping</code> and makes exactly the same
     * time - value associations. Runtime O(number of steps).
     * @param o the object to be compared with this mapping.
     * @return true if the specified object is an 
     * <code>IntegerObjectMapping</code> that is equivalent with this one,
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object o) {
        return (o != null) && (o instanceof IntegerObjectMapping) && ((IntegerObjectMapping) o).mapping.equals(mapping);
    }
    
    /**
     * Returns a hash code for this mapping. Runtime O(number of steps).
     * @return the hash code of the underlying <code>TreeSet</code>.
     */
    @Override
    public int hashCode() {
        return mapping.hashCode();
    }
    
    /**
     * Returns a string representation of this mapping.
     * Runtime O(number of steps).
     * @return the string representation of the underlying <code>TreeSet</code>.
     */
    @Override
    public String toString() {
        return mapping.toString();
    }
    
    /**
     * A utility class used for the underlying <code>TreeSet</code>. A mapping
     * of a time <code>t</code> to an arbitrary object <code>v</code> is stored 
     * by adding a <code>TimeObjectPair (t,v)</code> to the tree set.
     */
    public class TimeObjectPair implements Cloneable, Comparable<TimeObjectPair> {
        
        /**
         * Stores the time component of the pair.
         */                
        protected int time;
        
        /**
         * Stores the value component of this pair.
         */
        protected R value;
        
        /**
         * Constructs a new <code>TimeObjectPair</code> with the specified
         * values. Runtime O(1).
         * @param time the time component of the pair.
         * @param value the value component of the pair.
         */
        protected TimeObjectPair(int time, R value) {
            this.time = time;
            this.value = value;
        }
        
        /**
         * Sets the value of this <code>TimeObjectPair</code> to the specified
         * value. Runtime O(1).
         * @param newValue the new value of this time - object pair.
         */
        public void set(R newValue) {
            value = newValue;
        }
        
        /**
         * Returns the time component of this <code>TimeObjectPair</code>.
         * Runtime O(1).
         * @return the time component of this <code>TimeObjectPair</code>.
         */
        public int time() {
            return time;
        }

        /**
         * Returns the value component of this <code>TimeObjectPair</code>.
         * Runtime O(1).
         * @return the value component of this <code>TimeObjectPair</code>.
         */
        public R value() {
            return value;
        }

        /**
         * Compares two <code>TimeObjectPair</code>s by their time component.
         * Runtime O(1).
         * @param o the <code>TimeObjectPair</code> to be compared.
         * @return 0 if this pair is equal to the specified pair; a value less
         * than 0 if this pair's time component is numerically less than the 
         * specified pair's time component; and a value greater than 0 if this 
         * pair's time component is numerically greater than the specified 
         * pair's time component.
         */
        public int compareTo(TimeObjectPair o) {
            if (time > o.time) {
                return 1;
            } else if (time < o.time) {
                return -1;
            } else {
                return 0;
            }            
            //long t = (long) time;
            //long ot = (long) o.time();
            //return Math.round(Math.signum(t - ot));
        }
        
        /**
         * Creates a copy of this <code>TimeObjectPair</code>. Runtime O(1).
         * @return a copy of this <code>TimeObjectPair</code>.
         */
        @Override
        public TimeObjectPair clone() {
            return new TimeObjectPair(time, value);
        }
        
        /**
         * Compares this <code>TimeObjectPair</code> to the specified object.
         * The result is true if and only if the argument is not null and is a
         * <code>TimeObjectPair</code> which has the same time component. The
         * value component is ignored. This is due to the fact that the
         * underlying tree set must not contain two 
         * <code>TimeObjectPair</code>s with the same time component.
         * Runtime O(1).
         * @param o the object this mapping is to be compared with.
         * @return <code>true</code> if the given object represents an 
         * <code>TimeObjectPair</code> equivalent to this pair, 
         * <code>false</code> otherwise.
         */
        @Override
        public boolean equals(Object o) {
            return (o != null) && (o instanceof IntegerObjectMapping.TimeObjectPair) && ((TimeObjectPair) o).time() == time;
        }
        
        /**
         * Returns a hash code for this <code>TimeObjectPair</code>. Since
         * this hash code should be consistent with {@link #equals} just the
         * time component of the pair is used. Runtime O(1).
         * @return the time component of this <code>TimeObjectPair</code>.
         */
        @Override
        public int hashCode() {
            return time;
        }
        
        /**
         * Returns a string representation of this <code>TimeObjectPair</code>.
         * This representation is of the form "time = value". Runtime O(1).
         * @return a string representation of this <code>TimeObjectPair</code>.
         */
        @Override
        public String toString() {
            return String.format("%1$s = %2$s", time, value);
        }        
    }
}
