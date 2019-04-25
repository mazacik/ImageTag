package user_interface.event;

import control.filter.FilterTemplate;
import javafx.scene.input.MouseButton;
import system.InstanceRepo;
import user_interface.factory.node.popup.LeftClickMenu;
import user_interface.factory.stage.NumberInputStage;

public class TagListViewLEvent implements InstanceRepo {
    public TagListViewLEvent() {
        onMouseClick();

        onAction_menuNoTags();
        onAction_menuLimit();
        onAction_menuReset();
    }

    private void onMouseClick() {
        tagListViewL.setOnMouseClicked(event -> tagListViewL.requestFocus());
    }

    private void onAction_menuNoTags() {
        tagListViewL.getNodeNoTags().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                FilterTemplate.setMaxTagsValue(0);
                filter.setFilter(FilterTemplate.SHOW_MAX_X_TAGS);
                reload.doReload();
                hideLeftClickMenus();
            }
        });
    }
    private void onAction_menuLimit() {
        tagListViewL.getNodeLimit().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                int maxTags = new NumberInputStage("Maximum number of tags:").getResult();
                if (maxTags == -1) return;
                FilterTemplate.setMaxTagsValue(maxTags);
                filter.setFilter(FilterTemplate.SHOW_MAX_X_TAGS);
                reload.doReload();
                hideLeftClickMenus();
            }
        });
    }
    private void onAction_menuReset() {
        tagListViewL.getNodeReset().setOnMouseClicked(event -> {
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