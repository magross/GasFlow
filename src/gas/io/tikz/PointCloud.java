/**
 * PointCloud.java
 *
 */
package gas.io.tikz;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author Martin Groß
 */
public class PointCloud {

    static Random rng = new Random();

    static {

    }

    private static Point2D.Double generateGaussianPoint(double x, double y, double rx, double ry) {
        double dx = rng.nextGaussian() * rx;
        double dy = rng.nextGaussian() * ry;
        return new Point2D.Double(x + dx, y + dy);
    }

    private static Point2D.Double generateUniformPoint(double x, double y, double rx, double ry) {
        double dx = rng.nextDouble() * rx;
        double dy = rng.nextDouble() * ry;
        return new Point2D.Double(x + dx, y + dy);
    }

    public static Point2D.Double[] lloyds(int k, Point2D.Double[] points) {
        Point2D.Double[] centers = new Point2D.Double[k];
        for (int i = 0; i < centers.length; i++) {
            centers[i] = points[i];
        }
        System.out.printf("      \\onslide<%1$s-%2$s>{\n", 5, 6);
        for (int i = 0; i < centers.length; i++) {
            String color = "";
            switch (i) {
                case 0:
                    color = "Blue";
                    break;
                case 1:
                    color = "Red";
                    break;
                case 2:
                    color = "Green";
                    break;
            }
            System.out.printf("       \\node[centers%4$s] (q%1$s) at (%2$.2f,%3$.2f) {};\n", i, centers[i].getX(), centers[i].getY(), color);
        }
        System.out.println("      }");
        boolean changed = false;
        int[] assignment = new int[points.length];
        //System.out.println(Arrays.toString(centers));
        int iteration = 6;
        do {
            // Assign points to centroids
            for (int i = 0; i < points.length; i++) {
                double min = Double.POSITIVE_INFINITY;
                for (int j = 0; j < centers.length; j++) {
                    double dist = points[i].distanceSq(centers[j]);
                    if (dist < min) {
                        min = dist;
                        assignment[i] = j;
                    }
                }
            }
            //
            System.out.printf("      \\onslide<%1$s->{\n", iteration);
            for (int i = 0; i < points.length; i++) {
                String color = "";
                switch (assignment[i]) {
                    case 0:
                        color = "Blue";
                        break;
                    case 1:
                        color = "Red";
                        break;
                    case 2:
                        color = "Green";
                        break;
                }
                System.out.printf("       \\node[point%4$s] (q%1$s) at (%2$.2f,%3$.2f) {};\n", i, points[i].getX(), points[i].getY(), color);
            }
            System.out.println("      }");
            System.out.printf("      \\onslide<%1$s-%2$s>{\n", iteration, iteration);
            for (int i = 0; i < centers.length; i++) {
                String color = "";
                switch (i) {
                    case 0:
                        color = "Blue";
                        break;
                    case 1:
                        color = "Red";
                        break;
                    case 2:
                        color = "Green";
                        break;
                }
                System.out.printf("       \\node[centers%4$s] (q%1$s) at (%2$.2f,%3$.2f) {};\n", i, centers[i].getX(), centers[i].getY(), color);
            }
            System.out.println("      }");
            // Recompute centroids
            Point2D.Double[] newCenters = new Point2D.Double[k];
            for (int i = 0; i < centers.length; i++) {
                newCenters[i] = new Point2D.Double();
                int counter = 0;
                for (int j = 0; j < points.length; j++) {
                    if (assignment[j] == i) {
                        newCenters[i].setLocation(newCenters[i].getX() + points[j].getX(), newCenters[i].getY() + points[j].getY());
                        counter++;
                    }
                }
                newCenters[i].setLocation(newCenters[i].getX() / counter, newCenters[i].getY() / counter);
            }
            changed = false;
            for (int c = 0; c < centers.length; c++) {
                if (newCenters[c].distance(centers[c]) >= 0.000001) {
                    changed = true;
                }
            }
            centers = newCenters;
            System.out.printf("      \\onslide<%1$s-%2$s>{\n", iteration + 1, iteration + 2);
            for (int i = 0; i < centers.length; i++) {
                String color = "";
                switch (i) {
                    case 0:
                        color = "Blue";
                        break;
                    case 1:
                        color = "Red";
                        break;
                    case 2:
                        color = "Green";
                        break;
                }
                System.out.printf("       \\node[centers%4$s] (q%1$s) at (%2$.2f,%3$.2f) {};\n", i, centers[i].getX(), centers[i].getY(), color);
            }
            System.out.println("      }");
            iteration += 2;
            //System.out.println(Arrays.toString(centers));
        } while (changed);
        return centers;
    }

    public static int slide = 2;
    
    public static Point2D.Double[] kmeanspp(int k, Point2D.Double[] points) {
        slide++;
        int slideend = slide+4;
        Point2D.Double[] centers = new Point2D.Double[k];
        centers[0] = points[rng.nextInt(points.length)];        
        System.out.printf("       \\node<%5$s-%6$s>[centers%4$s] (q%1$s) at (%2$.2f,%3$.2f) {};\n", 0, centers[0].getX(), centers[0].getY(), "Blue",slide,slideend);
        slide++;
        for (int c = 1; c < centers.length; c++) {
            double sum = 0;
            double[] distances = new double[points.length];
            for (int p = 0; p < points.length; p++) {
                double min = Double.POSITIVE_INFINITY;
                for (int i = 0; i < c; i++) {
                    double dist = points[p].distanceSq(centers[i]);
                    if (dist < min) {
                        min = dist;
                    }
                }
                distances[p] = min;
                sum += min;
            }
            double r = rng.nextDouble()*sum;
            int index = 0;
            while (r > distances[index] || distances[index] == 0) {
                r -= distances[index];
                index++;                
            }
            centers[c] = points[index];
            String color = "";
            switch (c) {
                case 0:
                    color = "Blue";
                    break;
                case 1:
                    color = "Red";
                    break;
                case 2:
                    color = "Green";
                    break;
               case 3:
                    color = "Orange";
                    break;                    
               case 4:
                    color = "Gray";
                    break;                            
            }                        
            System.out.printf("       \\node<%5$s-%6$s>[centers%4$s] (q%1$s) at (%2$.2f,%3$.2f) {};\n", c, centers[c].getX(), centers[c].getY(), color,slide,slideend);
                   slide++;
        }
        return centers;
    }

    public static void main(String[] args) {
        Point2D.Double[] points = new Point2D.Double[50];
        for (int i = 0; i < 50; i++) {
            points[i] = generateUniformPoint(0, 0, 8.0, 3.0);
        }
        for (int i = 0; i < points.length; i++) {
            System.out.printf("      \\node[pointBlack] (q%1$s) at (%2$.2f,%3$.2f) {};\n", i, points[i].getX(), points[i].getY());
        }
        //Point2D.Double[] centers = lloyds(3, points);
        Point2D.Double[] centers = kmeanspp(5, points);
        kmeanspp(5, points);
        kmeanspp(5, points);
    }

}
