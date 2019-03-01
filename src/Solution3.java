
import java.util.Scanner;

public class Solution3 {

    public static int x, y;

    public static boolean readResult() {
        x = scanner.nextInt();
        y = scanner.nextInt();
        //scanner.nextLine();
        if (x == 0 && y == 0) {
            return true;
        } else if (x == -1 && y == -1) {
            System.exit(1);
            return true;
        } else {
            prepared[x][y] = true;
            //System.err.println("Prepared: " + x + " " + y);
            return false;
        }
    }

    public static boolean prepareRectangle(int x1, int y1, int width, int height) {
        int xmod, ymod;
        // Lower Border
        xmod = 0;
        while (xmod <= width - 2) {
            while (!prepared[x1 + xmod][y1]) {
                //System.err.println("Not Prepared: " + (x1+xmod) + " " + y1);
                System.out.println((x1 + xmod + 1) + " " + (y1 + 1));
                if (readResult()) {
                    return true;
                }
            }
            ++xmod;
        }
        while (!prepared[x1 + width - 1][y1] || !prepared[x1 + width][y1]) {
            //System.err.println("Not prepared II: " + prepared[x1 + width - 1][y1] + " " + prepared[x1 + width][y1]);
            System.out.println((x1 + width - 1) + " " + (y1 + 1));
            if (readResult()) {
                return true;
            }
        }
        // Left Border
        ymod = 0;
        while (ymod <= height - 2) {
            while (!prepared[x1][y1 + ymod]) {
                System.out.println((x1 + 1) + " " + (y1 + ymod + 1));
                if (readResult()) {
                    return true;
                }
            }
            ++ymod;
        }
        while (!prepared[x1][y1 + height - 1] || !prepared[x1][y1 + height]) {
            System.out.println((x1 + 1) + " " + (y1 + height - 1));
            if (readResult()) {
                return true;
            }
        }
        // Top Border
        xmod = 0;
        while (xmod <= width - 2) {
            while (!prepared[x1 + xmod][y1 + height]) {
                System.out.println((x1 + xmod + 1) + " " + (y1 + height - 1));
                if (readResult()) {
                    return true;
                }
            }
            ++xmod;
        }
        while (!prepared[x1 + width - 1][y1 + height] || !prepared[x1 + width][y1 + height]) {
            System.out.println((x1 + width - 1) + " " + (y1 + height - 1));
            if (readResult()) {
                return true;
            }
        }
        // Right Border
        ymod = 0;
        while (ymod <= height - 2) {
            while (!prepared[x1 + width][y1 + ymod]) {
                System.out.println((x1 + width - 1) + " " + (y1 + ymod + 1));
                if (readResult()) {
                    return true;
                }
            }
            ++ymod;
        }
        while (!prepared[x1 + width][y1 + height - 1] || !prepared[x1 + width][y1 + height]) {
            System.out.println((x1 + width - 1) + " " + (y1 + height - 1));
            if (readResult()) {
                return true;
            }
        }
        return false;
    }

