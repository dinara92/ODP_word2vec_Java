package tests;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

import page_node.PageNode;
import tfidfDocument.DocumentParser;
import utils.StringProcessingUtils;
import webpages_parser.Dmoz_Data;

public class DocumentParserTest {
	
	@Test
	public void testRemoveStopWords() {
		DocumentParser dp = new DocumentParser();
		String doc = "999 Society  Show dates and pictures for the band based out of Denver,?@$!#&~~ Colorado.";
		List<String> docTerms = StringProcessingUtils.removeStopWords(doc);
		//List<String> docTerms2 = StringProcessingUtils.removeStemmedStopWords(docTerms);
		System.out.println("docTerms : " + docTerms);
		//System.out.println("docTerms : " + docTerms2);

		//assertEquals(docTerms.size(), 8);
	}

	@Test
	public void testSavePageContentToDb() {
		String pageid = "1742390";
    	Map<String, PageNode> pageContent = new ConcurrentHashMap<String, PageNode>();

		List<String> lst = new ArrayList<String>();
		lst.add("1");
		lst.add("23");
		PageNode newPage = new PageNode();
		newPage.set_id("1336262");
		newPage.setCatid("670926");
		newPage.setFatherid("670924");
		//System.out.println("before db: " + tokenizedPage);
		newPage.setTokenizedPageAsString("[david, davis, real, estate, offers, residential, land, farms, commercial, properties, featured, listings, mls, search]");
		newPage.setTokenizedPageText(lst);
		pageContent.put(pageid, newPage);
		
		try {
			Dmoz_Data.odpByPagesSaveToDB(pageContent);
		} catch (FileNotFoundException e) {
			System.out.println("couldnt execute save");
			e.printStackTrace();
		}
	}
}
