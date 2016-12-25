/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tfidfDocument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import centroid.Centroid;
import centroid.Word2VecCentroid;
import inverted_index.InvertedIndex;
import node_info.CategoryTree;
import node_info.NodeInfo;
import page_node.PageNode;

/**
 * Class to read documents
 *
 */
public class DocumentParser {
	
	public List<Map<String, Double>> tfIdfCalculator2(List<PageNode> pages, InvertedIndex index)
			throws IOException {
		double tf; // term frequency
		double idf; // inverse document frequency
		double tf_idf = 0; // term requency inverse document frequency
		TfIdf tfIdfClass = new TfIdf();
		List<Map<String, Double>> tfidfDocsMap = new ArrayList<Map<String, Double>>();

		for (PageNode page : pages) {
			// System.out.println("Now calculating each docTermsArray for " +
			// docTermsArray.toString() + " with size " + docTermsArray.size());
			//System.out.println("calculating tfidf for " + docTermsArray);
			Map<String, Double> tfidfMap = new HashMap<String, Double>();

			for (String term : page.getTokenizedPage()) {
				tf = tfIdfClass.tfCalculator(page.getTokenizedPage(), term);
				idf = tfIdfClass.idfCalculatorNew(term, index);
				tf_idf = tf * idf;
				// vec_length = Math.pow(tfidfvectors[count], 2);
				/*** Normalize vectors ***/
				// TODO: normalize vectors, incorrect now
				// tfidfMap.put(term, tf_idf/(Math.sqrt(vec_length)));
				tfidfMap.put(term, tf_idf);
			}
			/*** Normalizing vectors ***/
			double vec_length = 0;
			for (String vec : tfidfMap.keySet()) {
				double tfidf_vec = tfidfMap.get(vec);
				vec_length += Math.pow(tfidf_vec, 2);
			}
			double norm = Math.sqrt(vec_length);
			// put Normalized vectors
			for (String term : tfidfMap.keySet()) {
				tfidfMap.put(term, tfidfMap.get(term) / (double)(norm));
			}
			/*** ***************** ***/
			
			tfidfDocsMap.add(tfidfMap);
		}
		return tfidfDocsMap;
	}

	
	
	public Map<String, Double> makeCentroid(List<Map<String, Double>> tfidfDocsMap) {
		Map<String, Double> centroid = new HashMap<String, Double>();

		for (Map<String, Double> doc : tfidfDocsMap) {
			for (String term : doc.keySet()) {
				if (centroid.containsKey(term)) {
					centroid.put(term, centroid.get(term) + doc.get(term));
				} else {
					centroid.put(term, doc.get(term));
				}
			}
		}
		/*** Normalizing centroids ***/
		double vec_length = 0;
		for (String vec : centroid.keySet()) {
			double tfidf_vec = centroid.get(vec);
			vec_length += Math.pow(tfidf_vec, 2);
		}
		double norm = Math.sqrt(vec_length);
		
		// put Normalized centroids and / tfidfDocsMap.size()
		for (String term : centroid.keySet()) {
			centroid.put(term, centroid.get(term) / (double)(norm) / (double) tfidfDocsMap.size());
		}
		/*** **************** ***/
		
		/*for (String term : centroid.keySet()) {
			centroid.put(term, centroid.get(term) / tfidfDocsMap.size());
		}*/
		return centroid;
	}
	