    static Scanner scanner;
    static boolean[][] prepared;
    //static int x1, y1;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        int numberOfTestCases = scanner.nextInt();
        //scanner.nextLine();
        for (int i = 0; i < numberOfTestCases; i++) {
            prepared = new boolean[1001][1001];
            int neccessary = scanner.nextInt();
            //scanner.nextLine();
            int width = (int) Math.round(Math.sqrt(neccessary)) -1;
            int height = (int) Math.ceil(neccessary * 1.0 / (width+1)) -1;
            //System.err.println(width + " " + height);
            // Start of the rectangle
            System.out.println("2 2");
            if (readResult()) {
                continue;
            }
            int x1 = x;
            int y1 = y;
            boolean finished = false;
            while (width >= 2 && height >= 2) {
                if (prepareRectangle(x1, y1, width, height)) {
                    finished = true;
                    break;
                }
                x1 = x1 + 1;
                y1 = y1 + 1;
                width = width - 2;
                height = height - 2;
            }
            if (finished) {
                continue;
            }

            if (width <= 1 && height <= 1) {
                while (!prepared[x1][y1]) {
                    System.out.println((x1) + " " + (y1));
                    if (readResult()) {
                        finished = true;
                        break;
                    }
                }
                while (!prepared[x1 + width][y1]) {
                    System.out.println((x1) + " " + (y1));
                    if (readResult()) {
                        finished = true;
                        break;
                    }
                }
                while (!prepared[x1][y1 + height]) {
                    System.out.println((x1) + " " + (y1));
                    if (readResult()) {
                        finished = true;
                        break;
                    }
                }
                while (!prepared[x1 + width][y1 + height]) {
                    System.out.println((x1) + " " + (y1));
                    if (readResult()) {
                        finished = true;
                        break;
                    }
                }
                if (finished) {
                    continue;
                }
            } else if (width <= 1 && height >= 2) {
                int ymod = 0;
                while (ymod <= height - 2) {
                    while (!prepared[x1][y1 + ymod]) {
                        System.out.println((x1) + " " + (y1 + ymod + 1));
                        if (readResult()) {
                            finished = true;
                            break;
                        }
                    }
                    if (finished) {
                        break;
                    }
                    ymod++;
                }
                if (finished) {
                    continue;
                }
                while (!prepared[x1][y1 + height - 1] || !prepared[x1][y1 + height]) {
                    System.out.println((x1) + " " + (y1 + height - 1));
                    if (readResult()) {
                        finished = true;
                        break;
                    }
                }
                if (finished) {
                    continue;
                }
                if (width == 1) {
                    ymod = 0;
                    while (ymod <= height - 2) {
                        while (!prepared[x1 + 1][y1 + ymod]) {
                            System.out.println((x1 + 1) + " " + (y1 + ymod + 1));
                            if (readResult()) {
                                finished = true;
                                break;
                            }
                        }
                        if (finished) {
                            break;
                        }
                        ymod++;
                    }
                    if (finished) {
                        continue;
                    }
                    while (!prepared[x1 + 1][y1 + height - 1] || !prepared[x1 + 1][y1 + height]) {
                        System.out.println((x1 + 1) + " " + (y1 + height - 1));
                        if (readResult()) {
                            finished = true;
                            break;
                        }
                    }
                    if (finished) {
                        continue;
                    }
                }
            } else if (width >= 2 && height <= 1) {
                int xmod = 0;
                while (xmod <= width - 2) {
                    while (!prepared[x1 + xmod][y1]) {
                        System.out.println((x1 + xmod + 1) + " " + (y1));
                        if (readResult()) {
                            finished = true;
                            break;
                        }
                    }
                    if (finished) {
                        break;
                    }
                    xmod++;
                }
                if (finished) {
                    continue;
                }
                while (!prepared[x1 + width - 1][y1] || !prepared[x1 + width][y1]) {
                    System.out.println((x1 + width - 1) + " " + (y1));
                    if (readResult()) {
                        finished = true;
                        break;
                    }
                }
                if (finished) {
                    continue;
                }
                if (height == 1) {
                    while (xmod <= width - 2) {
                        while (!prepared[x1 + xmod][y1 + height]) {
                            System.out.println((x1 + xmod + 1) + " " + (y1 + height));
                            if (readResult()) {
                                finished = true;
                                break;
                            }
                        }
                        if (finished) {
                            break;
                        }
                        xmod++;
                    }
                    if (finished) {
                        continue;
                    }
                    while (!prepared[x1 + width - 1][y1 + height] || !prepared[x1 + width][y1 + height]) {
                        System.out.println((x1 + width - 1) + " " + (y1 + height));
                        if (readResult()) {
                            finished = true;
                            break;
                        }
                    }
                    if (finished) {
                        continue;
                    }
                }
            }
        }
        System.exit(0);
    }

}
