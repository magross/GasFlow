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
 * Identifiable.java
 *
 */

package ds.graph;

/**
 * The <code>Identifiable</code> interface defines what we mean by
 * saying that an object is identifiable: the object must be able
 * to return its ID, an integer value, that is used to identify the object.
 * The ID is used to store identifiable objects efficiently in arraybased
 * datastructures.
 */
public interface Identifiable {
    
    /**
     * Returns an integer value, called ID, that can be used to identify the
     * corresponding object. The ID is usually used for equality checks and as a
     * simple, efficient hash function in conjuction with mappings 
     * ({@link IdentifiableObjectMapping}).
     * @return an integer value identifing the corresponding object.
     */
    int id();
    
    /**
     * Returns a clone of this <code>Identifiable</code> object.
     * @return a clone of this <code>Identifiable</code> object.
     */
    //Identifiable clone();

}
