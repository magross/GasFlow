
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Solution2 {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        int numberOfTestCases = 200; //scanner.nextInt();
        int[] numberOfNumbers = new int[numberOfTestCases];
        int[][] evenNumbers = new int[200][];
        int[][] oddNumbers = new int[200][];/*
        for (int i = 0; i < numberOfTestCases; i++) {
            numberOfNumbers[i] = scanner.nextInt();
            evenNumbers[i] = new int[(numberOfNumbers[i] + 1) / 2];
            oddNumbers[i] = new int[numberOfNumbers[i] / 2];
            for (int j = 0; j < numberOfNumbers[i]; j++) {
                if (j % 2 == 0) {
                    evenNumbers[i][j / 2] = scanner.nextInt();
                } else {
                    oddNumbers[i][j / 2] = scanner.nextInt();
                }
            }
        }*/
        for (int i = 0; i < numberOfTestCases; i++) {
            evenNumbers[i] = new int[50000];
            oddNumbers[i] = new int[50000];
            Random rng = new Random();
            for (int j = 0; j < 100000; j++) {
                if (j % 2 == 0) {
                    evenNumbers[i][j / 2] = rng.nextInt(1000000000);
                } else {
                    oddNumbers[i][j / 2] = rng.nextInt(1000000000);
                }
            }
        }
        for (int i = 0; i < numberOfTestCases; i++) {
            Arrays.sort(evenNumbers[i]);
            Arrays.sort(oddNumbers[i]);
            boolean violated = false;
            for (int j = 1; j < numberOfNumbers[i]; j++) {
                if ((j % 2 == 1) && oddNumbers[i][j / 2] < evenNumbers[i][(j - 1) / 2]) {
                    System.out.println("Case #" + (i + 1) + ": " + (j - 1));
                    violated = true;
                    break;
                } else if ((j % 2 == 0) && oddNumbers[i][(j - 1) / 2] > evenNumbers[i][j / 2]) {
                    System.out.println("Case #" + (i + 1) + ": " + (j - 1));
                    violated = true;
                    break;
                }
            }
            if (!violated) {
                System.out.println("Case #" + (i + 1) + ": OK");
            }
        }
        System.exit(0);
    }

}
