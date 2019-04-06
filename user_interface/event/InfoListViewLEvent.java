package user_interface.event;

import control.filter.FilterTemplate;
import javafx.scene.input.MouseButton;
import system.InstanceRepo;
import user_interface.factory.node.popup.LeftClickMenu;
import user_interface.factory.stage.NumberInputStage;

public class InfoListViewLEvent implements InstanceRepo {
    public InfoListViewLEvent() {
        onMouseClick();

        onAction_menuLimit();
        onAction_menuReset();
    }

    private void onMouseClick() {
        infoListViewL.setOnMouseClicked(event -> infoListViewL.requestFocus());
    }

    private void onAction_menuLimit() {
        infoListViewL.getNodeLimit().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (event.isControlDown()) {
                    int maxTags = new NumberInputStage("Maximum number of tags:").getResult();
                    if (maxTags == -1) return;
                    FilterTemplate.setMaxTagsValue(maxTags);
                } else {
                    FilterTemplate.setMaxTagsValue(0);
                }

                filter.setFilter(FilterTemplate.SHOW_MAX_X_TAGS);
                reload.doReload();
                hideLeftClickMenus();
            }
        });
    }
    private void onAction_menuReset() {
        infoListViewL.getNodeReset().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                filter.setFilter(FilterTemplate.SHOW_EVERYTHING);
                reload.doReload();
                hideLeftClickMenus();
            }
        });
    }

    private void hideLeftClickMenus() {
        LeftClickMenu.getInstanceList().forEach(leftClickMenu -> {
            if (leftClickMenu.isShowing()) {
                leftClickMenu.hide();
            }
        });
    }
}
