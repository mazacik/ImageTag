package base.tag;

import base.CustomList;
import com.google.gson.annotations.SerializedName;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;

public class Tag {
	@SerializedName("I") private int id;
	@SerializedName("L") private CustomList<String> list;
	
	public Tag(CustomList<String> list) {
		this.id = new Random().nextInt();
		this.list = list;
	}
	public Tag(String... strings) {
		this(new CustomList<>(Arrays.asList(strings)));
	}
	
	public boolean isEmpty() {
		for (String s : list) {
			if (s.isEmpty()) {
				return true;
			}
		}
		return false;
	}
	
	public int getID() {
		return id;
	}
	public String getString() {
		StringBuilder builder = new StringBuilder();
		for (String s : list) {
			builder.append(s);
		}
		return builder.toString();
	}
	public String getLevel(int level) {
		return list.get(level);
	}
	
	//todo remove
	public String getGroup() {
		return getLevel(0);
	}
	public String getName() {
		return getLevel(1);
	}
}
