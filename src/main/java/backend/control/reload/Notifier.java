package backend.control.reload;

import backend.list.BaseList;

public enum Notifier {
	ENTITYLIST_CHANGED,
	TAGLIST_CHANGED,
	
	FILTER_CHANGED,
	FILTER_NEEDS_REFRESH,
	
	SELECT_CHANGED,
	SELECT_TAGLIST_CHANGED,
	
	TARGET_CHANGED,
	TARGET_GROUP_CHANGED,
	
	VIEWMODE_CHANGED,
	;
	
	private BaseList<InvokeHelper> invokeHelpers;
	
	Notifier() {
		this.invokeHelpers = new BaseList<>();
	}
	
	public BaseList<InvokeHelper> getInvokeHelpers() {
		return this.invokeHelpers;
	}
}