	public List<Double> makeCentroidWord2Vec(List<List<Double>> pagesVectorsList) {
		/*
		List<Double> centroid = new ArrayList<Double>();
		for (List<Double> doc : pagesVectorsList) {
			for (int i =0; i<doc.size(); i++) {
				Double sum = centroid.get(i) + doc.get(i);
				
				if (!centroid.isEmpty()) {
					centroid.add(sum);
				} else {
					centroid.add(doc.get(i));
				}
			}
		}
		*/
		
		long millis = System.currentTimeMillis();
		long period = 0;
		
		if (pagesVectorsList.size() == 0) {
			System.out.println("Warning! category contains 0 pages");
			return Collections.emptyList();
		}
		List<Double> sampleRow = pagesVectorsList.get(0);
		int sampleColumnSize = sampleRow.size();
		List<Double> centroid = new ArrayList<Double>(Collections.nCopies(sampleColumnSize, 0.0));

		for (int j = 0; j < sampleColumnSize; j++) {
			for (int i = 0; i < pagesVectorsList.size(); i++) {
				centroid.set(j, centroid.get(j) + pagesVectorsList.get(i).get(j));
			}
		}

		for (int i = 0; i < centroid.size(); i++) {
			centroid.set(i, centroid.get(i)/pagesVectorsList.size());
		}

		//need to normalize
		
		/*** Normalizing centroids ***/
		/*double vec_length = 0;
		for (String vec : centroid.keySet()) {
			double tfidf_vec = centroid.get(vec);
			vec_length += Math.pow(tfidf_vec, 2);
		}
		double norm = Math.sqrt(vec_length);*/
		
		// put Normalized centroids and / tfidfDocsMap.size()
		/*for (String term : centroid.keySet()) {
			centroid.put(term, centroid.get(term) / (double)(norm) / (double) tfidfDocsMap.size());
		}*/
		/*** **************** ***/
		
		/*for (String term : centroid.keySet()) {
			centroid.put(term, centroid.get(term) / tfidfDocsMap.size());
		}*/
		//System.out.println("Centroid computed as " + centroid);
		//System.out.println("Centroid size " + centroid.size());
		period = System.currentTimeMillis() - millis;
		//System.out.println("\tMaking one centroid took: " + period + "ms");
		
		return centroid;
	}
	
	 public CategoryTree makeHieararchy(Map<String, NodeInfo> taxonomy) {
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
		CategoryTree finalTree = new CategoryTree();
		finalTree.hierarchy = tree;
		//finalTree.centroid = centroid;
		//finalTree.taxonomy = taxonomy;
		return finalTree;
	 }

	public void makeMergeCentroid(CategoryTree taxonomyTree, String rootId,
			Map<String, Centroid> centroidMap, Map<String, Centroid> mergeCentroidMap) {

		List<String> childList = taxonomyTree.hierarchy.get(rootId);
		Centroid currentCentroid = centroidMap.get(rootId);
		//System.out.println("This rootId is " + rootId);

		if (childList == null || childList.size() == 0) {
			/*if (currentCentroid == null) {
				System.out.println("Warning!!! Leaf node centroid is null for category " + rootId);
				currentCentroid = new Centroid();
			}*/ //don't need this, because always 1 + number of children(centroid)
			
			mergeCentroidMap.put(rootId, new Centroid(currentCentroid.getCentroid()));
			return;
		} else {
			/*** add centroid of a category and children's merge centroids ***/
			for (String child : childList) {
				makeMergeCentroid(taxonomyTree, child, centroidMap, mergeCentroidMap);
			}
			
			//already computed after recursion returns
			currentCentroid = centroidMap.get(rootId);
			Centroid mergeCentroid = new Centroid();
			assert(currentCentroid != null);
			if (currentCentroid != null) {
				
				/*adjust weights : add 0.8 - bigger weight to parent centroid */
				for(String term : currentCentroid.getCentroid().keySet()){
					currentCentroid.getCentroid().put(term, currentCentroid.getCentroid().get(term) * 0.9);
				}
				mergeCentroid.setCentroid(new HashMap<String, Double>(currentCentroid.getCentroid()));
				mergeCentroid.normalize();
			}
			double norm;
			for (String child : childList) {
				//Map<String, Double> childCentroid = mergeCentroids.get(child);
				Map<String, Double> childCentroid = mergeCentroidMap.get(child).getCentroid();
				norm = mergeCentroidMap.get(child).setCentroid_lengthNorm();
				
				//System.out.println("Looping through childList, this child is - " + child);
				/*** merge-centroid calculation by term addition ***/
				// if(merge_centroid!=null){

				for (String term : childCentroid.keySet()) {
					if (mergeCentroid.getCentroid().containsKey(term)) {
						mergeCentroid.getCentroid().put(term,
								mergeCentroid.getCentroid().get(term) + (childCentroid.get(term) * 0.1) /norm);
					} else {
						mergeCentroid.getCentroid().put(term, (childCentroid.get(term) * 0.1)/norm);
					}
				}
					
				/*** adding current_centroid to child merge - centroids - separately, after adding all children above  - didn't work ***/
					
				/*for(String termC : current_centroid.keySet()){
						if(mergeCentroid.containsKey(termC)){
							mergeCentroid.put(termC,
									(mergeCentroid.get(termC) + current_centroid.get(termC)) / (childList.size() + 1));
						} else {
							mergeCentroid.put(termC, current_centroid.get(termC) / (childList.size() + 1));
						}
					}*/
					
				//TODO():CHECK IT
				/*** Normalizing merge - centroids ***/
				
				/*double vec_length = 0;
				for (String vec : mergeCentroid.keySet()) {
					double tfidf_vec = mergeCentroid.get(vec);
					vec_length += Math.pow(tfidf_vec, 2);
				}*/
				
				// put Normalized merge - centroids
				//double numOfCentroids = (currentCentroid == null) ? childList.size() : (childList.size() +1);
				double numOfCentroids = childList.size() + 1;
				for (String term : mergeCentroid.getCentroid().keySet()) {
					mergeCentroid.getCentroid().put(term, mergeCentroid.getCentroid().get(term) / numOfCentroids);
				}
				mergeCentroid.normalize(); //- with this, 0,235 macro; 0,314 micro - BUT without weight adjusting for mc and c
			}
			mergeCentroidMap.put(rootId, mergeCentroid);
		}

		// }
	}
    

