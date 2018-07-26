package webpages_parser;

import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import page_node.PageNode;
import utils.StringProcessingUtils;

/**
 *
 * @author linski
 */
public class MatchWorker implements Runnable {

private String pageid;
private String url;
private String catid;
private String fatherid;
private String tokenizedPage;
private CountDownLatch countDownLatch;
private Map<String, PageNode> pageContent;
private HashSet<String> pagePrev;
String all_paragraphs = "";

public MatchWorker(String pageid, String url, String catid, String fatherid, String tokenizedPage, 
		CountDownLatch latch, Map<String, PageNode> pageContent, HashSet<String> pagePrev) {
    this.pageid = pageid;
    this.url = url;
    this.catid = catid;
    this.fatherid = fatherid;
    this.tokenizedPage = tokenizedPage;
    this.countDownLatch = latch;
    this.pageContent = pageContent;
    this.pagePrev = pagePrev;
}

	@Override
	public void run() {
        //Match allTextFromPage = null;
        List<String> tokenizedPageContentList = new ArrayList<String>();
        //match = new Match(App.connect(element).title());
        Elements divs = null;
        try {
        	divs = App.connect(url).select("div, p, b");
        	//System.out.println("Retrieved page " + pageid);
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
		//if(!pagePrev.contains(pageid)){
			try {
				//Dmoz_Data.savePageContentToDB(pageid, tokenizedPageContentList);
				//String tokenizedPageContent = Arrays.toString(tokenizedPageContentList.toArray());
				PageNode newPage = new PageNode();
				newPage.set_id(pageid);
				//System.out.println("pageid" + newPage.get_id());
				newPage.setCatid(catid);
				//System.out.println("catid" + newPage.getCatid());
	
				newPage.setFatherid(fatherid);
				//System.out.println("fatherid" + newPage.getFatherid());
	
	    		//System.out.println("before db: " + tokenizedPage);
				newPage.setTokenizedPageAsString(tokenizedPage);
				//System.out.println("tokenizedPage" + newPage.getTokenizedPageAsString());
	
				newPage.setTokenizedPageText(tokenizedPageContentList);
				//System.out.println("tokenizedPageText" + newPage.getTokenizedPageText());
	
				//pageContent.put(pageid, newPage);
				Dmoz_Data.saveByPageToDB(pageid, newPage);
				
			} catch (Exception e) {
				System.out.println("Failed to save url content to db");
				e.printStackTrace();
			}
		
		//}
		//else{
			//System.out.println("\tAlready parsed " + pageid);
		//}
		countDownLatch.countDown();
		
		System.out.println(countDownLatch.getCount() + " Tasks left to finish");
	}
}