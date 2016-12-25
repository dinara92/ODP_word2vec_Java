package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import centroid.Centroid;
import centroid.Word2VecCentroid;
import csv_utils.CSVUtils;
import hashmap.ConnectDb;
import hashmap.DBtoHashmap;
import inverted_index.InvertedIndex;
import node_info.CategoryTree;
import node_info.NodeInfo;
import page_node.PageNode;
import testDoc.TestDoc;
import tfidfDocument.CosineSim;
import tfidfDocument.DocumentParser;
import utils.StringProcessingUtils;

public class Main {

	public static Map<String, List<String>> makeHieararchyTree(Map<String, NodeInfo> taxonomy) {
		Map<String, List<String>> tree = new HashMap<String, List<String>>();
		List<String> childList;
		String fatherId;

		for (String catId: taxonomy.keySet()) {
			fatherId = taxonomy.get(catId).getFatherid();
			
			if (tree.containsKey(fatherId)) {
				childList = tree.get(fatherId);
				childList.add(catId);
				tree.put(fatherId, childList);
			} else {
				childList = new ArrayList<String>();
				childList.add(catId);
				tree.put(fatherId, childList);
			}
		}
		return tree;
	}
	
	public static Map<String, NodeInfo> applyTrainSetHeuristics(Map<String, NodeInfo> trainDataSet){
		
		Map<String, NodeInfo> heuristicTrainSetTaxonomy = new HashMap<String, NodeInfo>();
		
		
		NodeInfo node  = null;
		for (String key_catid_in_page : trainDataSet.keySet()) {
			/*
			if (!pageTaxonomy.containsKey(key_catid_in_page)) {
				// node.setCatid("this catid does not exist");
				System.out.println("No catid in category taxonomy: " + key_catid_in_page);
				continue;
			}
			*/
			
			List<PageNode> catPages = trainDataSet.get(key_catid_in_page).getPages();
			if (catPages == null) {
				catPages = Collections.emptyList();
			}
			
				/* ************** HEURISTICS3 ***************** */
			if (!(trainDataSet.get(key_catid_in_page).getCategory_topic().contains("/Regional/")) ||
					(trainDataSet.get(key_catid_in_page).getCategory_topic().contains("/Regional"))  ||
							(trainDataSet.get(key_catid_in_page).getCategory_topic().contains("/Regional/")) ||
							(trainDataSet.get(key_catid_in_page).getCategory_topic().contains("Top/World"))){
				
				//node = trainDataSet.get(key_catid_in_page);
				node = new NodeInfo();
				//if (node == null || node.getPages() == null || node.getPages().size() == 0) {

					
					node.setPages(catPages);
					
					
				//}
				// node.setPages(pageTaxonomy.get(key_catid_in_page));
				heuristicTrainSetTaxonomy.put(key_catid_in_page, node);
				} else {
					System.out.println("doesnt fit heuristic");
				}
				
		//}
			//}
		}
		return heuristicTrainSetTaxonomy;
	}
	
	public static Map<String, NodeInfo> applyHeuristics(Map<String, List<String>> categoryTaxonomy,
			Map<String, List<PageNode>> pageTaxonomy, List<String> fatherIdList, Map<String, NodeInfo> taxonomyAll,
			Map<String, List<String>> taxonomyTree) {
		
		Map<String, NodeInfo> heuristicTaxonomy = new HashMap<String, NodeInfo>();
		NodeInfo node  = null;
		for (String key_catid_in_page : categoryTaxonomy.keySet()) {
			/*
			if (!pageTaxonomy.containsKey(key_catid_in_page)) {
				// node.setCatid("this catid does not exist");
				System.out.println("No catid in category taxonomy: " + key_catid_in_page);
				continue;
			}
			*/
			int childCatPages = 0;
			List<String> childList = taxonomyTree.get(key_catid_in_page);
			List<PageNode> catPages = pageTaxonomy.get(key_catid_in_page);
			if (catPages == null) {
				catPages = Collections.emptyList();
			}
			int childPagesSize = catPages.size();
			/* ************** HEURISTICS3 ****************
			 * * */
			if (!(childList==null)){
				for(String child : childList){	
					if(pageTaxonomy.get(child)!=null){
						childCatPages = pageTaxonomy.get(child).size();
						childPagesSize += childCatPages;
					}
				}
			}
			if (catPages.size() >= 2) {
			if (childPagesSize >= 50) {
				
			/* ************** HEURISTICS1 ***************** */
			Pattern pattern = Pattern.compile("/[a-zA-Z]{1}/");
			Pattern pattern2 = Pattern.compile("/[a-zA-Z]{1}$");
			Matcher matcher = pattern.matcher(categoryTaxonomy.get(key_catid_in_page).get(0));
			Matcher matcher2 = pattern2.matcher(categoryTaxonomy.get(key_catid_in_page).get(0));

			/* ************** HEURISTICS1 ***************** */
			if (!(categoryTaxonomy.get(key_catid_in_page).get(0).contains("/Titles/")
					|| categoryTaxonomy.get(key_catid_in_page).get(0).contains("/Regional/")
					|| (categoryTaxonomy.get(key_catid_in_page).get(0).contains("/Titles")
					|| (categoryTaxonomy.get(key_catid_in_page).get(0).contains("/Regional")
					 || (categoryTaxonomy.get(key_catid_in_page).get(0).contains("Top/World")
							 || matcher.find()|| matcher2.find()))))){
			
				
					/* ************** HEURISTICS2 ***************** */
					if (fatherIdList.contains(key_catid_in_page)) {
				node = taxonomyAll.get(key_catid_in_page);
				if (node == null || node.getPages() == null || node.getPages().size() == 0) {
					System.out.println("Warning!!! Category " + key_catid_in_page + " isn't in taxonomyAll ");
					for (PageNode doc : catPages) {
						List<String> tokenizedDoc = StringProcessingUtils.removeStemmedStopWords(doc.getDescription());
						doc.setTokenizedPage(tokenizedDoc);
						// since page strings are tokenized, no need to keep whole
						//doc.setDescription(null);
					}
					node.setPages(catPages);
					node.setCategory_topic(categoryTaxonomy.get(key_catid_in_page).get(0));
					node.setFatherid(categoryTaxonomy.get(key_catid_in_page).get(1));
				}
				// node.setPages(pageTaxonomy.get(key_catid_in_page));
				// collectionOfPages.addAll(node.getPages());

				heuristicTaxonomy.put(key_catid_in_page, node);
				}}
				}
				}
		}
		return heuristicTaxonomy;
	}
	
	public static Map<String, NodeInfo> applySmallSizeHeuristics(Map<String, List<String>> categoryTaxonomy,
			Map<String, List<PageNode>> pageTaxonomy,  Map<String, NodeInfo> taxonomyAll) {
		Map<String, NodeInfo> taxonomy = new HashMap<String, NodeInfo>();
		NodeInfo node = null;
		System.out.println("category taxonomy keyset size is : " + categoryTaxonomy.keySet().size());
		for (String key_catid_in_page : categoryTaxonomy.keySet()) {
			/*
			if (key_catid_in_page.equals("425236") || key_catid_in_page.equals("957244")
					|| key_catid_in_page.equals("425086")) {
			 */
			if (key_catid_in_page.equals("272670") || key_catid_in_page.equals("425339")
					|| key_catid_in_page.equals("425148")) {
				node = new NodeInfo();
				List<PageNode> catPages = pageTaxonomy.get(key_catid_in_page);
				if (catPages == null) {
					catPages = Collections.emptyList();
				}
				node = taxonomyAll.get(key_catid_in_page);
				if (node == null || node.getPages() == null || node.getPages().size() == 0) {
					System.out.println("Warning!!! Category " + key_catid_in_page + " isn't in taxonomyAll ");
					for (PageNode doc : catPages) {
						List<String> tokenizedDoc = StringProcessingUtils.removeStemmedStopWords(doc.getDescription());
						doc.setTokenizedPage(tokenizedDoc);
					}
					node.setPages(catPages);
					node.setCategory_topic(categoryTaxonomy.get(key_catid_in_page).get(0));
					node.setFatherid(categoryTaxonomy.get(key_catid_in_page).get(1));
				}
				taxonomy.put(key_catid_in_page, node);
			}
		}
		return taxonomy;
	}