	public void makeMergeCentroidWord2Vec(CategoryTree taxonomyTree, String rootId,
			Map<String, Word2VecCentroid>  centroidMap, Map<String, Word2VecCentroid> mergeCentroidMap) {

		List<String> childList = taxonomyTree.hierarchy.get(rootId);
		Word2VecCentroid currentCentroid = centroidMap.get(rootId);
		//System.out.println("This rootId is " + rootId);

		if (childList == null || childList.size() == 0) {
			
			mergeCentroidMap.put(rootId, new Word2VecCentroid(currentCentroid.getCentroid()));
			return;
		} else {
			/*** add centroid of a category and children's merge centroids ***/
			for (String child : childList) {
				makeMergeCentroidWord2Vec(taxonomyTree, child, centroidMap, mergeCentroidMap);
			}
			
			//already computed after recursion returns
			currentCentroid = centroidMap.get(rootId);
			Word2VecCentroid mergeCentroid = new Word2VecCentroid();
			assert(currentCentroid != null);
			if (currentCentroid != null) {
				
				/*adjust weights : add 0.8 - bigger weight to parent centroid */
				for (int i =0; i < currentCentroid.getCentroid().size(); i++) {
					currentCentroid.getCentroid().set(i, currentCentroid.getCentroid().get(i) * 0.9);
				}
				mergeCentroid.setCentroid(new ArrayList<Double>(currentCentroid.getCentroid()));
				mergeCentroid.normalize();
			}
			double norm;
			for (String child : childList) {
				//Map<String, Double> childCentroid = mergeCentroids.get(child);
				List<Double> childCentroid = mergeCentroidMap.get(child).getCentroid();
				norm = mergeCentroidMap.get(child).setCentroid_lengthNorm();
				
				/*** merge-centroid calculation by term addition ***/
				// if(merge_centroid!=null){

				for (int i = 0; i < childCentroid.size(); i++) {
					 mergeCentroid.getCentroid().set(i, mergeCentroid.getCentroid().get(i) + childCentroid.get(i)/norm);
					 
				}
				//TODO():CHECK IT
				/*** Normalizing merge - centroids ***/
				
				/*double vec_length = 0;
				for (String vec : mergeCentroid.keySet()) {
					double tfidf_vec = mergeCentroid.get(vec);
					vec_length += Math.pow(tfidf_vec, 2);
				}*/
				
				// put Normalized merge - centroids
				//double numOfCentroids = (currentCentroid == null) ? childList.size() : (childList.size() +1);
				double numOfCentroids = childList.size() + 1;
				for (int i = 0; i < mergeCentroid.getCentroid().size(); i++) {
					mergeCentroid.getCentroid().set(i, mergeCentroid.getCentroid().get(i) / numOfCentroids);
				}
				mergeCentroid.normalize(); //- with this, 0,235 macro; 0,314 micro - BUT without weight adjusting for mc and c
			}
			mergeCentroidMap.put(rootId, mergeCentroid);
		}

		// }
	}
	
    /**
     * Method to calculate cosine similarity between all the documents.
     */
	 /*
    public void getCosineSimilarity() {
        for (int i = 0; i < tfidfDocsVector.size(); i++) {
            for (int j = 0; j < tfidfDocsVector.size(); j++) {
            
                System.out.println("between " + i + " and " + j + "  =  "
                                   + new CosineSimilarity().cosineSimilarity
                                       (
                                         tfidfDocsVector.get(i), 
                                         tfidfDocsVector.get(j)
                                       )
                                  );
            }
        }
    } */
}

