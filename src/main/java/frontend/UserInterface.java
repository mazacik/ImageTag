package frontend;

import frontend.component.center.CenterPane;
import frontend.component.side.FilterPane;
import frontend.component.side.select.SelectPane;
import frontend.component.top.ToolbarPane;
import frontend.stage.primary.PrimaryStage;

public abstract class UserInterface {
	public static final int SIDE_WIDTH = 300;
	
	public static void initialize() {
		toolbarPane.initialize();
		centerPane.initialize();
		filterPane.initialize();
		selectPane.initialize();
	}
	
	private static final ToolbarPane toolbarPane = new ToolbarPane();
	public static ToolbarPane getToolbarPane() {
		return toolbarPane;
	}
	
	private static final CenterPane centerPane = new CenterPane();
	public static CenterPane getCenterPane() {
		return centerPane;
	}
	
	private static final FilterPane filterPane = new FilterPane();
	public static FilterPane getFilterPane() {
		return filterPane;
	}
	
	private static final SelectPane selectPane = new SelectPane();
	public static SelectPane getSelectPane() {
		return selectPane;
	}
	
	private static final PrimaryStage stage = new PrimaryStage();
	public static PrimaryStage getStage() {
		return stage;
	}
}
