package server.control.reload;

import server.base.CustomList;

public enum Notifier {
	ENTITYLIST_CHANGED,
	TAGLIST_CHANGED,
	
	FILTER_CHANGED,
	FILTER_NEEDS_REFRESH,
	
	SELECT_CHANGED,
	SELECT_TAGLIST_CHANGED,
	
	TARGET_CHANGED,
	TARGET_COLLECTION_CHANGED,
	
	VIEWMODE_CHANGED,
	;
	
	private CustomList<InvokeHelper> invokeHelpers;
	
	Notifier() {
		this.invokeHelpers = new CustomList<>();
	}
	
	public CustomList<InvokeHelper> getInvokeHelpers() {
		return this.invokeHelpers;
	}
}
