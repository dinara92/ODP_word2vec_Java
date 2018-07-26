package read_and_write_file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.mysql.jdbc.Statement;

import hashmap.ConnectDb;
import page_node.PageNode;

public class ReadWriteFile {

	static int i =0;
	
	public static void listFilesForFolder(final File folder) {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	            System.out.println(fileEntry.getName());
	            readWriteOneFile(fileEntry.getName());
	            i++;
	        }
	    }
	}
	
	public static void readWriteOneFile(String file_name){


		try {
			PrintStream clean_pages = new PrintStream(new FileOutputStream("/home/dinara/word2vec/"
					+ "1-billion-word-language-modeling-benchmark-r13output/cleaned_files/" + file_name + "_cleaned" + ".txt"));

			File file = new File("/home/dinara/word2vec/1-billion-word-language-modeling-benchmark-r13output/training-monolingual.tokenized.shuffled/" + file_name);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {


				clean_pages.println(utils.StringProcessingUtils.removeStemmedStopWords(line));
			}
			fileReader.close();
	        clean_pages.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
public static void readFromDBWriteToFile() throws SQLException, FileNotFoundException {
    	
		PrintStream pages = new PrintStream(new FileOutputStream(
			      "odp_crawled_pages_first_time.txt"));

		ConnectDb.initPropertiesForSave();
		final boolean dbConnected = ConnectDb.checkConnection();
		if (!dbConnected) {
			System.exit(1);
		}

		System.out.println("Successfully connected to " + ConnectDb.getEngine().toUpperCase() + " database "
				+ ConnectDb.getDB() + "@" + ConnectDb.getHost() + " as user " + ConnectDb.getUsername());

		String query_pages = "SELECT * FROM dmoz_pages_no_world_pagecontent_parsed" + ";";

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
			
			
			//System.out.println(row.get("pages").toString() + " " + row.get("page_content").toString());
			//pages.println(row.get("pages").toString() + " " + row.get("page_content").toString());
			
		}

			pages.close();
	}

	 public static void main( String[] args ) {
		
		//final File folder = new File("/home/dinara/word2vec/1-billion-word-language-modeling-benchmark-r13output/training-monolingual.tokenized.shuffled");
		//listFilesForFolder(folder);
		//System.out.println("Files in folder " + i);

		 try {
			readFromDBWriteToFile();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    }
	    
	
	
	
	
	
	
}
