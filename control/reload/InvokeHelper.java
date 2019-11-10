package control.reload;

import java.lang.reflect.Method;

public class InvokeHelper {
	private Reloadable instance;
	private Method method;
	
	public InvokeHelper(Reloadable instance, Method method) {
		this.instance = instance;
		this.method = method;
	}
	
	public Reloadable getInstance() {
		return instance;
	}
	public Method getMethod() {
		return method;
	}
}
