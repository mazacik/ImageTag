package backend.control.reload;

import main.Root;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public enum InvokeHelper {
	FILTER_REFRESH(1, Root.FILTER, method(Root.FILTER, "refresh")),
	
	PANE_FILTER_RELOAD(3, Root.FILTER_PANE, method(Root.FILTER_PANE, "reload")),
	PANE_SELECT_RELOAD(3, Root.SELECT_PANE, method(Root.SELECT_PANE, "reload")),
	
	PANE_FILTER_REFRESH(4, Root.FILTER_PANE, method(Root.FILTER_PANE, "refresh")),
	PANE_SELECT_REFRESH(4, Root.SELECT_PANE, method(Root.SELECT_PANE, "refresh")),
	
	PANE_TOOLBAR_RELOAD(5, Root.TOOLBAR_PANE, method(Root.TOOLBAR_PANE, "reload")),
	PANE_GALLERY_RELOAD(5, Root.GALLERY_PANE, method(Root.GALLERY_PANE, "reload")),
	PANE_DISPLAY_RELOAD(5, Root.DISPLAY_PANE, method(Root.DISPLAY_PANE, "reload")),
	;
	
	private static Method method(Object object, String methodName) {
		try {
			return object.getClass().getMethod(methodName);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private final int priority;
	private final Object instance;
	private final Method method;
	
	InvokeHelper(int priority, Object instance, Method method) {
		this.priority = priority;
		this.instance = instance;
		this.method = method;
	}
	
	public void invoke() {
		try {
			method.invoke(instance);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public int getPriority() {
		return priority;
	}
}
