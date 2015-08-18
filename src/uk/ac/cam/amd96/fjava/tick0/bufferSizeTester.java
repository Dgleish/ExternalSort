package uk.ac.cam.amd96.fjava.tick0;

import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 * Created by Alex on 26/07/2015.
 */
public class bufferSizeTester {
    static int iterations = 30;
    static long time;
    static long totalTime;
    static double defaultDivisor = 4.0;
    public static void main(String[] args) throws Exception {

        BufferedWriter b = new BufferedWriter(new FileWriter("bufferOut.txt"));
        System.out.println("Initialising test 4: ");
        for (int i = 1; i < 11; i++) {
            time = 0;
            totalTime = 0;
            for (int j = 0; j < iterations; j++) {
                try {
                    time = ExternalSortTest.main2(new String[]{args[0], args[1], String.valueOf(10), String.valueOf(1.5), String.valueOf(i)});
                    totalTime += time;
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                b.write(time + ", ");
            }
            b.newLine();
            System.out.println("buffer = " + i + " took " + (totalTime / iterations) + " ms on average");
            b.write("buffer = " + i + " took " + (totalTime / iterations) + " ms on average");
            b.newLine();
            b.flush();
        }
        b.flush();
        b.close();
        System.out.println("Done");
    }



}
