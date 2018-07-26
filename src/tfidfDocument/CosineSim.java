package tfidfDocument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import centroid.Centroid;
import centroid.Word2VecCentroid;
import node_info.NodeInfo;
import page_node.PageNode;

public class CosineSim {

	
	public String maxCosineSimilarity (Map<String, Double> categoryTfidfDocsVector, Map<String, Centroid> categoryCentroids) {
		Map<String, Double> cosineSim = new HashMap<String, Double>();
		double cosineSimSum = 0;
		Map<String, Double> cosineSimSumsMap = new HashMap<String, Double>();
		String assignedCatId = null;
		
		double vec_length = 0;
		//double centroid_length;

		for (String vec : categoryTfidfDocsVector.keySet()) {
			double tfidf_vec = categoryTfidfDocsVector.get(vec);
			vec_length += Math.pow(tfidf_vec, 2);
		}
		double normV = Math.sqrt(vec_length);
		
		for(String catid : categoryCentroids.keySet()) {
			Map<String, Double> centroid = categoryCentroids.get(catid).getCentroid();
			double centroid_lengthNorm = categoryCentroids.get(catid).getCentroid_lengthNorm();
			
			cosineSimSum = 0;
			cosineSim.clear();
			for (String term : categoryTfidfDocsVector.keySet()) {
					if (centroid.containsKey(term)) {
						//System.out.println("This is centroid to be multiplied : " + centroid.get(term));
						//System.out.println("This is document vector to be multiplied : " + categoryTfidfDocsVector.get(term));
						cosineSim.put(term, centroid.get(term) * (double) categoryTfidfDocsVector.get(term));
					} else {
						//cosineSim.put(term, (double) 0);
					}
			}
			for (double value: cosineSim.values()) {
				cosineSimSum += value;
			}
			
			/*centroid_length = 0;
			
			for (String vec : centroid.keySet()) {
				double tfidf_vec = centroid.get(vec);
				centroid_length += Math.pow(tfidf_vec, 2);
			}
			double normC = Math.sqrt(centroid_length);*/
			double normC = centroid_lengthNorm;
			
			
			/*** add all cosineSimilarities to Map, in which each catid corresponds to a cosineSimilarity ***/
			cosineSimSumsMap.put(catid, cosineSimSum /(double)(normV * normC));
		}
		/*** get the cosineSimMax - most similar to category corresponding to cosineSimMax ***/
		double cosineSimMax = Collections.max(cosineSimSumsMap.values());
		//System.out.println("Max cos similarity is " + cosineSimMax);
		
		/*** get catId of the cosineSimMax - most similar ***/
		 for (Entry<String, Double> entry : cosineSimSumsMap.entrySet()) {
			 int diff = Double.compare(entry.getValue(), cosineSimMax);
	            if (diff == 0) {
	                //System.out.println("This is cosineSimMax : " + entry.getValue() + " with category " + entry.getKey() + " is equal to above");
	            	assignedCatId = entry.getKey();
	            	break;
	            }
		 }
		 return assignedCatId;
	}
	
	
	public static void computeCosineSimWord2VecForEntity (Map<String, PageNode>  entityMap, Map<String, Word2VecCentroid> centroidMap){
		
		CosineSim cosineSim = new CosineSim();
		List<String> returnedEntities = new ArrayList<String>();

		String assignedEntity;
		String assignedCatid;


			/*for(String catid : centroidMap.keySet()) {
				assignedEntity = cosineSim.maxCosineSimilarityWord2VecEntity(catid, centroidMap.get(catid), entityMap);		
				returnedEntities.add(assignedEntity);
			}*/
			
			for(String entity : entityMap.keySet()) {
				assignedCatid = cosineSim.maxCosineSimilarityWord2VecCategoriesForEntity(entityMap.get(entity), centroidMap);		
				returnedEntities.add(assignedCatid);
			}
			

	}
	
