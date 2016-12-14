package centroid;

import java.util.HashMap;
import java.util.Map;

public class Centroid {
	Map<String, Double> centroid = new HashMap<String, Double>();
	double centroid_lengthNorm = 0.0;

	public Centroid() {
		
	}
	
	public Centroid(Map<String, Double> centroid) {
		this.centroid = centroid;
		this.setCentroid_lengthNorm();
	}

	public Map<String, Double> getCentroid() {
		return centroid;
	}
	
	public void setCentroid(Map<String, Double> centroid) {
		this.centroid = centroid;
	}
	
	public double getCentroid_lengthNorm() {
		return centroid_lengthNorm;
	}
	
	public double setCentroid_lengthNorm() {
		centroid_lengthNorm = 0;
		for (String vec : centroid.keySet()) {
			double tfidf_vec = centroid.get(vec);
			centroid_lengthNorm += Math.pow(tfidf_vec, 2);
		}
		centroid_lengthNorm = Math.sqrt(centroid_lengthNorm);
		return centroid_lengthNorm;
	}

	public void normalize() {
		setCentroid_lengthNorm();
		for (String term: centroid.keySet()) {
			centroid.put(term, centroid.get(term)/centroid_lengthNorm);
		}
	}
}
