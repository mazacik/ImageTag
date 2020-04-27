package backend.list.entity;

import backend.list.BaseList;
import backend.list.collection.Collection;
import backend.list.tag.Tag;
import backend.list.tag.TagList;
import backend.misc.EntityType;
import backend.misc.FileUtil;
import com.google.gson.annotations.SerializedName;
import frontend.component.gallery.Tile;
import main.Root;

import java.io.File;

public class Entity {
	@SerializedName("n") private String name;
	@SerializedName("t") private final BaseList<Integer> tagIDs;
	@SerializedName("s") private final long size;
	@SerializedName("c") private int collectionID;
	@SerializedName("f") private EntityType entityType;
	@SerializedName("d") private long mediaDuration;
	
	private transient Collection collection;
	private transient TagList tagList;
	private transient Tile tile;
	
	public Entity(File file) {
		this.name = FileUtil.createEntityName(file);
		this.tagIDs = new BaseList<>();
		this.size = file.length();
		this.collectionID = 0;
		this.entityType = null;
		this.mediaDuration = 0;
		
		this.collection = null;
		this.tile = null;
	}
	
	public void addTag(Tag tag) {
		getTagList().add(tag, true);
		tagIDs.add(tag.getID());
	}
	public void addTag(int tagID) {
		this.addTag(Root.TAGLIST.getTag(tagID));
	}
	public void removeTag(Tag tag) {
		getTagList().remove(tag);
		tagIDs.remove((Integer) tag.getID());
	}
	public void removeTag(int tagID) {
		this.removeTag(Root.TAGLIST.getTag(tagID));
	}
	public void removeTag(TagList tagList) {
		tagList.forEach(this::removeTag);
	}
	public void clearTags() {
		getTagList().clear();
		tagList.clear();
	}
	
	public void initTags() {
		for (int tagID : this.getTagIDList()) {
			this.getTagList().add(Root.TAGLIST.getTag(tagID));
		}
	}
	
	public Entity getRepresentingEntity() {
		if (this.hasCollection()) {
			if (collection.isOpen()) {
				return this;
			} else {
				return Root.FILTER.getFilteredList(collection).getFirst();
			}
		} else {
			return this;
		}
	}
	
	public boolean hasCollection() {
		return collectionID != 0;
	}
	
	public String getName() {
		return name;
	}
	public BaseList<Integer> getTagIDList() {
		return tagIDs;
	}
	public long getSize() {
		return size;
	}
	public int getCollectionID() {
		return collectionID;
	}
	public EntityType getEntityType() {
		if (entityType == null) {
			entityType = FileUtil.getMediaType(this);
		}
		return entityType;
	}
	public long getMediaDuration() {
		return mediaDuration;
	}
	public Collection getCollection() {
		return collection;
	}
	public TagList getTagList() {
		if (tagList == null) {
			tagList = new TagList();
		}
		return tagList;
	}
	public Tile getTile() {
		if (tile == null) {
			tile = new Tile(this);
		}
		return tile;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public void setCollectionID(int collectionID) {
		this.collectionID = collectionID;
	}
	public void setMediaDuration(long mediaDuration) {
		this.mediaDuration = mediaDuration;
	}
	public void setCollection(Collection collection) {
		this.collection = collection;
	}
}
