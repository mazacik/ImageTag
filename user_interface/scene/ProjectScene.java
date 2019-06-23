package user_interface.scene;

import database.loader.Project;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lifecycle.InstanceManager;
import lifecycle.LifeCycleManager;
import user_interface.nodes.NodeUtil;
import user_interface.nodes.base.EditNode;
import user_interface.nodes.base.TextNode;
import user_interface.style.SizeUtil;
import user_interface.style.StyleUtil;
import user_interface.style.enums.ColorType;
import utils.FileUtil;

public class ProjectScene {
    private final Scene scene;

    public ProjectScene() {
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

        HBox hBoxProjectName = NodeUtil.getHBox(ColorType.DEF, ColorType.DEF, lblProjectName, edtProjectName);
        HBox hBoxProjectDirectory = NodeUtil.getHBox(ColorType.DEF, ColorType.DEF, lblProjectDirectory, edtProjectDirectory, btnProjectDirectory);
        HBox hBoxWorkingDirectory = NodeUtil.getHBox(ColorType.DEF, ColorType.DEF, lblWorkingDirectory, edtWorkingDirectory, btnWorkingDirectory);

        hBoxProjectName.setSpacing(5 * SizeUtil.getGlobalSpacing());
        hBoxProjectDirectory.setSpacing(5 * SizeUtil.getGlobalSpacing());
        hBoxWorkingDirectory.setSpacing(5 * SizeUtil.getGlobalSpacing());

        TextNode btnCreateProject = new TextNode("Create Project", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        TextNode btnCancel = new TextNode("Cancel", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);

        btnProjectDirectory.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                String projectDirectory = FileUtil.directoryChooser(scene);
                if (!projectDirectory.isEmpty()) {
                    edtProjectDirectory.setText(projectDirectory);
                }
            }
        });
        btnWorkingDirectory.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                edtWorkingDirectory.setText(FileUtil.directoryChooser(scene));
            }
        });

        btnCreateProject.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> createProject(edtProjectDirectory.getText(), edtProjectName.getText()));
        btnCancel.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> SceneUtil.showIntroScene());

        HBox hBoxCreateCancel = NodeUtil.getHBox(ColorType.DEF, ColorType.DEF, btnCreateProject, btnCancel);

        VBox vBoxMain = NodeUtil.getVBox(ColorType.DEF, ColorType.DEF,
                hBoxProjectName,
                hBoxProjectDirectory,
                hBoxWorkingDirectory,
                hBoxCreateCancel
        );
        vBoxMain.setSpacing(5 * SizeUtil.getGlobalSpacing());
        vBoxMain.setPadding(new Insets(15 * SizeUtil.getGlobalSpacing()));

        Scene projectScene = new Scene(vBoxMain);
        projectScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                createProject(edtProjectDirectory.getText(), edtProjectName.getText());
            }
        });

        return projectScene;
    }

    private void createProject(String projectDirectory, String projectName) {
        String projectFile = projectDirectory + projectName + ".json";
        Project project = new Project(projectFile, projectDirectory);
        project.writeToDisk();
        InstanceManager.getSettings().addProjectPath(projectFile);
        LifeCycleManager.startLoading(project);
    }

    public void show() {
        InstanceManager.getMainStage().setScene(scene);
		StyleUtil.applyStyle();
    }
}
