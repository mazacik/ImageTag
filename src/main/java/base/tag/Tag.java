package base.tag;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.text.WordUtils;

public class Tag {
	@SerializedName("g") private String group;
	@SerializedName("n") private String name;
	
	public Tag(String group, String name) {
		this.setGroup(group);
		this.setName(name);
	}
	
	public boolean isEmpty() {
		return group.isEmpty() || name.isEmpty();
	}
	
	public String getGroup() {
		return group;
	}
	public String getName() {
		return name;
	}
	public String getFull() {
		return group + name;
	}
	
	public void setGroup(String group) {
		this.group = WordUtils.capitalize(group, '-', '/', ' ');
	}
	public void setName(String name) {
		this.name = WordUtils.capitalize(name, '-', '/', ' ');
	}
}
