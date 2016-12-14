package testDoc;

import java.util.List;
import java.util.Map;

public class TestDoc {

	List<Map<String, Double>> testDoc;
	public List<Map<String, Double>> getTestDoc() {
		return testDoc;
	}
	public void setTestDoc(List<Map<String, Double>> testDoc) {
		this.testDoc = testDoc;
	}
	double testDoc_lengthNorm;


	public double getCentroid_lengthNorm() {
		double normC = Math.sqrt(testDoc_lengthNorm);
		return normC;
	}
	public void setCentroid_lengthNorm() {
		
		testDoc_lengthNorm = 0;
		/*for (String vec : testDoc.keySet()) {
			double tfidf_vec = testDoc.get(vec);
			testDoc_lengthNorm += Math.pow(tfidf_vec, 2);
		}*/
		
		
	}
}