	public static Map<String, NodeInfo> applyTestHeuristics(Map<String, NodeInfo> taxonomy) {
		Map<String, NodeInfo> heuristicTaxonomy = new HashMap<String, NodeInfo>();
		NodeInfo node  = null;
		for (String key_catid_in_page : taxonomy.keySet()) {
			/*
			if (!pageTaxonomy.containsKey(key_catid_in_page)) {
				// node.setCatid("this catid does not exist");
				System.out.println("No catid in category taxonomy: " + key_catid_in_page);
				continue;
			}
			*/
			
			List<PageNode> catPages = taxonomy.get(key_catid_in_page).getPages();
			if (catPages == null) {
				catPages = Collections.emptyList();
			}
			
				/* ************** HEURISTICS3 ***************** */
				if (catPages.size() < 5) {
			//if (key_catid_in_page.equals("425236") || key_catid_in_page.equals("957244")
			//		|| key_catid_in_page.equals("425086")) {
				node = taxonomy.get(key_catid_in_page);
				if (node == null || node.getPages() == null || node.getPages().size() == 0) {
					//System.out.println("Warning!!! Category " + key_catid_in_page + " isn't in taxonomyAll ");
					for (PageNode doc : catPages) {
						List<String> tokenizedDoc = StringProcessingUtils.removeStemmedStopWords(doc.getDescription());
						doc.setTokenizedPage(tokenizedDoc);
						// since page strings are tokenized, no need to keep whole
						//doc.setDescription(null);
					}
					node.setPages(catPages);
					//node.setCategory_topic(categoryTaxonomy.get(key_catid_in_page).get(0));
					//node.setFatherid(categoryTaxonomy.get(key_catid_in_page).get(1));
				}
				// node.setPages(pageTaxonomy.get(key_catid_in_page));

				heuristicTaxonomy.put(key_catid_in_page, node);
				}
				
		//}
			//}
		}
		return heuristicTaxonomy;
	}
	
	public static Map<String, NodeInfo> applyNoHeuristics(Map<String, List<String>> categoryTaxonomy,
			Map<String, List<PageNode>> pageTaxonomy) {
		Map<String, NodeInfo> taxonomyAll = new HashMap<String, NodeInfo>();
		NodeInfo node = null;

		for (String key_catid_in_page : categoryTaxonomy.keySet()) {
			node = new NodeInfo();
			List<PageNode> catPages = pageTaxonomy.get(key_catid_in_page);
			if (catPages == null) {
				catPages = Collections.emptyList();
			}
			for (PageNode doc: catPages) {
				List<String> tokenizedDoc = StringProcessingUtils.removeStemmedStopWords(doc.getDescription());
				doc.setTokenizedPage(tokenizedDoc);
				doc.setDescription(null);
				//page_terms.add(tokenizedDoc);
			}
			node.setPages(catPages);
			node.setCategory_topic(categoryTaxonomy.get(key_catid_in_page).get(0));
			node.setFatherid(categoryTaxonomy.get(key_catid_in_page).get(1));
			taxonomyAll.put(key_catid_in_page, node);
		} 
		//System.out.println("Warning: the categories are : " + warnCategoryList);
		return taxonomyAll;
	}
	
	/*public static Map<String, NodeInfo> applyCategoryHeuristics(Map<String, List<String>> categoryTaxonomy) {
		Map<String, NodeInfo> taxonomyAll = new HashMap<String, NodeInfo>();
		NodeInfo node = null;

		for (String key_catid_in_page : categoryTaxonomy.keySet()) {
			node = new NodeInfo();
			List<PageNode> catPages = pageTaxonomy.get(key_catid_in_page);
			if (catPages == null) {
				catPages = Collections.emptyList();
			}
			for (PageNode doc: catPages) {
				List<String> tokenizedDoc = StringProcessingUtils.removeStemmedStopWords(doc.getDescription());
				doc.setTokenizedPage(tokenizedDoc);
				doc.setDescription(null);
				//page_terms.add(tokenizedDoc);
			}
			node.setPages(catPages);
			node.setCategory_topic(categoryTaxonomy.get(key_catid_in_page).get(0));
			node.setFatherid(categoryTaxonomy.get(key_catid_in_page).get(1));
			taxonomyAll.put(key_catid_in_page, node);
		} 
		//System.out.println("Warning: the categories are : " + warnCategoryList);
		return taxonomyAll;
	}
	
	public static Map<String, NodeInfo> applyPageHeuristics(Map<String, List<PageNode>> pageTaxonomy) {
		Map<String, NodeInfo> taxonomyAll = new HashMap<String, NodeInfo>();
		NodeInfo node = null;

		for (String key_catid_in_page : pageTaxonomy.keySet()) {
			node = new NodeInfo();
			List<PageNode> catPages = pageTaxonomy.get(key_catid_in_page);
			if (catPages == null) {
				catPages = Collections.emptyList();
			}
			for (PageNode doc: catPages) {
				List<String> tokenizedDoc = StringProcessingUtils.removeStemmedStopWords(doc.getDescription());
				doc.setTokenizedPage(tokenizedDoc);
				doc.setDescription(null);
				//page_terms.add(tokenizedDoc);
			}
			node.setPages(catPages);
			taxonomyAll.put(key_catid_in_page, node);
		} 
		//System.out.println("Warning: the categories are : " + warnCategoryList);
		return taxonomyAll;
	}
	*/
	public static Map<String, List<Map<String, Double>>> computeCategoryTfidfDocsVectors (Map<String, NodeInfo> taxonomy, InvertedIndex invIndex) throws IOException{
		Map<String, List<Map<String, Double>>> categoryTfidfDocsVectorMap = new HashMap<String, List<Map<String, Double>>>();
		DocumentParser dp = new DocumentParser();
		//TestDoc testDoc;
		for (String catId : taxonomy.keySet()) {
			List<Map<String, Double>> tfidfDocsVector = dp.tfIdfCalculator2(taxonomy.get(catId).getPages(), invIndex);
			//testDoc = new TestDoc();
			//testDoc.setTestDoc(tfidfDocsVector);
			categoryTfidfDocsVectorMap.put(catId, tfidfDocsVector);
		}
		return categoryTfidfDocsVectorMap;
	}
	
	public static Map<String, Centroid> computeCentroids(Map<String, NodeInfo> taxonomy, InvertedIndex invIndex) throws IOException {
		int count_categories = 0;
		Map<String, Centroid> centroidMap = new HashMap<String, Centroid>();
		Centroid centroidThis;
		DocumentParser dp = new DocumentParser();
		for (String catId : taxonomy.keySet()) {
			List<Map<String, Double>> tfidfDocsVector = dp.tfIdfCalculator2(taxonomy.get(catId).getPages(), invIndex);
			Map<String, Double> catCentroid = dp.makeCentroid(tfidfDocsVector);
			centroidThis = new Centroid();

			centroidThis.setCentroid(catCentroid);
			centroidThis.setCentroid_lengthNorm();
			//centroidMap.put(catId, catCentroid);/*** important ***/
			centroidMap.put(catId, centroidThis);
			// TaxonomyAll.put(node.getCatid(), node);
			/*
			System.out.println(
					"\n" + "Centroid size is : " + catCentroid.size() + " Centroid # is : " + count_categories);

			for (String tfidf : catCentroid.keySet()) {
				System.out.print(catCentroid.get(tfidf) + ", ");
			}
			*/
			count_categories++;
		}
		System.out.println("Computed centroids for " + count_categories + " categories");
		return centroidMap;
	}
	
