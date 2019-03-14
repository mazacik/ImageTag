package user_interface.node_factory.template.intro;

import database.object.DataLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import system.CommonUtil;
import system.InstanceRepo;
import user_interface.node_factory.NodeFactory;
import user_interface.node_factory.template.ColorModeSwitch;
import user_interface.node_factory.template.generic.TitleBar;
import user_interface.node_factory.template.generic.WindowDirectoryChooser;
import user_interface.node_factory.utils.ColorType;

import java.io.File;
import java.util.ArrayList;

public class IntroWindow extends Stage implements InstanceRepo {
    public IntroWindow() {
        VBox vBoxL = NodeFactory.getVBox(ColorType.ALT);

        ArrayList<String> recentDirectoriesListClone = new ArrayList<>(coreSettings.getRecentDirectoriesList());
        recentDirectoriesListClone.forEach(item -> {
            if (new File(item).exists()) {
                vBoxL.getChildren().add(NodeFactory.getIntroWindowCell(item));
            } else {
                logger.debug(this, item + " does not exist, removing it from recent directory list");
                coreSettings.getRecentDirectoriesList().remove(item);
            }
        });
        vBoxL.getChildren().forEach(node -> {
            if (node instanceof IntroWindowCell) {
                node.setOnMouseClicked(event -> {
                    if (event.getPickResult().getIntersectedNode().getParent().equals(((IntroWindowCell) node).getNodeRemove())) {
                        coreSettings.getRecentDirectoriesList().remove(((IntroWindowCell) node).getPath());
                        vBoxL.getChildren().remove(node);
                    } else {
                        startLoading((IntroWindowCell) node);
                    }
                });
            }
        });
        vBoxL.setPadding(new Insets(5));
        vBoxL.setPrefWidth(200);
        NodeFactory.addNodeToBackgroundManager(vBoxL, ColorType.ALT);

        Label btnChoose = NodeFactory.getLabel("Choose a Directory", ColorType.DEF, ColorType.DEF, ColorType.DEF, ColorType.ALT);
        btnChoose.setFont(CommonUtil.getFont());
        btnChoose.setFocusTraversable(false);
        btnChoose.setOnMouseClicked(event -> this.directoryChooser());

        VBox vBoxR = NodeFactory.getVBox(ColorType.DEF);
        vBoxR.getChildren().add(btnChoose);
        vBoxR.getChildren().add(new ColorModeSwitch());
        vBoxR.setSpacing(10);
        vBoxR.setPrefWidth(500);
        vBoxR.setAlignment(Pos.CENTER);
        NodeFactory.addNodeToBackgroundManager(vBoxR, ColorType.DEF);

        HBox hBox = NodeFactory.getHBox(ColorType.DEF, vBoxL, vBoxR);
        VBox.setVgrow(hBox, Priority.ALWAYS);

        VBox vBoxMain = NodeFactory.getVBox(ColorType.DEF);
        vBoxMain.getChildren().add(new TitleBar(this, "Welcome"));
        vBoxMain.getChildren().add(hBox);

        Scene introScene = new Scene(vBoxMain);
        introScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (!coreSettings.getRecentDirectoriesList().isEmpty()) {
                    this.startLoading((IntroWindowCell) vBoxL.getChildren().get(0));
                } else {
                    this.directoryChooser();
                }
            }
        });
        CommonUtil.updateNodeProperties();
        this.setScene(introScene);
        this.setHeight(649); //todo dynamic calculation?
        this.initStyle(StageStyle.UNDECORATED);
        this.setResizable(false);
        this.centerOnScreen();
        this.setOnCloseRequest(event -> {
            userSettings.writeToDisk();
            logger.debug(this, "application exit");
        });
        this.show();
        logger.debug(this, "waiting for directory");
    }

    private void directoryChooser() {
        String sourcePath = new WindowDirectoryChooser(this, "Choose a Directory", "C:\\").getResultValue();
        if (!sourcePath.isEmpty()) {
            char lastchar = sourcePath.charAt(sourcePath.length() - 1);
            if (sourcePath.length() > 3 && (lastchar != '\\' || lastchar != '/')) {
                sourcePath += "\\";
            }
            coreSettings.setCurrentDirectory(sourcePath);
            new DataLoader().start();
            this.close();
        }
    }

    private void startLoading(IntroWindowCell introWindowCell) {
        coreSettings.setCurrentDirectory(introWindowCell.getPath());
        userSettings.writeToDisk();
        new DataLoader().start();
        this.close();
    }
}
