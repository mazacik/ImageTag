package base.entity;

import base.CustomList;

import java.io.File;

public class Collection extends EntityList {
	public Collection() {
	
	}
	public Collection(java.util.Collection<? extends Entity> c) {
		super(c);
	}
	public Collection(Entity... entities) {
		super(entities);
	}
	public Collection(CustomList<File> fileList) {
		super(fileList);
	}
	
	public boolean isOpen() {
		return CollectionUtil.getOpenCollections().contains(this);
	}
}
