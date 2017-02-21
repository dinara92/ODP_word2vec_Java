package tests;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

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
		String pageid = "1742389";
		List<String> lst = new ArrayList<String>();
		lst.add("1");
		lst.add("23");
		try {
			Dmoz_Data.savePageContentToDB(pageid, lst);
		} catch (FileNotFoundException | InterruptedException | ExecutionException e) {
			System.out.println("couldnt execute save");
			e.printStackTrace();
		}
	}
}
