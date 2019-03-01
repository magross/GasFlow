
import java.util.Locale;
import java.util.Scanner;

public class Solution4 {

    public static void printRotatedPoint(double x, double y, double z, double angle, double beta) {
        double x1 = x; 
        double y1 = y*Math.cos(angle)-z*Math.sin(angle);
        double z1 = y*Math.sin(angle)+z*Math.cos(angle);        
        double x2 = x1*Math.cos(beta) - y1*Math.sin(beta);
        double y2 = x1*Math.sin(beta) + y1*Math.cos(beta);
        double z2 = z1;
        System.out.println(x2 + " " + y2 + " " + z2);
    }    
    
    public static void rotateByRAndPrint(double x, double y, double z) {
        double x1 = R[0][0]*x + R[0][1]*y + R[0][2]*z; 
        double y1 = R[1][0]*x + R[1][1]*y + R[1][2]*z;
        double z1 = R[2][0]*x + R[2][1]*y + R[2][2]*z;
        System.out.println(x1 + " " + y1 + " " + z1);
    }    

    static double R[][] = new double[3][3];
    
    public static void main(String[] args) {
        Locale.setDefault(Locale.CANADA);
        Scanner scanner = new Scanner(System.in);
        scanner.useLocale(Locale.CANADA);
        int numberOfTestCases = scanner.nextInt();
        double[] areas = new double[numberOfTestCases];
        for (int i = 0; i < numberOfTestCases; i++) {
            areas[i] = scanner.nextDouble();
        }
        for (int i = 0; i < numberOfTestCases; i++) {
            System.out.println("Case #"+(i+1)+":");
            
            if (areas[i] <= Math.sqrt(2)) {
                double angle = Math.asin(areas[i] / Math.sqrt(2)) - Math.PI/4;
                printRotatedPoint(0.5,0.0,0.0,angle,0);
                printRotatedPoint(0.0,0.5,0.0,angle,0);
                printRotatedPoint(0.0,0.0,0.5,angle,0);            
            } else {
                double y = Math.sqrt(1.0/6.0 - 2.0/36.0*areas[i]*areas[i]) + 2.0/6.0 * areas[i];
                double x = areas[i] - 2*y;
                double z = y;
               
                
                R[0][0] = 1 - x*x / (1 + y); R[0][1] = -x;                R[0][2] = -x*z/(1+y);
                R[1][0] = x;                 R[1][1] = 1-(x*x+z*z)/(1+y); R[1][2] = z;
                R[2][0] = -x*z/(1+y);        R[2][1] = -z;                R[2][2] = 1 - z*z/(1+y);
                 
                rotateByRAndPrint(0.5,0.0,0.0);
                rotateByRAndPrint(0.0,0.5,0.0);
                rotateByRAndPrint(0.0,0.0,0.5);
            }
        }
        System.exit(0);
        
    }

}
