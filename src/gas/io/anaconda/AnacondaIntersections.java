/**
 * AnacondaIntersections.java
 *
 */

package gas.io.anaconda;

import com.google.common.collect.HashBiMap;
import gas.io.IntersectionType;
import gas.io.XMLIntersections;
import java.util.LinkedHashMap;

/**
 *
 * @author Martin Groß
 */
public class AnacondaIntersections extends XMLIntersections<AnacondaIntersection> {

    public AnacondaIntersections() {
        intersections = HashBiMap.create();
    }

    @Override
    protected AnacondaIntersection createIntersection(String intersectionType) {
        return AnacondaIntersection.createNewIntersection(IntersectionType.getType(intersectionType));
    }
}
