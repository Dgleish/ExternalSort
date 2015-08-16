package uk.ac.cam.amd96.fjava.tick0;

import java.io.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class ExternalSortTest {
    //file length
    private static long length;
    //size of fixed length portions that are sorted in memory = 1 'page'
    private static long pageSize;
    //number of ints in one page
    private static long pageSizeInt;
    //how many input buffers
    private static int fanAmt;
    private static int bufferSize;
    //space available to JVM
    private static long memory;
    //if the fanAmt is sufficiently close to the number of blocks just merge all together
    private static int threshold = 5;

    public static void sort(String f1, String f2) throws IOException {
        RandomAccessFile rafA = new RandomAccessFile(f1, "rw");
        RandomAccessFile rafB = new RandomAccessFile(f2, "rw");
        RandomAccessFile rafC = new RandomAccessFile("Tick0Data/test17c.dat", "r");
        length = rafA.length();
        if (length < pageSize) {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(rafC.getFD())));
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(rafB.getFD())));
            System.out.println("File fits in memory, sort conventionally");
            //create array and feed all the integers into it
            int[] nums = new int[(int) length / 4];
            for (int i = 0; i < length / 4; i++) {
                nums[i] = dis.readInt();
            }
            //sort the array
            Arrays.sort(nums);
            //write out all the integers in the array
            for (int i = 0; i < length / 4; i++) {
                dos.writeInt(nums[i]);
            }
            dos.flush();

            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(
                    new RandomAccessFile(f2, "r").getFD()
            )));

            DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(
                    new RandomAccessFile(f1, "rw").getFD()
            )));
            try {
                while (true) {
                    dataOutputStream.writeInt(dataInputStream.readInt());
                }
            } catch (EOFException e) {
            }finally {
                dataOutputStream.flush();
            }
        } else {
            //sort chunks of data conventionally and write to fileB
            pageSort(new DataInputStream(new BufferedInputStream(new FileInputStream(rafC.getFD()))),
                    new DataOutputStream(new BufferedOutputStream(new FileOutputStream(rafB.getFD()))));
            //now do k-way merges of each page
            boolean readingFromA = false;
            int numPasses;
            int blocksLeft;
            int totalBlocks;
            int blocksMerged;
            int k;
            //loop until the file is one block

            for (long blockSize = pageSize; blockSize < length; blockSize *= fanAmt) {
                numPasses = 0;
                totalBlocks = (int) Math.ceil(length / (double) blockSize);
                blocksMerged = 0;
                if (readingFromA) {
                    while (blocksMerged < totalBlocks) {
                        blocksLeft = totalBlocks - blocksMerged;
                        if (blocksLeft - fanAmt > 0 && blocksLeft - fanAmt < threshold) {
                            k = blocksLeft;
                        } else {
                            k = Math.min(fanAmt, blocksLeft);
                        }
                        blocksMerged += merge(f1,
                                f2,
                                blockSize, blockSize * numPasses * fanAmt, k);
                        numPasses++;
                    }
                } else {
                    while (blocksMerged < totalBlocks) {
                        blocksLeft = totalBlocks - blocksMerged;
                        /*  if k is close enough to all the blocks, merge between all of them to save
                            two passes                                                               */
                        if (blocksLeft - fanAmt > 0 && blocksLeft - fanAmt < threshold) {
                            k = blocksLeft;
                        } else {
                            k = Math.min(fanAmt, blocksLeft);
                        }
                        blocksMerged += merge(f2,
                                f1,
                                blockSize, blockSize * numPasses * fanAmt, k);
                        numPasses++;
                    }
                }
                readingFromA = !readingFromA;
            }
            if (!readingFromA) { //need to copy contents completely to A
                DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(
                        new RandomAccessFile(f2, "r").getFD()
                )));

                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(
                        new RandomAccessFile(f1, "rw").getFD()
                )));
                try {
                    while (true) {
                        dos.writeInt(dis.readInt());
                    }
                } catch (EOFException e) {
                    dos.flush();
                }
            }
        }
    }

    private static void pageSort(DataInputStream dis, DataOutputStream dos) throws IOException {

        int[] nums = new int[(int) pageSizeInt];
        long numberCount = 0;
        while (numberCount * 4 < length - pageSize) {
            for (int i = 0; i < pageSizeInt; i++) {
                nums[i] = dis.readInt();
                numberCount++;
            }
            Arrays.sort(nums);
            for (int j = 0; j < pageSizeInt; j++) {
                dos.writeInt(nums[j]);
            }


        }
        //do last page, which has size <= pageSize
        int j = 0;
        nums = new int[(int)((length/4) - numberCount)];
        try {
            for (j = 0; j < pageSizeInt; j++) {
                nums[j] = dis.readInt();
            }
        } catch (EOFException e) {
        } finally {
            Arrays.sort(nums);
            for (int i = 0; i < j; i++) {
                dos.writeInt(nums[i]);
            }
            dos.flush();
        }
    }

    private static int merge(String inputFile, String outputFile, long blockSize, long startPos, int k) throws IOException {
        //do a k-way merge
        bufferSize = (int) ((memory / (k + 2)) * 0.7); //approximate space for k buffers and an output buffer
        RandomAccessFile rafOut = new RandomAccessFile(outputFile, "rw");
        rafOut.seek(startPos);
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(rafOut.getFD()), bufferSize * 2));
        long bufferCount = 0;
        if (startPos + (k - 1) * blockSize > length) {
            throw new IOException("Error: start position goes over the end of the file");
        }

        //keep priority queue of ints in buffers
        PriorityQueue<NewDataInputStream> q = new PriorityQueue<NewDataInputStream>(
                new Comparator<NewDataInputStream>() {
                    @Override
                    public int compare(NewDataInputStream d1, NewDataInputStream d2) {
                        return Integer.compare(d1.peek(), d2.peek());
                    }
                }
        );

        //Map number of integers read from each data stream to that data stream object
        Map<NewDataInputStream, MutableLong> numsReadMap = new HashMap<NewDataInputStream, MutableLong>(k);

        //create the input buffers and add them to priority queue
        NewDataInputStream d;
        while (bufferCount < k) {
            //create buffered stream size bufferSize
            d = new NewDataInputStream(new BufferedInputStream(
                    new FileInputStream(new RandomAccessFile(inputFile, "rw").getFD()), bufferSize));
            numsReadMap.put(d, new MutableLong(0l));
            d.skip((bufferCount * blockSize) + startPos);
            q.offer(d);
            bufferCount++;
        }
        //continuously empty buffers
        NewDataInputStream dis;
        while (!q.isEmpty()) {
            //get the stream at the head of the queue
            dis = q.poll();
            //increment the integer count for this input stream
            numsReadMap.get(dis).increment();
            //write out smallest int
            dos.writeInt(dis.extract());
            //put the stream back in so that the queue reorders
            q.offer(dis);
            //check for empty data streams (EoF) or when a stream has read the maximum number of ints
            Iterator<NewDataInputStream> it = q.iterator();
            while (it.hasNext()) {
                dis = it.next();
                if (dis.isEmpty()) { //empty
                    it.remove();
                    numsReadMap.remove(dis);
                } else if (numsReadMap.get(dis).getValue() == (blockSize / 4)) {
                   it.remove();
                    numsReadMap.remove(dis);
                }
            }
        }
        dos.flush();
        return k;
    }

    private static String byteToHex(byte b) {
        String r = Integer.toHexString(b);
        if (r.length() == 8) {
            return r.substring(6);
        }
        return r;
    }

    public static String checkSum(String f) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            DigestInputStream ds = new DigestInputStream(
                    new FileInputStream(f), md);
            byte[] b = new byte[512];
            while (ds.read(b) != -1)
                ;

            String computed = "";
            for (byte v : md.digest())
                computed += byteToHex(v);

            return computed;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "<error computing checksum>";
    }

    public static long main2(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        String f1 = args[0];
        String f2 = args[1];
        fanAmt = Integer.parseInt(args[2]);
        memory = Runtime.getRuntime().freeMemory();
        pageSize = memory / 4;
        //pageSize needs to be multiple of 4
        pageSize -= pageSize % 4;
        pageSizeInt = pageSize / 4;
        sort(f1, f2);
        return (System.currentTimeMillis() - start);
    }
}





