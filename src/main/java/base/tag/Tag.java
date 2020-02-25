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
	
	public void replaceLevelsFromStart(int numLevelsToReplaceFromStart, CustomList<String> listLevelsToReplaceWith) {
		for (int i = numLevelsToReplaceFromStart; i < levels.size(); i++) {
			listLevelsToReplaceWith.add(levels.get(i));
		}
		
		this.levels.setAll(listLevelsToReplaceWith);
		updateStringValue();
		
		for (Tag tag : TagList.getMain()) {
			if (stringValue.startsWith(tag.getStringValue()) && tag != this) {
				tag.levels.add(tag.levels.getLast());
				tag.updateStringValue();
			}
		}
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
	public CustomList<String> getLevels() {
		return levels;
	}
}
