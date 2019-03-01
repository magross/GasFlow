/*
 * AnacondaNetworkFile.java
 * 
 * 
 */
package gas.io.anaconda;

import gas.io.XMLConnections;
import gas.io.XMLIntersections;
import gas.io.XMLNetworkFile;

/**
 *
 * @author Martin Groß
 */
public class AnacondaNetworkFile extends XMLNetworkFile {

    public AnacondaNetworkFile(String fileName) {
        readFromFile(fileName);
    }

    @Override
    protected XMLIntersections createIntersections() {
        return new AnacondaIntersections();
    }

    @Override
    protected XMLConnections createConnections(XMLIntersections intersections) {
        return new AnacondaConnections(intersections);
    }

    public static void main(String[] args) {        
        AnacondaNetworkFile net = new AnacondaNetworkFile("../../anaconda/examples/onePipe/onePipe.xml");
        AnacondaInitialValuesFile aivf = new AnacondaInitialValuesFile();
        aivf.readFromFile("../../anaconda/examples/onePipe/onePipe_initial.xml");
        AnacondaControlSettingsFile acsf = new AnacondaControlSettingsFile();
        acsf.readFromFile("../../anaconda/examples/threeCompressors/threeCompressors_control.xml");
        AnacondaBoundaryConditionsFile abcf = new AnacondaBoundaryConditionsFile();
        abcf.readFromFile("../../anaconda/examples/threeCompressors/threeCompressors_boundary.xml");
        AnacondaNetworkFile net2 = new AnacondaNetworkFile("../../anaconda/examples/singleCompressor/singleCompressor.xml");
        AnacondaNetworkFile net3 = new AnacondaNetworkFile("../../anaconda/examples/twoCompressors/twoCompressors.xml");
        AnacondaNetworkFile net4 = new AnacondaNetworkFile("../../anaconda/examples/threeCompressors/threeCompressors.xml");
    }
}