	public static Map<String, Word2VecCentroid> computeCentroidsWord2Vec(Map<String, NodeInfo> taxonomy) throws IOException {
		int count_categories = 0;
		Map<String, Word2VecCentroid> centroidMap = new HashMap<String, Word2VecCentroid>();
		Word2VecCentroid centroidThis;
		DocumentParser dp = new DocumentParser();
		List<List<Double>> eachPageVectorsList = new ArrayList<List<Double>>();
		List<Double> word2vecVector;
		
		for (String catId : taxonomy.keySet()) {
			eachPageVectorsList.clear();
			List<PageNode> pagesInThisCateg = taxonomy.get(catId).getPages();
			for(PageNode page: pagesInThisCateg) {
					word2vecVector = page.getWord2VecVectors();
					eachPageVectorsList.add(word2vecVector);
			}
			List<Double> catCentroid = dp.makeCentroidWord2Vec(eachPageVectorsList);
			centroidThis = new Word2VecCentroid();

			centroidThis.setCentroid(catCentroid);
			centroidMap.put(catId, centroidThis);
	
			count_categories++;
		}
		System.out.println("Computed centroids for " + count_categories + " categories");
		return centroidMap;
	}
	
	
	public static Map<String, List<Integer>> computeCosineSim (Map<String, List<Map<String, Double>>> categoryTfidfDocsVectors, Map<String, Centroid> categoryCentroids){
		
		CosineSim cosineSim = new CosineSim();
		List<String> returnedCatids = new ArrayList<String>();
		List<Integer> tpFnFpList;
		Map<String, List<Integer>> returnedCatidsMapTpFnFp = new HashMap<String, List<Integer>>();
		Map<String, Integer> FPMap = new HashMap<String, Integer>();
		Map<String, List<String>> returnedCatidsMap = new HashMap<String, List<String>>();
		int current_hit_countTP = 0;
		int current_miss_countFN = 0;
		String assignedCatId;
		
		for(String catid : categoryTfidfDocsVectors.keySet()) {
			returnedCatids.clear();
			tpFnFpList = new ArrayList<Integer>();
		//String catid = "419823"; //425216, 425244, 423721, 272660, 418977, 418269, 418240, 428695, 425236, 957244, 425086 assigned to 957244 each time
		//419823 - this  s only 2 docs -> all docs assigned to training set?
		
			//if(categoryTfidfDocsVectors.get(catid).size() >3 ){
			for(Map<String, Double> doc : categoryTfidfDocsVectors.get(catid)) {
				assignedCatId = cosineSim.maxCosineSimilarity(doc, categoryCentroids);		
				returnedCatids.add(assignedCatId);
				
				//if (!assignedCatId.equals(catid)) {
				//	System.out.println("Page of category " + catid + " was falsely assigned to " + assignedCatId);
				//}
				
				if (FPMap.containsKey(assignedCatId)) {
					FPMap.put(assignedCatId, FPMap.get(assignedCatId) + 1);
				} else {
					FPMap.put(assignedCatId, 1);
				}
			}
			//System.out.println("For category " + catid +  " Assigned catids are : " + returnedCatids);
			
			current_hit_countTP = Collections.frequency(returnedCatids, catid);
			//System.out.println("For catid " + catid + "hit count is " + current_hit_countTP);
			
			int currentClassFP = FPMap.get(catid) == null ? 0 : FPMap.get(catid);
			FPMap.put(catid, currentClassFP - current_hit_countTP);
			//System.out.println("TP is : " + hit_countTP);
			//current_miss_countFN = returnedCatids.size() - current_hit_countTP;
			current_miss_countFN = categoryTfidfDocsVectors.get(catid).size() - current_hit_countTP;
			//System.out.println("FN is : " + miss_countFN);
			//System.out.println("Pair of TP and FN is : " + pairHitMiss);

		//}
			tpFnFpList.add(current_hit_countTP);
			tpFnFpList.add(current_miss_countFN);
			returnedCatidsMapTpFnFp.put(catid, tpFnFpList);
			
		//else System.out.println("Too small test set");
		}
		

		for(String catid : FPMap.keySet()) {
			if(returnedCatidsMapTpFnFp.containsKey(catid)){ //because of the below comment, added this condition
			tpFnFpList = returnedCatidsMapTpFnFp.get(catid); //here tpFnFpList becomes null, as catid in returnedCatidsMapTpFnFp is from test set doc vectors
			tpFnFpList.add(FPMap.get(catid));
			returnedCatidsMapTpFnFp.put(catid, tpFnFpList);
			}
			//System.out.println("FP for this category : " + catid + " is " + FPMap.get(catid));
			
		}

		return returnedCatidsMapTpFnFp;
	}
		
	public static Map<String, List<Integer>> computeCosineSimWord2Vec (Map<String, NodeInfo>  testDataNodes, Map<String, Word2VecCentroid> centroidMap){
		
		CosineSim cosineSim = new CosineSim();
		List<String> returnedCatids = new ArrayList<String>();
		List<Integer> tpFnFpList;
		Map<String, List<Integer>> returnedCatidsMapTpFnFp = new HashMap<String, List<Integer>>();
		Map<String, Integer> FPMap = new HashMap<String, Integer>();
		Map<String, List<String>> returnedCatidsMap = new HashMap<String, List<String>>();
		int current_hit_countTP = 0;
		int current_miss_countFN = 0;
		String assignedCatId;
		int assignedToFather = 0;
		
		for(String catid : testDataNodes.keySet()) {
			returnedCatids.clear();
			tpFnFpList = new ArrayList<Integer>();
		//String catid = "419823"; //425216, 425244, 423721, 272660, 418977, 418269, 418240, 428695, 425236, 957244, 425086 assigned to 957244 each time
		//419823 - this  s only 2 docs -> all docs assigned to training set?
	
			//if(categoryTfidfDocsVectors.get(catid).size() >3 ){
			for(PageNode doc : testDataNodes.get(catid).getPages()) {
				assignedCatId = cosineSim.maxCosineSimilarityWord2Vec(doc, centroidMap);		
				returnedCatids.add(assignedCatId);
				
				if (!assignedCatId.equals(catid)) {
					//System.out.println("Page id: " + doc.get_id() + " of category " + catid + " was falsely assigned to " + assignedCatId);
					//HERE CAN MAKE RECURSIVE CONDITION TO CHECK GRAND - GRAND FATHERS
					if(assignedCatId.equals(testDataNodes.get(catid).getFatherid())){
						//System.out.println("\tThis page is assigned to its category FATHER!");
						assignedToFather++;
					}
				}
				
				if (FPMap.containsKey(assignedCatId)) {
					FPMap.put(assignedCatId, FPMap.get(assignedCatId) + 1);
				} else {
					FPMap.put(assignedCatId, 1);
				}
			}
			//System.out.println("For category " + catid +  " Assigned catids are : " + returnedCatids);
			
			current_hit_countTP = Collections.frequency(returnedCatids, catid);
			//System.out.println("For catid " + catid + "hit count is " + current_hit_countTP);
			
			int currentClassFP = FPMap.get(catid) == null ? 0 : FPMap.get(catid);
			FPMap.put(catid, currentClassFP - current_hit_countTP);
			//System.out.println("TP is : " + hit_countTP);
			//current_miss_countFN = returnedCatids.size() - current_hit_countTP;
			current_miss_countFN = testDataNodes.get(catid).getPages().size() - current_hit_countTP;
			//System.out.println("FN is : " + miss_countFN);
			//System.out.println("Pair of TP and FN is : " + pairHitMiss);

		//}
			tpFnFpList.add(current_hit_countTP);
			tpFnFpList.add(current_miss_countFN);
			returnedCatidsMapTpFnFp.put(catid, tpFnFpList);
			
		//else System.out.println("Too small test set");
		}
		
		
		System.out.println("\tTotal these number of docs were assigned to FATHER of its category: " + assignedToFather);
		for(String catid : FPMap.keySet()) {
			if(returnedCatidsMapTpFnFp.containsKey(catid)){ //because of the below comment, added this condition
			tpFnFpList = returnedCatidsMapTpFnFp.get(catid); //here tpFnFpList becomes null, as catid in returnedCatidsMapTpFnFp is from test set doc vectors
			tpFnFpList.add(FPMap.get(catid));
			returnedCatidsMapTpFnFp.put(catid, tpFnFpList);
			}
			//System.out.println("FP for this category : " + catid + " is " + FPMap.get(catid));
			
		}

		return returnedCatidsMapTpFnFp;
	}

