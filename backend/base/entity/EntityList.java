package application.backend.base.entity;

import application.backend.base.CustomList;
import application.backend.base.tag.Tag;
import application.backend.base.tag.TagList;
import application.backend.util.JointGroupUtil;
import application.main.InstanceCollector;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

public class EntityList extends CustomList<Entity> implements InstanceCollector {
	public EntityList() {
	
	}
	public EntityList(Entity... entities) {
		this.addAll(Arrays.asList(entities));
	}
	public EntityList(Collection<? extends Entity> c) {
		super(c);
	}
	
	public void sort() {
		super.sort(Comparator.comparing(Entity::getName));
	}
	
	public TagList getTagsAll() {
		if (!this.isEmpty()) {
			TagList tagList = new TagList();
			
			for (Entity entity : this) {
				tagList.addAll(entity.getTagList());
			}
			
			return tagList;
		} else {
			return new TagList();
		}
	}
	public TagList getTagsIntersect() {
		if (!this.isEmpty()) {
			TagList tagsIntersect = new TagList();
			
			//check every tag of the first object
			for (Tag tag : this.getFirst().getTagList()) {
				//check if all objects contain the tag
				for (Entity entity : this) {
					if (entity.getTagList().containsEqual(tag)) {
						//if the last object contains the tag, all before do too, add
						if (entity.equals(this.getLast())) {
							tagsIntersect.add(tag);
						}
						//if any of the objects doesn't contain the tag, break
					} else {
						break;
					}
				}
			}
			
			return tagsIntersect;
		} else {
			return new TagList();
		}
	}
	
	public CustomList<Integer> getJointIDs() {
		CustomList<Integer> jointIDs = new CustomList<>();
		for (Entity entity : this) {
			if (entity.getJointID() != 0) {
				jointIDs.add(entity.getJointID());
			}
		}
		return jointIDs;
	}
	
	public Entity getRandom(CustomList<Entity> customList) {
		Entity entity = customList.getRandom();
		if (entity != null) {
			if (entity.getJointID() == 0) {
				return entity;
			} else {
				return filter.checkList(JointGroupUtil.getJointObjects(entity)).getRandom();
			}
		} else return null;
	}
}
