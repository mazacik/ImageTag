package baseobject.entity;

import baseobject.CustomList;
import baseobject.tag.Tag;
import baseobject.tag.TagList;
import main.InstanceCollector;
import tools.EntityGroupUtil;

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
					if (entity.getTagList().containsEqualTo(tag)) {
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
	
	public Entity getRandom(CustomList<Entity> customList) {
		Entity entity = customList.getRandom();
		if (entity != null) {
			if (entity.getEntityGroupID() == 0) {
				return entity;
			} else {
				return filter.checkList(entity.getEntityGroup()).getRandom();
			}
		} else return null;
	}
}