	 public static List<Integer> uniqueRandNum(int upperBound) {
	        ArrayList<Integer> list = new ArrayList<Integer>();
	        for (int i = 0; i < upperBound; i++) {
	            list.add(new Integer(i));
	        }
			return list;
	    }
	 
	 static Map<String, Map<String, NodeInfo>> computeDataSets(Map<String, NodeInfo> taxonomy) throws SQLException, FileNotFoundException {
		 
		Map<String, NodeInfo> taxonomyTrain = new HashMap<String, NodeInfo>();
		Map<String, NodeInfo> taxonomyTest = new HashMap<String, NodeInfo>();
		Map<String, Map<String, NodeInfo>> dataSets = new HashMap<String, Map<String, NodeInfo>>();
				 
		
        for(String catid : taxonomy.keySet()) {
        	List<PageNode> thisCategoryDocsList = taxonomy.get(catid).getPages();
        	List<PageNode> testThisCategoryDocsList = new ArrayList<PageNode>();
        	List<PageNode> trainThisCategoryDocsList = new ArrayList<PageNode>();
        	
        	
        	NodeInfo nodeTest = new NodeInfo();
        	NodeInfo nodeTrain = new NodeInfo();
        	
        	List<Integer> listShuffled = uniqueRandNum(thisCategoryDocsList.size());
        	//System.out.println("List of random numbers before being shuffled has items : " + listShuffled);
            Collections.shuffle(listShuffled);
        	//System.out.println("List of random numbers after being shuffled has items : " + listShuffled);
            
        	int testDataSize = (int) (0.3 *  thisCategoryDocsList.size());
            //System.out.println("testDataSize is " + testDataSize);
            
        	List<Integer> test_rand_list = new ArrayList<Integer>();

            for(int i = 0; i < testDataSize; i++) {
            	int rand = listShuffled.get(i);
            	test_rand_list.add(rand);
            	testThisCategoryDocsList.add(thisCategoryDocsList.get(rand));
            	
            }

            for(int i = testDataSize; i < thisCategoryDocsList.size(); i++) {
            	int rand = listShuffled.get(i);
            	if(test_rand_list.contains(rand)) {
            		System.out.println("Test set already contains this item!");
            	} else {
                	//System.out.print("This is random number : " + rand + " ");
                	//System.out.println("It will add this document to training set: " + thisCategoryDocsList.get(rand));
                	trainThisCategoryDocsList.add(thisCategoryDocsList.get(rand)); 	
            	}	
            }
            
        	nodeTest.setFatherid(taxonomy.get(catid).getFatherid());
            nodeTest.setPages(testThisCategoryDocsList);
            nodeTest.setCategory_topic(taxonomy.get(catid).getCategory_topic());
            nodeTest.setCatid(catid);
        	//System.out.println("Number of test documents in category is : " + nodeTest.getPagesTerms().size());
        	nodeTrain.setFatherid(taxonomy.get(catid).getFatherid());
        	nodeTrain.setPages(trainThisCategoryDocsList);
        	nodeTrain.setCategory_topic(taxonomy.get(catid).getCategory_topic());
        	nodeTrain.setCatid(catid);

        	taxonomyTest.put(catid, nodeTest);
        	taxonomyTrain.put(catid, nodeTrain);  

        }
        

        dataSets.put("trainData", taxonomyTrain);
		dataSets.put("testData", taxonomyTest);
        return dataSets;
	}
	
	 public static void lineNumberReader(File file)
	    {

	    	try{

	    		
	    		if(file.exists()){

	    		    FileReader fr = new FileReader(file);
	    		    LineNumberReader lnr = new LineNumberReader(fr);

	    		    int linenumber = 0;

	    	            while (lnr.readLine() != null){
	    	        	linenumber++;
	    	            }

	    	            System.out.println("Total number of lines : " + linenumber);

	    	            lnr.close();


	    		}else{
	    			 System.out.println("File does not exists!");
	    		}

	    	}catch(IOException e){
	    		e.printStackTrace();
	    	}

	    }
	 
	public static double macroF1(Map<String, List<Integer>> cosineSimC) {
		/*** macro F1 ***/
		double p = 0;
		double r = 0;
		double f1 = 0;
		double pAverage = 0;
		double rAverage = 0;
		double denominator;

		List<Integer> tpFnFpList;
		Map<String, Double> pMap = new HashMap<String, Double>();
		Map<String, Double> rMap = new HashMap<String, Double>();

		for (String catid : cosineSimC.keySet()) {
			tpFnFpList = cosineSimC.get(catid);
			assert (tpFnFpList.size() == 3);
			// because when / by 0 was retuning NaN
			denominator = (double) (tpFnFpList.get(0) + tpFnFpList.get(2));
			if (denominator == 0) {
				p = 0;
			} else {
				p = tpFnFpList.get(0) / denominator;
			}
			denominator = (double) (tpFnFpList.get(0) + tpFnFpList.get(1));
			if (denominator == 0) {
				r = 0;
			} else {
				r = tpFnFpList.get(0) / denominator;
			}
			pMap.put(catid, p);
			rMap.put(catid, r);
		}
		for (Double p_value : pMap.values()) {
			pAverage += p_value;
		}
		for (Double r_value : rMap.values()) {
			rAverage += r_value;
		}
		pAverage = pAverage / pMap.size();
		System.out.println("pMap size is : " + pMap.size());
		System.out.println("Precision average is : " + pAverage);
		rAverage = rAverage / rMap.size();
		System.out.println("rMap size is : " + rMap.size());
		System.out.println("Recall average is : " + rAverage);
		f1 = 2 * pAverage * rAverage / (pAverage + rAverage);

		return f1;

	}
	
