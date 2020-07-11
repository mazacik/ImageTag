package backend.reload;

import frontend.UserInterface;
import main.Main;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public enum InvokeHelper {
	FILTER_REFRESH(1, Main.FILTER, method(Main.FILTER, "refresh")),
	
	PANE_FILTER_RELOAD(3, UserInterface.getFilterPane(), method(UserInterface.getFilterPane(), "reload")),
	PANE_SELECT_RELOAD(3, UserInterface.getSelectPane(), method(UserInterface.getSelectPane(), "reload")),
	
	PANE_FILTER_REFRESH(4, UserInterface.getFilterPane(), method(UserInterface.getFilterPane(), "refresh")),
	PANE_SELECT_REFRESH(4, UserInterface.getSelectPane(), method(UserInterface.getSelectPane(), "refresh")),
	
	PANE_TOOLBAR_RELOAD(5, UserInterface.getToolbarPane(), method(UserInterface.getToolbarPane(), "reload")),
	PANE_GALLERY_RELOAD(5, UserInterface.getGalleryPane(), method(UserInterface.getGalleryPane(), "reload")),
	PANE_DISPLAY_RELOAD(5, UserInterface.getDisplayPane(), method(UserInterface.getDisplayPane(), "reload")),
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
