/**
 * GasLibIntersections.java
 *
 */

package gas.io.gaslib;

import com.google.common.collect.HashBiMap;
import gas.io.IntersectionType;
import gas.io.XMLIntersections;
import java.util.LinkedHashMap;

/**
 *
 * @author Martin Groß
 */
public class GasLibIntersections extends XMLIntersections<GasLibIntersection> {

    public GasLibIntersections() {
        intersections = HashBiMap.create();
    }

    @Override
    protected GasLibIntersection createIntersection(String intersectionType) {
        return GasLibIntersection.createNewIntersection(IntersectionType.getType(intersectionType));
    }
}
