package user_interface;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import settings.SettingsEnum;
import system.CommonUtil;
import system.InstanceRepo;
import user_interface.factory.NodeFactory;
import user_interface.factory.node.TitleBar;
import user_interface.factory.node.popup.DataObjectRCM;
import user_interface.factory.node.popup.InfoObjectRCM;
import user_interface.factory.util.enums.ColorType;
import user_interface.singleton.center.FullViewEvent;
import user_interface.singleton.center.TileViewEvent;
import user_interface.singleton.top.TopMenuEvent;

public class MainStage extends Stage implements InstanceRepo {
    private final HBox hBox = NodeFactory.getHBox(ColorType.DEF, infoListViewL, tileView, infoListViewR);
    private final DataObjectRCM dataObjectRCM = new DataObjectRCM();
    private final InfoObjectRCM infoObjectRCM = new InfoObjectRCM();

    public void initialize() {
        logger.debug(this, "user_interface initialize start");
        setDefaultValues();
        initializeEvents();
        logger.debug(this, "user_interface initialize done");
    }
    private void setDefaultValues() {
        TitleBar titleBar = new TitleBar(this, false);
        titleBar.setLeft(topMenu);
        titleBar.setPadding(new Insets(0, 5, 0, 0));
        this.setScene(new Scene(NodeFactory.getVBox(ColorType.DEF, titleBar, hBox)));
        this.initStyle(StageStyle.UNDECORATED);
        HBox.setHgrow(infoListViewL, Priority.ALWAYS);
        HBox.setHgrow(infoListViewR, Priority.ALWAYS);

        this.setOnShowing(event -> reload.doReload());
        this.setOnShown(event -> {
            infoListViewL.postInit();
            infoListViewR.postInit();
            tileView.postInit();

            target.set(mainDataList.get(0));

            CommonUtil.updateNodeProperties();
            this.setWidth(CommonUtil.coreSettings.valueOf(SettingsEnum.MAINSCENE_WIDTH));
            this.setHeight(CommonUtil.coreSettings.valueOf(SettingsEnum.MAINSCENE_HEIGHT));
            this.centerOnScreen();
        });
        this.setOnCloseRequest(event -> {
            mainDataList.writeToDisk();
            logger.debug(this, "application exit");
        });
    }
    private void initializeEvents() {
        new GlobalEvent();

        new TopMenuEvent();
        new TileViewEvent();
        new FullViewEvent();
    }

    public void swapDisplayMode() {
        if (this.isFullView()) {
            hBox.getChildren().set(hBox.getChildren().indexOf(fullView), tileView);
            tileView.adjustViewportToCurrentTarget();
        } else {
            hBox.getChildren().set(hBox.getChildren().indexOf(tileView), fullView);
            fullView.reload();
        }
    }
    public boolean isFullView() {
        return hBox.getChildren().contains(fullView);
    }

    public DataObjectRCM getDataObjectRCM() {
        return dataObjectRCM;
    }
    public InfoObjectRCM getInfoObjectRCM() {
        return infoObjectRCM;
    }
}
