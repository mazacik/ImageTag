package user_interface.singleton.side;

import control.filter.FilterManager;
import javafx.scene.input.MouseButton;
import system.Instances;
import user_interface.factory.menu.ClickMenuLeft;
import user_interface.factory.stage.StageUtil;
import user_interface.factory.stage.Stages;

public class TagListViewLEvent implements Instances {
    public TagListViewLEvent() {
        onMouseClick();

        onAction_menuSettings();
        onAction_menuReset();
    }

    private void onMouseClick() {
        tagListViewL.setOnMouseClicked(event -> tagListViewL.requestFocus());
    }

    private void onAction_menuSettings() {
        tagListViewL.getNodeSettings().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                StageUtil.show(Stages.FILTER_SETTINGS);
                ClickMenuLeft.hideAll();
            }
        });
    }
    private void onAction_menuReset() {
        tagListViewL.getNodeReset().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                FilterManager.reset();
                reload.doReload();
                ClickMenuLeft.hideAll();
            }
        });
    }
}
