package control.filter;

public class FilterSettings {
	private boolean showImages = true;
	private boolean showGifs = false;
	private boolean showVideos = false;
	private boolean showOnlyNewEntities = false;
	private boolean enableLimit = false;
	private int limit = 0;
	
	private double whitelistFactor = 1.00;
	private double blacklistFactor = 0.01;
	
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
	public boolean isShowOnlyNewEntities() {
		return showOnlyNewEntities;
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
	public void setShowOnlyNewEntities(boolean showOnlyNewEntities) {
		this.showOnlyNewEntities = showOnlyNewEntities;
	}
	public void setEnableLimit(boolean enableLimit) {
		this.enableLimit = enableLimit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	public double getWhitelistFactor() {
		return whitelistFactor;
	}
	public double getBlacklistFactor() {
		return blacklistFactor;
	}
	public void setWhitelistFactor(double whitelistFactor) {
		this.whitelistFactor = whitelistFactor;
	}
	public void setBlacklistFactor(double blacklistFactor) {
		this.blacklistFactor = blacklistFactor;
	}
	
	public double getSimilarityFactor() {
		return similarityFactor;
	}
	public void setSimilarityFactor(double similarityFactor) {
		this.similarityFactor = similarityFactor;
	}
}
