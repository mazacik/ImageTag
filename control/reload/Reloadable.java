package control.reload;

import baseobject.CustomList;

import java.lang.reflect.Method;

public interface Reloadable {
	boolean reload();
	
	default CustomList<Method> getMethodsToInvokeOnNextReload() {
		return null;
	}
}