	public static void computePagesDataSets(Map<String, NodeInfo> taxonomyTest, Map<String, NodeInfo> taxonomyTrain) throws SQLException{
		
        int id_test = 0;
		int id_train = 0;
		//FILES for training and testing pages
		/*PrintStream testPages = new PrintStream(new FileOutputStream(
		          "testPages_.txt"));
		PrintStream trainPages = new PrintStream(new FileOutputStream(
		          "trainPages_.txt"));*/
		
		ConnectDb.initPropertiesForSave();
		final boolean dbConnected = ConnectDb.checkConnection();
		if (!dbConnected) {
			System.exit(1);
		}

		System.out.println("Successfully connected to " + ConnectDb.getEngine().toUpperCase() + " database "
				+ ConnectDb.getDB() + "@" + ConnectDb.getHost() + " as user " + ConnectDb.getUsername());

		PreparedStatement statement_save;

        for(String catid : taxonomyTest.keySet()){
        	
        	for(PageNode page : taxonomyTest.get(catid).getPages())
        	{
        		String tokenizedPage = Arrays.toString(page.getTokenizedPage().toArray());
        		id_test = id_test+1;
        		
        		String query_set_test_pages = "INSERT INTO dmoz_pages_test (pages, catid, fatherid, pageid)"
        		        + " values (?, ?, ?, ?);";

        		statement_save = (PreparedStatement) ConnectDb.getConnection().prepareStatement(query_set_test_pages);
        		statement_save.setString(1,tokenizedPage);
        		statement_save.setString(2, catid);
        		statement_save.setString(3, taxonomyTest.get(catid).getFatherid());
        		statement_save.setString(4, page.get_id());

        		//statement_save.executeUpdate();
        		//testPages.println(tokenizedPage);

        	}
        	
        }

        for(String catid : taxonomyTrain.keySet()){
        	
        	for(PageNode page : taxonomyTrain.get(catid).getPages())
        	{
        		//System.out.println(Arrays.toString(page.getTokenizedPage().toArray()));
        		String tokenizedPage = Arrays.toString(page.getTokenizedPage().toArray());
        		id_train = id_train+1;
        		
        		String query_set_train_pages = "INSERT INTO dmoz_pages_train (pages, catid, fatherid, pageid)"
        		        + " values (?, ?, ?, ?);";

        		statement_save = (PreparedStatement) ConnectDb.getConnection().prepareStatement(query_set_train_pages);
        		statement_save.setString(1,tokenizedPage);
        		statement_save.setString(2, catid);
        		statement_save.setString(3, taxonomyTrain.get(catid).getFatherid());
        		statement_save.setString(4, page.get_id());

        		//statement_save.executeUpdate();
        		//trainPages.println(tokenizedPage);

        	}
        	
        }	
		//UNSELECT TO COMMIT
        //ConnectDb.getConnection().commit();
        //testPages.close();
        //trainPages.close();
		
		System.out.println("Number of rows should be added to test_pages: " + id_test);
		System.out.println("Number of rows should be added to train_pages: " + id_train);
		
		//File file =new File("c:\\ihave10lines.txt");
		File file_test_pages =new File("C:/Users/dinaraDILab/java_projects/ODP-Classifier/testPages.txt");
		File file_train_pages =new File("C:/Users/dinaraDILab/java_projects/ODP-Classifier/trainPages.txt");

		lineNumberReader(file_test_pages);
		lineNumberReader(file_train_pages);

	}
	
	public static void saveHeuristicsCategoriesToDB(Map<String, NodeInfo> taxonomyAllHeuristics) throws SQLException{
		
		ConnectDb.initPropertiesForSave();
		final boolean dbConnected2 = ConnectDb.checkConnection();
		if (!dbConnected2) {
			System.exit(1);
		}
		
		System.out.println("Successfully connected to " + ConnectDb.getEngine().toUpperCase() + " database "
				+ ConnectDb.getDB() + "@" + ConnectDb.getHost() + " as user " + ConnectDb.getUsername());
		PreparedStatement statement_save;
		
		for(String catid : taxonomyAllHeuristics.keySet()){
		        		
		        		String query_set_heuristics_categories = "INSERT INTO dmoz_categories_all (topic, catid, fatherid)"
		        		        + " values (?, ?, ?);";
		
		        		statement_save = (PreparedStatement) ConnectDb.getConnection().prepareStatement(query_set_heuristics_categories);
		        		statement_save.setString(1,taxonomyAllHeuristics.get(catid).getCategory_topic());
		        		statement_save.setString(2, catid);
		        		statement_save.setString(3, taxonomyAllHeuristics.get(catid).getFatherid());
		
		        		statement_save.executeUpdate();
		        
		        }
		//UNCOMMENT TO COMMIT
        //ConnectDb.getConnection().commit();
	
	}
	
	public static void computeCategoryDataSets(Map<Integer, NodeInfo> taxonomyAllHeuristics) throws SQLException, FileNotFoundException {
		 
		Map<Integer, NodeInfo> taxonomyTrain = new HashMap<Integer, NodeInfo>();
		Map<Integer, NodeInfo> taxonomyTest = new HashMap<Integer, NodeInfo>();
		Map<String, Map<String, NodeInfo>> dataSets = new HashMap<String, Map<String, NodeInfo>>();
		
		int id_test = 0;
		int id_train = 0;
		
		//FILES for train and test categories
		/*PrintStream testCategories = new PrintStream(new FileOutputStream(
		          "testCategories.txt"));
		PrintStream trainCategories = new PrintStream(new FileOutputStream(
		          "trainCategories.txt"));*/
		
		ConnectDb.initPropertiesForSave();
		final boolean dbConnected = ConnectDb.checkConnection();
		if (!dbConnected) {
			System.exit(1);
		}

		System.out.println("Successfully connected to " + ConnectDb.getEngine().toUpperCase() + " database "
				+ ConnectDb.getDB() + "@" + ConnectDb.getHost() + " as user " + ConnectDb.getUsername());

		PreparedStatement statement_save;
		 
        	NodeInfo nodeTest;
        	NodeInfo nodeTrain;
        	
        	List<Integer> listShuffled = uniqueRandNum(taxonomyAllHeuristics.size());
            Collections.shuffle(listShuffled);
            System.out.println("\tSHUFFLED LIST " + listShuffled);
            
        	int testDataSize = (int) (0.3 *  taxonomyAllHeuristics.size());
            //System.out.println("testDataSize is " + testDataSize);
            
        	List<Integer> test_rand_list = new ArrayList<Integer>();

            for(int i = 0; i < testDataSize; i++) {
            	int rand = listShuffled.get(i);
            	test_rand_list.add(rand);

            	//System.out.print("\tThis is random number : " + rand + " ");
            	nodeTest = new NodeInfo();
            	nodeTest.setFatherid(taxonomyAllHeuristics.get(rand).getFatherid());
                nodeTest.setCategory_topic(taxonomyAllHeuristics.get(rand).getCategory_topic());
                nodeTest.setCatid(taxonomyAllHeuristics.get(rand).getCatid());
                
            	taxonomyTest.put(rand, nodeTest);

            }

            for(int i = testDataSize; i < taxonomyAllHeuristics.size(); i++) {
            	int rand = listShuffled.get(i);
            	if(test_rand_list.contains(rand)) {
            		System.out.println("Test set already contains this item!");
            	} else {
            		nodeTrain = new NodeInfo();
            		nodeTrain.setFatherid(taxonomyAllHeuristics.get(rand).getFatherid());
            		nodeTrain.setCategory_topic(taxonomyAllHeuristics.get(rand).getCategory_topic());
            		nodeTrain.setCatid(taxonomyAllHeuristics.get(rand).getCatid());
                    
                	taxonomyTrain.put(rand, nodeTrain);
            	}	
            }
            

        System.out.println("TEST CATEGORIES NUMBER " + taxonomyTest.size());
        System.out.println("TRAIN CATEGORIES NUMBER " + taxonomyTrain.size());

        for(Integer id : taxonomyTest.keySet()){
        	
        		id_test = id_test+1;
        		
        		String query_set_heuristics_categories_test = "INSERT INTO dmoz_categories_test (topic, catid, fatherid)"
        		        + " values (?, ?, ?);";

        		statement_save = (PreparedStatement) ConnectDb.getConnection().prepareStatement(query_set_heuristics_categories_test);
        		statement_save.setString(1,taxonomyTest.get(id).getCategory_topic());
        		statement_save.setString(2, taxonomyTest.get(id).getCatid());
        		statement_save.setString(3, taxonomyTest.get(id).getFatherid());

        		statement_save.executeUpdate();
        		
        		List<String> tokenizedCateg = StringProcessingUtils.removeStemmedStopWords(taxonomyAllHeuristics.get(id).getCategory_topic());
        		//testCategories.println(tokenizedCateg);

        }

        for(Integer id : taxonomyTrain.keySet()){
        	
    		id_train = id_train+1;
    		
    		String query_set_heuristics_categories_train = "INSERT INTO dmoz_categories_train (topic, catid, fatherid)"
    		        + " values (?, ?, ?);";

    		statement_save = (PreparedStatement) ConnectDb.getConnection().prepareStatement(query_set_heuristics_categories_train);
    		statement_save.setString(1,taxonomyTrain.get(id).getCategory_topic());
    		statement_save.setString(2, taxonomyTrain.get(id).getCatid());
    		statement_save.setString(3, taxonomyTrain.get(id).getFatherid());

    		statement_save.executeUpdate();
    		List<String> tokenizedCateg = StringProcessingUtils.removeStemmedStopWords(taxonomyAllHeuristics.get(id).getCategory_topic());
    		//trainCategories.println(tokenizedCateg);
    }
		//UNSELECT TO COMMIT
        //ConnectDb.getConnection().commit();
        //testCategories.close();
        //trainCategories.close();
		
		System.out.println("Number of rows should be added to test_categories: " + id_test);
		System.out.println("Number of rows should be added to train_categories: " + id_train);
		
		File file_test_categ =new File("C:/Users/dinaraDILab/java_projects/ODP-Classifier/testCategories.txt");
		File file_train_categ =new File("C:/Users/dinaraDILab/java_projects/ODP-Classifier/trainCategories.txt");

		lineNumberReader(file_test_categ);
		lineNumberReader(file_train_categ);

	}
	
