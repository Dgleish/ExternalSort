package uk.ac.cam.amd96.fjava.tick0;

import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 * Created by Alex on 26/07/2015.
 */
public class kTester {
    static int iterations = 10;
    static long time;
    static long totalTime;
    public static void main(String[] args) throws Exception {

        BufferedWriter b = new BufferedWriter(new FileWriter("outNew.txt"));
        System.out.println("Initialising test: ");
        for (int i = 2; i < 31; i++) {
            time = 0;
            totalTime = 0;
            for (int j = 0; j < iterations; j++) {
                try {
                    time = ExternalSortTest.main2(new String[]{args[0], args[1], String.valueOf(i)});
                    totalTime += time;
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                b.write(time + ", ");
            }
            b.newLine();
            b.write("K = " + i + " took " + (totalTime / iterations) + " ms on average");
            System.out.println("K = " + i + " took " + (totalTime / iterations) + " ms on average");
            b.newLine();
            b.flush();
        }
        b.flush();
        System.out.println("Initialising test 2: ");
        for (int i = 2; i < 31; i++) {
            time = 0;
            totalTime = 0;
            for (int j = 0; j < iterations; j++) {
                try {
                    time = ExternalSortTest.main2(new String[]{args[0], args[1], String.valueOf(i)});
                    totalTime += time;
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                b.write(time + ", ");
            }
            b.newLine();
            b.write("K = " + i + " took " + (totalTime / iterations) + " ms on average");
            System.out.println("K = " + i + " took " + (totalTime / iterations) + " ms on average");
            b.newLine();
            b.flush();
        }
        b.flush();
        b.close();
        System.out.println("Done");
    }
}
