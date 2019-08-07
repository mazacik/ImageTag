package application.gui.panes.side;

import application.gui.nodes.popup.ClickMenuLeft;
import application.gui.stage.StageUtil;
import application.main.Instances;
import javafx.scene.input.MouseButton;

public class FilterPaneEvent {
	public FilterPaneEvent() {
		onMouseClick();
		
		onAction_menuRefresh();
		onAction_menuSettings();
		onAction_menuReset();
	}
	
	private void onMouseClick() {
		Instances.getFilterPane().setOnMouseClicked(event -> Instances.getFilterPane().requestFocus());
	}
	
	private void onAction_menuRefresh() {
		Instances.getFilterPane().getNodeRefresh().setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				Instances.getFilter().refresh();
				Instances.getReload().doReload();
				ClickMenuLeft.hideAll();
			}
		});
	}
	private void onAction_menuSettings() {
		Instances.getFilterPane().getNodeSettings().setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				StageUtil.showStageFilterOptions();
				ClickMenuLeft.hideAll();
			}
		});
	}
	private void onAction_menuReset() {
		Instances.getFilterPane().getNodeReset().setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				Instances.getFilter().reset();
				Instances.getReload().doReload();
				ClickMenuLeft.hideAll();
			}
		});
	}
}
