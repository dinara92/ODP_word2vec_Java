package document_node;

import java.util.List;

public class Document {
	public int position;
	public String word_term;
	public List<String> listOfWordTerms;
	public int docno;

	public List<String> getListOfWordTerms() {
		return listOfWordTerms;
	}

	public void setListOfWordTerms(List<String> listOfWordTerms) {
		this.listOfWordTerms = listOfWordTerms;
	}

	public Document(int docno, int position) {
		this.docno = docno;
		this.position = position;
	}

	public String getWord_term() {
		return word_term;
	}

	public void setWord_term(String word_term) {
		this.word_term = word_term;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	// Custom toString() Method.
		@Override
		public String toString() {
			StringBuilder str = new StringBuilder("Position in a document: " + position + "\n");

			return str.toString();
}
 	}