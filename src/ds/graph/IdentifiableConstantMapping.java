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
 * IdentifiableConstantMapping.java
 *
 */
package ds.graph;

/**
 * 
 * @author gross
 * @param <D>
 */
public class IdentifiableConstantMapping<D extends Identifiable> extends IdentifiableIntegerMapping<D> {

    public static IdentifiableConstantMapping<Edge> UNIT_EDGE_MAPPING = new IdentifiableConstantMapping<Edge>(1);
    public static IdentifiableConstantMapping<Node> UNIT_NODE_MAPPING = new IdentifiableConstantMapping<Node>(1);

    protected int constant;

    public IdentifiableConstantMapping(int constant) {
        this.constant = constant;
    }

    @Override
    public int get(D identifiableObject) {
        return constant;
    }

    @Override
    public void set(D identifiableObject, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void increase(D identifiableObject, int amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(D identifiableObject, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void decrease(D identifiableObject, int amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int maximum() {
        return constant;
    }

    @Override
    public int minimum(Iterable<D> identifiableObjects) {
        return constant;
    }

    @Override
    public void initializeWith(int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getDomainSize() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDomainSize(int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDefinedFor(D identifiableObject) {
        return true;
    }

    @Override
    public IdentifiableConstantMapping<D> clone() {
        return new IdentifiableConstantMapping<D>(constant);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof IdentifiableConstantMapping)) {
            return false;
        }
        IdentifiableConstantMapping iom = (IdentifiableConstantMapping) o;
        return iom.constant != constant;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.constant;
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        builder.append("constant " + constant);
        builder.append(']');
        return builder.toString();
    }
}
