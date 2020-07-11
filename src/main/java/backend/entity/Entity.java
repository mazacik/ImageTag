package backend.entity;

import backend.BaseList;
import backend.group.Group;
import backend.misc.FileUtil;
import backend.tag.Tag;
import backend.tag.TagList;
import com.google.gson.annotations.SerializedName;
import frontend.component.gallery.Tile;
import main.Main;

import java.io.File;

public class Entity {
	public Entity(File file) {
		this.name = FileUtil.createEntityName(file);
		this.tagIDList = new BaseList<>();
		this.size = file.length();
		this.groupID = 0;
		this.type = null;
		this.mediaDuration = 0;
		
		this.group = null;
		this.tile = null;
	}
	
	public void addTag(Tag tag) {
		getTagList().add(tag, true);
		tagIDList.add(tag.getID());
	}
	public void addTag(int tagID) {
		this.addTag(Main.DB_TAG.getTag(tagID));
	}
	public void removeTag(Tag tag) {
		getTagList().remove(tag);
		tagIDList.remove((Integer) tag.getID());
	}
	public void removeTag(int tagID) {
		this.removeTag(Main.DB_TAG.getTag(tagID));
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
			this.getTagList().add(Main.DB_TAG.getTag(tagID));
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
	
	@SerializedName("n") private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@SerializedName("t") private final BaseList<Integer> tagIDList;
	public BaseList<Integer> getTagIDList() {
		return tagIDList;
	}
	
	@SerializedName("s") private final long size;
	public long getSize() {
		return size;
	}
	
	@SerializedName("g") private int groupID;
	public int getGroupID() {
		return groupID;
	}
	public void setGroupID(int groupID) {
		this.groupID = groupID;
	}
	public boolean hasGroup() {
		return groupID != 0;
	}
	
	//todo do not serialize stuff that can be created on init and does not need to be serialized
	@SerializedName("f") private EntityType type;
	public EntityType getType() {
		if (type == null) {
			type = FileUtil.getMediaType(this);
		}
		return type;
	}
	
	@SerializedName("d") private long mediaDuration;
	public long getMediaDuration() {
		return mediaDuration;
	}
	public void setMediaDuration(long mediaDuration) {
		this.mediaDuration = mediaDuration;
	}
	
	@SerializedName("fav") private boolean favorite;
	public boolean isFavorite() {
		return favorite;
	}
	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}
	
	private transient Group group;
	public Group getGroup() {
		return group;
	}
	public void setGroup(Group group) {
		this.group = group;
	}
	
	private transient TagList tagList;
	public TagList getTagList() {
		if (tagList == null) {
			tagList = new TagList();
		}
		return tagList;
	}
	
	private transient Tile tile;
	public Tile getTile() {
		if (tile == null) {
			tile = new Tile(this);
		}
		return tile;
	}
}
