package com.company;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

/**
 * decompressor class for lempel ziv
 */
public class Decompressor {

    byte[] data;
    public void decomLZ() throws IOException {
        File file = new File("huffout.txt");
        int length =(int) file.length();
        DataInputStream innFil = new DataInputStream(new BufferedInputStream(new FileInputStream("huffout.txt")));
        data = new byte[length];
        innFil.readFully(data);
        ArrayList<Byte> outData = new ArrayList<>();
        short invInterval = 0;
        invInterval = (short)(byteToShort(data,0)-1);
        boolean flagVisited = false;
        boolean noIntervall = false;
        /**
         * The decompressor loop, lots of bugtesting here so lots of numbers and some strange booleans, but it reads the data for the flagportions correctly
         * and finds the next flag when interval = 0
         */
        for (int i=2;i<data.length-2;i++){
            if(invInterval==0){
                noIntervall = true;
            }
            while(!(invInterval<1)){
                invInterval--;
                if(!flagVisited) {
                    outData.add(data[i]);
                }
                else flagVisited = false;
                if (i<data.length){
                    i++;
                }
                else{break;};
            }
            if (!noIntervall) {
                outData.add(data[i]);
            }
            if(i+5>data.length-1){
                break;
            }
            noIntervall = false;
            invInterval = (short)(byteToShort(data,i+5)+1);
            invInterval+=-1;
            short stretch = (short)(byteToShort(data,i+1));
            short jump = (short)(byteToShort(data,i+3)-2);
            int tempSize = outData.size();
            for (int j=0;j<stretch;j++){
                outData.add(outData.get(tempSize-1-jump-stretch+j));
            }
            if(i+5<data.length) i+=5;
            flagVisited = true;
        }
        DataOutputStream outFil = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("decom.txt")));
        byte[] outList = new byte[outData.size()];
        for (int i=0;i<outData.size();i++){
            outList[i]=outData.get(i);
        }
        outFil.write(outList,0,outList.length);
        outFil.close();
    }
    public static short byteToShort(byte[] data,int index){
        return (short)(((data[index] & 0xFF) << 8) | (data[index+1] & 0xFF));
    }
}
