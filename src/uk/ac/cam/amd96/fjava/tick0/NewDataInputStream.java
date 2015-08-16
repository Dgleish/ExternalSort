package uk.ac.cam.amd96.fjava.tick0;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Alex on 16/08/2015.
 */
public class NewDataInputStream extends DataInputStream{
    private int head;
    private boolean isEmpty = false;
    public NewDataInputStream(InputStream inputStream) throws IOException{
        super(inputStream);
    }

    public int extract(){
        int old = head;
        try {
            head = this.readInt();
            return old;
        }catch (IOException e){
            isEmpty = true;
            return old;
        }

    }

    public int peek(){
        return head;
    }

    @Override
    public long skip(long n) throws IOException {
        long result = super.skip(n);
        head = this.readInt();
        return result;
    }

    public boolean isEmpty(){
        return isEmpty;
    }


}
