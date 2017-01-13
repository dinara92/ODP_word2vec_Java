package centroid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Word2VecCentroid {

	
	List<Double> centroid = new ArrayList<Double>();
	double centroid_lengthNorm = 0.0;

	public Word2VecCentroid() {
		
	}
	
	public Word2VecCentroid(List<Double> centroid) {
		this.centroid = centroid;
		this.setCentroid_lengthNorm();
	}

	public List<Double> getCentroid() {
		return centroid;
	}
	
	public void setCentroid(List<Double> centroid) {
		this.centroid = centroid;
		setCentroid_lengthNorm();
	}
	
	public void setCentroid_noNorm(List<Double> centroid) {
		this.centroid = centroid;
	}
	
	public double getCentroid_lengthNorm() {
		return centroid_lengthNorm;
	}
	
	public double setCentroid_lengthNorm() {
		centroid_lengthNorm = 0;
		for (Double value : centroid) {
			centroid_lengthNorm += Math.pow(value, 2);
		}
		centroid_lengthNorm = Math.sqrt(centroid_lengthNorm);
		return centroid_lengthNorm;
	}
	

	public void normalize() {
		setCentroid_lengthNorm();
		for (int i = 0; i < centroid.size(); i++) {
			centroid.set(i, centroid.get(i)/centroid_lengthNorm);
		}
	}
}
