package webpages_parser;

import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import utils.StringProcessingUtils;

/**
 *
 * @author linski
 */
public class MatchWorker implements Runnable {

private String pageid;
private String url;
private CountDownLatch countDownLatch;
String all_paragraphs = "";

public MatchWorker(String pageid, String url, CountDownLatch latch) {
    this.pageid = pageid;
    this.url = url;
    this.countDownLatch = latch;
}

	@Override
	public void run() {
        //Match allTextFromPage = null;
        List<String> tokenizedPageContentList = new ArrayList<String>();
        //match = new Match(App.connect(element).title());
        Elements divs = null;
        try {
        	divs = App.connect(url).select("div, p, b");
        	System.out.println("Retrieved page " + pageid);
        } catch (Exception e) {
        	System.out.println("Failed to retrieve page on url: " + url);
			e.printStackTrace();
        }
        if (divs == null) {
        	countDownLatch.countDown();
        	return;
        }
	    for (Element div : divs) {
	        //System.out.println(div.ownText());
	    	String new_div = div.ownText() + " ";
	        all_paragraphs += new_div;
	        //System.out.println("This is text from page: " + all_paragraphs);
	    }
	     //allTextFromPage = new Match(all_paragraphs);
	     //System.out.println("This is text from page: " + allTextFromPage);
		assertNotNull(all_paragraphs);
		//System.out.println("all paragraph " + all_paragraphs);
		tokenizedPageContentList = StringProcessingUtils.removeStemmedStopWords(all_paragraphs);
		//System.out.println("tokenized string " + tokenizedPageContentList);
		try {
			Dmoz_Data.savePageContentToDB(pageid, tokenizedPageContentList);
		} catch (Exception e) {
			System.out.println("Failed to save url content to db");
			e.printStackTrace();
		}
		countDownLatch.countDown();
		System.out.println(countDownLatch.getCount() + " Tasks left to finish");
	}
}