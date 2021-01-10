package com.company;

import java.io.*;
import java.util.ArrayList;

/**
 * lempel ziv ccompressor class
 */
public class LZ77 {
    private byte[] data;

    public LZ77() {
    }

    public void compress(String filepath, short slidingWindow) throws IOException {
        File file = new File(filepath);
        data = new byte[(int)file.length()];
        ArrayList<Byte> tempList= new ArrayList<>();
        DataInputStream innfil = new DataInputStream(new BufferedInputStream(new FileInputStream(filepath)));
        DataOutputStream outFil = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("huffout.txt")));
        innfil.readFully(data);
        tempList.add((byte)0);
        tempList.add((byte)0);
        short interval = 0;
        for (int i=0;i<data.length;i++){
            short[] retVal= longestLength(i,slidingWindow);
            if (retVal[0]>6){
                addShortAsByte(retVal[0],tempList);
                addShortAsByte(retVal[1],tempList);
                tempList.add((byte)0);
                tempList.add((byte)0);
                interval+=0;
                int tempint = tempList.size();
                addShortAsByte(tempint-interval-8, interval,tempList);
                i+=retVal[0]-1;
                interval = 0;
            }
            else{
                tempList.add(data[i]);
                interval++;
            }

        }
        addShortAsByte(tempList.size()-interval-2, interval,tempList);
        byte[] outList = new byte[tempList.size()];
        for (int i=0;i<tempList.size();i++){
            outList[i]=tempList.get(i);
        }
        outFil.write(outList,0,tempList.size());
        outFil.close();

    }

    /**
     * finds longest match for a given char in src
     * @param index
     * @param slidingWindow
     * @return
     */
    short[] longestLength(int index, short slidingWindow){
        short longest = 0;
        short currentLength = 0;
        int indexLongestMatch = 0;
        short tempSlidingWindow = slidingWindow;
        if (slidingWindow > index){
            tempSlidingWindow = (short)index;
        }
        for (int i=index;i<index+tempSlidingWindow-1;i++){
            if (data[index+currentLength] == data[i-tempSlidingWindow]){
                currentLength++;
            }
            else{
                if (currentLength > longest){
                    longest = currentLength;
                    indexLongestMatch =(i-1-tempSlidingWindow);
                }
                currentLength = 0;
            }
            if (index+currentLength > data.length-2){
                if (currentLength > longest) {
                    longest = currentLength;
                    indexLongestMatch = i-1-tempSlidingWindow;
                }
                break;
            }
        }
        short[] tempInt = new short[2];
        tempInt[0] = longest;
        tempInt[1] = (short) ( index-(indexLongestMatch));
        return tempInt;
    }
    public static void addShortAsByte(int index, short num,ArrayList<Byte> tempList){
        tempList.set(index+1,(byte)(num & 0xff));
        tempList.set(index,(byte)((num >> 8) & 0xff));
    }
    public static void addShortAsByte(short num,ArrayList<Byte> tempList){
        tempList.add((byte)((num >> 8) & 0xff));
        tempList.add((byte)(num & 0xff));
    }
}
