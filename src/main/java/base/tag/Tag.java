package base.tag;

import base.CustomList;
import com.google.gson.annotations.SerializedName;

import java.util.Random;

public class Tag {
	@SerializedName("I") private int id;
	@SerializedName("L") private CustomList<String> levels;
	
	public Tag(CustomList<String> levels) {
		this.id = new Random().nextInt();
		this.levels = levels;
		
		setStringValue();
	}
	
	public void replaceLevelsFromStart(int levelCount, CustomList<String> levels) {
		CustomList<String> levelsNew = new CustomList<>(levels);
		for (int i = levelCount; i < this.levels.size(); i++) {
			levelsNew.add(this.levels.get(i));
		}
		this.levels.setAll(levelsNew);
		setStringValue();
	}
	
	private transient String stringValue;
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
	public int getNumLevels() {
		return levels.size();
	}
	public String getLevel(int level) {
		return levels.get(level);
	}
}
