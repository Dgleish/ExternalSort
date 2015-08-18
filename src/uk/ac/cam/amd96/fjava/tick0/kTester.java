package uk.ac.cam.amd96.fjava.tick0;

import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 * Created by Alex on 26/07/2015.
 */
public class kTester {
    static int iterations = 30;
    static long time;
    static long totalTime;
    static double defaultDivisor = 1.5;
    public static void main(String[] args) throws Exception {

        BufferedWriter b = new BufferedWriter(new FileWriter("kOut.txt"));
        System.out.println("Initialising test: ");
        for (int i = 2; i < 31; i++) {
            b.write(i + ":");
            b.newLine();
            time = 0;
            totalTime = 0;
            for (int j = 0; j < iterations; j++) {
                try {
                    time = ExternalSortTest.main2(new String[]{args[0], args[1], String.valueOf(i), String.valueOf(defaultDivisor)});
                    totalTime += time;
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                b.write(time + ", ");
            }
            b.newLine();
            System.out.println("K = " + i + " took " + (totalTime / iterations) + " ms on average");
            b.newLine();
            b.flush();
        }
        b.flush();
        System.out.println("Initialising test 2: ");
        for (int i = 30; i > 1; i--) {
            b.write(i + ":");
            b.newLine();
            time = 0;
            totalTime = 0;
            for (int j = 0; j < iterations; j++) {
                try {
                    time = ExternalSortTest.main2(new String[]{args[0], args[1], String.valueOf(i), String.valueOf(defaultDivisor)});
                    totalTime += time;
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                b.write(time + ", ");
            }
            b.newLine();
            System.out.println("K = " + i + " took " + (totalTime / iterations) + " ms on average");
            b.newLine();
            b.flush();
        }
        b.flush();
        b.close();
        System.out.println("Done");
        memTester.main(args);
    }



}
