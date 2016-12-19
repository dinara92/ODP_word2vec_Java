package tfidfDocument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import centroid.Centroid;
import centroid.Word2VecCentroid;
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
			cosineSim.clear();
			for (int i = 0; i < doc.getWord2VecVectors().size(); i++) {
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
}
