package backend.entity;

import backend.BaseList;
import backend.misc.FileUtil;
import backend.reload.Notifier;
import backend.reload.Reload;
import backend.tag.Tag;
import backend.tag.TagList;
import main.Main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

public class EntityList extends BaseList<Entity> {
	public EntityList() {
	
	}
	public EntityList(Collection<? extends Entity> c) {
		super(c);
	}
	public EntityList(Entity... entities) {
		super(Arrays.asList(entities));
	}
	public EntityList(BaseList<File> fileList) {
		fileList.forEach(file -> this.add(new Entity(file)));
	}
	
	private final boolean ascending = true;
	public void sortByName() {
		if (ascending) {
			super.sort(Comparator.comparing(Entity::getName));
		} else {
			super.sort(Comparator.comparing(Entity::getName).reversed());
		}
	}
	public void sortByCreationDate() {
		super.sort((o1, o2) -> {
			long time1 = 0;
			long time2 = 0;
			try {
				time1 = Files.readAttributes(new File(FileUtil.getFileEntity(o1)).toPath(), BasicFileAttributes.class).creationTime().toMillis();
				time2 = Files.readAttributes(new File(FileUtil.getFileEntity(o2)).toPath(), BasicFileAttributes.class).creationTime().toMillis();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (ascending) {
				return Long.compare(time2, time1);
			} else {
				return Long.compare(time1, time2);
			}
		});
	}
	public void sortByLastModifyDate() {
		super.sort((o1, o2) -> {
			long time1 = 0;
			long time2 = 0;
			try {
				time1 = Files.readAttributes(new File(FileUtil.getFileEntity(o1)).toPath(), BasicFileAttributes.class).lastModifiedTime().toMillis();
				time2 = Files.readAttributes(new File(FileUtil.getFileEntity(o2)).toPath(), BasicFileAttributes.class).lastModifiedTime().toMillis();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (ascending) {
				return Long.compare(time2, time1);
			} else {
				return Long.compare(time1, time2);
			}
		});
	}
	
	public EntityList createRepresentingList() {
		EntityList representingEntityList = new EntityList();
		BaseList<Integer> groups = new BaseList<>();
		
		for (Entity entity : this) {
			if (entity.hasGroup()) {
				if (!groups.contains(entity.getGroupID())) {
					if (entity.getGroup().isOpen()) {
						groups.add(entity.getGroupID());
						representingEntityList.addAll(Main.FILTER.getFilteredList(entity.getGroup()));
					} else {
						groups.add(entity.getGroupID());
						representingEntityList.add(entity);
					}
				}
			} else {
				representingEntityList.add(entity);
			}
		}
		
		return representingEntityList;
	}
	
	public void addTag(Integer tagID) {
		this.forEach(entity -> entity.addTag(tagID));
	}
	public void removeTag(Integer tagID) {
		this.forEach(entity -> entity.removeTag(tagID));
	}
	public void clearTags() {
		this.forEach(Entity::clearTags);
	}
	
	public void mergeTags() {
		BaseList<Integer> tagIDs = this.getTagIDList();
		
		this.forEach(entity -> {
			entity.getTagIDList().setAll(new BaseList<>(tagIDs));
			entity.initTags();
		});
		
		Reload.notify(Notifier.SELECT_TAGLIST_CHANGED);
	}
	
	public BaseList<Integer> getTagIDList() {
		BaseList<Integer> tagIDList = new BaseList<>();
		this.forEach(entity -> tagIDList.addAll(entity.getTagIDList(), true));
		return tagIDList;
	}
	public BaseList<Integer> getTagIDListIntersect() {
		if (!this.isEmpty()) {
			BaseList<Integer> tagIDList = new BaseList<>();
			//check every tag of the first object
			for (int tagID : this.getFirst().getTagIDList()) {
				//check if all objects contain the tagID
				for (Entity entity : this) {
					if (entity.getTagIDList().contains(tagID)) {
						//if the last object contains the tagID, all before do too, add
						if (entity.equals(this.getLast())) {
							tagIDList.add(tagID, true);
						}
					} else {
						//if any of the objects doesn't contain the tag, break
						break;
					}
				}
			}
			return tagIDList;
		} else {
			return new BaseList<>();
		}
	}
	
	public TagList getTagList() {
		TagList tagList = new TagList();
		this.forEach(entity -> tagList.addAll(entity.getTagList(), true));
		return tagList;
	}
	public TagList getTagListIntersect() {
		if (!this.isEmpty()) {
			TagList tagListIntersect = new TagList();
			//check every tag of the first object
			for (Tag tag : this.getFirst().getTagList()) {
				//check if all objects contain the tagID
				for (Entity entity : new EntityList(this)) {
					if (entity.getTagList().contains(tag)) {
						//if the last object contains the tag, all before do too, add
						if (entity.equals(this.getLast())) {
							tagListIntersect.add(tag, true);
						}
					} else {
						//if any of the objects doesn't contain the tag, break
						break;
					}
				}
			}
			return tagListIntersect;
		} else {
			return new TagList();
		}
	}
	
	public boolean isGroup() {
		if (this.isEmpty()) {
			return false;
		} else {
			int groupID = this.getFirst().getGroupID();
			if (groupID == 0) return false;
			for (Entity entity : this) {
				if (entity.getGroupID() != groupID) {
					return false;
				}
			}
			return true;
		}
	}
}
