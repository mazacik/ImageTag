package backend.misc;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class ListType implements ParameterizedType {
	
	private final Type type;
	
	public ListType(Type type) {
		this.type = type;
	}
	
	@Override
	public Type[] getActualTypeArguments() {
		return new Type[]{type};
	}
	
	@Override
	public Type getRawType() {
		return List.class;
	}
	
	@Override
	public Type getOwnerType() {
		return null;
	}
	
}
