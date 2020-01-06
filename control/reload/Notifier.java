package control.reload;

import base.CustomList;

public enum Notifier {
	ENTITY_LIST_MAIN,
	TAG_LIST_MAIN,
	FILTER,
	TARGET,
	SELECT,
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
