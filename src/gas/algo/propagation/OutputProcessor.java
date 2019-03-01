/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gas.algo.propagation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gross
 */
public class OutputProcessor {

    public static void main(String[] args) {
        //System.out.println(f1.getName());
        FileReader fileReader;
        FileWriter fileWriter;
        File f1 = new File("c:\\data\\outputtimes.txt");
        //File f3 = null;
        String line = null;
        try {
            fileReader = new FileReader(f1);
            //fileWriter = new FileWriter(f3);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            //BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            int counter = 0;
            
            String name  = null;
            String reduction = null;
            String time = null;
            while ((line = bufferedReader.readLine()) != null) {
                switch (counter) {
                    case 0:
                        name = line;
                        break;
                    case 1:
                        break;
                    case 2:
                        reduction = line.substring(line.indexOf("This is a ")+10, line.indexOf("%")+1);
                        break;
                    case 3:
                        time = line;
                        time = time.substring(0, time.indexOf(" "));
                        double t = Double.parseDouble(time);
                        t = Math.round(t / 1000.0);
                        time = String.valueOf(t) + " ms";
                        break;
                    default:
                        throw new AssertionError();
                }
                counter++;
                if (counter == 4) {
                    //bufferedWriter.append(name + "\t" + reduction + "\t" + time);
                    //bufferedWriter.newLine();
                    System.out.println(name + "\t" + reduction + "\t" + time);
                    counter = 0;
                }
            }
            fileReader.close();
            //fileWriter.flush();
            //fileWriter.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OutputProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OutputProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            System.err.println(line);
        }
    }
}
