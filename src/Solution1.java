
import java.util.Scanner;

public class Solution1 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int numberOfTestCases = scanner.nextInt();
        scanner.nextLine();
        int[] shields = new int[numberOfTestCases];
        String[] programs = new String[numberOfTestCases];
        for (int i = 0; i < numberOfTestCases; i++) {
            shields[i] = scanner.nextInt();
            programs[i] = scanner.nextLine();
        }
        for (int i = 0; i < numberOfTestCases; i++) {
            int numberOfShots = 0;
            int numberOfCharges = 0;
            int totalDamage = 0;
            int damage = 1;
            for (int j = 0; j < programs[i].length(); j++) {
                if (programs[i].charAt(j) == 'S') {
                    ++numberOfShots;
                    totalDamage += damage;
                }
                if (programs[i].charAt(j) == 'C') {
                    ++numberOfCharges;
                    damage *= 2;
                }
            }
            if (numberOfShots > shields[i]) {
                System.out.println("Case #" + (i + 1) + ": IMPOSSIBLE");
            } else {
                if (programs[i].length() == 1) {
                    System.out.println("Case #" + (i + 1) + ": 0");
                } else {
                    int reduceBy = totalDamage - shields[i];
                    int hacks = 0;
                    char[] program = programs[i].toCharArray();
                    while (reduceBy > 0) {
                        boolean foundShot = false;
                        boolean foundCharge = false;
                        int lastChargeIndex = -1;
                        int lastShotIndex = -1;
                        int hackValue = 1;
                        for (int j = program.length - 1; j >= 0; j--) {
                            if (!foundShot && program[j] == 'S') {
                                foundShot = true;
                                lastShotIndex = j;
                                continue;
                            }
                            if (foundShot && !foundCharge && program[j] == 'C') {
                                lastChargeIndex = j;
                                foundCharge = true;
                                continue;
                            }
                            if (foundShot && foundCharge && program[j] == 'C') {
                                hackValue *= 2;
                            }
                        }
                        //System.out.println("HackValue: " + hackValue);
                        //System.out.println("LastShotIndex: " + lastShotIndex);
                        // Hacks
                        
                        for (int j = lastChargeIndex; j <= lastShotIndex - 1; j++) {
                            char first = program[j];
                            char second = program[j + 1];
                            if (first == 'C' && second == 'S') {
                                program[j] = 'S';
                                program[j + 1] = 'C';
                                reduceBy -= hackValue;
                            } else if (first == 'C' && second == 'C') {

                            } else {
                                throw new AssertionError("This case should not happen");
                            }
                            ++hacks;
                            if (reduceBy <= 0) {
                                break;
                            }
                        }
                    }
                    System.out.println("Case #" + (i + 1) + ": " + hacks);
                }
            }
        }
        System.exit(0);
    }

}
