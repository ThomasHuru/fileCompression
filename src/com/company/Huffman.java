package com.company;

import java.io.*;
import java.util.*;

/**
 * class for huffman compression
 */
public class Huffman {
    /**
     * frequencynode is used to store the bytes with their frequency in a priorityqueue
     */
    static class FrequencyNode implements Comparable<FrequencyNode>{
        int value;
        int count;

        FrequencyNode(byte value, int count){
            this.value = value;
            this.count = count;
        }

        @Override
        public int compareTo(FrequencyNode o) {
            return this.count-o.count;
        }
    }

    /**
     * should have it's own unique class file, and is also used by HuffDecode
     */
    static class HuffTree {
        HuffNode head;

        HuffTree() {
        }

        static class HuffNode implements Comparable<HuffNode> {
            int weight;
            HuffNode left = null;
            HuffNode right = null;
            byte value;

            HuffNode(int weight, byte value) {
                this.weight = weight;
                this.value = value;
            }

            @Override
            public int compareTo(HuffNode o) {
                if (this.weight == o.weight) {
                    return this.value - o.value;
                }
                else return this.weight-o.weight;
            }
            public String toString(){
                return this.weight+"";
            }
        }

        /**
         * this generates the tree based on a frequencyarray
         * @param src
         */
        void generateTree(int[] src){
            PriorityQueue<HuffNode> pq= new PriorityQueue<>();
            for(int i=0;i<src.length;i++){
                pq.add(new HuffNode(src[i], (byte) i));
            }
            while(pq.size()>1){
                HuffNode node1 = pq.poll();
                HuffNode node2 = pq.poll();
                HuffNode tempNode = new HuffNode(node1.weight + node2.weight, (byte) 0xFF);
                tempNode.left=node1;
                tempNode.right=node2;
                pq.add(tempNode);
            }
            this.head=pq.poll();
        }
    }

    void HCompress() throws IOException {
        File file = new File("C:/Users/Thoma/IdeaProjects/compressor/src/com/company/diverse.txt");
        int length = (int) file.length();
        DataInputStream innFil = new DataInputStream(new BufferedInputStream(new FileInputStream("C:/Users/Thoma/IdeaProjects/compressor/src/com/company/diverse.txt")));
        DataOutputStream utFil = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("huff.txt")));
        byte[] data = new byte[length];
        innFil.readFully(data);
        int[] freqArray = new int[256];
        for (int i=0;i<freqArray.length;i++){
            freqArray[i]=0;
        }
        for (int i=0;i<data.length;i++){
            freqArray[Byte.toUnsignedInt(data[i])]++;
        }
        PriorityQueue<FrequencyNode> queue= new PriorityQueue<>();
        for (int i=0;i<freqArray.length;i++){
            queue.add(new FrequencyNode((byte)i,freqArray[i]));
        }
        HuffTree tree = new HuffTree();
        tree.generateTree(freqArray);
        /**
         * generates the Huffman encoded version of data
         */
        byte[] huffData = transStart(tree,data);
        /**
         * for storing frequency array
         */
        byte[] byteFreqArray = new byte[256*4];
        for (int i=0;i<256;i++){
            int tempInt = freqArray[i];
            byteFreqArray[i*4]=(byte)(tempInt >> 24);
            byteFreqArray[i*4+1]=(byte)(tempInt >> 16);
            byteFreqArray[i*4+2]=(byte)(tempInt >> 8);
            byteFreqArray[i*4+3]=(byte)(tempInt);
        }
        byte[] sum = new byte[huffData.length+byteFreqArray.length];
        for (int i=0;i<byteFreqArray.length;i++){
            sum[i]=byteFreqArray[i];
        }
        for (int i=byteFreqArray.length;i<sum.length;i++){
            sum[i]=huffData[i-byteFreqArray.length];
        }
        utFil.write(sum);
        utFil.close();

    }

    /**
     * traverses and finds the Huffmancode for each byte
     * @param node
     * @param byteString
     * @param map
     */
    private void byteTrans(HuffTree.HuffNode node, String byteString, Map<Byte, String> map){
        if (node.left == null && node.right == null){
            map.put(node.value,byteString);
        }
        if(node.left!=null) byteTrans(node.left,byteString+"1",map);
        if (node.right!=null) byteTrans(node.right,byteString+"0",map);
    }

    /**
     * this function returnes an encoded version of src
     * @param tree
     * @param src
     * @return
     */
    private byte[] transStart(HuffTree tree, byte[] src){
        Map<Byte, String> map= new LinkedHashMap<>();
        byteTrans(tree.head,"",map);
        String retString = "";
        for (int i=0;i<src.length;i++){
            retString+=map.get(src[i]);
        }
        String[] retSplit = retString.split("(?<=\\G.{8})");
        byte[] retArr = new byte[retSplit.length+1];
        for (int i=0;i<retSplit.length;i++){
            retArr[i]=(byte)Short.parseShort(retSplit[i],2);
        }
        retArr[retSplit.length] = (byte)(retSplit[retSplit.length-1].length());
        return retArr;
    }
}
