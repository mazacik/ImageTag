package baseobject.tag;

import baseobject.entity.Entity;
import com.google.gson.reflect.TypeToken;
import control.reload.ChangeIn;
import main.InstanceCollector;
import tools.FileUtil;
import tools.JsonUtil;

import java.lang.reflect.Type;

public class TagListMain extends TagList implements InstanceCollector {
	private static final Type typeToken = new TypeToken<TagListMain>() {}.getType();
	
	public void initialize() {
		TagList allTags = readFromDisk();
		if (allTags != null) {
			this.addAll(allTags);
		}
		
		for (Entity entity : entityListMain) {
			TagList tagList = entity.getTagList();
			
			for (Tag tag : tagList) {
				if (this.containsEqualTo(tag)) {
					tagList.set(tagList.indexOf(tag), this.getTag(tag));
				} else {
					this.add(tag);
				}
			}
		}
		
		this.sort();
	}
	
	public void writeToDisk() {
		JsonUtil.write(this, typeToken, FileUtil.getProjectFileTags());
	}
	private TagList readFromDisk() {
		return (TagList) JsonUtil.read(typeToken, FileUtil.getProjectFileTags());
	}
	
	public boolean add(Tag tag) {
		if (super.add(tag)) {
			reload.notify(ChangeIn.TAG_LIST_MAIN);
			return true;
		}
		return false;
	}
	public boolean remove(Tag tag) {
		if (super.remove(tag)) {
			reload.notify(ChangeIn.TAG_LIST_MAIN);
			return true;
		}
		return false;
	}
}
