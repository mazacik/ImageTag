package gui.stage.template;

public enum ButtonBooleanValue {
	YES(true),
	NO(false),
	CANCEL(false);
	
	private boolean booleanValue;
	
	ButtonBooleanValue(boolean booleanValue) {
		this.booleanValue = booleanValue;
	}
	
	public boolean getBooleanValue() {
		return booleanValue;
	}
}