	public static void saveAllCategToDB(Map<String, NodeInfo> taxonomyAllHeuristics) throws SQLException, FileNotFoundException {
	
		int id = 0;
		//FILE for all categories
		//PrintStream allCategoriesClean = new PrintStream(new FileOutputStream(
		//          "allCategoriesClean.txt"));
		
		
		ConnectDb.initPropertiesForSave();
		final boolean dbConnected = ConnectDb.checkConnection();
		if (!dbConnected) {
			System.exit(1);
		}

		System.out.println("Successfully connected to " + ConnectDb.getEngine().toUpperCase() + " database "
				+ ConnectDb.getDB() + "@" + ConnectDb.getHost() + " as user " + ConnectDb.getUsername());
		PreparedStatement statement_save;

        for(String catid : taxonomyAllHeuristics.keySet()){
        	
        		id = id+1;
        		
        		String query_set_all_categories = "INSERT INTO dmoz_categories_all_clean (topic, catid, fatherid)"
        		        + " values (?, ?, ?);";
        		List<String> tokenizedCateg = StringProcessingUtils.removeStemmedStopWords(taxonomyAllHeuristics.get(catid).getCategory_topic());
        		String tokenizedCategDB = Arrays.toString(tokenizedCateg.toArray());

        		statement_save = (PreparedStatement) ConnectDb.getConnection().prepareStatement(query_set_all_categories);
        		statement_save.setString(1,tokenizedCategDB);
        		statement_save.setString(2, catid);
        		statement_save.setString(3, taxonomyAllHeuristics.get(catid).getFatherid());

        		statement_save.executeUpdate();
        		
        		//allCategoriesClean.println(tokenizedCateg);

        }


		//UNSELECT TO COMMIT
        //ConnectDb.getConnection().commit();
        //allCategoriesClean.close();
		
		System.out.println("Number of rows should be added to categories: " + id);
		
		File file_test_categ =new File("C:/Users/dinaraDILab/java_projects/ODP-Classifier/allCategoriesClean.txt");

		lineNumberReader(file_test_categ);

	}
	
	public static List<Double> listOfStringToListOfDoubles(List<String> str_list){
		
		List<Double> listOfDoubles = new ArrayList<Double>();
		double num;
        for(String str: str_list) {
        	try {
            	num = Double.parseDouble(str);
        	} catch (NumberFormatException nfe) {
        		System.out.println("Couldn't parse row to double, skipping " + nfe);
        		continue;
        	}
        	//System.out.println(num);
        	listOfDoubles.add(num);
        }
        //System.out.println(listOfDoubles.size());
		return listOfDoubles;
	}
	
	public static Map<String, NodeInfo> csvToHashMap(String csvFile) throws FileNotFoundException{
		NodeInfo nodeTrain;
		PageNode page;
		Map<String, NodeInfo> trainDataSetHeuristicsByCateg = new HashMap<String, NodeInfo>();
		//Map<String, List<PageNode>> thisCategoryPagesMap = new HashMap<String, List<PageNode>>();
		Map<String, NodeInfo> thisCategoryPagesMap = new HashMap<String, NodeInfo>();

		String currentCategory;
		//String csvFile = "C:/Users/dinaraDILab/word2vec/trainDataVecs_csv_file.csv";

        Scanner scanner = new Scanner(new File(csvFile));
        scanner.nextLine();
        while (scanner.hasNext()) {
            List<String> line = CSVUtils.parseLine(scanner.nextLine());
            //System.out.println("Document [id= " + line.get(0) + ", catid= " + line.get(1) + " , pages=" + line.get(3) + "\"" +"]");
        	
        	page = new PageNode();
			currentCategory = line.get(1);
			page.set_id(line.get(4));//get pageid to match 'id' field of dmoz_external_pages IMPORTANT!
            page.setCatid(line.get(1));
            page.setTokenizedPage(StringProcessingUtils.tokenizeString(line.get(3)));
            page.setFatherid(line.get(2));
            
        	List<String> str_list = StringProcessingUtils.tokenizeStringBySpace(line.get(5));
			page.setWord2VecVectors(listOfStringToListOfDoubles(str_list));

            
        	if (thisCategoryPagesMap.containsKey(currentCategory)) {
	    		//thisCategoryPagesMap.get(currentCategory).add(page);
				thisCategoryPagesMap.get(currentCategory).getPages().add(page);

			} else {
				List<PageNode> allCategoryPages = new ArrayList<PageNode>();
				allCategoryPages.add(page);
				
				NodeInfo categNode = new NodeInfo();
				categNode.setFatherid(page.getFatherid());
				categNode.setPages(allCategoryPages);
				//thisCategoryPagesMap.put(currentCategory, allCategoryPages);
				thisCategoryPagesMap.put(currentCategory, categNode);

			}
        }
        scanner.close();
        
        for(String catid :thisCategoryPagesMap.keySet()){
        	//System.out.println("Category " + catid + " has" + " " + thisCategoryPagesMap.get(catid));
        	nodeTrain = new NodeInfo();
        	nodeTrain.setCatid(catid);
			nodeTrain.setFatherid(thisCategoryPagesMap.get(catid).getFatherid()); //added this
        	nodeTrain.setPages(thisCategoryPagesMap.get(catid).getPages());
        	//nodeTrain.setCategory_topic();
        	
        	trainDataSetHeuristicsByCateg.put(catid, nodeTrain);
        }
			
        //for(String catid :trainDataSetHeuristicsByCateg.keySet()){
        	//System.out.println("Category " + catid + " has" + " " + trainDataSetHeuristicsByCateg.get(catid));
        //}
		return trainDataSetHeuristicsByCateg;
	}
	
