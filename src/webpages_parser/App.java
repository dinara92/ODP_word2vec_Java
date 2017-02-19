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
public class App 
{
    
    public static Document connect(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).timeout(0).get();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return doc;
    }
    
    
    public static NodeInfo passUrl_() throws SQLException, FileNotFoundException, InterruptedException, ExecutionException{
    	
    	Map<String, NodeInfo> taxonomyAll = Dmoz_Data.makeTaxonomyAll();
    	//Map<String, NodeInfo> taxonomyWithPageText = new HashMap<String, NodeInfo>();
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
			System.out.println("starting to parse text from url ");
			long millis = System.currentTimeMillis();

    		//handleThreads(newNode.getPages(), newNode);
			System.out.println("took " + (System.currentTimeMillis() - millis) +  " ms");

    		//taxonomyWithPageText.put(catid, newNode);
    		 
    		//Dmoz_Data.AllODPSetSaveToFile_(newNode);

    }
    	return newNode;
    }
    
    public static void handleThreads(List<PageNode> pages, NodeInfo node) throws FileNotFoundException, SQLException, InterruptedException, ExecutionException{
    	
    	
    Collection<Match> matches = new ArrayList<Match>();
    Map<String, FutureTask<Match>> matchesMap = new HashMap<String, FutureTask<Match>>();
    Collection<Future<Match>> results = new ArrayList<Future<Match>>();
    List<String> pageText;
    //String[] elements = new String[12];
    //Arrays.fill(elements, "http://movieweb.com/movie/iron-man/");
    
    ArrayList<String> elements = new ArrayList<String>();
    for(PageNode page: pages){
    	elements.add(page.getPage_link());
    }
    //for (String element : elements) {
    /*for(PageNode page: pages){

        pageText = new ArrayList<String>();

        MatchWorker matchWorker = new MatchWorker(page.getPage_link());
        FutureTask<Match> task = new FutureTask<Match>(matchWorker);
        results.add(task);
		pageText = StringProcessingUtils.removeStemmedStopWords(task.get().toString());

        matchesMap.put(page.get_id(), task);
        
        Thread matchThread = new Thread(task);
        matchThread.start();
    }
    
    for(Future<Match> match : results) {
        try {
            matches.add(match.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
	long millis = System.currentTimeMillis();

    ExecutorService executorService = Executors.newFixedThreadPool(30);
	List<Future<Match>> handles = new ArrayList<Future<Match>>();
	Future<Match> handle;
	//String[] elements = new String[12];
	//Arrays.fill(elements, "http://movieweb.com/movie/iron-man/");
	String element = "";
	for (int i=0; i < elements.size(); i++) {
		element = elements.get(i);
		System.out.println("This is element "+ i + " : " + element);
		MatchWorker matchWorker = new MatchWorker(element);
		handle = executorService.submit(matchWorker);
		handles.add(handle);
	}
  
	for (Future<Match> h : handles) {
		try {
			h.get();
		} 
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	executorService.shutdownNow();
	System.out.println("Running parallel took " + (System.currentTimeMillis() - millis) +  " ms");
	
    
    //Dmoz_Data.AllODPSetSaveToFile_(node, matchesMap);
    
    for (Match m : matches) {
        System.out.println(m);
    }

    }
    
    
    public static Map<String, PageNode> getLinks() throws SQLException{
    	
    	Map<String, PageNode> PagesMap = new HashMap<String, PageNode>();
		PageNode page = null;
		
		ConnectDb.initPropertiesForSave();
		final boolean dbConnected = ConnectDb.checkConnection();
		if (!dbConnected) {
			System.exit(1);
		}

		System.out.println("Successfully connected to " + ConnectDb.getEngine().toUpperCase() + " database "
				+ ConnectDb.getDB() + "@" + ConnectDb.getHost() + " as user " + ConnectDb.getUsername());

		String query_pages = "SELECT pageid, link FROM dmoz_pages_no_world_pagecontent" + ";";

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
			
			page = new PageNode();
			page.set_id(row.get("pageid").toString());
			page.setPage_link(row.get("link").toString());
			
			PagesMap.put(page.get_id(), page);
			
		}

		return PagesMap;
	}
	

	
    public static void runThreads(Map<String, PageNode> PagesMap){
    	
   
    	long millis = System.currentTimeMillis();

        ExecutorService executorService = Executors.newFixedThreadPool(50);
    	//List<Future<Match>> handles = new ArrayList<Future<Match>>();
    	Future<Match> handle;
    	//String[] elements = new String[12];
    	//Arrays.fill(elements, "http://movieweb.com/movie/iron-man/");
    	String element = "";
    	for(String page_id: PagesMap.keySet()){ 
    		
    		element = PagesMap.get(page_id).getPage_link();
    		//System.out.println("This is element "+ page_id + " : " + element);
    		MatchWorker matchWorker = new MatchWorker(element);
    		handle = executorService.submit(matchWorker);
    		//handles.add(handle);
    		List<String> tokenizedPageContentList = new ArrayList<String>();
			try {
				String raw_h = handle.get().toString();
				assertNotNull(raw_h);
				tokenizedPageContentList = StringProcessingUtils.removeStemmedStopWords(raw_h);
        		System.out.println(page_id + " : " + tokenizedPageContentList);
        		

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

    		try {
				Dmoz_Data.savePageContentToDB(page_id, tokenizedPageContentList);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
      
    	/*for (Future<Match> h : handles) {
    		try {
    			String raw_h = h.get().toString();
    			System.out.println(raw_h);
    			List<String> tokenizedPageContentList = StringProcessingUtils.removeStemmedStopWords(raw_h);
        		//String tokenizedPageContent_new = Arrays.toString(tokenizedPageContentList.toArray());
        		System.out.println(tokenizedPageContentList);
    		} 
    		catch (Exception ex) {
    			ex.printStackTrace();
    		}
    	}*/
    	
    	executorService.shutdownNow();
    	System.out.println("Running parallel took " + (System.currentTimeMillis() - millis) +  " ms");


        }
    
    
    
    public static void main( String[] args ) throws IOException, SQLException, InterruptedException, ExecutionException
    {
    	//passUrl_();
    	Map<String, PageNode> pages = getLinks();
    	runThreads(pages);

    }
}