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
 * IntegerIntegerMapping.java
 *
 */

package ds.graph;

import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;

import ds.graph.IntegerIntegerMapping.TimeIntegerPair;

/**
 * The <code>IntegerIntegerMapping</code> class represents a mapping from 
 * integers to integers. It is a specialized version of 
 * <code>IntegerObjectMapping</code> made for mappings from integers to 
 * integers. These mappings are particulary useful for functions taking
 * time as a parameter. Therefore values of this mapping's domain are referred
 * to as time henceforth.
 * Internally, the <code>IntegerIntegerMapping</code> is considered as a step
 * function. Consequently, the mapping is stored as a sorted collection of step
 * starts which is obviously sufficient to encode the mapping. 
 * The size needed to encode an <code>IntegerIntegerMapping</code> is therefore
 * linear in the number of steps required.
 * In order to access steps efficiently, a TreeSet is used which in turn is 
 * based on a red-black tree. This allows the addition, removal and search for
 * steps in O(log (number of steps)) time.
 * For mappings of integers to arbitrary values see
 * {@link IntegerObjectMapping}.
 */
public class IntegerIntegerMapping implements Cloneable, Iterable<TimeIntegerPair> {
    
    /**
     * Stores the mapping internally. Must not be null.
     */
    protected TreeSet<TimeIntegerPair> mapping;
    
    /*
     * Stores whether the mapping should be interpreted as piecewise constant or
     * piecewise linear.
     */
    protected boolean linear;
    
    /**
     * Creates a new <code>IntegerIntegerMapping</code> that is defined for all
     * integer values. Initially, all integers are mapped to 0. Runtime O(1).
     */
    public IntegerIntegerMapping() {
        mapping = new TreeSet<TimeIntegerPair>();
        set(Integer.MIN_VALUE, 0);
        set(Integer.MAX_VALUE, 0);
    }

    /**
     * Creates a new <code>IntegerIntegerMapping</code> that is defined for all
     * integer values. Initially, all integers are mapped to 0. Runtime O(1).
     * @param linear if <code>true</code> this mapping is interpreted as 
     * piecewise linear by the get-function instead of piecewise constant. 
     */
    public IntegerIntegerMapping(boolean linear) {
        mapping = new TreeSet<TimeIntegerPair>();
        this.linear = linear;
        set(Integer.MIN_VALUE, 0);        
        set(Integer.MAX_VALUE, 0);
    }
    
    public int getMaximumValue() {
        int maximum = Integer.MIN_VALUE;
        for (TimeIntegerPair tip : mapping) {
            if (tip.value() > maximum) {
                maximum = tip.value();
            }
        }
        return maximum;
    }
    
    public boolean isZero() {
        boolean result = true;
        for (TimeIntegerPair tip : mapping) {
            if (tip.value() != 0) {
                result = false;
            }
        }
        return result;
    }
    
    public int getLastTimeWithNonZeroValue() {
        TimeIntegerPair tip = mapping.lower(new TimeIntegerPair(Integer.MAX_VALUE, 0));
        if (tip.value() != 0) {
            throw new AssertionError("This should not happen.");
        }
        if (tip.time() == Integer.MIN_VALUE) {
            return 0;
        } else {
            return tip.time();
        }        
    }
    
    /**
     * Checks how the internally stored values are interpreted by the get 
     * method.
     * @return <code>true</code> if this mapping is considered to represent a
     * piecewise linear function, <code>false</code> if it is interpreted as
     * piecewise constant.
     */
    public boolean isPiecewiseLinear() {
        return linear;
    }
    
    /**
     * Returns the integer associated with the specified value. 
     * Runtime O(log (number of steps)).
     * @param time the value for which the associated integer is to be returned.
     * @return the integer associated with the specified value.
     */
    public int get(int time) {
        return mapping.floor(new TimeIntegerPair(time, 0)).value();
    }
    
