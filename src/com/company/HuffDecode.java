package com.company;

import java.io.*;
import java.util.*;

public class HuffDecode {
    void decode() throws IOException {
        File file = new File("decom.txt");
        int length = (int) file.length();
        DataInputStream innFil = new DataInputStream(new BufferedInputStream(new FileInputStream("decom.txt")));
        DataOutputStream utFil = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("out.txt")));
        byte[] rawData = new byte[length];
        innFil.readFully(rawData);
        byte[] tempFreq = Arrays.copyOfRange(rawData,0,256*4-1);
        byte[] data = Arrays.copyOfRange(rawData,256*4,length);
        int[] freq = new int[256];
        for (int i=0;i<255;i++){
            freq[i]=((tempFreq[i*4] & 0xFF) << 24) |
                    ((tempFreq[i*4+1] & 0xFF) << 16) |
                    ((tempFreq[i*4+2] & 0xFF) << 8) |
                    (tempFreq[i * 4 + 3] & 0xFF);
        }
        Huffman.HuffTree tree = new Huffman.HuffTree();
        tree.generateTree(freq);
        String byteString = "";
        /**
         * creates a string from the bytes stored in data, the lst byte signifies the actual length of the second to
         * last byte.
         */
        for (int i=0;i<data.length-2;i++){
            String tempString = Integer.toBinaryString((data[i]+256)%256);
            while (tempString.length()<8){
                tempString="0"+tempString;
            }
            byteString+=tempString;
        }
        int tempint = data[data.length-1];
        String tempString = Integer.toBinaryString((data[data.length-2]+256)%256);
        while (tempString.length()<tempint){
            tempString="0"+tempString;
        }
        byteString+=tempString;
        int currentChar = Character.getNumericValue(byteString.charAt(0));
        ArrayList<Byte> retArr = new ArrayList<>();
        /**
         * this part generates the original code by traversing the tree
         */
        for(int i=0;i<byteString.length();i+=0){
            Huffman.HuffTree.HuffNode pointer = tree.head;
            while((pointer.left !=null && pointer.right != null) && i<byteString.length()){
                if (currentChar == 1){ pointer = pointer.left;}
                else{ pointer = pointer.right;}
                i++;
                if (i<byteString.length())currentChar= Character.getNumericValue(byteString.charAt(i));
            }
            retArr.add(pointer.value);
            if(i>byteString.length()-2){break;}
        }
        byte[] fin = new byte[retArr.size()];
        for (int i=0;i<retArr.size();i++){
            fin[i]=retArr.get(i);
        }
        utFil.write(fin);
        utFil.close();
    }
}
