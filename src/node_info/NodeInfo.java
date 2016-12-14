package node_info;
import java.util.List;
import java.util.Map;

import page_node.PageNode;

public class NodeInfo {

	String catid;
	public String getCatid() {
		return catid;
	}
	public void setCatid(String catid) {
		this.catid = catid;
	}


	String fatherid, category_topic;
	
	List<PageNode> page_terms = null; // this is previous page_terms
	//next line is a Mapping of docNumber to its Doc - List of String terms
	//Map<Integer, List<String>> page_termsMap;
	
	
	/*public Map<Integer, List<String>> getPage_termsMap() {
		return page_termsMap;
	}
	public void setPage_termsMap(Map<Integer, List<String>> page_termsMap) {
		this.page_termsMap = page_termsMap;
	}*/

	Map<String, Double> centroid;
	int centroid_vector_count;
	
	public int getCentroid_vector_count() {
		return centroid.size();
	}
	public void setCentroid_vector_count(int centroid_vector_count) {
		this.centroid_vector_count = centroid_vector_count;
	}
	public Map<String, Double> getCentroid() {
		return centroid;
	}
	public void setCentroid(Map<String, Double> catCentroid) {
		this.centroid = catCentroid;
	}

	public String getFatherid() {
		return fatherid;
	}
	public void setFatherid(String fatherid) {
		this.fatherid = fatherid;
	}
	public String getCategory_topic() {
		return category_topic;
	}
	public void setCategory_topic(String category_topic) {
		this.category_topic = category_topic;
	}
	
	/*** This is previous page_terms ***/
	public List<PageNode> getPages() {
		return page_terms;
	}

	public void setPages(List<PageNode> list) {
		this.page_terms = list;
	}
	
	
	// Custom toString() Method.
	@Override
	public String toString() {
		/*** print Topics ***/
		//StringBuilder str = new StringBuilder("Category ID: " + category_topic + "\n");
		 
		
		 /*** print term vectors ***/
		 /*int count = 0;
		  for(double[] element : getTfIdfVectorsofEachPageInThisCategory()) {
			 str.append("\tpage " + count + " tfidf vector is " + Arrays.toString(element) + "vector_count" + element.length  +"\n");
			 count++;
		}*/

	/*** print Centroids ***/
	//str.append("\tCentroid vector is " + Arrays.toString(getCentroid()) + "\nCentroid length is "+ getCentroid_vector_count() + "\n");
	
		StringBuilder str = new StringBuilder(page_terms.toString());

		
		return str.toString();
	}
	
}
