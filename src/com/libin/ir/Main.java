package com.libin.ir;

import java.io.File;
import java.io.IOException;

public class Main {

	public static void main(String[] args){
		File dataDir=new File("E:/BufferFolder/lucene/docs/");
		File indexDir=new File("E:/BufferFolder/lucene/index");
		try {
			Indexer.index(dataDir, indexDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//String query="ÖÐÎÄ";
		try {
			//Queryer.search(indexDir, query);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
