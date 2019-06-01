package user_interface.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import database.loader.DirectoryUtil;
import database.loader.LoaderUtil;
import database.loader.Project;
import lifecycle.InstanceManager;
import system.CommonUtil;
import user_interface.factory.NodeUtil;
import user_interface.factory.base.CheckBoxNode;
import user_interface.factory.base.EditNode;
import user_interface.factory.base.TextNode;
import user_interface.factory.util.enums.ColorType;
import user_interface.singleton.utils.SizeUtil;

public class ProjectScene {
    private final Scene scene;

    ProjectScene() {
        scene = create();
    }

    private Scene create() {
        TextNode lblProjectName = new TextNode("Project Name:", ColorType.DEF, ColorType.DEF);
        EditNode edtProjectName = new EditNode("Project1");
        TextNode lblProjectDirectory = new TextNode("Project Directory:", ColorType.DEF, ColorType.DEF);
        EditNode edtProjectDirectory = new EditNode("");
        TextNode btnProjectDirectory = new TextNode("...", ColorType.ALT, ColorType.ALT, ColorType.DEF, ColorType.ALT);
        TextNode lblWorkingDirectory = new TextNode("Working Directory:", ColorType.DEF, ColorType.DEF);
        EditNode edtWorkingDirectory = new EditNode("");
        TextNode btnWorkingDirectory = new TextNode("...", ColorType.ALT, ColorType.ALT, ColorType.DEF, ColorType.ALT);

        double width = SizeUtil.getStringWidth(lblWorkingDirectory.getText()) + lblWorkingDirectory.getPadding().getLeft() + lblWorkingDirectory.getPadding().getRight();
        lblProjectName.setPrefWidth(width);
        lblProjectDirectory.setPrefWidth(width);
        lblWorkingDirectory.setPrefWidth(width);

        lblProjectName.setAlignment(Pos.CENTER_LEFT);
        lblProjectDirectory.setAlignment(Pos.CENTER_LEFT);
        lblWorkingDirectory.setAlignment(Pos.CENTER_LEFT);

        edtProjectName.setPrefWidth(300);
        edtProjectDirectory.setPrefWidth(300);
        edtWorkingDirectory.setPrefWidth(300);

        HBox hBoxProjectName = NodeUtil.getHBox(ColorType.DEF, lblProjectName, edtProjectName);
        HBox hBoxProjectDirectory = NodeUtil.getHBox(ColorType.DEF, lblProjectDirectory, edtProjectDirectory, btnProjectDirectory);
        HBox hBoxWorkingDirectory = NodeUtil.getHBox(ColorType.DEF, lblWorkingDirectory, edtWorkingDirectory, btnWorkingDirectory);

        hBoxProjectName.setSpacing(5 * SizeUtil.getGlobalSpacing());
        hBoxProjectDirectory.setSpacing(5 * SizeUtil.getGlobalSpacing());
        hBoxWorkingDirectory.setSpacing(5 * SizeUtil.getGlobalSpacing());

        CheckBoxNode cbSubdirs = new CheckBoxNode("Search in subdirectories ?");
        cbSubdirs.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> cbSubdirs.setSelected(!cbSubdirs.isSelected()));

        TextNode btnCreateProject = new TextNode("Create Project", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        TextNode btnCancel = new TextNode("Cancel", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);

        btnProjectDirectory.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                String projectDirectory = DirectoryUtil.directoryChooser(scene);
                if (!projectDirectory.isEmpty()) {
                    edtProjectDirectory.setText(projectDirectory);
                }
            }
        });
        btnWorkingDirectory.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                edtWorkingDirectory.setText(DirectoryUtil.directoryChooser(scene));
            }
        });

        btnCreateProject.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            String projectFile = edtProjectDirectory.getText() + edtProjectName.getText() + ".json";
            String workingDirectory = edtWorkingDirectory.getText();

            Project project = new Project(projectFile, workingDirectory);
            project.writeToDisk();
            InstanceManager.getSettings().addProjectPath(projectFile);
            LoaderUtil.startLoading(workingDirectory);
        });
        btnCancel.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> SceneUtil.showIntroScene());

        HBox hBoxCreateCancel = NodeUtil.getHBox(ColorType.DEF, btnCreateProject, btnCancel);

        VBox vBoxMain = NodeUtil.getVBox(ColorType.DEF,
                hBoxProjectName,
                hBoxProjectDirectory,
                hBoxWorkingDirectory,
                cbSubdirs,
                hBoxCreateCancel
        );
        vBoxMain.setSpacing(5 * SizeUtil.getGlobalSpacing());
        vBoxMain.setPadding(new Insets(15 * SizeUtil.getGlobalSpacing()));

        Scene projectScene = new Scene(vBoxMain);
        projectScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                //todo attach to create button
            }
        });

        CommonUtil.updateNodeProperties(projectScene);
        return projectScene;
    }
    void show() {
        InstanceManager.getMainStage().setScene(scene);
    }
}
