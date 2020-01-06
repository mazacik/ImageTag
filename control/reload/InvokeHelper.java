package control.reload;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InvokeHelper {
	private Object instance;
	private Method method;
	
	public InvokeHelper(Object instance, Method method) {
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
}
