package base.tag;

import base.CustomList;
import com.google.gson.annotations.SerializedName;

import java.util.Random;

public class Tag {
	@SerializedName("I") private int id;
	@SerializedName("L") private CustomList<String> levels;
	
	private transient String stringValue;
	
	public Tag(CustomList<String> levels) {
		this.id = new Random().nextInt();
		this.levels = levels;
	}
	
	public boolean isEmpty() {
		for (String s : levels) {
			if (s.isEmpty()) {
				return true;
			}
		}
		return false;
	}
	
	public String getLevel(int level) {
		return levels.get(level);
	}
	public String getLevelLast() {
		return levels.get(levels.size() - 1);
	}
	
	public String getStringValue() {
		return stringValue;
	}
	public void setStringValue() {
		StringBuilder builder = new StringBuilder();
		levels.forEach(builder::append);
		stringValue = builder.toString();
	}
	
	public int getID() {
		return id;
	}
	public CustomList<String> getLevels() {
		return levels;
	}
	
	public void replaceLevelsFromStart(int count, CustomList<String> levels) {
		CustomList<String> levelsNew = new CustomList<>(levels);
		for (int i = count; i < this.levels.size(); i++) {
			levelsNew.add(this.levels.get(i));
		}
		this.levels.setAll(levelsNew);
		setStringValue();
	}
}