    /**
     * Maps the integer <code>time</code> to the integer <code>value</code>.
     * Runtime O(log (number of steps)).
     * @param time the integer for which an association is to be made.
     * @param value the value to be associated with the integer.
     */
    public void set(int time, int value) {
        TimeIntegerPair tip = new TimeIntegerPair(time, value);
        TimeIntegerPair floor = mapping.floor(tip);
        if (floor != null && floor.equals(tip)) {
            floor.set(value);
        } else {
            mapping.add(tip);
        }
    }
    
    /**
     * A convenience method for increasing the value associated with a single
     * integer. It is equivalent to <code>increase(time, time+1, amount)</code>.
     * Runtime O(log (number of steps)).
     * @param time the integer for which the associated value is to be 
     * increased.
     * @param amount the amount by which the value is to be increased.
     */
    public void increase(int time, int amount) {
        increase(time, time + 1, amount);
    }
    
    /**
     * A convenience method for increasing the values associated with a range of
     * integers from <code>fromTime</code> (inclusively) to <code>toTime</code>
     * (exclusively). It is equivalent to calling 
     * <code>set(time, get(time) + amount)</code> for all integers in the range
     * defined above. Runtime O(log (number of steps) + number of steps changed).
     * @param fromTime the first integer for which the associated value is to be
     * increased.
     * @param toTime the first integer after <code>fromTime</code> for which 
     * the associated value is <b>not</b> to be increased.
     * @param amount the amount by which the values are to be increased.
     * @exception IllegalArgumentException if <code>toTime</code> is less equal
     * than <code>fromTime</code>.
     */
    public void increase(int fromTime, int toTime, int amount) {
        if (toTime <= fromTime) throw new IllegalArgumentException(Localization.getInstance (
		).getString ("ds.Graph.toTimeException"));
        TimeIntegerPair from = new TimeIntegerPair(fromTime, 0);
        TimeIntegerPair to = new TimeIntegerPair(toTime, 0);
        TimeIntegerPair first = mapping.floor(from);
        int lastBefore = mapping.lower(to).value();
        TimeIntegerPair last = mapping.ceiling(to);
        if (first.time() < fromTime) {
            mapping.add(new TimeIntegerPair(fromTime, first.value() + amount));
        } else {
            first.set(first.value() + amount);
        }
        if (toTime < last.time()) {
            mapping.add(new TimeIntegerPair(toTime, lastBefore));
        }        
        NavigableSet<TimeIntegerPair> subSet = mapping.subSet(mapping.floor(from), false, mapping.ceiling(to), false);
        for (TimeIntegerPair tip : subSet) {
            tip.set(tip.value() + amount);
        }
        if (mapping.lower(first) != null && mapping.lower(first).value() == first.value()) {
            mapping.remove(first);
        }
    }    
    
    /**
     * A convenience method for decreasing the value associated with a single
     * integer. It is equivalent to 
     * <code>increase(time, time+1, -amount)</code>.
     * Runtime O(log (number of steps)).
     * @param time the integer for which the associated value is to be 
     * decreased.
     * @param amount the amount by which the value is to be decreased.
     */
    public void decrease(int time, int amount) {
        increase(time, -amount);
    }

    /**
     * A convenience method for decreasing the values associated with a range of
     * integers from <code>fromTime</code> (inclusively) to <code>toTime</code>
     * (exclusively). It is equivalent to calling 
     * <code>increase(fromTime, toTime, -amount)</code>.
     * defined above. Runtime O(log (number of steps) + number of steps changed).
     * @param fromTime the first integer for which the associated value is to be
     * decreased.
     * @param toTime the first integer after <code>fromTime</code> for which 
     * the associated value is <b>not</b> to be decreased.
     * @param amount the amount by which the values are to be decreased.
     */
    public void decrease(int fromTime, int toTime, int amount) {
        increase(fromTime, toTime, -amount);
    }
    
