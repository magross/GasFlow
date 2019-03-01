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
 * Mappings.java
 *
 */

package ds.graph;

/**
 *
 * @author Martin Groß
 */
public class Mappings {
    
    public static <T extends Identifiable> String toString(Iterable<T> domain, IdentifiableIntegerMapping<T> mapping) {
        StringBuilder result = new StringBuilder();
        result.append("[");
        boolean isEmpty = true;
        for (T identifiable : domain) {
            isEmpty = false;
            if (mapping.isDefinedFor(identifiable)) {
                if (mapping.get(identifiable) == Integer.MAX_VALUE) {
                    result.append(identifiable.toString() + " = MAX_INT");
                } else {
                    result.append(identifiable.toString() + " = " + mapping.get(identifiable));
                }
            } else {
                result.append(identifiable.toString() + " = UNDEFINED");
            }            
            result.append(", ");
        }
        if (!isEmpty) result.delete(result.length()-2, result.length());
        result.append("]");
        return result.toString();
    }
    
    public static <T extends Identifiable> String toString(Iterable<T> domain, IdentifiableObjectMapping<T,?> mapping) {        
        StringBuilder result = new StringBuilder();
        result.append("[");
        boolean isEmpty = true;
        for (T identifiable : domain) {
            isEmpty = false;
            result.append(identifiable.toString() + " = " + mapping.get(identifiable));
            result.append(", ");
        }
        if (!isEmpty) result.delete(result.length()-2, result.length());
        result.append("]");
        return result.toString();
    }    

}
