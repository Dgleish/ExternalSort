package uk.ac.cam.amd96.fjava.tick0;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Alex on 12/07/2015.
 */
public class SortTester {

    public static long check(RandomAccessFile f, long count) {
        if(count == -1){
            try {
                long i = 1;
                int prev = f.readInt();
                int curr;

                while (true) {
                    curr = f.readInt();
                    if (curr < prev) {
                        System.out.println("Jumped from " + prev + " to " + curr);
                        return i;
                    }
                    prev = curr;
                    i++;
                }
            } catch (IOException e) {
                System.out.println("Checked the whole file");
                return 0;
            }
        }else {
            try {
                long i = 1;
                int prev = f.readInt();
                int curr;

                while (i < count) {
                    curr = f.readInt();
                    if (curr < prev) {
                        System.out.println("Jumped from " + prev + " to " + curr);
                        return i;
                    }
                    prev = curr;
                    i++;
                }
                return 0;
            } catch (IOException e) {
                System.out.println("Checked the whole file");
                return 0;
            }
        }
    }
}
