/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gas.algo.propagation;

import gas.io.gaslib.GasLibZipArchive;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zeroturnaround.zip.ZipUtil;

/**
 *
 * @author gross
 */
public class InstanceSplitter {

    public static void main(String[] args) throws IOException {
        GasLibZipArchive archive = new GasLibZipArchive("c:\\data\\GasLib-582-v2.zip", true);
        for (String file : archive.getScenarioFiles().keySet()) {
            System.out.println(file);
            String dir = "c:\\data\\test\\" + file.replace("/", "\\").substring(0, file.length() - 4) + "\\";
            //String dir2 = "c:\\data\\test\\mod\\" + file.replace("/", "\\").substring(0, file.length() - 4) + "\\";
            String fi = file.substring(file.indexOf("/") + 1);
            //System.out.println(dir);
            //System.out.println(file.indexOf("/"));
            File dirFile = new File(dir);
            File zipTarget = new File(dirFile.getParentFile().getAbsolutePath() + fi.substring(0, fi.length() - 4) + ".zip");
            if (!zipTarget.exists()) {
                archive.extractScenarioToFile(file, dir + file.substring(file.indexOf("/") + 1));
                archive.extractNetworkToFile(dir + fi.substring(0, fi.length() - 4) + ".net");
                ZipUtil.pack(dirFile, zipTarget);
            }
            BoundPropagationSimpleMain.networkFilename = zipTarget.getAbsolutePath().substring(0, zipTarget.getAbsolutePath().length() - 4) + "_reduced.net";
            BoundPropagationSimpleMain.scenarioFilename = zipTarget.getAbsolutePath().substring(0, zipTarget.getAbsolutePath().length() - 4) + "_reduced.scn";
            BoundPropagationSimpleMain.scenarioFilename =BoundPropagationSimpleMain.scenarioFilename.replace("test", "test\\output");
            File f1 = new File(BoundPropagationSimpleMain.networkFilename);
            File f2 = new File(BoundPropagationSimpleMain.scenarioFilename);
            String outputNet = zipTarget.getAbsolutePath().substring(0, zipTarget.getAbsolutePath().length() - 4) + "_reducedHeader.net";
            outputNet = outputNet.replace("test","test\\output");
            File f3 = new File(outputNet);
            String outputScn = zipTarget.getAbsolutePath().substring(0, zipTarget.getAbsolutePath().length() - 4) + "_reducedModified.scn";
            outputScn = outputScn.replace("test","test\\output");
            System.out.println(outputScn);
            File f4 = new File(outputScn);
            // 
            if (!f1.exists() || !f2.exists()) {
                BoundPropagationSimpleMain.processFile(zipTarget);
            }
            /*
            if (f2.exists() && !f4.exists()) {
                System.out.println("Scenario Modification: " + f1.getName());
                FileReader fileReader;
                FileWriter fileWriter;
                try {
                    fileReader = new FileReader(f2);
                    fileWriter = new FileWriter(f4);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        if (line.contains("<pressure bound=\"lower\" unit=\"bar\" value=\"0.0\"/>")) {                            
                            bufferedWriter.append("      <pressure bound=\"lower\" unit=\"bar\" value=\"1.01325\"/>");
                            bufferedWriter.newLine();
                        } else {
                            bufferedWriter.append(line);
                            bufferedWriter.newLine();
                        }
                        if (line.contains("<pressure bound=\"upper\" unit=\"bar\" value=\"0.0\"/>")) {                            
                            bufferedWriter.append("      <pressure bound=\"upper\" unit=\"bar\" value=\"1.01325\"/>");
                            bufferedWriter.newLine();
                        } else {
                            bufferedWriter.append(line);
                            bufferedWriter.newLine();
                        }           
                        if (line.contains("<pressure bound=\"both\" unit=\"bar\" value=\"0.0\"/>")) {                            
                            bufferedWriter.append("      <pressure bound=\"both\" unit=\"bar\" value=\"1.01325\"/>");
                            bufferedWriter.newLine();
                        } else {
                            bufferedWriter.append(line);
                            bufferedWriter.newLine();
                        }                        
                    }
                    fileReader.close();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(InstanceSplitter.class.getName()).log(Level.SEVERE, null, ex);
                }

            }*/

            if (f1.exists() && !f3.exists()) {
                //System.out.println(f1.getName());
                FileReader fileReader;
                FileWriter fileWriter;
                try {
                    fileReader = new FileReader(f1);
                    fileWriter = new FileWriter(f3);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    StringBuffer stringBuffer = new StringBuffer();
                    String line;
                    bufferedReader.readLine();
                    bufferedReader.readLine();
                    bufferedWriter.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                    bufferedWriter.newLine();
                    bufferedWriter.append("<network xmlns:xsi = \"http://www.w3.org/2001/XMLSchema-instance\"");
                    bufferedWriter.newLine();
                    bufferedWriter.append("         xmlns=\"http://gaslib.zib.de/Gas\"");
                    bufferedWriter.newLine();
                    bufferedWriter.append("         xsi:schemaLocation=\"http://gaslib.zib.de/Gas Gas.xsd\"");
                    bufferedWriter.newLine();
                    bufferedWriter.append("         xmlns:framework=\"http://gaslib.zib.de/Framework\">");
                    bufferedWriter.newLine();
                    while ((line = bufferedReader.readLine()) != null) {
                        bufferedWriter.append(line);
                        bufferedWriter.newLine();
                    }
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    bufferedReader.close();           
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(InstanceSplitter.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        BoundPropagationSimpleMain.printResults();
    }

}
