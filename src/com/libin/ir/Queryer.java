package com.libin.ir;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
	
public class Queryer {
	
	private int hitsPerPage=5;
	private int pageNo=1;
	private int pageTotal=5;
	

	public int getPageTotal() {
		return pageTotal;
	}


	public void setPageTotal(int pageTotal) {
		this.pageTotal = pageTotal;
	}


	public int getHitsPerPage() {
		return hitsPerPage;
	}


	public void setHitsPerPage(int hitsPerPage) {
		this.hitsPerPage = hitsPerPage;
	}


	public int getPageNo() {
		return pageNo;
	}


	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public Queryer(){
		
	}
	

	/**
	 * 
	 * @param indexDir : directory of index
	 * @param str : word to be found
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws CorruptIndexException 
	 */
	
	public String search(File indexDir, String str ) throws Exception{
		String result="";
		IndexSearcher searcher = new IndexSearcher(
				FSDirectory.open(indexDir), true);// read-only   
		String field = "contents";
		
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_29);
		QueryParser parser = new QueryParser(Version.LUCENE_29,field,analyzer);
		Query query = parser.parse(str); 
		
	    TopScoreDocCollector collector = TopScoreDocCollector.create(
	            5*hitsPerPage, false);
	    searcher.search(query,collector);
	    
	    ScoreDoc[] hits = collector.topDocs( (pageNo-1)*hitsPerPage, hitsPerPage ).scoreDocs;
	    System.out.println("finding "+hits.length+" results.");
	    if(hits.length<hitsPerPage){
	    	pageTotal=pageNo;
	    }
	    
	      // 高亮显示设置 
	      Highlighter highlighter = null; 
	      SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter( 
	          "<span class=\"red\">", "</span>"); 
	      highlighter = new Highlighter(simpleHTMLFormatter, new QueryScorer(query)); 
	      // 这个500是指定关键字字符串的context的长度，你可以自己设定，因为不可能返回整篇正文内容 
	      highlighter.setTextFragmenter(new SimpleFragmenter(500)); 
	      
	    for (int i = 0; i < hits.length; i++) {  
	    	System.out.println("................................");
            Document doc = searcher.doc(hits[i].doc);
            System.out.println(doc.get("path"));
	    	result+="This is the " + ((pageNo-1)*hitsPerPage+i+1) +"th result : <br/>";
	    	result+="path: "+doc.get("path")+"<br/>";
            String value=doc.get(field);
            if(value!=null){
            	TokenStream tokenStream = analyzer.tokenStream(field,new StringReader(value));
                String fragment = highlighter.getBestFragment(tokenStream, value);
                result+=fragment+"<br/><br/><br/>";
                System.out.println(fragment); 
            }
	    }
	    return result=="" ? "No result" : result;
	}
	
}
