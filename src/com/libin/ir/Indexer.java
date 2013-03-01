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
import org.apache.poi.hslf.HSLFSlideShow;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.model.TextRun;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.htmlparser.Parser;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.TextExtractingVisitor;

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
				}else if(suffix.equals("xls")){
					temp=readExcel(dataDir);
				}else if(suffix.equals("ppt") || suffix.equals("pptx")){
					temp=readPowerPoint(dataDir);
				}else if(suffix.equals("html")){
					temp=readHtml(dataDir);
				}else if(suffix.equals("xml")){
					temp=readXML(dataDir);
				}else{
					temp=readTXT(dataDir);
				}
				
				System.out.println(temp);
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
    
    private static String readTXT(File inputFile) throws IOException{
    	String line;
    	String charset=checkEncode(inputFile);
    	StringBuffer temp = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile),charset));      
        while  ((line=reader.readLine()) != null )   {      
           temp.append(line);
           temp.append('\n');
        }       
        reader.close();
        return temp.toString();
    }
    
    private static String readXML(File inputFile) throws IOException {
    	SAXReader saxReader = new SAXReader();
    	org.dom4j.Document document;
		try {
			document = saxReader.read(inputFile);
			String content=document.asXML();
			content=content.replaceAll("<[^>]*>", "");
/*	    	ByteArrayOutputStream out = new ByteArrayOutputStream(); 
	        OutputFormat format = new OutputFormat("  ", true, "UTF-8"); 
	        XMLWriter writer = new XMLWriter(out, format); 
	        writer.write(document); 
	        String content = out.toString("UTF-8");*/
	        return content;
		} catch (DocumentException e) {
			e.printStackTrace();
			return null;
		}

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
    
    public static String readExcel(File inputFile) throws IOException {  
        FileInputStream fis = new FileInputStream(inputFile);  
        StringBuilder sb = new StringBuilder();  
        HSSFWorkbook wb=new HSSFWorkbook(fis);   
        
        int sheetNum=wb.getNumberOfSheets();   
        
        for(int i=0;i<sheetNum;i++)   
        {   
            HSSFSheet childSheet = wb.getSheetAt(i);
            int rowNum = childSheet.getPhysicalNumberOfRows(); 
            for(int j=0;j<rowNum;j++)   
            {   
                HSSFRow row = childSheet.getRow(j);    
                int cellNum=row.getPhysicalNumberOfCells();   
                   
                for(int k=0;k<cellNum;k++)   
                {   
                	String content=row.getCell(k).toString()+" ";
                	sb.append(content);
                }   
            }   
               
        }   

        fis.close();  
        return sb.toString();  
    }  

    public static String readPowerPoint(File inputFile) throws IOException { 
    	StringBuffer content = new StringBuffer();
    	FileInputStream fis=new FileInputStream(inputFile);
    	SlideShow ss=new SlideShow(new HSLFSlideShow(fis));
    	Slide[] slides=ss.getSlides();
    	for(int i=0;i<slides.length;i++){
    		TextRun[] tr=slides[i].getTextRuns();
    		for(int j=0;j<tr.length;j++){
    			content.append(tr[j].getText());
    		}
    		//content.append(slides[i].getTitle());
    	}
    	return content.toString();
    }
    
    public static String readHtml(File inputFile){
//    	StringBean sb = new StringBean();
//        String htmlPath = inputFile.getAbsolutePath();
//        //设置不需要得到页面所包含的链接信息
//        sb.setLinks(false);
//        //设置将不间断空格由正规空格所替代
//        sb.setReplaceNonBreakingSpaces(true);
//        //设置将一序列空格由一个单一空格所代替
//        sb.setCollapse(true);
//        //传入要解析的URL
//        sb.setURL(htmlPath);
//        return sb.getStrings();
        
    	String path=inputFile.getAbsolutePath();
    	Parser parser;
		try {
			parser = new Parser(path);
	    	TextExtractingVisitor visitor = new TextExtractingVisitor();
	        parser.visitAllNodesWith(visitor); 
	        return visitor.getExtractedText();
		} catch (ParserException e) {
			e.printStackTrace();
			return null;
		}
    }
    
    private static String checkEncode(File inputFile) throws IOException{
    	
    	FileInputStream fis=new FileInputStream(inputFile);
        byte[] head = new byte[3];  
         fis.read(head);   
         String code = "";  
    
             code = "gb2312";  
         if (head[0] == -1 && head[1] == -2 )  
             code = "UTF-16";  
         if (head[0] == -2 && head[1] == -1 )  
             code = "Unicode";  
         if(head[0]==-17 && head[1]==-69 && head[2] ==-65)  
             code = "UTF-8";  
           
         System.out.println(code); 
         fis.close();
         
         return code;
    }
}
