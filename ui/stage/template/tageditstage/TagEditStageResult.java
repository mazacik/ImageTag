package ui.stage.template.tageditstage;

public class TagEditStageResult {
	private String group;
	private String name;
	private boolean addToSelect;
	
	public TagEditStageResult(String group, String name, boolean addToSelect) {
		this.group = group;
		this.name = name;
		this.addToSelect = addToSelect;
	}
	
	public String getGroup() {
		return group;
	}
	public String getName() {
		return name;
	}
	public boolean isAddToSelect() {
		return addToSelect;
	}
}