    /**
     * Adds the specified mapping to this mapping. 
     * <code>IntegerIntegerMapping</code> objects are treated as mathematical
     * functions Z -> Z for this purpose.
     * Runtime(number of steps in <code>mapping</code>).
     * @param mapping the mapping to be added to this mapping.
     * @exception NullPointerException if mapping is null.
     */
    public void addMapping(IntegerIntegerMapping mapping) {
        TimeIntegerPair last = null;
        for (TimeIntegerPair tip :  mapping) {
            if (last == null) {
                last = tip;
                continue;
            }
            increase(last.time(), tip.time(), last.value());
        }
    }
    
    /**
     * Subtracts the specified mapping from this mapping. 
     * <code>IntegerIntegerMapping</code> objects are treated as mathematical
     * functions Z -> Z for this purpose.
     * Runtime(number of steps in <code>mapping</code>).
     * @param mapping the mapping to be subtracted to this mapping.
     * @exception NullPointerException if mapping is null.
     */    
    public void subtractMapping(IntegerIntegerMapping mapping) {
        TimeIntegerPair last = null;
        for (TimeIntegerPair tip :  mapping) {
            if (last == null) {
                last = tip;
                continue;
            }            
            decrease(last.time(), tip.time(), last.value());
        }
    }
    
    /**
     * Computes the integral of this mapping. 
     * <code>IntegerIntegerMapping</code> is considered a step function with 
     * step starts defined by its mapping for this purpose. The result (a
     * piecewise linear function) is interpreted as an 
     * <code>IntegerIntegerMapping</code> by defining a map for each start point
     * of a linear segment. Runtime(number of steps).
     * @return the integral of this mapping. 
     */
    public IntegerIntegerMapping integral() {
        IntegerIntegerMapping summatedMapping = new IntegerIntegerMapping(true);
        int sum = 0;
        int lastTime = 0;
        int lastValue = 0;
        boolean first = true;
        for (TimeIntegerPair tip : mapping) {
            //System.out.println("Start: " + tip);            
            lastTime = tip.time();            
            lastValue = tip.value();
            first = (tip.time() == Integer.MIN_VALUE);
            if (!first) {
                //System.out.println(tip + " " + lastTime + " " + lastValue);
                sum += (tip.time() - lastTime) * lastValue;
                //System.out.println(sum);
                summatedMapping.set(tip.time(), sum);
                lastTime = tip.time();
                lastValue = tip.value();
            }
        }        
        return summatedMapping;
    }
    
    /**
     * Returns an iterator over the time - integer mappings in this
     * <code>IntegerIntegerMapping</code>. Runtime O(1).
     * @return an iterator over the time - integer mappings in this
     * <code>IntegerIntegerMapping</code>.
     */
    @Override
    public Iterator<TimeIntegerPair> iterator() {
        return mapping.iterator();
    }
    
    /**
     * Returns a copy of this mapping. Runtime O(number of steps).
     * @return a copy of this mapping.
     */
    @Override
    public IntegerIntegerMapping clone() {
        IntegerIntegerMapping clone = new IntegerIntegerMapping();
        for (TimeIntegerPair tip : mapping) {
            clone.set(tip.time(), tip.value());
        }
        return clone;
    }
    
    /**
     * Checks whether the specified object is equivalent to this mapping. This
     * is the case if and only if the specified object is not <code>null</code,
     * of type <code>IntegerIntegerMapping</code> and makes exactly the same
     * time - integer associations. Runtime O(number of steps).
     * @param o the object to be compared with this mapping.
     * @return true if the specified object is an 
     * <code>IntegerIntegerMapping</code> that is equivalent with this one,
     * <code>false</code>  otherwise.
     */
    @Override
    public boolean equals(Object o) {
        return (o != null) && (o instanceof IntegerIntegerMapping) && ((IntegerIntegerMapping) o).mapping.equals(mapping);
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
        String result = mapping.toString().replace(", " + Integer.MAX_VALUE + " = 0", "");
        result = result.replace(Integer.MIN_VALUE + " = 0, ", "");
        if (result.equals("[" + Integer.MIN_VALUE + " = 0]")) {
            return "[0]";
        } else {
            return result;
        }
    }
    
