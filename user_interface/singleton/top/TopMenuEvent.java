package user_interface.singleton.top;

import database.loader.LoaderUtil;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lifecycle.InstanceManager;
import system.CommonUtil;
import user_interface.factory.menu.ClickMenuLeft;
import user_interface.factory.stage.UserSettingsStage;

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
        TopMenu topMenu = InstanceManager.getTopMenu();
        topMenu.setOnMouseClicked(event -> topMenu.requestFocus());
    }

    private void onAction_menuSave() {
        TopMenu topMenu = InstanceManager.getTopMenu();
        topMenu.getNodeSave().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                InstanceManager.getMainDataList().writeToDisk();
                ClickMenuLeft.hideAll();
            }
        });
    }
    private void onAction_menuImport() {
        TopMenu topMenu = InstanceManager.getTopMenu();
        topMenu.getNodeImport().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                LoaderUtil.importFiles();
                ClickMenuLeft.hideAll();
            }
        });
    }
    private void onAction_menuSettings() {
        TopMenu topMenu = InstanceManager.getTopMenu();
        topMenu.getNodeSettings().setOnMouseClicked(event -> {
            Stage userSettingsStage = new UserSettingsStage();
            userSettingsStage.show();
            CommonUtil.updateNodeProperties(userSettingsStage.getScene());
        });
    }
    private void onAction_menuExit() {
        TopMenu topMenu = InstanceManager.getTopMenu();
        topMenu.getNodeExit().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                topMenu.fireEvent(new WindowEvent(InstanceManager.getMainStage(), WindowEvent.WINDOW_CLOSE_REQUEST));
            }
        });
    }

    private void onAction_menuRandom() {
        TopMenu topMenu = InstanceManager.getTopMenu();
        topMenu.getNodeRandom().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                InstanceManager.getSelect().setRandom();
                InstanceManager.getReload().doReload();
            }
        });
    }
    private void onAction_menuFullView() {
        TopMenu topMenu = InstanceManager.getTopMenu();
        topMenu.getNodeFullview().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                CommonUtil.swapViewMode();
                InstanceManager.getReload().doReload();
            }
        });
    }
}
