package application.gui.panes;

public interface NodeBase {
	boolean reload();
	boolean getNeedsReload();
	void setNeedsReload(boolean needsReload);
}
