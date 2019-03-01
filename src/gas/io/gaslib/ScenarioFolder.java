/**
 * ScenarioFolder.java
 *
 */

package gas.io.gaslib;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Groß
 */
public class ScenarioFolder {

    private Map<String, GasLibScenario> scenarios;

    public ScenarioFolder(String string) {
        this(Paths.get(string));
    }

    public ScenarioFolder(Path folder) {
        scenarios = new LinkedHashMap<>();
        try {
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(folder, "*.scn");
            for (Path path : directoryStream) {
                GasLibScenarioFile file = new GasLibScenarioFile();
                file.readFromFile(path.toString());
                for (GasLibScenario scenario : file.getScenarios().values()) {
                    scenarios.put(scenario.getId(), scenario);
                }
            }            
        } catch (IOException ex) {
            Logger.getLogger(ScenarioFolder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
