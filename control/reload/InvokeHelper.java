package control.reload;

import java.lang.reflect.Method;

public class InvokeHelper {
	private Object instance;
	private Method method;
	
	public InvokeHelper(Object instance, Method method) {
		this.instance = instance;
		this.method = method;
	}
	
	public Object getInstance() {
		return instance;
	}
	public Method getMethod() {
		return method;
	}
}
