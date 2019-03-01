
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


public class Solution {

    /*
     * Complete the timeConversion function below.
     */
    static String timeConversion(String s) {
        int hours = Integer.parseInt(s.substring(0, 2));
        String remainder = s.substring(2, s.length() - 2);
        if (s.endsWith("AM")) {
            if (hours == 12) {
                hours = 0;
            }
        } else {
            if (hours < 12) {
                hours += 12;
            }
        }
        return String.format("%02d",hours) + remainder; 
    }

    private static final Scanner scan = new Scanner(System.in);

    public static void main(String[] args) throws IOException {

        String s = scan.nextLine();

        String result = timeConversion(s);

        System.out.println(result);


        System.out.close();
    }
}
