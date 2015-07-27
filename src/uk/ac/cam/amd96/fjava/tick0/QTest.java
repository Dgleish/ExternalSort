package uk.ac.cam.amd96.fjava.tick0;

import java.util.PriorityQueue;

/**
 * Created by Alex on 25/07/2015.
 */
public class QTest {
    public static void main(String[] args) {
        PriorityQueue<Integer> q = new PriorityQueue<>();
        q.add(5);
        q.add(4);
        System.out.println(q.peek());
    }
}
