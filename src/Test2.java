
import java.util.ArrayList;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Martin
 */
public class Test2 {

    public static void main(String[] args) {

        double eps = 1.0 / 64.0;
        List<Double> lengths = new ArrayList<>();
        double length = eps;
        int iteration = 2;
        while (iteration <= 10) {
            lengths.add(length);
            System.out.println(length);
            length = 1 - Math.pow(1 - eps, iteration);
            int i = iteration;
            while (i > 2) {
                i -= 2;
                //length -= (Math.pow(1 - eps, i));
            }
            iteration++;
        }
    }

}
