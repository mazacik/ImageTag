package user_interface.singleton.side;

import javafx.scene.input.MouseButton;
import lifecycle.InstanceManager;
import user_interface.factory.menu.ClickMenuLeft;
import user_interface.factory.stage.StageUtil;
import user_interface.factory.stage.Stages;

public class TagListViewLEvent {
    public TagListViewLEvent() {
        onMouseClick();

        onAction_menuSettings();
        onAction_menuReset();
    }

    private void onMouseClick() {
        InstanceManager.getFilterPane().setOnMouseClicked(event -> InstanceManager.getFilterPane().requestFocus());
    }

    private void onAction_menuSettings() {
        InstanceManager.getFilterPane().getNodeSettings().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                StageUtil.show(Stages.FILTER_SETTINGS);
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
