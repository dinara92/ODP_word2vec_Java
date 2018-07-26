package inverted_index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import document_node.Document;
import node_info.NodeInfo;
import page_node.PageNode;
import utils.StringProcessingUtils;


public class InvertedIndex {

	
    private List<String> allDocs = new ArrayList<String>(); //to hold all docs
    private Map<String, List<Document>> index = new HashMap<String, List<Document>>();

    public InvertedIndex(Map<String, NodeInfo> taxonomy) throws IOException {
		for(String catId : taxonomy.keySet()) {
			NodeInfo node = taxonomy.get(catId);
			if(node!=null){
				if(node.getPages()!=null){
				for (PageNode page: node.getPages()) {
					indexDoc(page.getTokenizedPage());
				}
			}
        }}
    }

    public Map<String, List<Document>> getIndex() {
    	return index;
    }
    
    public void setIndex(Map<String, List<Document>> index) {
    	this.index = index;
    }
    
    public int getAllDocsSize() {
    	return allDocs.size();
    }

	// adds document to index variable
	public List<String> indexDoc(List<String> tokenizedDoc) throws IOException {
		// int docno = allDocs.indexOf(document);
		// if (docno == -1) {}
		// assume every doc is new and unique, they dont repeat
		allDocs.add(String.join(" ", tokenizedDoc));
		int docno = allDocs.size() - 1;
		int pos = 0;

		for (String _word : tokenizedDoc) {
			String word = _word.toLowerCase();
			pos++; // number of terms in this document
			// if (isStopword(word))
			// continue;
			List<Document> idx = index.get(word);
			if (idx == null) {
				idx = new LinkedList<Document>();
				index.put(word, idx);
			}
			idx.add(new Document(docno, pos));
		}
		return tokenizedDoc;
		/***
		 * COMMENT THIS OUT if want to see a term and its occurence in documents
		 ***/
		/*
		 * for(String term : index.keySet()) { System.out.println("key is: " +
		 * term + " " + index.get(term).size());//size is number of documents a
		 * term appears in //- it is like df in idf equation }
		 */
	}
	public int searchOccurences(String term) {
		/*** SEARCH ***/
		String word = term.toLowerCase();
		List<Document> idx = index.get(word);
		int occurences = (idx == null ? 0 : idx.size());
		return occurences;
	}

	// public void search(String term) {
	public Set<String> search(String term) {

		/*** SEARCH ***/
		Set<String> answer = new HashSet<String>();
		String word = term.toLowerCase();
		List<Document> idx = index.get(word);
		if (idx != null) {
			for (Document t : idx) {
				answer.add(allDocs.get(t.docno));
			}
		} else {
			System.out.println("Term not found in this index!");
		}

		/*
		 * System.out.println("The term is: " + word); for (String f : answer) {
		 * System.out.print("This term is in these docs:  " + f + "\n"); }
		 */
		return answer;
	}
	
}
