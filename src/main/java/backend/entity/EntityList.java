package backend.entity;

import backend.BaseList;
import backend.Pair;
import backend.PairList;
import backend.misc.FileUtil;
import backend.reload.Notifier;
import backend.reload.Reload;
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
		super();
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
	public void sortByLastModificationDate() {
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
				if (!groups.contains(entity.getEntityGroupID())) {
					if (entity.getEntityGroup().isOpen()) {
						groups.add(entity.getEntityGroupID());
						representingEntityList.addAll(Main.FILTER.getFilteredList(entity.getEntityGroup()));
					} else {
						groups.add(entity.getEntityGroupID());
						representingEntityList.add(entity);
					}
				}
			} else {
				representingEntityList.add(entity);
			}
		}
		
		return representingEntityList;
	}
	
	public void addTag(String tag) {
		this.forEach(entity -> entity.getTagList().add(tag, true));
	}
	public void addTags(Collection<String> tags) {
		this.forEach(entity -> entity.getTagList().addAll(tags, true));
	}
	
	public void removeTag(String tag) {
		this.forEach(entity -> entity.getTagList().remove(tag));
	}
	public void removeTags(Collection<String> tags) {
		this.forEach(entity -> entity.getTagList().removeAll(tags));
	}
	
	public void setTags(Collection<String> tags) {
		this.clearTags();
		this.addTags(tags);
	}
	
	public void clearTags() {
		this.forEach(entity -> entity.getTagList().clear());
	}
	
	public void mergeTags() {
		this.forEach(entity -> entity.getTagList().setAll(this.getTagList()));
		Reload.notify(Notifier.SELECT_TAGLIST_CHANGED);
	}
	
	public PairList<String, Integer> getTagListWithCount() {
		PairList<String, Integer> tagList = new PairList<>();
		
		this.forEach(entity -> entity.getTagList().forEach(tag -> {
			Pair<String, Integer> pair = tagList.getPair(tag);
			if (pair != null) {
				pair.setValue(pair.getValue() + 1);
			} else {
				tagList.add(new Pair<>(tag, 1));
			}
		}));
		
		return tagList;
	}
	public BaseList<String> getTagList() {
		BaseList<String> tagList = new BaseList<>();
		this.forEach(entity -> tagList.addAll(entity.getTagList(), true));
		return tagList;
	}
	public BaseList<String> getTagListIntersect() {
		if (!this.isEmpty()) {
			EntityList helperThis = new EntityList(this); //avoids async ConcurrentModificationException
			BaseList<String> tagListIntersect = new BaseList<>();
			//check every tag of the first object
			for (String tag : this.getFirst().getTagList()) {
				//check if all objects contain the tagID
				for (Entity entity : helperThis) {
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
			return new BaseList<>();
		}
	}
	
	public boolean isGroup() {
		if (this.isEmpty()) {
			return false;
		} else {
			int groupID = this.getFirst().getEntityGroupID();
			if (groupID == 0) return false;
			for (Entity entity : this) {
				if (entity.getEntityGroupID() != groupID) {
					return false;
				}
			}
			return true;
		}
	}
}