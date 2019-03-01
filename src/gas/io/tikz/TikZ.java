/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gas.io.tikz;

import gas.algo.GSPG;
import gas.algo.propagation.BoundPropagation;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin
 */
public class TikZ {

    private final StringBuilder builder;
    private final Map<String, String> packages;
    private final Stack<String> stack;    
    private String documentClass;
    
    public TikZ() {
        builder = new StringBuilder();
        documentClass = "standalone";
        packages = new LinkedHashMap<>();
        stack = new Stack<>();
    }

    protected void addLine(String string, Object... arguments) {
        indent();
        builder.append(String.format(string+"\n", arguments));
    }

    protected void indent() {   
        if (!stack.isEmpty()) {
            builder.append(String.format("%1$"+stack.size()+"s"," "));
        }        
    }

    public TikZ addEdge(String start, String end) {
        return addEdge(start, end, "edgeStyle");
    }

    public TikZ addEdge(String start, String end, String edgeStyle) {
        addLine("\\draw[%1$s] (%2$s) -- (%3$s);",edgeStyle,start,end);
        return this;
    }

    public TikZ addNode(String id, double x, double y) {
        return addNode(id, x, y, "nodeStyle", "");
    }

    public TikZ addNode(String id, double x, double y, String nodeStyle, String caption) {
        addLine("\\node[%1$s] (%2$s) at (%3$s,%4$s) {%5$s};",nodeStyle,id,x,y,caption);
        return this;
    }

    public TikZ addTikZStyle(String styleName, String style) {
        addLine("\\tikzstyle{%1$s}=[%2$s]", styleName, style);
        return this;
    }
    
    public TikZ begin(String environment) {
        return begin(environment, "");
    }
    
    public TikZ begin(String environment, String optional) {
        if (!optional.isEmpty()) {
            addLine("\\begin{%1$s}[%2$s]",environment, optional);
        } else {
            addLine("\\begin{%1$s}",environment);
        }
        stack.push(environment);
        return this;
    }

    public TikZ end() {
        String string = stack.pop();
        addLine("\\end{%1$s}",string);
        return this;
    }   
    
    @Override
    public String toString() {
        return builder.toString();
    }
    
