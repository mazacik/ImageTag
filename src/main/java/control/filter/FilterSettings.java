package control.filter;

public class FilterSettings {
	private boolean showImages = true;
	private boolean showGifs = false;
	private boolean showVideos = false;
	private boolean onlySession = false;
	private boolean enableLimit = false;
	private int limit = 0;
	
	private double similarityFactor = 0.5;
	
	FilterSettings() {
	
	}
	
	public boolean isShowImages() {
		return showImages;
	}
	public boolean isShowGifs() {
		return showGifs;
	}
	public boolean isShowVideos() {
		return showVideos;
	}
	public boolean isOnlySession() {
		return onlySession;
	}
	public boolean isEnableLimit() {
		return enableLimit;
	}
	public int getLimit() {
		return limit;
	}
	
	public void setShowImages(boolean showImages) {
		this.showImages = showImages;
	}
	public void setShowGifs(boolean showGifs) {
		this.showGifs = showGifs;
	}
	public void setShowVideos(boolean showVideos) {
		this.showVideos = showVideos;
	}
	public void setOnlySession(boolean onlySession) {
		this.onlySession = onlySession;
	}
	public void setEnableLimit(boolean enableLimit) {
		this.enableLimit = enableLimit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	public double getSimilarityFactor() {
		return similarityFactor;
	}
	public void setSimilarityFactor(double similarityFactor) {
		this.similarityFactor = similarityFactor;
	}
}
