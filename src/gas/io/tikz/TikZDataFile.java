/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gas.io.tikz;

import gas.io.gml.GML;
import static gas.io.gml.GML.fromGraph;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin
 */
public class TikZDataFile {

    private String xLabel;
    private String yLabel;
    private List<double[]> data;
    
    public TikZDataFile(String xLabel, String yLabel) {
        this.xLabel = xLabel;
        this.yLabel = yLabel;
        data = new LinkedList<>();
    }   
    
    public void addDataPoint(double... x) {
        data.add(x);
    }
    
    public void writeToFile(String filename) {
        try {
            PrintStream out = new PrintStream(new FileOutputStream(filename));
            //out.printf("%1$s, %2$s\n", xLabel, yLabel);
            for (double[] point : data) {
                for (int i = 0; i < point.length; i++) {
                    out.print(point[i]);
                    if (i < point.length - 1) {
                        out.print(" ");
                    } else {
                        out.print("\n");
                    }
                }
            }
            out.flush();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GML.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
}
