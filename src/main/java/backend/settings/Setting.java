package backend.settings;

public class Setting<T> {
	
	private T value;
	
	private transient T defaultValue;
	private transient T minValue;
	private transient T maxValue;
	
	private transient final boolean userModifiable;
	
	public Setting(T defaultValue, boolean userModifiable) {
		this(defaultValue, null, null, userModifiable);
	}
	
	public Setting(T defaultValue, T minValue, T maxValue, boolean userModifiable) {
		this(defaultValue, defaultValue, minValue, maxValue, userModifiable);
	}
	
	public Setting(T value, T defaultValue, T minValue, T maxValue, boolean userModifiable) {
		this.value = value;
		this.defaultValue = defaultValue;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.userModifiable = userModifiable;
	}
	
	public T resetValue() {
		value = defaultValue;
		return value;
	}
	
	public T getValue() {
		return value;
	}
	
	public void setValue(T value) {
		this.value = value;
	}
	
	public T getDefaultValue() {
		return defaultValue;
	}
	
	public T getMinValue() {
		return minValue;
	}
	
	public T getMaxValue() {
		return maxValue;
	}
	
	public boolean isUserModifiable() {
		return userModifiable;
	}
	
}
