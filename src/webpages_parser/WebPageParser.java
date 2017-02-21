package webpages_parser;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import node_info.NodeInfo;
import page_node.PageNode;
import utils.StringProcessingUtils;
public class WebPageParser {

	

	
public static String getPageText(String url){
	
    String all_paragraphs = "";
    String all_paragraphs_old = "";


	 try {
		  long millis = System.currentTimeMillis();
	      Document doc = Jsoup.connect(url)
	    		   .timeout(0)
	               .get();
	      System.out.println("getting page took " + (System.currentTimeMillis() - millis) + " ms");
	      //Elements paragraphs = doc.select("div.content > p");
	      Elements divs = doc.select("div, p, b"); 
	      for (Element div : divs) {
	          //System.out.println(div.ownText());
	    	  String new_div = div.ownText() + " ";
	          all_paragraphs+=new_div;
	      }
		//for(Element p : paragraphs)
	    		//System.out.println(p.text());
				//all_paragraphs+=p.text();
      
	    }
	    catch (IOException ex) {
	      Logger.getLogger(WebPageParser.class.getName())
	            .log(Level.SEVERE, null, ex);
	    }
	return all_paragraphs;
	
	  }
	

public static Map<String, NodeInfo> passUrl() throws SQLException{
	
	Map<String, NodeInfo> taxonomyAll = Dmoz_Data.makeTaxonomyAll();
	Map<String, NodeInfo> taxonomyWithPageText = new HashMap<String, NodeInfo>();
	List<String> pageText;
	List<PageNode> pagesTexts;

	for(String catid: taxonomyAll.keySet()){
		//String catid = "389230";
		//String catid = "1012357";
		//String catid = "427390";

		//pagesTexts.clear();
		pagesTexts = new ArrayList<PageNode>();
		
		NodeInfo newNode = new NodeInfo();
		for(PageNode page: taxonomyAll.get(catid).getPages()){
			//pageText.clear();
			pageText = new ArrayList<String>();

			PageNode newPage = new PageNode();
			String url_link = page.getPage_link();
			//String url_link = "http://movieweb.com/movie/iron-man/";

			//pageText = StringProcessingUtils.removeStemmedStopWords(getPageText(url_link));
			//System.out.println("This page's " + page.get_id() +  " all paragraphs: " + pageText);
			//System.out.println("\tThis page's " + page.get_id() +  " page title + description: " + page.getTokenizedPage());
			

			newPage.set_id(page.get_id());
			newPage.setCatid(catid);
			//newPage.setTokenizedPageText(pageText);
			newPage.setPage_link(url_link);
			newPage.setTokenizedPage(page.getTokenizedPage());
			newPage.setFatherid(page.getFatherid());
			pagesTexts.add(newPage);
				
		}
		newNode.setCatid(catid);
		newNode.setFatherid(taxonomyAll.get(catid).getFatherid());
		newNode.setPages(pagesTexts);
		taxonomyWithPageText.put(catid, newNode);
}
	return taxonomyWithPageText;
}

public static NodeInfo passUrl_() throws SQLException, FileNotFoundException, InterruptedException, ExecutionException{
	
	Map<String, NodeInfo> taxonomyAll = Dmoz_Data.makeTaxonomyAll();
	//Map<String, NodeInfo> taxonomyWithPageText = new HashMap<String, NodeInfo>();
	List<String> pageText;
	List<PageNode> pagesTexts;
	NodeInfo newNode = null;
	
	for(String catid: taxonomyAll.keySet()){
		//String catid = "389230";
		//String catid = "1012357";
		//String catid = "427390";

		//pagesTexts.clear();
		pagesTexts = new ArrayList<PageNode>();
		
		newNode = new NodeInfo();

		for(PageNode page: taxonomyAll.get(catid).getPages()){
			//pageText.clear();
			pageText = new ArrayList<String>();

			PageNode newPage = new PageNode();
			String url_link = page.getPage_link();
			//String url_link = "http://movieweb.com/movie/iron-man/";

			//System.out.println("starting to parse text from url " + url_link);
			//long millis = System.currentTimeMillis();
			//pageText = StringProcessingUtils.removeStemmedStopWords(getPageText(url_link));
			
			
			//System.out.println("took " + (millis - System.currentTimeMillis()) +  " ms");
			//System.out.println("This page's " + page.get_id() +  " all paragraphs: " + pageText);
			//System.out.println("\tThis page's " + page.get_id() +  " page title + description: " + page.getTokenizedPage());
			

			newPage.set_id(page.get_id());
			newPage.setCatid(catid);
			//newPage.setTokenizedPageText(pageText);
			newPage.setPage_link(url_link);
			newPage.setTokenizedPage(page.getTokenizedPage());
			newPage.setFatherid(page.getFatherid());
			pagesTexts.add(newPage);
				
		}
		newNode.setCatid(catid);
		newNode.setFatherid(taxonomyAll.get(catid).getFatherid());
		newNode.setPages(pagesTexts);
		//taxonomyWithPageText.put(catid, newNode);
		
		Dmoz_Data.AllODPSetSaveToFile_byNode(newNode);

}
	return newNode;
}


public static void main(String[] args) throws SQLException, FileNotFoundException, InterruptedException, ExecutionException  {
	
	//String url = "http://www.cinemablend.com/previews/Iron-Man-1877.html";
	//getPageText(url);
	//Map<String, NodeInfo> taxonomyWithPageText = passUrl();
	//Dmoz_Data.AllODPSetSaveToFile(taxonomyWithPageText);
	
	//NodeInfo taxonomyWithPageText = passUrl_();
	//Dmoz_Data.AllODPSetSaveToFile_old(taxonomyWithPageText);
	//Map<String, NodeInfo> taxonomyWithPageText = passUrl();
	//Dmoz_Data.AllODPSetSaveToFile(taxonomyWithPageText);
	//in the last version, I used passUrl_() to call write pagecontent db (not fully filled now - only 160k entries)
	passUrl_();
}
}