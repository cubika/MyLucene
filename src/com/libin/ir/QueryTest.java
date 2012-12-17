package com.libin.ir;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class QueryTest {

	private Queryer queryer=new Queryer();

	@Test
	public void testSearch() {
		File indexFile=new File("E:/BufferFolder/lucene/index");
		
		try {
			assertNotNull(queryer.search(indexFile, "°×ÔÆ"));
			assertEquals(queryer.getPageNo(), 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
