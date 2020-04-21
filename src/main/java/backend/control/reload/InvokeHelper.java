package backend.control.reload;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InvokeHelper {
	private int priority;
	private Object instance;
	private Method method;
	
	public InvokeHelper(int priority, Object instance, Method method) {
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
