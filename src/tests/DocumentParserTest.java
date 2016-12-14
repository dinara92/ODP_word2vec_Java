package tests;

import java.util.List;

import org.junit.Test;

import tfidfDocument.DocumentParser;
import utils.StringProcessingUtils;

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

}
