package com.libin.ir;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;

public class Indexer {
	
	/**
	 * 
	 * @param dataDir : directory of documents
	 * @param indexDir : directory of index files
	 * @return
	 */
	public static int index(File dataDir, File indexDir) throws IOException{
		if(!dataDir.exists() || !dataDir.canRead()){
			System.err.println(dataDir.getAbsolutePath() +
					" doesn't exit or isn't readable ! ");
			return -1;
		}
		
		IndexWriter writer = new IndexWriter(FSDirectory.open(indexDir),
				new StandardAnalyzer(Version.LUCENE_29), true,
				IndexWriter.MaxFieldLength.LIMITED);
		writer.setMaxFieldLength(25000);

		System.out.println("Indexing to "+indexDir+" ...");
		long  startTime = System.currentTimeMillis();
		indexDocs(dataDir, writer);
		int numIndexed = writer.numDocs();
		System.out.println("optmizing ...");
		writer.optimize();
		writer.close();
		long endTime = System.currentTimeMillis();
		System.out.println("It costs "+(endTime-startTime)+" ms");
		System.out.println("index process is over.");
		return numIndexed;
	}
	
	private static void indexDocs(File dataDir, IndexWriter writer) throws IOException{
		if(dataDir.canRead()){
			if(dataDir.isDirectory()){
				File[] files=dataDir.listFiles();
				if(files!=null){
					for(File f : files){
						indexDocs(f,writer);
					}
				}
			}else{
				System.out.println("adding file "+dataDir);
				
				String temp=null;
				String fileName=dataDir.getName();      
				String suffix=fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
				if(suffix.equals("doc") || suffix.equals("docx")){
					temp=readWord(dataDir);
				}else if(suffix.equals("pdf")){
					temp=readPdf(dataDir);
				}else{
					temp=FileReaderAll(dataDir.getCanonicalPath(),"GBK");
				}
				
				Document doc=new Document();
		        /** 
		         * Field.Index有五个属性，分别是：  
		         * Field.Index.ANALYZED：分词索引  
		         * Field.Index.NOT_ANALYZED：分词进行索引，如作者名，日期等，本身为一单词，不再需要分词。  
		         * Field.Index.NO：不进行索引，存放不能被搜索的内容如文档的一些附加属性如文档类型, URL等。  
		         * Field.Index.NOT_ANALYZED_NO_NORMS：不使用分词索引，不使用存储规则。  
		         * Field.Index.ANALYZED_NO_NORMS：使用分词索引，不使用存储规则。 
		         */  
				doc.add(new Field("path",dataDir.getCanonicalPath(),Field.Store.YES,Field.Index.NOT_ANALYZED));
				doc.add(new Field("contents",temp,Field.Store.YES,Field.Index.ANALYZED,Field.TermVector.WITH_POSITIONS_OFFSETS));
				writer.addDocument(doc);
				//writer.addDocument(FileDocument.Document(dataDir));
			}
		}
	}
	
    private static String FileReaderAll(String FileName, String charset) throws IOException{
    	String line;
    	StringBuffer temp = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(FileName),charset));      
        while  ((line=reader.readLine()) != null )   {      
           temp.append(line);
           temp.append('\n');
        }       
        reader.close();      
        return temp.toString();      
   }  

    private static String readWord(File inputFile) throws IOException{
       FileInputStream fis = new FileInputStream(inputFile);  
       HWPFDocument doc = new HWPFDocument(fis);  
       Range rang = doc.getRange();  
       String text = rang.text();  
       fis.close();  
       return text;  
    }
    
    private static String readPdf(File inputFile) throws IOException{
        FileInputStream fis = new FileInputStream(inputFile);  
        PDFParser p = new PDFParser(fis);  
        p.parse();  
        PDDocument pdd = p.getPDDocument();  
        PDFTextStripper ts = new PDFTextStripper();  
        String text = ts.getText(p.getPDDocument());
        pdd.close();  
        fis.close();  
        return text;  
    }
}
