
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class SolutionR1 {

    public static boolean DEBUG = false;
    
    public static List<Integer> tryVCut(char[][] waffle, int cutAfter) {
        int choc = 0;
        LinkedList<Integer> result = new LinkedList<>();
        for (int r = 0; r < waffle.length; r++) {
            for (int c = 0; c <= cutAfter; c++) {
                if (waffle[r][c] == '@') {
                    choc++;
                }
            }
            if (choc == chocPerPiece) {
                if (r < waffle.length-1) {
                    result.add(r);
                }                
                choc = 0;
            } else if (choc > chocPerPiece) {
                return null;
            }
        }
        if (choc != 0 || result.isEmpty()) {
            return null;
        }
        return result;
    }

    public static int lastRow;
    
    public static boolean tryHCut(char[][] waffle, int firstCol, int firstRow, int lastRow) {
        if (DEBUG) {
            System.err.println("   trying HCut " + firstCol + " " + firstRow + " " + lastRow);
        }
        int choc = 0;
        int resets = 0;
        int column = firstCol;
        //boolean broken = false;
        while (choc < chocPerPiece && column < waffle[0].length) {
            for (int r = firstRow; r <= lastRow; r++) {
                if (waffle[r][column] == '@') {
                    choc++;
                }
            }
            if (choc > chocPerPiece) {
                break;
            } else if (choc == chocPerPiece) {
                choc = 0;
                resets++;
            }
            column++;
        }
        if (choc > 0) {
            if (DEBUG) System.err.println("   false");
            return false;
        } else {
            
            if (resets == verticalCuts) {
                if (DEBUG) System.err.println("   true");
                return true;
            } else {
                if (DEBUG) System.err.println("   false2");
                return false;
            }
        }
    }
    
    public static boolean tryHCutRepeat(char[][] waffle, int firstCol, int firstRow, int lastRow) {
        if (DEBUG) {
            System.err.println("  trying HCut repeatedly " + firstCol + " " + firstRow + " " + lastRow);
        }
        boolean first = tryHCut(waffle, firstCol, firstRow, lastRow);
        while (!first && isZeroChoc(waffle, firstCol-1, lastRow+1)) {
           lastRow++;
           first = tryHCut(waffle, firstCol, firstRow, lastRow);
        }
        SolutionR1.lastRow = lastRow;
        return first;
    }
        
    
    public static boolean isZeroChoc(char[][] waffle, int lastCol, int row) {
        if (row == waffle.length) {
            return false;
        }
        boolean result = true;
        for (int c = 0; c <= lastCol; c++) {
            if (waffle[row][c] == '@') {
                return false;
            }
        }
        return result;
    }

    public static boolean tryAllPossibleVCuts(char[][] waffle) {
        //LinkedList<Integer> result = new LinkedList();
        for (int c = 0; c < waffle[0].length; c++) {
            if (DEBUG) System.err.println(" Trying " + c);
            List<Integer> inducedHCuts = tryVCut(waffle, c);
            if (DEBUG) System.err.println(" Induced Cuts: " + inducedHCuts + " vs. " + horizontalCuts);
            if (inducedHCuts != null && inducedHCuts.size() == horizontalCuts) {
                if (c < waffle[0].length - 1) {                    
                    if (!tryHCutRepeat(waffle, c+1, 0, inducedHCuts.get(0))) continue;
                    boolean broken = false;
                    for (int h = 0; h < inducedHCuts.size()-1; h++) {
                        if (!tryHCutRepeat(waffle, c+1, lastRow+1, inducedHCuts.get(h+1))) {
                            broken = true;
                            break;
                        }
                    }
                    if (!tryHCutRepeat(waffle, c+1, lastRow+1, waffle.length-1)) continue;
                    if (!broken) {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }


    public static int chocPerPiece, horizontalCuts, verticalCuts;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int numberOfTestCases = scanner.nextInt();
        scanner.nextLine();
        boolean[] answers = new boolean[numberOfTestCases]; 
        for (int i = 0; i < numberOfTestCases; i++) {
            int rows = scanner.nextInt();
            int columns = scanner.nextInt();
            horizontalCuts = scanner.nextInt();
            verticalCuts = scanner.nextInt();
            scanner.nextLine();
            char[][] waffle = new char[rows][columns];
            int chocolates = 0;
            for (int r = 0; r < rows; r++) {
                waffle[r] = scanner.nextLine().toCharArray();
                for (int c = 0; c < columns; c++) {
                    if (waffle[r][c] == '@') {
                        chocolates++;
                    }
                }
            }

            int numberOfPieces = (horizontalCuts + 1) * (verticalCuts + 1);
            if (chocolates % numberOfPieces > 0) {
                //System.out.println("Case #" + (i + 1) + ": IMPOSSIBLE");
                answers[i] = false;
                continue;
            } else if (chocolates == 0) {
                answers[i] = true;
                continue;
            }
            chocPerPiece = chocolates / numberOfPieces;
            if (DEBUG) System.err.println(chocPerPiece);
            if (tryAllPossibleVCuts(waffle)) {                
                answers[i] = true;
            } else {
                answers[i] = false;
                //System.out.println("Case #" + (i + 1) + ": IMPOSSIBLE");
            }
        }
        for (int i = 0; i < numberOfTestCases; i++) {
            if (answers[i]) {
                System.out.println("Case #" + (i + 1) + ": POSSIBLE");
            } else {
                System.out.println("Case #" + (i + 1) + ": IMPOSSIBLE");
            }
        }

        System.exit(0);
    }

}
