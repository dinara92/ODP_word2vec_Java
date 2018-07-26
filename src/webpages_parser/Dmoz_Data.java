package webpages_parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import hashmap.ConnectDb;
import hashmap.DBtoHashmap;
import main.Main;
import node_info.NodeInfo;
import page_node.PageNode;
import utils.StringProcessingUtils;

public class Dmoz_Data {

	public static Map<String, NodeInfo> makeTaxonomyAll() throws SQLException{
		
		ConnectDb.initProperties();
		final boolean dbConnected = ConnectDb.checkConnection();
		if (!dbConnected) {
			System.exit(1);
		}

		System.out.println("Successfully connected to " + ConnectDb.getEngine().toUpperCase() + " database "
				+ ConnectDb.getDB() + "@" + ConnectDb.getHost() + " as user " + ConnectDb.getUsername());

		String query_pages = "SELECT * FROM dmoz_externalpages" + ";";
		String query_categories = "SELECT * FROM dmoz_categories" + ";";

		Statement stmt = null;
		Statement stmt2 = null;

		try {
			stmt = (Statement) ConnectDb.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt2 = (Statement) ConnectDb.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ResultSet rs_pages = stmt.executeQuery(query_pages);
		ResultSet rs_categories = stmt2.executeQuery(query_categories);

		DBtoHashmap resultSettoHash = new DBtoHashmap();
		System.out.println("db pages result set size : " + rs_pages.getFetchSize());
		System.out.println("db categories result set size : " + rs_categories.getFetchSize());
		Map<String, List<PageNode>> PageTaxonomy = resultSettoHash.resultSetPageWithLinkToList(rs_pages);
		Map<String, List<String>> CategoryTaxonomy = resultSettoHash.resultSetCategoryToList(rs_categories);
		Map<String, List<String>> testPagesDB = resultSettoHash.resultSetCategoryToList(rs_categories);

		Map<String, NodeInfo> taxonomyAll;
		Map<String, NodeInfo> taxonomyHeuristicsWorldReg;
		Map<String, NodeInfo> taxonomyHeuristicsWorld;
		
		taxonomyAll = Main.applyNoHeuristics(CategoryTaxonomy, PageTaxonomy);
		//taxonomyHeuristicsWorldReg = Main.applyHeuristicsWorldReg(CategoryTaxonomy, PageTaxonomy, taxonomyAll);
		taxonomyHeuristicsWorld = Main.applyHeuristicsWorld(CategoryTaxonomy, PageTaxonomy, taxonomyAll);
		
		return taxonomyHeuristicsWorld;
	}

	public static void AllODPSetSaveToFile(Map<String, NodeInfo> taxonomy) throws FileNotFoundException{
			
	    int id = 0;			
		//PrintStream trainPages_bigSet = new PrintStream(new FileOutputStream(
		//	          "dmoz____.txt"));
			
			
	        ConnectDb.initPropertiesForSave();
			final boolean dbConnected = ConnectDb.checkConnection();
			if (!dbConnected) {
				System.exit(1);
			}
	
			System.out.println("Successfully connected to " + ConnectDb.getEngine().toUpperCase() + " database "
					+ ConnectDb.getDB() + "@" + ConnectDb.getHost() + " as user " + ConnectDb.getUsername());
	
			PreparedStatement statement_save;
			
	        for(String catid : taxonomy.keySet()){
	        	
	        	for(PageNode page : taxonomy.get(catid).getPages())
	        	{
	        		//System.out.println(Arrays.toString(page.getTokenizedPage().toArray()));
	        		String tokenizedPage = Arrays.toString(page.getTokenizedPage().toArray());
	        		
	        		//System.out.println(Arrays.toString(page.getTokenizedPageText().toArray()));
	        		//String tokenizedPageContent = Arrays.toString(page.getTokenizedPageText().toArray());
	        		id = id+1;
	        		
	        		String query_set_pages = "INSERT INTO dmoz_pages_no_world_pagelinks (pages, catid, fatherid, pageid, link)"
	        		        + " values (?, ?, ?, ?, ?);";
	        		
	        		try {
		        		statement_save = (PreparedStatement) ConnectDb.getConnection().prepareStatement(query_set_pages);
						statement_save.setString(1,tokenizedPage);
						statement_save.setString(2, catid);
		        		statement_save.setString(3, taxonomy.get(catid).getFatherid());
		        		statement_save.setString(4, page.get_id());
		        		statement_save.setString(5, page.getPage_link().toString());

		        		statement_save.executeUpdate();
		        		
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
	        			System.out.println("Failed to insert into db ODP page " + page.get_id());

					}
	        		
	        		//trainPages_bigSet.println(tokenizedPage);
	
	        	}
	        	
	        }	
	        //UNSELECT TO COMMIT
	        try {
				ConnectDb.getConnection().commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        //trainPages_bigSet.close();
			
			System.out.println("Number of rows should be added to db: " + id);
			
			//File file_trainPages_bigSet =new File("C:/Users/dinaraDILab/java_projects/ODP-Word2Vec/dmoz_pages_train_no_world_only.txt");
			//Main.lineNumberReader(file_trainPages_bigSet);
	
		
	}
	
	
	public static void AllODPSetSaveToFile_byNode(NodeInfo node) throws FileNotFoundException, InterruptedException, ExecutionException{
		
	    int id = 0;			
		//PrintStream trainPages_bigSet = new PrintStream(new FileOutputStream(
		//	          "dmoz____.txt"));
			
			
	        ConnectDb.initPropertiesForSave();
			final boolean dbConnected = ConnectDb.checkConnection();
			if (!dbConnected) {
				System.exit(1);
			}
	
			//System.out.println("Successfully connected to " + ConnectDb.getEngine().toUpperCase() + " database "
			//		+ ConnectDb.getDB() + "@" + ConnectDb.getHost() + " as user " + ConnectDb.getUsername());
	
			PreparedStatement statement_save;
			
	       // for(String catid : taxonomy.keySet()){
	        	
	        	for(PageNode page : node.getPages())
	        	{
	        		//System.out.println(Arrays.toString(page.getTokenizedPage().toArray()));
	        		String tokenizedPage = Arrays.toString(page.getTokenizedPage().toArray());
	        		
	        		//System.out.println(Arrays.toString(page.getTokenizedPageText().toArray()));
	        		//String tokenizedPageContent = Arrays.toString(page.getTokenizedPageText().toArray());
	        		//String tokenizedPageContent = matchesMap.get(page.get_id()).get().toString();
	        		//List<String> tokenizedPageContentList = StringProcessingUtils.removeStemmedStopWords(tokenizedPageContent);
	        		//String tokenizedPageContent_new = Arrays.toString(tokenizedPageContentList.toArray());
	        		id = id+1;
	        		
	        		String query_set_pages = "INSERT INTO dmoz_pages_no_world_pagelinks (pages, catid, fatherid, pageid, link)"
	        		        + " values (?, ?, ?, ?, ?);";
	        		
	        		try {
	            		statement_save = (PreparedStatement) ConnectDb.getConnection().prepareStatement(query_set_pages);
		        		statement_save.setString(1,tokenizedPage);
		        		//statement_save.setString(2, tokenizedPageContent_new);
		        		statement_save.setString(2, node.getCatid());
		        		statement_save.setString(3, node.getFatherid());
		        		statement_save.setString(4, page.get_id());
		        		statement_save.setString(5, page.getPage_link());

		        		statement_save.executeUpdate();
		    	        //UNSELECT TO COMMIT
		    	        ConnectDb.getConnection().commit();
	        		} catch (SQLException e) {
	        			System.out.println("Failed to insert into db ODP page");
	        			e.printStackTrace();
	        		}
	        		//trainPages_bigSet.println(tokenizedPage);
	
	        	}
	        	
	        //}	
	        		
	        //trainPages_bigSet.close();
			
			//System.out.println("Number of rows should be added to db: " + id);
			
			//File file_trainPages_bigSet =new File("C:/Users/dinaraDILab/java_projects/ODP-Word2Vec/dmoz_pages_train_no_world_only.txt");
			//Main.lineNumberReader(file_trainPages_bigSet);
	
		
	}

	public static synchronized void savePageContentToDB(String pageid, List<String> match) throws FileNotFoundException, InterruptedException, ExecutionException{	
		
	   ConnectDb.initPropertiesForSave();
		final boolean dbConnected = ConnectDb.checkConnection();
		if (!dbConnected) {
			System.exit(1);
		}

		PreparedStatement statement_update;   	
		
		//1243968
		String query_set_pages = "UPDATE dmoz_pages_no_world_pagelinks SET page_content = ? WHERE pageid = ?;";
		String tokenizedPageContent = Arrays.toString(match.toArray());

		try {
			statement_update = (PreparedStatement) ConnectDb.getConnection().prepareStatement(query_set_pages);
			statement_update.setString(1,tokenizedPageContent);
			statement_update.setString(2,pageid);

			statement_update.executeUpdate();
			//UNSELECT TO COMMIT
	     ConnectDb.getConnection().commit();
		} catch (SQLException e) {
			System.out.println("Failed to update db with parsed page content");
			e.printStackTrace();
		}

		//System.out.println("Updated");

	}
	
	public static synchronized void saveByPageToDB(String pageid, PageNode page) throws FileNotFoundException, InterruptedException, ExecutionException{	
		
		   ConnectDb.initPropertiesForSave();
			final boolean dbConnected = ConnectDb.checkConnection();
			if (!dbConnected) {
				System.exit(1);
			}

			PreparedStatement statement_save;   	
			
			String query_set_pages = "INSERT INTO dmoz_pages_no_world_pagecontent_parsed (pages, catid, fatherid, pageid, page_content)"
    		        + " values (?, ?, ?, ?, ?);";			
    		//String tokenizedPage = Arrays.toString(page.getTokenizedPage().toArray());
    		String tokenizedPageContent = Arrays.toString(page.getTokenizedPageText().toArray());
    		//System.out.println(tokenizedPage);
    		//System.out.println(tokenizedPageContent);
			try {
				statement_save = (PreparedStatement) ConnectDb.getConnection().prepareStatement(query_set_pages);
				statement_save.setString(1,page.getTokenizedPageAsString());
        		statement_save.setString(2, page.getCatid());
        		statement_save.setString(3, page.getFatherid());
        		statement_save.setString(4, page.get_id());
        		statement_save.setString(5, tokenizedPageContent);
        		statement_save.executeUpdate();
        		//System.out.println("Is executed");

				//UNSELECT TO COMMIT
		     ConnectDb.getConnection().commit();
			} catch (SQLException e) {
				System.out.println("Failed to update db with parsed page content");
				e.printStackTrace();
			}

		}
	
	
	public static void odpByPagesSaveToDB(Map<String, PageNode> taxonomy) throws FileNotFoundException{
		
	    int id = 0;			
		//PrintStream trainPages_bigSet = new PrintStream(new FileOutputStream(
		//	          "dmoz____.txt"));
			
			
	        ConnectDb.initPropertiesForSave();
			final boolean dbConnected = ConnectDb.checkConnection();
			if (!dbConnected) {
				System.exit(1);
			}
	
			System.out.println("Successfully connected to " + ConnectDb.getEngine().toUpperCase() + " database "
					+ ConnectDb.getDB() + "@" + ConnectDb.getHost() + " as user " + ConnectDb.getUsername());
	
			PreparedStatement statement_save;
			
	        	
	        	for(String page : taxonomy.keySet())
	        	{
	    			
	    			String query_set_pages = "INSERT INTO dmoz_pages_no_world_pagecontent_parsed_new (pages, catid, fatherid, pageid, page_content)"
	        		        + " values (?, ?, ?, ?, ?);";			
	        		//String tokenizedPage = Arrays.toString(page.getTokenizedPage().toArray());
	        		String tokenizedPageContent = Arrays.toString(taxonomy.get(page).getTokenizedPageText().toArray());
	        		//System.out.println(tokenizedPage);
	        		//System.out.println(tokenizedPageContent);
	    			try {
	    				statement_save = (PreparedStatement) ConnectDb.getConnection().prepareStatement(query_set_pages);
	    				statement_save.setString(1,taxonomy.get(page).getTokenizedPageAsString());
	            		statement_save.setString(2, taxonomy.get(page).getCatid());
	            		statement_save.setString(3, taxonomy.get(page).getFatherid());
	            		statement_save.setString(4, taxonomy.get(page).get_id());
	            		statement_save.setString(5, tokenizedPageContent);
	            		statement_save.executeUpdate();
	            		//System.out.println("Is executed");

	    				//UNSELECT TO COMMIT
	    		     ConnectDb.getConnection().commit();
	    			} catch (SQLException e) {
	    				System.out.println("Failed to update db with parsed page content");
	    				e.printStackTrace();
	    			}
	        	}
	        	
	        	
	        //UNSELECT TO COMMIT
	        try {
				ConnectDb.getConnection().commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        //trainPages_bigSet.close();
			
			System.out.println("Number of rows should be added to db: " + id);
			
			//File file_trainPages_bigSet =new File("C:/Users/dinaraDILab/java_projects/ODP-Word2Vec/dmoz_pages_train_no_world_only.txt");
			//Main.lineNumberReader(file_trainPages_bigSet);
	
		
	}
	
}
