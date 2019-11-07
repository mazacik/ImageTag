package application.backend.control.reload;

import application.backend.base.CustomList;

import java.lang.reflect.Method;

public interface Reloadable {
	boolean reload();
	
	default CustomList<Method> getMethodsToInvokeOnNextReload() {
		return null;
	}
}