	public String maxCosineSimilarityWord2Vec (PageNode doc, Map<String, Word2VecCentroid> categoryCentroids) {
		List<Double> cosineSim = new ArrayList<Double>();
		double cosineSimSum = 0;
		Map<String, Double> cosineSimSumsMap = new HashMap<String, Double>();
		String assignedCatId = null;
		
		double vec_length = 0;
		//double centroid_length;

		for (Double value : doc.getWord2VecVectors()) {
			vec_length += Math.pow(value, 2);
		}
		double normV = Math.sqrt(vec_length);
		
		for(String catid : categoryCentroids.keySet()) {
			List<Double> centroid = categoryCentroids.get(catid).getCentroid();
			if (centroid.isEmpty()) {
				System.out.println("Centroid for category " + catid + " is empty, skipping cosine similarity calculation");
				continue;
			}
			double centroid_lengthNorm = categoryCentroids.get(catid).getCentroid_lengthNorm();
			
			cosineSimSum = 0;
			//cosineSim.clear();
			for (int i = 0; i < doc.getWord2VecVectors().size(); i++) {
				if (doc.getWord2VecVectors().isEmpty()) {
					System.out.println("Warning! test document is empty");
					continue;
				}
				if (cosineSim.size() <= i) {
					cosineSim.add(0.0);
				}
				cosineSim.set(i, doc.getWord2VecVectors().get(i) * centroid.get(i));
			}
			for (double value: cosineSim) {
				cosineSimSum += value;
			}
			
			/*centroid_length = 0;
			
			for (String vec : centroid.keySet()) {
				double tfidf_vec = centroid.get(vec);
				centroid_length += Math.pow(tfidf_vec, 2);
			}
			double normC = Math.sqrt(centroid_length);*/
			double normC = centroid_lengthNorm;
			
			
			/*** add all cosineSimilarities to Map, in which each catid corresponds to a cosineSimilarity ***/
			cosineSimSumsMap.put(catid, cosineSimSum /(double)(normV * normC));
		}
		/*** get the cosineSimMax - most similar to category corresponding to cosineSimMax ***/
		double cosineSimMax = Collections.max(cosineSimSumsMap.values());
		//System.out.println("Max cos similarity is " + cosineSimMax);
		
		/*** get catId of the cosineSimMax - most similar ***/
		 for (Entry<String, Double> entry : cosineSimSumsMap.entrySet()) {
			 int diff = Double.compare(entry.getValue(), cosineSimMax);
	            if (diff == 0) {
	                //System.out.println("This is cosineSimMax : " + entry.getValue() + " with category " + entry.getKey() + " is equal to above");
	            	assignedCatId = entry.getKey();
	            	break;
	            }
		 }
		 return assignedCatId;
	}
	
	public String maxCosineSimilarityWord2VecEntity (String catid, Word2VecCentroid categoryCentroid, Map<String, PageNode>  entityMap) {
		List<Double> cosineSim = new ArrayList<Double>();
		double cosineSimSum = 0;
		Map<String, Double> cosineSimSumsMap = new HashMap<String, Double>();
		String assignedCatId = null;
	
		
		List<Double> centroid = categoryCentroid.getCentroid();
		
		double centroid_lengthNorm = categoryCentroid.getCentroid_lengthNorm();
		double normC = centroid_lengthNorm;

		if (centroid.isEmpty()) {
			System.out.println("Centroid for category " + catid + " is empty, skipping cosine similarity calculation");
		}
		else{
			
		
		for(String entity : entityMap.keySet()) {
			
			double vec_length = 0;
			for (Double value : entityMap.get(entity).getWord2VecVectors()) {
				vec_length += Math.pow(value, 2);
			}
			double normV = Math.sqrt(vec_length);

			cosineSimSum = 0;
			//cosineSim.clear();
			for (int i = 0; i < entityMap.get(entity).getWord2VecVectors().size(); i++) {
				if (entityMap.get(entity).getWord2VecVectors().isEmpty()) {
					System.out.println("Warning! test document is empty");
					continue;
				}
				if (cosineSim.size() <= i) {
					cosineSim.add(0.0);
				}
				cosineSim.set(i, entityMap.get(entity).getWord2VecVectors().get(i) * centroid.get(i));
			}
			for (double value: cosineSim) {
				cosineSimSum += value;
			}
			
			/*** add all cosineSimilarities to Map, in which each catid corresponds to a cosineSimilarity ***/
			cosineSimSumsMap.put(entity, cosineSimSum /(double)(normV * normC));
		}
			
        Map<String, Double> sortedMap = sortByValue(cosineSimSumsMap);
        //System.out.println("\tFor catid : " + catid);
        printMap2(sortedMap, catid);
		
		/*** get the cosineSimMax - most similar to category corresponding to cosineSimMax ***/
		//double cosineSimMax = Collections.max(cosineSimSumsMap.values());
		
		/*** get catId of the cosineSimMax - most similar ***/
		 /*for (Entry<String, Double> entry : cosineSimSumsMap.entrySet()) {
			 int diff = Double.compare(entry.getValue(), cosineSimMax);
	            if (diff == 0) {
	                System.out.println("This is cosineSimMax : " + entry.getValue() + " of catid: " + catid + " with entity " + entry.getKey() + " is equal to above");
	            	assignedCatId = entry.getKey();
	            	break;
	            }
		 }*/
		 
			}
		 return assignedCatId;
	}
	
