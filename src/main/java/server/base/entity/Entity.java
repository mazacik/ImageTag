package server.base.entity;

import client.ui.main.gallery.Tile;
import com.google.gson.annotations.SerializedName;
import main.Root;
import server.base.CustomList;
import server.base.collection.Collection;
import server.base.tag.Tag;
import server.base.tag.TagList;
import server.enums.MediaType;
import server.misc.FileUtil;

import java.io.File;

public class Entity {
	@SerializedName("n") private String name;
	@SerializedName("t") private CustomList<Integer> tagIDs;
	@SerializedName("s") private long size;
	@SerializedName("c") private int collectionID;
	@SerializedName("f") private MediaType mediaType;
	@SerializedName("d") private long mediaDuration;
	
	private transient Collection collection;
	private transient TagList tagList;
	private transient Tile tile;
	
	public Entity(File file) {
		this.name = FileUtil.createEntityName(file);
		this.tagIDs = new CustomList<>();
		this.size = file.length();
		this.collectionID = 0;
		this.mediaType = null;
		this.mediaDuration = 0;
		
		this.collection = null;
		this.tile = null;
	}
	
	public void addTag(Tag tag) {
		getTagList().addImpl(tag, true);
		tagIDs.addImpl(tag.getID());
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
	public CustomList<Integer> getTagIDs() {
		return tagIDs;
	}
	public long getSize() {
		return size;
	}
	public int getCollectionID() {
		return collectionID;
	}
	public MediaType getMediaType() {
		if (mediaType == null) {
			mediaType = FileUtil.getMediaType(this);
		}
		return mediaType;
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
