package backend.list.tag;

import backend.list.BaseList;
import com.google.gson.annotations.SerializedName;

import java.util.Random;

public class Tag {
	@SerializedName("I") private int id;
	@SerializedName("L") private BaseList<String> levels;
	
	public Tag(BaseList<String> levels) {
		this.id = new Random().nextInt();
		this.levels = levels;
		
		updateStringValue();
	}
	
	public void replaceLevelsFromStart(int numLevelsToReplaceFromStart, BaseList<String> listLevelsToReplaceWith) {
		BaseList<String> listLevelsToReplaceWithLocal = new BaseList<>(listLevelsToReplaceWith);
		for (int i = numLevelsToReplaceFromStart; i < levels.size(); i++) {
			listLevelsToReplaceWithLocal.add(levels.get(i));
		}
		
		levels.setAll(listLevelsToReplaceWithLocal);
		updateStringValue();
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
	public BaseList<String> getLevels() {
		return levels;
	}
}
