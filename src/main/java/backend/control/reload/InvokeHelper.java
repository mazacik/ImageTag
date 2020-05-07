package backend.control.reload;

import main.Main;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public enum InvokeHelper {
	FILTER_REFRESH(1, Main.FILTER, method(Main.FILTER, "refresh")),
	
	PANE_FILTER_RELOAD(3, Main.FILTER_PANE, method(Main.FILTER_PANE, "reload")),
	PANE_SELECT_RELOAD(3, Main.SELECT_PANE, method(Main.SELECT_PANE, "reload")),
	
	PANE_FILTER_REFRESH(4, Main.FILTER_PANE, method(Main.FILTER_PANE, "refresh")),
	PANE_SELECT_REFRESH(4, Main.SELECT_PANE, method(Main.SELECT_PANE, "refresh")),
	
	PANE_TOOLBAR_RELOAD(5, Main.TOOLBAR_PANE, method(Main.TOOLBAR_PANE, "reload")),
	PANE_GALLERY_RELOAD(5, Main.GALLERY_PANE, method(Main.GALLERY_PANE, "reload")),
	PANE_DISPLAY_RELOAD(5, Main.DISPLAY_PANE, method(Main.DISPLAY_PANE, "reload")),
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
