package user_interface.main.side;

import javafx.scene.input.MouseButton;
import lifecycle.InstanceManager;
import user_interface.nodes.menu.ClickMenuLeft;
import user_interface.stage.StageUtil;
import user_interface.stage.Stages;

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
				StageUtil.show(Stages.STAGE_FILTER_SETTINGS);
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
