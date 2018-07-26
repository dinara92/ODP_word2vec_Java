package webpages_parser;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mysql.jdbc.Statement;

import hashmap.ConnectDb;
import hashmap.DBtoHashmap;
import node_info.NodeInfo;
import page_node.PageNode;
import utils.StringProcessingUtils;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Hello world!
 *
 */
public class App {
    
    public static Document connect(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).timeout(0).get();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return doc;
    }
    
    
    public static void passUrl_() throws SQLException, FileNotFoundException, InterruptedException, ExecutionException{
    	
    	Map<String, NodeInfo> taxonomyAll = Dmoz_Data.makeTaxonomyAll();
    	Map<String, NodeInfo> taxonomyWithPageText = new HashMap<String, NodeInfo>();
    	//List<String> pageText;
    	List<PageNode> pagesTexts;
    	NodeInfo newNode = null;
    	
    	for(String catid: taxonomyAll.keySet()){
    		//String catid = "389230";
    		//String catid = "1012357";
    		//String catid = "427390";
    		//String catid = "995702";
    		//String catid = "995706";
    		//pagesTexts.clear();
    		pagesTexts = new ArrayList<PageNode>();
    		
    		newNode = new NodeInfo();
    		
    		
    		for(PageNode page: taxonomyAll.get(catid).getPages()){
    			//pageText.clear();
    			//pageText = new ArrayList<String>();

    			PageNode newPage = new PageNode();
    			String url_link = page.getPage_link();
    			
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
			//System.out.println("starting to parse text from url ");
			//long millis = System.currentTimeMillis();

    		//handleThreads(newNode.getPages(), newNode);
			//System.out.println("took " + (System.currentTimeMillis() - millis) +  " ms");

    		taxonomyWithPageText.put(catid, newNode);
    		 

    }
		System.out.println("Taxonomy made, now saving to db");
    	//Dmoz_Data.AllODPSetSaveToFile(taxonomyWithPageText);

    }
    
    public static Map<String, PageNode> getLinks() throws SQLException {
    	
    	Map<String, PageNode> pagesMap = new HashMap<String, PageNode>();
		PageNode page = null;
		
		ConnectDb.initPropertiesForSave();
		final boolean dbConnected = ConnectDb.checkConnection();
		if (!dbConnected) {
			System.exit(1);
		}

		System.out.println("Successfully connected to " + ConnectDb.getEngine().toUpperCase() + " database "
				+ ConnectDb.getDB() + "@" + ConnectDb.getHost() + " as user " + ConnectDb.getUsername());

		String query_pages = "SELECT * FROM dmoz_pages_no_world_pagelinks" + ";";

		Statement stmt = null;

		try {
			stmt = (Statement) ConnectDb.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ResultSet rs_pages = stmt.executeQuery(query_pages);

		ResultSetMetaData md = rs_pages.getMetaData();
		int columns = md.getColumnCount();
		
		while (rs_pages.next()) {
			Map<String, Object> row = new HashMap<String, Object>(columns);
			for (int i = 1; i <= columns; ++i) {
				row.put(md.getColumnName(i), rs_pages.getObject(i));
			}
			
			PageNode pageThis = new PageNode();
			pageThis.set_id(row.get("pageid").toString());
			pageThis.setCatid(row.get("catid").toString());
			pageThis.setFatherid(row.get("fatherid").toString());
			pageThis.setTokenizedPageAsString(row.get("pages").toString());
			pageThis.setPage_link(row.get("link").toString());
			pagesMap.put(row.get("pageid").toString(), pageThis);
			
		}

		return pagesMap;
	}
	

public static HashSet<String> getPreviouslyNotAddedPageIds() throws SQLException {
    	
    	HashSet<String> pagesIdSet = new HashSet<String>();
		PageNode page = null;
		
		ConnectDb.initPropertiesForSave();
		final boolean dbConnected = ConnectDb.checkConnection();
		if (!dbConnected) {
			System.exit(1);
		}

		System.out.println("Successfully connected to " + ConnectDb.getEngine().toUpperCase() + " database "
				+ ConnectDb.getDB() + "@" + ConnectDb.getHost() + " as user " + ConnectDb.getUsername());

		String query_pages = "SELECT * FROM dmoz_pages_no_world_pagelinks "
				+ "WHERE dmoz_pages_no_world_pagelinks.pageid NOT IN "
				+ "(SELECT dmoz_pages_no_world_pagecontent_parsed.pageid "
				+ "FROM dmoz_pages_no_world_pagecontent_parsed)" + ";";

		Statement stmt = null;

		try {
			stmt = (Statement) ConnectDb.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ResultSet rs_pages = stmt.executeQuery(query_pages);

		ResultSetMetaData md = rs_pages.getMetaData();
		int columns = md.getColumnCount();
		
		while (rs_pages.next()) {
			Map<String, Object> row = new HashMap<String, Object>(columns);

			for (int i = 1; i <= columns; ++i) {
				row.put(md.getColumnName(i), rs_pages.getObject(i));
				//System.out.println("This is md: " + md.getColumnName(i) +  rs_pages.getObject(i));

			}
			
			pagesIdSet.add(row.get("pageid").toString());
			
		}

		return pagesIdSet;
	}

    public static void runThreads(Map<String, PageNode> pagesMap, Map<String, PageNode> pageContent, HashSet<String> pagePrev) {
    	
    	/* For TEST
    	List<String> pageIdList = new ArrayList<String>();
    	pageIdList.add("1742390");
    	pageIdList.add("501598");
    	pageIdList.add("501612");
    	pageIdList.add("500834");
    	pageIdList.add("500856");
    	int numOfTasks = pageIdList.size();
    	*/
    	
    	
    	//int numOfTasks = pagesMap.size();
    	int numOfTasks = pagePrev.size(); //number of left tasks, after 1st db was already filled

    	CountDownLatch countDownLatch = new CountDownLatch(numOfTasks);
    	System.out.println(numOfTasks + " Tasks to run, starting..");
    	long millis = System.currentTimeMillis();

        ExecutorService executorService = Executors.newFixedThreadPool(250);
    	//List<Future<Match>> handles = new ArrayList<Future<Match>>();
    	//String[] elements = new String[12];
    	//Arrays.fill(elements, "http://movieweb.com/movie/iron-man/");
    	String pageUrl = "";
    	String catid = "";
    	String fatherid = "";
    	String tokenizedPage = "";
    	
    	//for(String pageId: pageIdList) {   //for TEST
    	for(String pageId: pagePrev) {
    		
    		pageUrl = pagesMap.get(pageId).getPage_link();
    		catid = pagesMap.get(pageId).getCatid();
    		fatherid = pagesMap.get(pageId).getFatherid();
    		tokenizedPage = pagesMap.get(pageId).getTokenizedPageAsString();
    		//System.out.println("This is element "+ page_id + " : " + element);
    		//if(!pagePrev.contains(pageId)){ //with this, too slow
    		Runnable matchWorker = new MatchWorker(pageId, pageUrl, catid, fatherid, tokenizedPage, countDownLatch, pageContent, pagePrev);
    		executorService.execute(matchWorker);
    		//}

    	}
    	
    	executorService.shutdown();
    	
    	finishWork(countDownLatch);
    	System.out.println("Running parallel took " + (System.currentTimeMillis() - millis) +  " ms");
    }
    
    
    
    public static void main( String[] args ) {
    	try {
        	Map<String, PageNode> pages = getLinks();
        	HashSet<String> pagesPrev = getPreviouslyNotAddedPageIds();
        	System.out.println("Map size " + pages.size());
        	System.out.println("Hashset size " + pagesPrev.size());
        	System.out.println(pagesPrev.size() + " Tasks to run, starting.."); //number of left tasks, after 1st db was already filled
        	
        	//Map<String, PageNode> pageContent = new ConcurrentHashMap<String, PageNode>();
        	//runThreads(pages, pageContent, pagesPrev);
        	
    		/*try {
    			System.out.println("Now writing to db...");
    			Dmoz_Data.odpByPagesSaveToDB(pageContent);
    		} catch (FileNotFoundException e) {
    			e.printStackTrace();
    		}*/

    	} catch (Exception e) {
    		System.out.println("Error in main");
    		e.printStackTrace();
    	}
    }
    
    public static void finishWork(CountDownLatch countDownLatch) {
        try {

            System.out.println("START WAITING");
            countDownLatch.await();
            System.out.println("DONE WAITING");
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}