	public static void runCentroidsFromCSV() throws IOException{
		
		
		Map<String, NodeInfo> trainDataSetHeuristicsByCateg = new HashMap<String, NodeInfo>();
		Map<String, NodeInfo> trainDataSetByCateg = new HashMap<String, NodeInfo>();
		Map<String, NodeInfo> testDataSet = new HashMap<String, NodeInfo>();
		Map<String, NodeInfo> testDataSetNew = new HashMap<String, NodeInfo>();

		String csvFileTrain = "C:/Users/dinaraDILab/word2vec/trainDataVecs_google_news - nan_row_deleted.csv";
		//String csvFileTrain_bigSet = "C:/Users/dinaraDILab/word2vec/table_pages_train_bigset.csv";

		String csvFileTest = "C:/Users/dinaraDILab/word2vec/testDataVecs_google_news.csv";

		System.out.println("Started read train data from csv");
		trainDataSetHeuristicsByCateg = csvToHashMap(csvFileTrain);
		//trainDataSetByCateg = csvToHashMap(csvFileTrain_bigSet);
		System.out.println("Finished read train data from csv");

		System.out.println("Started read test data from csv");
		testDataSet = csvToHashMap(csvFileTest);
		System.out.println("Finished read test data from csv");

		//from txt file, will need to read to trainDataSetHeuristics and testDataSetHeuristics data structures
		//InvertedIndex invIndexTrain = invertedIndexTrainData(trainDataSetHeuristicsByCateg);
		//Map<String, Centroid> centroidMap = runCentroidsPart(trainDataSetHeuristicsByCateg, invIndexTrain);
		
		System.out.println("\tNow calculating centroids");
		Map<String, Word2VecCentroid> centroidMap = runCentroidsWord2VecPart(trainDataSetHeuristicsByCateg);

		//testDataSetNew = applyTestHeuristics(testDataSet);
		
		//Map<String, List<Map<String, Double>>> vectorsTestDataSet = computeVectorsTestDataSet(testDataSet);
		
		/* centroids evaluation */
		//Map<String, Centroid> centroidMapHeuristics = applyHeuristicsToCentroids(centroidMap, testDataSet);
		System.out.println("\tNow computing cosine similarity");
		Map<String, List<Integer>> cosineSimC = computeCosineSimCWord2Vec(testDataSet, centroidMap);
		System.out.println("\tNow evaluating");
		evalCentroids(cosineSimC);
		
		/* m - centroids evaluation */
		
		Map<String, Word2VecCentroid> mergeCentroidMap = makeMergeCentroidsWord2Vec(trainDataSetHeuristicsByCateg);

		//Map<String, Centroid> centroidMapHeuristics = applyHeuristicsToCentroids(centroidMap, testDataSet);
		//Map<String, List<Integer>> cosineSimC = computeCosineSimC(vectorsTestDataSet, centroidMap);
		//evalCentroids(cosineSimC);
		
	}
	
		public static void main(final String[] args) throws SQLException, IOException {

		long millis = System.currentTimeMillis();
		long period = 0;
		
		System.out.println("Starting from main..");
		runCentroidsFromCSV();
		
		period = System.currentTimeMillis() - millis;
		System.out.println("Forming centroid and evaluating took " + period + "ms");
		millis = System.currentTimeMillis();
		}
		
		
	public static void makeMergeCentroids(Map<String, Centroid> centroids, Map<String, NodeInfo> taxonomyAll,
				Map<String, NodeInfo> testDataSet, Map<String, List<Map<String, Double>>> vectorsTestDataSet) {
			/*** Hierarchy ***/
			long millis = System.currentTimeMillis();
			DocumentParser dp = new DocumentParser();
			CategoryTree categoryTree = dp.makeHieararchy(taxonomyAll);
			Map<String, List<String>> tree = categoryTree.hierarchy;
			long period = System.currentTimeMillis() - millis;
			System.out.println("computing hierarchy took " + period + "ms");
			//System.out.println("children of node 0 are: " + tree.get("0"));*
			
			/*** Merge-centroid calculation ***/
			
			// Map<String, Double> catMergeCentroid =
			// dp.makeMergeCentroid(categoryTree, "377116", centroidMap);
			
			millis = System.currentTimeMillis();
			Map<String, Centroid> mergeCentroids = new HashMap<String, Centroid>();
			dp.makeMergeCentroid(categoryTree, "0", centroids, mergeCentroids);
			System.out.println("Merge centroid have " + mergeCentroids.size() + " classes");
			period = System.currentTimeMillis() - millis;
			System.out.println("computing merge centroid for root 0 took " + period + "ms");
			
			/* merge-centroid evaluation */
			Map<String, Centroid> mergeCentroidMapHeuristics = applyHeuristicsToCentroids(mergeCentroids, testDataSet);
			Map<String, List<Integer>> cosineSimMC = computeCosineSimC(vectorsTestDataSet, mergeCentroidMapHeuristics);
			evalCentroids(cosineSimMC);
			
		}

	public static void makeMergeCentroidsWord2Vec(Map<String, Word2VecCentroid> centroids, Map<String, NodeInfo> taxonomyAll,
			Map<String, NodeInfo> testDataSet, Map<String, NodeInfo> vectorsTestDataSet) {
		/*** Hierarchy ***/
		long millis = System.currentTimeMillis();
		DocumentParser dp = new DocumentParser();
		CategoryTree categoryTree = dp.makeHieararchy(taxonomyAll);
		Map<String, List<String>> tree = categoryTree.hierarchy;
		long period = System.currentTimeMillis() - millis;
		System.out.println("computing hierarchy took " + period + "ms");
		//System.out.println("children of node 0 are: " + tree.get("0"));*
		
		/*** Merge-centroid calculation ***/
		
		// Map<String, Double> catMergeCentroid =
		// dp.makeMergeCentroid(categoryTree, "377116", centroidMap);
		
		millis = System.currentTimeMillis();
		Map<String, Word2VecCentroid> mergeCentroids = new HashMap<String, Word2VecCentroid>();
		dp.makeMergeCentroidWord2Vec(categoryTree, "0", centroids, mergeCentroids);
		System.out.println("Merge centroid have " + mergeCentroids.size() + " classes");
		period = System.currentTimeMillis() - millis;
		System.out.println("computing merge centroid for root 0 took " + period + "ms");
		
		/* merge-centroid evaluation */
		//Map<String, Word2VecCentroid> mergeCentroidMapHeuristics = applyHeuristicsToCentroids(mergeCentroids, testDataSet);
		Map<String, List<Integer>> cosineSimMC = computeCosineSimCWord2Vec(vectorsTestDataSet, mergeCentroids);
		evalCentroids(cosineSimMC);
		
	}
	
	public static Map<String, NodeInfo> buildTrainSet(Map<String, NodeInfo> taxonomyAll,
			Map<String, NodeInfo> taxonomyTest) {
		Map<String, NodeInfo> taxonomyTrain = new HashMap<String, NodeInfo>();
		for (String catid : taxonomyAll.keySet()) {
			List<PageNode> trainPagesTerms = new ArrayList<PageNode>();
			NodeInfo node = new NodeInfo();
			Set<String> testIds = getPageIds(taxonomyTest.get(catid));
			for (PageNode doc : taxonomyAll.get(catid).getPages()) {
				if (taxonomyTest.containsKey(catid)) {
					if (!testIds.contains(doc.get_id())) {
						trainPagesTerms.add(doc);
					}
				} else {
					trainPagesTerms.add(doc);
				}
			}
			node.setPages(trainPagesTerms);
			node.setFatherid(taxonomyAll.get(catid).getFatherid());
			node.setCatid(catid);
			node.setCategory_topic(taxonomyAll.get(catid).getCategory_topic());
			taxonomyTrain.put(catid, node);
		}
		return taxonomyTrain;
	}
	
