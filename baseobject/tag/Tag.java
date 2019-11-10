package baseobject.tag;

import org.apache.commons.text.WordUtils;

public class Tag {
	private String group;
	private String name;
	
	public Tag(String group, String name) {
		set(group, name);
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
		return group + " - " + name;
	}
	
	public void setGroup(String group) {
		this.group = WordUtils.capitalizeFully(group, '-', '/', ' ');
	}
	public void setName(String name) {
		this.name = WordUtils.capitalizeFully(name, '-', '/', ' ');
	}
	public void set(String group, String name) {
		this.setGroup(group);
		this.setName(name);
	}
}
