package application.frontend.pane;

public interface PaneInterface {
	boolean reload();
	boolean getNeedsReload();
	void setNeedsReload(boolean needsReload);
}
