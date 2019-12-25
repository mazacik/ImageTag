package control.reload;

import base.CustomList;

public enum ChangeIn {
	ENTITY_LIST_MAIN,
	TAG_LIST_MAIN,
	FILTER,
	TARGET,
	SELECT,
	TAGS_OF_SELECT,
	VIEWMODE,
	;
	
	private CustomList<InvokeHelper> subscribers;
	
	ChangeIn() {
		this.subscribers = new CustomList<>();
	}
	
	public CustomList<InvokeHelper> getSubscribers() {
		return this.subscribers;
	}
}
