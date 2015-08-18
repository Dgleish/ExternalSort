package uk.ac.cam.amd96.fjava.tick0;

import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 * Created by Alex on 26/07/2015.
 */
public class memTester {
    static int iterations = 30;
    static long time;
    static long totalTime;
    static double defaultDivisor = 4.0;
    public static void main(String[] args) throws Exception {

        BufferedWriter b = new BufferedWriter(new FileWriter("memOut.txt"));
        System.out.println("Initialising test 3: ");
        for (int i = 1; i < 9; i++) {
            time = 0;
            totalTime = 0;
            for (int j = 0; j < iterations; j++) {
                try {
                    time = ExternalSortTest.main2(new String[]{args[0], args[1], String.valueOf(10), String.valueOf(8.0/(double)i)});
                    totalTime += time;
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                b.write(time + ", ");
            }
            b.newLine();
            System.out.println("mem = " + (8.0/(double)i) + " took " + (totalTime / iterations) + " ms on average");
            b.newLine();
            b.flush();
        }
        b.flush();
        b.close();
        System.out.println("Done");
        bufferSizeTester.main(args);
    }



}
