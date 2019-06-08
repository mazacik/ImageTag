package user_interface.singleton.top;

import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lifecycle.InstanceManager;
import user_interface.factory.menu.ClickMenuLeft;
import user_interface.factory.stage.UserSettingsStage;
import user_interface.utils.SceneUtil;

public class TopMenuEvent {
    public TopMenuEvent() {
        onMouseClick();

        onAction_menuSave();
        onAction_menuImport();
        onAction_menuSettings();
        onAction_menuExit();

        onAction_menuRandom();
        onAction_menuFullView();
    }

    private void onMouseClick() {
        ToolbarPane toolbarPane = InstanceManager.getToolbarPane();
        toolbarPane.setOnMouseClicked(event -> toolbarPane.requestFocus());
    }

    private void onAction_menuSave() {
        ToolbarPane toolbarPane = InstanceManager.getToolbarPane();
        toolbarPane.getNodeSave().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                InstanceManager.getObjectListMain().writeToDisk();
                InstanceManager.getTagListMain().writeDummyToDisk();
                ClickMenuLeft.hideAll();
            }
        });
    }
    private void onAction_menuImport() {
        ToolbarPane toolbarPane = InstanceManager.getToolbarPane();
        toolbarPane.getNodeImport().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                //FileUtil.importFiles();
                ClickMenuLeft.hideAll();
            }
        });
    }
    private void onAction_menuSettings() {
        ToolbarPane toolbarPane = InstanceManager.getToolbarPane();
        toolbarPane.getNodeSettings().setOnMouseClicked(event -> {
            Stage userSettingsStage = new UserSettingsStage();
            userSettingsStage.show();
        });
    }
    private void onAction_menuExit() {
        ToolbarPane toolbarPane = InstanceManager.getToolbarPane();
        toolbarPane.getNodeExit().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                toolbarPane.fireEvent(new WindowEvent(InstanceManager.getMainStage(), WindowEvent.WINDOW_CLOSE_REQUEST));
            }
        });
    }

    private void onAction_menuRandom() {
        ToolbarPane toolbarPane = InstanceManager.getToolbarPane();
        toolbarPane.getNodeRandom().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                InstanceManager.getSelect().setRandom();
                InstanceManager.getReload().doReload();
            }
        });
    }
    private void onAction_menuFullView() {
        ToolbarPane toolbarPane = InstanceManager.getToolbarPane();
        toolbarPane.getNodeFullview().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                SceneUtil.swapViewMode();
                InstanceManager.getReload().doReload();
            }
        });
    }
}