    public void writeToFile(String filename) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename))) {
            writer.write("\\documentclass{standalone}\n");
            writer.write("\\usepackage{tikz}\n");
            //writer.write("\\usepackage[margin=5mm]{geometry}\n");
            //writer.write("\\pagestyle{empty}\n");
            writer.write("\\begin{document}\n");      
            writer.write("\\tikzstyle{nodeStyle}=[draw,circle,minimum size=5mm,shade, shading=axis, color=black,left color=gray!10, right color=gray, shading angle=45,inner sep=0mm,outer sep=0mm]\n");
            writer.write("\\tikzstyle{twoNode}=[nodeStyle]\n");
            writer.write("\\tikzstyle{sourceStyle}=[nodeStyle,left color=green, right color=green]\n");
            writer.write("\\tikzstyle{sinkStyle}=[nodeStyle,left color=red, right color=red]\n");
            writer.write("\\tikzstyle{terminalStyle}=[nodeStyle,left color=blue, right color=blue]\n");
            writer.write("\\tikzstyle{linkStyle}=[rectangle,minimum size=4mm]\n");
            writer.write("\\tikzstyle{twoNode}=[nodeStyle]\n");
            writer.write("\\tikzstyle{edgeStyle}=[line width=1mm]\n");
            writer.write("\\tikzstyle{bridge}=[dotted,thick]\n");
            writer.write("\\tikzstyle{pipe}=[edgeStyle]\n");
            writer.write("\\tikzstyle{shortPipe}=[edgeStyle]\n");
            writer.write("\\tikzstyle{valve}=[edgeStyle,yellow]\n");
            writer.write("\\tikzstyle{controlValve}=[edgeStyle,orange]\n");
            writer.write("\\tikzstyle{resistor}=[edgeStyle,gray]\n");
            writer.write("\\tikzstyle{compressorStation}=[edgeStyle,red]\n");
            //writer.write("\\tikzstyle{twoNode}=[inner sep=0mm,outer sep=0mm,draw,fill,circle,minimum size=0pt,black]\n");
            //writer.write(" \\begin{figure}[p]\n");
            //writer.write(" \\centering\n");
            writer.write(toString());
            //writer.write(" \\end{figure}\n");
            writer.write("\\end{document}\n");
        } catch (IOException ex) {
            Logger.getLogger(TikZ.class.getName()).log(Level.SEVERE, null, ex);
        }         
    }
    
    public static void main(String[] args) {
        TikZ t = new TikZ();
        t.begin("tikzpicture");

        t.end();
        System.out.println(t);
    }

    public void compileTeX(File tikzFile) {
        try {
            System.out.printf(" Creating PDF (%1$s) from the TikZ...\n", tikzFile.getPath().replace(".tex", ".pdf"));
            Process exec = Runtime.getRuntime().exec("pdflatex --extra-mem-top=10000000" + " -output-directory=" + tikzFile.getParent() + " " + tikzFile.getPath());
            try (BufferedReader in = new BufferedReader(new InputStreamReader(exec.getInputStream()))) {
                String line;
                boolean error = false;
                while ((line = in.readLine()) != null) {
                    if (error || line.contains("Error") || line.contains("error") || line.startsWith("!")) {
                        System.out.println("  An error occured during PDF generation: " + line);
                        exec.destroyForcibly();
                        error = true;
                        return;
                    }
                }
                try {
                    exec.waitFor();
                } catch (InterruptedException ex) {
                    Logger.getLogger(BoundPropagation.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println(" PDF finished.");
            } catch (Exception ex) {
                System.out.println(ex);
            }
            Files.deleteIfExists(Paths.get(tikzFile.getPath().replace(".tex", ".aux")));
            Files.deleteIfExists(Paths.get(tikzFile.getPath().replace(".tex", ".log")));
        } catch (IOException ex) {
            System.out.println("Error");
            Logger.getLogger(GSPG.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

/*
\\usetikzlibrary{decorations,decorations.pathmorphing,decorations.pathreplacing}
\pgfdeclaredecoration{sqWave}{initial}
{
    \state{initial}[width=0.5cm]
    {
        \pgfpathlineto{\pgfpoint{0.25cm}{0cm}}
        \pgfpathlineto{\pgfpoint{0.25cm}{0.25cm}}
        \pgfpathlineto{\pgfpoint{0.5cm}{0.25cm}}
        \pgfpathlineto{\pgfpoint{0.5cm}{0}}
    }
    \state{final}
    {
        \pgfpathlineto{\pgfpointdecoratedpathlast}
    }
}

\pgfdeclaredecoration{switch}{initial}
{
    \state{initial}[width=+\pgfdecoratedinputsegmentremainingdistance]
    {
        \pgfsetlinewidth{1pt}
        \pgfpathcircle{\pgfpoint{0}{0}}{0.05\pgfdecoratedinputsegmentremainingdistance}
        \pgfpathlineto{\pgfpoint{0.25\pgfdecoratedinputsegmentremainingdistance}{0}}
        \pgfpathcircle{\pgfpoint{0.25\pgfdecoratedinputsegmentremainingdistance}{0}}%
            {0.05\pgfdecoratedinputsegmentremainingdistance}
        \pgfpathlineto{\pgfpoint{0.75\pgfdecoratedinputsegmentremainingdistance}{\pgfdecorationsegmentlength}}
        \pgfpathcircle{\pgfpoint{0.75\pgfdecoratedinputsegmentremainingdistance}{0}}%
            {0.05\pgfdecoratedinputsegmentremainingdistance}
        \pgfpathlineto{\pgfpoint{\pgfdecoratedinputsegmentremainingdistance}{0}}
        \pgfpathcircle{\pgfpoint{\pgfdecoratedinputsegmentremainingdistance}{0}}%
            {0.05\pgfdecoratedinputsegmentremainingdistance}
    }
    \state{final}
    {
        \pgfpathmoveto{\pgfpointdecoratedpathlast}
    }
}

\pgfdeclaredecoration{cell}{initial}
{
    \state{initial}[width=+\pgfdecoratedinputsegmentremainingdistance]
    {
        \pgfsetlinewidth{1pt}
        \pgfpathcircle{\pgfpoint{0}{0}}{0.05\pgfdecoratedinputsegmentremainingdistance}
        \pgfpathlineto{\pgfpoint{0.5\pgfdecoratedinputsegmentremainingdistance}{0}}
        \pgfpathlineto{\pgfpoint{0.5\pgfdecoratedinputsegmentremainingdistance}{\pgfdecorationsegmentlength}}
        \pgfpathlineto{\pgfpoint{0.5\pgfdecoratedinputsegmentremainingdistance}{-\pgfdecorationsegmentlength}}
        \pgfpathcircle{\pgfpoint{0.6\pgfdecoratedinputsegmentremainingdistance}{0}}%
            {0}
        \pgfpathlineto{\pgfpoint{0.6\pgfdecoratedinputsegmentremainingdistance}{-0.5\pgfdecorationsegmentlength}}
        \pgfpathlineto{\pgfpoint{0.65\pgfdecoratedinputsegmentremainingdistance}{-0.5\pgfdecorationsegmentlength}}
        \pgfpathlineto{\pgfpoint{0.65\pgfdecoratedinputsegmentremainingdistance}{0.5\pgfdecorationsegmentlength}}
        \pgfpathlineto{\pgfpoint{0.6\pgfdecoratedinputsegmentremainingdistance}{0.5\pgfdecorationsegmentlength}}
        \pgfpathlineto{\pgfpoint{0.6\pgfdecoratedinputsegmentremainingdistance}{0}}
        \pgfpathlineto{\pgfpoint{\pgfdecoratedinputsegmentremainingdistance}{0}}
        \pgfpathcircle{\pgfpoint{\pgfdecoratedinputsegmentremainingdistance}{0}}%
            {0.05\pgfdecoratedinputsegmentremainingdistance}
    }
    \state{final}
    {
        \pgfpathmoveto{\pgfpointdecoratedpathlast}
    }
}

\pgfdeclaredecoration{compressor}{initial}
{
    \state{initial}[width=+\pgfdecoratedinputsegmentremainingdistance]
    {
        \pgfsetlinewidth{1pt}
        \pgfpathcircle{\pgfpoint{0.50\pgfdecoratedinputsegmentremainingdistance}{0}}{1.0\pgfdecorationsegmentlength}
    }
    \state{final}
        {
        \pgfpathlineto{\pgfpointdecoratedpathlast}
    }
}

\pgfdeclaredecoration{valve}{initial}
{
    \state{initial}[width=+\pgfdecoratedinputsegmentremainingdistance]
    {
        \pgfsetlinewidth{1pt}
        \pgfpathlineto{\pgfpoint{0.25\pgfdecoratedinputsegmentremainingdistance}{0}}
        \pgfpathlineto{\pgfpoint{0.25\pgfdecoratedinputsegmentremainingdistance}{1.0\pgfdecorationsegmentlength}}
        \pgfpathlineto{\pgfpoint{0.25\pgfdecoratedinputsegmentremainingdistance}{-1.0\pgfdecorationsegmentlength}}
        \pgfpathlineto{\pgfpoint{0.50\pgfdecoratedinputsegmentremainingdistance}{0}}
        \pgfpathlineto{\pgfpoint{0.25\pgfdecoratedinputsegmentremainingdistance}{1.0\pgfdecorationsegmentlength}}
        \pgfpathlineto{\pgfpoint{0.25\pgfdecoratedinputsegmentremainingdistance}{-1.0\pgfdecorationsegmentlength}}
        \pgfpathlineto{\pgfpoint{0.50\pgfdecoratedinputsegmentremainingdistance}{0}}
        \pgfpathlineto{\pgfpoint{0.75\pgfdecoratedinputsegmentremainingdistance}{1.0\pgfdecorationsegmentlength}}
        \pgfpathlineto{\pgfpoint{0.75\pgfdecoratedinputsegmentremainingdistance}{-1.0\pgfdecorationsegmentlength}}
        \pgfpathlineto{\pgfpoint{0.50\pgfdecoratedinputsegmentremainingdistance}{0}}
        \pgfpathlineto{\pgfpoint{0.75\pgfdecoratedinputsegmentremainingdistance}{-1.0\pgfdecorationsegmentlength}}
        \pgfpathlineto{\pgfpoint{0.75\pgfdecoratedinputsegmentremainingdistance}{0}}
        \pgfpathlineto{\pgfpoint{\pgfdecoratedinputsegmentremainingdistance}{0}}
    }
    \state{final}
    {
        \pgfpathlineto{\pgfpointdecoratedpathlast}
    }
}

\pgfdeclaredecoration{controlvalve}{initial}
{
    \state{initial}[width=+\pgfdecoratedinputsegmentremainingdistance]
    {
        \pgfsetlinewidth{1pt}
        \pgfpathlineto{\pgfpoint{0.25\pgfdecoratedinputsegmentremainingdistance}{0}}
        \pgfpathlineto{\pgfpoint{0.25\pgfdecoratedinputsegmentremainingdistance}{1.0\pgfdecorationsegmentlength}}
        \pgfpathlineto{\pgfpoint{0.25\pgfdecoratedinputsegmentremainingdistance}{-1.0\pgfdecorationsegmentlength}}
        \pgfpathlineto{\pgfpoint{0.75\pgfdecoratedinputsegmentremainingdistance}{-1.0\pgfdecorationsegmentlength}}
        \pgfpathlineto{\pgfpoint{0.75\pgfdecoratedinputsegmentremainingdistance}{1.0\pgfdecorationsegmentlength}}
        \pgfpathlineto{\pgfpoint{0.25\pgfdecoratedinputsegmentremainingdistance}{1.0\pgfdecorationsegmentlength}}
        \pgfpathlineto{\pgfpoint{0.25\pgfdecoratedinputsegmentremainingdistance}{-1.0\pgfdecorationsegmentlength}}
        \pgfpathlineto{\pgfpoint{0.25\pgfdecoratedinputsegmentremainingdistance}{1.0\pgfdecorationsegmentlength}}
        \pgfpathlineto{\pgfpoint{0.75\pgfdecoratedinputsegmentremainingdistance}{1.0\pgfdecorationsegmentlength}}
        \pgfpathlineto{\pgfpoint{0.75\pgfdecoratedinputsegmentremainingdistance}{0}}
        \pgfpathlineto{\pgfpoint{0.25\pgfdecoratedinputsegmentremainingdistance}{-1.0\pgfdecorationsegmentlength}}
        \pgfpathlineto{\pgfpoint{0.75\pgfdecoratedinputsegmentremainingdistance}{0}}
        \pgfpathlineto{\pgfpoint{0.25\pgfdecoratedinputsegmentremainingdistance}{1.0\pgfdecorationsegmentlength}}
        \pgfpathlineto{\pgfpoint{0.75\pgfdecoratedinputsegmentremainingdistance}{0}}
        \pgfpathlineto{\pgfpoint{\pgfdecoratedinputsegmentremainingdistance}{0}}
    }
    \state{final}
    {
        \pgfpathlineto{\pgfpointdecoratedpathlast}
    }
}

\pgfdeclaredecoration{resistor}{initial}
{
    \state{initial}[width=+\pgfdecoratedinputsegmentremainingdistance]
    {
        \pgfsetlinewidth{1pt}
        \pgfpathlineto{\pgfpoint{0.25\pgfdecoratedinputsegmentremainingdistance}{0}}
        \pgfpathlineto{\pgfpoint{0.25\pgfdecoratedinputsegmentremainingdistance}{1.0\pgfdecorationsegmentlength}}
        \pgfpathlineto{\pgfpoint{0.25\pgfdecoratedinputsegmentremainingdistance}{-1.0\pgfdecorationsegmentlength}}
        \pgfpathlineto{\pgfpoint{0.75\pgfdecoratedinputsegmentremainingdistance}{-1.0\pgfdecorationsegmentlength}}
        \pgfpathlineto{\pgfpoint{0.75\pgfdecoratedinputsegmentremainingdistance}{1.0\pgfdecorationsegmentlength}}
        \pgfpathlineto{\pgfpoint{0.25\pgfdecoratedinputsegmentremainingdistance}{1.0\pgfdecorationsegmentlength}}
        \pgfpathlineto{\pgfpoint{0.25\pgfdecoratedinputsegmentremainingdistance}{-1.0\pgfdecorationsegmentlength}}
        \pgfpathlineto{\pgfpoint{0.75\pgfdecoratedinputsegmentremainingdistance}{-1.0\pgfdecorationsegmentlength}}
        \pgfpathlineto{\pgfpoint{0.75\pgfdecoratedinputsegmentremainingdistance}{0}}
    }
    \state{final}
    {
        \pgfpathlineto{\pgfpointdecoratedpathlast}
    }
}


\begin{document}
\tikzstyle{nodeStyle}=[draw,circle,minimum size=5mm,shade, shading=axis, color=black,left color=gray!10, right color=gray, shading angle=45,inner sep=0mm,outer sep=0mm]
\tikzstyle{twoNode}=[nodeStyle]
\tikzstyle{sourceStyle}=[nodeStyle,left color=green, right color=green]
\tikzstyle{sinkStyle}=[nodeStyle,left color=red, right color=red]
\tikzstyle{linkStyle}=[rectangle,minimum size=4mm]
\tikzstyle{twoNode}=[nodeStyle]
\tikzstyle{edgeStyle}=[line width=1mm]
\tikzstyle{bridge}=[dotted,thick]
\tikzstyle{pipe}=[edgeStyle]
\tikzstyle{shortPipe}=[edgeStyle]
\tikzstyle{valve}=[edgeStyle, decorate, decoration=valve]
\tikzstyle{controlValve}=[edgeStyle, decorate, decoration=controlvalve]
\tikzstyle{resistor}=[edgeStyle, decorate, decoration=resistor]
\tikzstyle{compressorStation}=[edgeStyle, decorate, decoration=compressor, red]*/