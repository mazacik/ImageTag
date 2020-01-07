package control.reload;

import base.CustomList;

public enum Notifier {
	FILTER_NEEDS_REFRESH,
	
	FILTER,
	SELECT,
	TARGET,
	
	ENTITY_LIST_MAIN,
	TAG_LIST_MAIN,
	
	TAGS_OF_SELECT,
	VIEWMODE,
	;
	
	private CustomList<InvokeHelper> invokeHelpers;
	
	Notifier() {
		this.invokeHelpers = new CustomList<>();
	}
	
	public CustomList<InvokeHelper> getInvokeHelpers() {
		return this.invokeHelpers;
	}
}
