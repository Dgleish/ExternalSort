package uk.ac.cam.amd96.fjava.tick0;

/**
 * Created by Alex on 25/07/2015.
 */
public class MutableLong {
    long mutLong;

    public MutableLong(long l){
        mutLong = l;
    }

    public MutableLong increment(){
        mutLong++;
        return this;
    }

    public long getValue(){
        return mutLong;
    }
}
