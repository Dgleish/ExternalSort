package uk.ac.cam.amd96.fjava.tick0;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Created by Alex on 26/07/2015.
 */
public class kTester {
    static int iterations = 4;
    static int totalTime;
    public static void main(String[] args) throws Exception {

        BufferedWriter b = new BufferedWriter(new FileWriter("out.txt"));
        System.out.println("Initialising test: ");
        for (int i = 30; i > 1; i-= 3) {
            totalTime = 0;
            for (int j = 0; j < iterations; j++) {
                try {
                    totalTime += ExternalSortTest.main2(new String[]{args[0], args[1], String.valueOf(i)});
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                b.write(totalTime + ", ");
            }
            b.newLine();
            b.write("K = " + i + " took " + (totalTime / iterations) + " seconds on average");
            b.newLine();
            b.flush();
        }
        b.flush();
        b.close();
        System.out.println("Done");
    }
}
