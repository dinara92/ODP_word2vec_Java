package page_node;

import java.util.List;

public class PageNode {
	String doc_id;
	String description;
	List<String> tokenizedPage;
	String catid;
	List<Double> word2VecVectors;
	String fatherid;
	
	public String getFatherid() {
		return fatherid;
	}

	public void setFatherid(String fatherid) {
		this.fatherid = fatherid;
	}

	public List<Double> getWord2VecVectors() {
		return word2VecVectors;
	}

	public void setWord2VecVectors(List<Double> word2VecVectors) {
		this.word2VecVectors = word2VecVectors;
	}

	public String getCatid() {
		return catid;
	}

	public void setCatid(String catid) {
		this.catid = catid;
	}

	public String get_id() {
		return doc_id;
	}

	public void set_id(String doc_id) {
		this.doc_id = doc_id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setTokenizedPage(List<String> tokenizedPage) {
		this.tokenizedPage = tokenizedPage;
	}

	public List<String> getTokenizedPage() {
		return this.tokenizedPage;
	}

	// Custom toString() Method.
	/*public String toString(){
		return description;
	}*/
	
	public String toString(){
		return tokenizedPage.toString();
	}
	
	
}
