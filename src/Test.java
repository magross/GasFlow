
import java.util.ArrayList;
import java.util.LinkedList;
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
public class Test {

    public static final int D = 16;

    public static class Vertex {

        int[] coordinates = new int[D];

        public Vertex(int i) {
            for (int j = 0; j < D; j++) {
                coordinates[j] = i % 2;
                i = i / 2;
            }
        }

        public int dist(Vertex v) {
            int dist = 0;
            for (int i = 0; i < D; i++) {
                if (coordinates[i] - v.coordinates[i] != 0) {
                    dist++;
                }
            }
            return dist;
        }

    }

    public static void main(String[] args) {
        List<Vertex> vertices = new LinkedList<>();
        vertices.add(new Vertex(0));
        double totalDistances = 0.0;
        int bestDist = 0;
        do {
            int countAdditions = 0;
            bestDist = 0;

            for (int i = 1; i < Math.pow(2, D); i++) {
                Vertex v = new Vertex(i);

                int dist = D * 2;
                for (Vertex vertex : vertices) {
                    if (dist > v.dist(vertex)) {
                        dist = v.dist(vertex);
                    }
                }
                if (dist > bestDist) {
                    bestDist = dist;
                }
            }

            List<Vertex> best = new LinkedList<>();
            for (int i = 1; i < Math.pow(2, D); i++) {
                Vertex v = new Vertex(i);

                int dist = D * 2;
                for (Vertex vertex : vertices) {
                    if (dist > v.dist(vertex)) {
                        dist = v.dist(vertex);
                    }
                }
                if (dist == bestDist) {
                    vertices.add(v);
                    countAdditions++;
                }
            }

            System.out.println(bestDist + ": " + countAdditions);
            totalDistances += Math.sqrt(bestDist) * countAdditions;
        } while (bestDist > 1);
        System.out.println("Total: " + totalDistances + " vs. " + (Math.pow(2,D)-1));
        
    }

}
