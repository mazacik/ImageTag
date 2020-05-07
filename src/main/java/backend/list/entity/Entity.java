package backend.list.entity;

import backend.list.BaseList;
import backend.list.group.Group;
import backend.list.tag.Tag;
import backend.list.tag.TagList;
import backend.misc.EntityType;
import backend.misc.FileUtil;
import com.google.gson.annotations.SerializedName;
import frontend.component.gallery.Tile;
import main.Main;

import java.io.File;

public class Entity {
	@SerializedName("n") private String name;
	@SerializedName("t") private final BaseList<Integer> tagIDs;
	@SerializedName("s") private final long size;
	@SerializedName("g") private int groupID;
	@SerializedName("f") private EntityType entityType;
	@SerializedName("d") private long mediaDuration;
	
	private transient Group group;
	private transient TagList tagList;
	private transient Tile tile;
	
	public Entity(File file) {
		this.name = FileUtil.createEntityName(file);
		this.tagIDs = new BaseList<>();
		this.size = file.length();
		this.groupID = 0;
		this.entityType = null;
		this.mediaDuration = 0;
		
		this.group = null;
		this.tile = null;
	}
	
	public void addTag(Tag tag) {
		getTagList().add(tag, true);
		tagIDs.add(tag.getID());
	}
	public void addTag(int tagID) {
		this.addTag(Main.TAGLIST.getTag(tagID));
	}
	public void removeTag(Tag tag) {
		getTagList().remove(tag);
		tagIDs.remove((Integer) tag.getID());
	}
	public void removeTag(int tagID) {
		this.removeTag(Main.TAGLIST.getTag(tagID));
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
			this.getTagList().add(Main.TAGLIST.getTag(tagID));
		}
	}
	
	public Entity getRepresentingEntity() {
		if (this.hasGroup()) {
			if (group.isOpen()) {
				return this;
			} else {
				return Main.FILTER.getFilteredList(group).getFirst();
			}
		} else {
			return this;
		}
	}
	
	public boolean hasGroup() {
		return groupID != 0;
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
	public int getGroupID() {
		return groupID;
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
	public Group getGroup() {
		return group;
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
	public void setGroupID(int groupID) {
		this.groupID = groupID;
	}
	public void setMediaDuration(long mediaDuration) {
		this.mediaDuration = mediaDuration;
	}
	public void setGroup(Group group) {
		this.group = group;
	}
}
