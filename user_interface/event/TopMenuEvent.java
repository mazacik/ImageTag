package user_interface.event;

import javafx.scene.input.MouseButton;
import javafx.stage.WindowEvent;
import system.CommonUtil;
import system.InstanceRepo;
import user_interface.factory.node.popup.LeftClickMenu;

import java.io.IOException;

public class TopMenuEvent implements InstanceRepo {
    public TopMenuEvent() {
        onMouseClick();

        onAction_menuSave();
        onAction_menuImport();
        onAction_menuExit();

        onAction_menuInpaint();

        onAction_menuRandom();
        onAction_menuFullView();
    }

    private void onMouseClick() {
        topMenu.setOnMouseClicked(event -> topMenu.requestFocus());
    }

    private void onAction_menuSave() {
        topMenu.getNodeSave().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                mainDataList.writeToDisk();
                hideLeftClickMenus();
            }
        });
    }
    private void onAction_menuImport() {
        topMenu.getNodeImport().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                CommonUtil.importFiles();
                hideLeftClickMenus();
            }
        });
    }
    private void onAction_menuExit() {
        topMenu.getNodeExit().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                topMenu.fireEvent(new WindowEvent(mainStage, WindowEvent.WINDOW_CLOSE_REQUEST));
            }
        });
    }

    private void onAction_menuInpaint() {
        topMenu.getNodeInpaint().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                try {
                    //Runtime.getRuntime().exec(settings.getInpaintExecutable);
                    Runtime.getRuntime().exec("C:\\Michal\\Program\\Inpaint v7.2\\Inpaint.exe");
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    private void onAction_menuRandom() {
        topMenu.getNodeRandom().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                select.setRandom();
                reload.doReload();
            }
        });
    }
    private void onAction_menuFullView() {
        topMenu.getNodeFullview().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                CommonUtil.swapDisplayMode();
            }
        });
    }
}