    /**
     * A utility class used for the underlying <code>TreeSet</code>. A mapping
     * of a time <code>t</code> to an integer value <code>v</code> is stored by 
     * adding a <code>TimeIntegerPair (t,v)</code> to the tree set.
     */
    public class TimeIntegerPair implements Cloneable, Comparable<TimeIntegerPair> {
        
        /**
         * Stores the time component of the pair.
         */                
        protected int time;
        
        /**
         * Stores the integer component of this pair.
         */
        protected int value;
        
        /**
         * Constructs a new <code>TimeIntegerPair</code> with the specified
         * values. Runtime O(1).
         * @param time the time component of the pair.
         * @param value the integer component of the pair.
         */
        protected TimeIntegerPair(int time, int value) {
            this.time = time;
            this.value = value;
        }
        
        /**
         * Sets the value of this <code>TimeIntegerPair</code> to the specified
         * value. Runtime O(1).
         * @param newValue the new value of this time - integer pair.
         */
        public void set(int newValue) {
            value = newValue;
        }
        
        /**
         * Returns the time component of this <code>TimeIntegerPair</code>.
         * Runtime O(1).
         * @return the time component of this <code>TimeIntegerPair</code>.
         */
        public int time() {
            return time;
        }

        /**
         * Returns the integer component of this <code>TimeIntegerPair</code>.
         * Runtime O(1).
         * @return the integer component of this <code>TimeIntegerPair</code>.
         */
        public int value() {
            return value;
        }

        /**
         * Compares two <code>TimeIntegerPair</code>s by their time component.
         * Runtime O(1).
         * @param o the <code>TimeIntegerPair</code> to be compared.
         * @return 0 if this pair is equal to the specified pair; a value less
         * than 0 if this pair's time component is numerically less than the 
         * specified pair's time component; and a value greater than 0 if this 
         * pair's time component is numerically greater than the specified 
         * pair's time component.
         */
        public int compareTo(TimeIntegerPair o) {
            if (time > o.time) {
                return 1;
            } else if (time < o.time) {
                return -1;
            } else {
                return 0;
            }
            //long temp = time;
            //return Math.round(Math.signum(temp - o.time()));
        }
        
        /**
         * Creates a copy of this <code>TimeIntegerPair</code>. Runtime O(1).
         * @return a copy of this <code>TimeIntegerPair</code>.
         */
        @Override
        public TimeIntegerPair clone() {
            return new TimeIntegerPair(time, value);
        }
        
        /**
         * Compares this <code>TimeIntegerPair</code> to the specified object.
         * The result is true if and only if the argument is not null and is a
         * <code>TimeIntegerPair</code> which has the same time component. The
         * integer component is ignored. This is due to the fact that the
         * underlying tree set must not contain two 
         * <code>TimeIntegerPair</code>s with the same time component.
         * Runtime O(1).
         * @param o the object this mapping is to be compared with.
         * @return <code>true</code> if the given object represents an 
         * <code>TimeIntegerPair</code> equivalent to this pair, 
         * <code>false</code> otherwise.
         */
        @Override
        public boolean equals(Object o) {
            return (o != null) && (o instanceof TimeIntegerPair) && ((TimeIntegerPair) o).time() == time;
        }
        
        /**
         * Returns a hash code for this <code>TimeIntegerPair</code>. Since
         * this hash code should be consistent with {@link #equals} just the
         * time component of the pair is used. Runtime O(1).
         * @return the time component of this <code>TimeIntegerPair</code>.
         */
        @Override
        public int hashCode() {
            return time;
        }
        
        /**
         * Returns a string representation of this <code>TimeIntegerPair</code>.
         * This representation is of the form "time = value". Runtime O(1).
         * @return a string representation of this <code>TimeIntegerPair</code>.
         */
        @Override
        public String toString() {
            return String.format("%1$s = %2$s", time, value);
        }        
    }
}
