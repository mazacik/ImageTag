package user_interface.singleton.side;

import control.filter.FilterManager;
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
        InstanceManager.getTagListViewL().setOnMouseClicked(event -> InstanceManager.getTagListViewL().requestFocus());
    }

    private void onAction_menuSettings() {
        InstanceManager.getTagListViewL().getNodeSettings().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                StageUtil.show(Stages.FILTER_SETTINGS);
                ClickMenuLeft.hideAll();
            }
        });
    }
    private void onAction_menuReset() {
        InstanceManager.getTagListViewL().getNodeReset().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                FilterManager.reset();
                InstanceManager.getReload().doReload();
                ClickMenuLeft.hideAll();
            }
        });
    }
}