	public String maxCosineSimilarityWord2VecCategoriesForEntity (PageNode doc, Map<String, Word2VecCentroid> categoryCentroids) {
		List<Double> cosineSim = new ArrayList<Double>();
		double cosineSimSum = 0;
		Map<String, Double> cosineSimSumsMap = new HashMap<String, Double>();
		String assignedCatId = null;
		
		double vec_length = 0;
		//double centroid_length;

		for (Double value : doc.getWord2VecVectors()) {
			vec_length += Math.pow(value, 2);
		}
		double normV = Math.sqrt(vec_length);
		
		for(String catid : categoryCentroids.keySet()) {
			List<Double> centroid = categoryCentroids.get(catid).getCentroid();
			if (centroid.isEmpty()) {
				System.out.println("Centroid for category " + catid + " is empty, skipping cosine similarity calculation");
				continue;
			}
			double centroid_lengthNorm = categoryCentroids.get(catid).getCentroid_lengthNorm();
			
			cosineSimSum = 0;
			//cosineSim.clear();
			for (int i = 0; i < doc.getWord2VecVectors().size(); i++) {
				if (doc.getWord2VecVectors().isEmpty()) {
					System.out.println("Warning! test document is empty");
					continue;
				}
				if (cosineSim.size() <= i) {
					cosineSim.add(0.0);
				}
				cosineSim.set(i, doc.getWord2VecVectors().get(i) * centroid.get(i));
			}
			for (double value: cosineSim) {
				cosineSimSum += value;
			}
			
			/*centroid_length = 0;
			
			for (String vec : centroid.keySet()) {
				double tfidf_vec = centroid.get(vec);
				centroid_length += Math.pow(tfidf_vec, 2);
			}
			double normC = Math.sqrt(centroid_length);*/
			double normC = centroid_lengthNorm;
			
			
			/*** add all cosineSimilarities to Map, in which each catid corresponds to a cosineSimilarity ***/
			cosineSimSumsMap.put(catid, cosineSimSum /(double)(normV * normC));
		}
		/*** get the cosineSimMax - most similar to category corresponding to cosineSimMax ***/
		//double cosineSimMax = Collections.max(cosineSimSumsMap.values());
		//System.out.println("Max cos similarity is " + cosineSimMax);
		
		//System.out.println("\nSorted Map......By Value");
        Map<String, Double> sortedMap = sortByValue(cosineSimSumsMap);
        //System.out.println("\tFor entity : " + doc.get_id());
        printMap(sortedMap, doc.get_id());
        
		/*** get catId of the cosineSimMax - most similar ***/
		 /*for (Entry<String, Double> entry : cosineSimSumsMap.entrySet()) {
			 int diff = Double.compare(entry.getValue(), cosineSimMax);
	            if (diff == 0) {
	                System.out.println("This is cosineSimMax : " + entry.getValue() + " of entity: " + doc.get_id() + " with category " + entry.getKey() + " is equal to above");
	            	assignedCatId = entry.getKey();
	            	break;
	            }
		 }*/
		 return assignedCatId;
	}
	
    private static Map<String, Double> sortByValue(Map<String, Double> unsortMap) {

        // 1. Convert Map to List of Map
        List<Map.Entry<String, Double>> list =
                new LinkedList<Map.Entry<String, Double>>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
        for (Map.Entry<String, Double> entry : list) {
        	//if(entry.getValue()<0)
        		sortedMap.put(entry.getKey(), entry.getValue());
        }


        return sortedMap;
    }
    
    public static <K, V> void printMap(Map<K, V> map, String entity) {
    	int i = 0;
        //System.out.println("Is entity : " + entity);

    	if(entity.equals("db_Java") || entity.equals("db_Vase") || entity.equals("db_Bird") || entity.equals("db_DNA")
    			|| entity.equals("db_Apple_Inc") || entity.equals("db_Apple") || entity.equals("db_Fedora")){
            System.out.println("\tFor entity : " + entity);

	        for (Map.Entry<K, V> entry : map.entrySet()) {
	        	i++;
	            System.out.println("Key : " + entry.getKey()
	                    + " Value : " + entry.getValue());
	            if(i>11)
	            	break;
	        }
    	}
    }
    
    public static <K, V> void printMap2(Map<K, V> map, String catid) {
    	int i = 0;
        //System.out.println("Is entity : " + entity);

    	if(catid.equals("380930") || catid.equals("423767") || catid.equals("961556")){  	
            System.out.println("\tFor catid : " + catid);

	        for (Map.Entry<K, V> entry : map.entrySet()) {
	        	i++;
	            System.out.println("Key : " + entry.getKey()
	                    + " Value : " + entry.getValue());
	            if(i>50)
	            	break;
	        }
    	}
    }
}