	/*this function is to get train Pages like this: all ODP pages - pages from dmoz_pages_test table in dmoz2106_heuristics database*/
	public static Map<String, NodeInfo> buildTrainSet2(Map<String, NodeInfo> taxonomyAll,
			Map<String, NodeInfo> taxonomyTest) {
		Map<String, NodeInfo> taxonomyTrain = new HashMap<String, NodeInfo>();
		for (String catid : taxonomyAll.keySet()) {
			List<PageNode> trainPagesTerms = new ArrayList<PageNode>();
			NodeInfo node = new NodeInfo();
			Set<String> testIds = getPageIds(taxonomyTest.get(catid));
			for (PageNode doc : taxonomyAll.get(catid).getPages()) {
				if (taxonomyTest.containsKey(catid)) {
					if (!testIds.contains(doc.get_id())) {
						trainPagesTerms.add(doc);
					}
				} else {
					trainPagesTerms.add(doc);
				}
			}
			node.setPages(trainPagesTerms);
			node.setCategory_topic(taxonomyAll.get(catid).getCategory_topic());
			taxonomyTrain.put(catid, node);
		}
		return taxonomyTrain;
	}
	
	private static Set<String> getPageIds(NodeInfo node) {
			Set<String> idsList = new HashSet<String>();
			if (node == null) {
				return idsList;
			}
			List<PageNode> pages = node.getPages();
			if (pages == null) {
				return idsList;
			}
			for (PageNode page: pages) {
				idsList.add(page.get_id());
			}
			return idsList;
		}

		public static InvertedIndex invertedIndexTrainData(Map<String, NodeInfo> trainDataSet) throws IOException{
			long millis = System.currentTimeMillis();
			long period = 0;
			
			/*** Index - train***/
			millis = System.currentTimeMillis();
			InvertedIndex invIndexTrain = new InvertedIndex(trainDataSet);
			period = System.currentTimeMillis() - millis;
			System.out.println("\nforming index for trainData took " + period + "ms");
			System.out.println("trainData index size is " + invIndexTrain.getAllDocsSize());
			
			return invIndexTrain;
		}
		
		public static Map<String, List<Map<String, Double>>> computeVectorsTestDataSet(Map<String, NodeInfo> testDataSet) throws IOException{
			long millis = System.currentTimeMillis();
			long period = 0;
			
			/*** Index - test***/
			millis = System.currentTimeMillis();
			InvertedIndex invIndexTest = new InvertedIndex(testDataSet);
			period = System.currentTimeMillis() - millis;
			System.out.println("\nforming index for testData took " + period + "ms");
			System.out.println("testData index size is " +invIndexTest.getAllDocsSize() + "\n");
			
			/*** TfIdfs of documents from test data for testing ***/
			millis = System.currentTimeMillis();
			Map<String, List<Map<String, Double>>> CategoryTfidfDocsVectorsMap = computeCategoryTfidfDocsVectors(testDataSet, invIndexTest);
			period = System.currentTimeMillis() - millis;
			System.out.println("computing CategoryTfidfDocsVectors took " + period + "ms");
			
			return CategoryTfidfDocsVectorsMap;
		}
		
		
		public static void evalCentroids (Map<String, List<Integer>> cosineSimC){
			
			long millis = System.currentTimeMillis();
			long period = 0;
			
			/*** macro F1 ***/
			millis = System.currentTimeMillis();
			double f1Centroid = macroF1(cosineSimC);
			System.out.println("Macro F1 for centroid is " + f1Centroid);
			period = System.currentTimeMillis() - millis;
			System.out.println("computing f1 took " + period + "ms");

			/*** micro F1 ***/
			
			 millis = System.currentTimeMillis();
			 int tpHit = 0;
			 int fnMiss = 0;
			 int fp = 0; 
			 List<Integer> tpFnFpList;
			 for(String catid: cosineSimC.keySet()) {
				tpFnFpList = cosineSimC.get(catid);
				//System.out.println("Category " + catid + " TP: " + tpFnFpList.get(0) + "  FN : " + tpFnFpList.get(1) + "  FP: " + tpFnFpList.get(2));
				tpHit += tpFnFpList.get(0);
				fnMiss += tpFnFpList.get(1);
				fp += tpFnFpList.get(2);
			}
			period = System.currentTimeMillis() - millis;
			System.out.println("computing number of hits and misses took " + period + "ms");
			System.out.println("Number of all hits TP is : " + tpHit);
			System.out.println("Number of all misses FN is : " + fnMiss);
			System.out.println("Number of all misses FP is : " + fp);
			
			/*for(String item : cosineSim.keySet()){
				System.out.println("Current CatId is: " + item + " Assigned CatId List is : " + cosineSim.get(item));
			}*/
		}
		
		public static Map<String, Centroid> applyHeuristicsToCentroids(Map<String, Centroid> centroidMap,
				Map<String, NodeInfo> testData) {
			/*** Apply heuristics to get only heuristics centroids ***/
			Map<String, Centroid> centroidsMapHeuristics = new HashMap<>();
			for (String catId: testData.keySet()) {
				centroidsMapHeuristics.put(catId, centroidMap.get(catId));
			}
			return centroidsMapHeuristics;
		}
		

		public static Map<String, List<Integer>> computeCosineSimC (Map<String, List<Map<String, Double>>>  CategoryTfidfDocsVectorsMap, Map<String, Centroid> centroidMapHeuristics)
		{
			long millis = System.currentTimeMillis();
			long period = 0;
			/*** Cosine similarity for centroids ***/
			millis = System.currentTimeMillis();
			Map<String, List<Integer>> cosineSimC = computeCosineSim(CategoryTfidfDocsVectorsMap, centroidMapHeuristics);
			period = System.currentTimeMillis() - millis;
			System.out.println("computing cosineSimilarity for centroids took " + period + "ms");
			
			return cosineSimC;
		}
		
		public static Map<String, List<Integer>> computeCosineSimCWord2Vec (Map<String, NodeInfo>  testDataNodes, Map<String, Word2VecCentroid> centroidMap) {
			long millis = System.currentTimeMillis();
			long period = 0;
			/*** Cosine similarity for centroids ***/
			millis = System.currentTimeMillis();
			Map<String, List<Integer>> cosineSimC = computeCosineSimWord2Vec(testDataNodes, centroidMap);
			period = System.currentTimeMillis() - millis;
			System.out.println("computing cosineSimilarity for centroids took " + period + "ms");
			
			return cosineSimC;
		}

		public static Map<String, Centroid> runCentroidsPart(Map<String, NodeInfo> trainDataSet, InvertedIndex invIndexTrain) throws IOException {
			long millis = System.currentTimeMillis();
			long period = 0;
			
			/*** Centroids ***/
			millis = System.currentTimeMillis();
			Map<String, Centroid> centroidMap = computeCentroids(trainDataSet, invIndexTrain);

			period = System.currentTimeMillis() - millis;
			System.out.println("computing centroids took " + period + "ms");
			return centroidMap;
			
		}
		
		public static Map<String, Word2VecCentroid> runCentroidsWord2VecPart(Map<String, NodeInfo> trainDataSet) throws IOException {
			long millis;
			long period = 0;
			
			millis = System.currentTimeMillis();
			Map<String, Word2VecCentroid> centroidMap = computeCentroidsWord2Vec(trainDataSet);

			period = System.currentTimeMillis() - millis;
			System.out.println("computing centroids took " + period + "ms");
			return centroidMap;
			
		}
		
}
