package com.company;

import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {
		/**
		 * this runs the program, it creates some dump files on the way, sorry... but it will compress into one file
		 * huffout.txt, and then decompress into out. Please remember that i wrote this code on a fever and i'm feeling very done
		 * now that it works as is.
		 * the file that is compressed is diverse.txt, but the path refers to the path on my computer, so just change that in the
		 * Huffman class if you wish to test.
		 *
		 * The decompression classes uses no data from the compression classes of course.
		 */
	Huffman huffman = new Huffman();
	huffman.HCompress();
	LZ77 lz77 = new LZ77();
	lz77.compress("huff.txt",(short)10000);
	Decompressor decompressor = new Decompressor();
	HuffDecode huffDecode = new HuffDecode();
	decompressor.decomLZ();
	huffDecode.decode();
}}
