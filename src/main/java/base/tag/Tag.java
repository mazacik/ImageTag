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
		
		updateStringValue();
	}
	
	public void replaceLevelsFromStart(int numLevelsToReplace, CustomList<String> listLevelsToReplaceWith) {
		CustomList<String> listLevelsAfter = new CustomList<>(listLevelsToReplaceWith);
		for (int i = numLevelsToReplace; i < this.levels.size(); i++) {
			listLevelsAfter.add(this.levels.get(i));
		}
		
		StringBuilder builder = new StringBuilder();
		listLevelsAfter.forEach(builder::append);
		String stringValueAfter = builder.toString();
		
		for (Tag tag : TagList.getMain()) {
			if (stringValueAfter.startsWith(tag.getStringValue())) {
				//todo needs testing
				tag.levels.add("_temp_");
				tag.updateStringValue();
			}
		}
		
		this.levels.setAll(listLevelsAfter);
		this.stringValue = stringValueAfter;
	}
	
	private transient String stringValue;
	public String getStringValue() {
		return stringValue;
	}
	public void updateStringValue() {
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
	public String getLevelLast() {
		return levels.get(levels.size() - 1);
	}
}
