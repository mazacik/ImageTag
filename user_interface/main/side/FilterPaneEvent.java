package user_interface.main.side;

import javafx.scene.input.MouseButton;
import main.InstanceManager;
import user_interface.nodes.menu.ClickMenuLeft;
import user_interface.stage.StageUtil;

public class FilterPaneEvent {
	public FilterPaneEvent() {
		onMouseClick();
		
		onAction_menuRefresh();
		onAction_menuSettings();
		onAction_menuReset();
	}
	
	private void onMouseClick() {
		InstanceManager.getFilterPane().setOnMouseClicked(event -> InstanceManager.getFilterPane().requestFocus());
	}
	
	private void onAction_menuRefresh() {
		InstanceManager.getFilterPane().getNodeRefresh().setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				InstanceManager.getFilter().refresh();
				InstanceManager.getReload().doReload();
				ClickMenuLeft.hideAll();
			}
		});
	}
	private void onAction_menuSettings() {
		InstanceManager.getFilterPane().getNodeSettings().setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				StageUtil.showStageFilterOptions();
				ClickMenuLeft.hideAll();
			}
		});
	}
	private void onAction_menuReset() {
		InstanceManager.getFilterPane().getNodeReset().setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				InstanceManager.getFilter().reset();
				InstanceManager.getReload().doReload();
				ClickMenuLeft.hideAll();
			}
		});
	}
}
