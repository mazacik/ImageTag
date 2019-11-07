package application.frontend.stage.main;

import application.backend.base.CustomList;
import application.backend.loader.Project;
import application.backend.util.FileUtil;
import application.backend.util.enums.SystemUtil;
import application.frontend.component.custom.RecentProjectNode;
import application.frontend.component.simple.EditNode;
import application.frontend.component.simple.SeparatorNode;
import application.frontend.component.simple.TextNode;
import application.frontend.decorator.SizeUtil;
import application.frontend.stage.StageManager;
import application.frontend.stage.base.StageBase;
import application.main.InstanceCollector;
import application.main.LifecycleManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

import java.io.File;

public class IntroStage extends StageBase implements InstanceCollector {
	private HBox introScene;
	private VBox createProjectScene;
	private VBox editProjectScene;
	
	public IntroStage() {
		double width = SizeUtil.getUsableScreenWidth() / 2;
		double height = SizeUtil.getUsableScreenHeight() / 2;
		this.setWidth(width);
		this.setHeight(height);
		this.setMinWidth(width);
		this.setMinHeight(height);
		this.setAlwaysOnTop(false);
		
		this.setOnCloseRequest(event -> settings.writeToDisk());
		
		initIntroScene();
		initCreateProjectScene();
		initEditProjectScene();
		
		showIntroScene();
	}
	
	private VBox vBoxRecentProjects;
	private void initIntroScene() {
		TextNode btnOpenProject = new TextNode("Open Project", false, true, true);
		btnOpenProject.addEventFilter(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, () -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Project");
			fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
			File file = fileChooser.showOpenDialog(StageManager.getMainStage());
			if (file != null) {
				String projectFilePath = file.getAbsolutePath();
				settings.getRecentProjects().add(0, projectFilePath);
				Project project = Project.readFromDisk(projectFilePath);
				LifecycleManager.startLoading(project);
			}
		});
		
		TextNode btnNewProject = new TextNode("Create a New Project", false, true, true);
		btnNewProject.addEventFilter(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, this::showCreateProjectScene);
		
		TextNode applicationNameNode = new TextNode("Tagallery", false, false, false, false);
		applicationNameNode.setFont(new Font(48));
		applicationNameNode.setPadding(new Insets(-20, 0, 20, 0));
		
		VBox vBoxStartMenu = new VBox();
		vBoxStartMenu.setSpacing(10);
		vBoxStartMenu.setMinWidth(500);
		vBoxStartMenu.setAlignment(Pos.CENTER);
		vBoxStartMenu.setPrefWidth(SizeUtil.getUsableScreenWidth());
		vBoxStartMenu.setPadding(new Insets(-40, 0, 40, 0));
		vBoxStartMenu.getChildren().add(new ImageView(new Image("application/frontend/resource/logo-1.png")));
		vBoxStartMenu.getChildren().add(applicationNameNode);
		vBoxStartMenu.getChildren().add(btnNewProject);
		vBoxStartMenu.getChildren().add(btnOpenProject);
		//vBoxStartMenu.getChildren().add(new ColorModeSwitchNode());
		
		vBoxRecentProjects = new VBox();
		vBoxRecentProjects.setMinWidth(400);
		vBoxRecentProjects.setPadding(new Insets(5));
		refreshRecentProjectNodes();
		
		introScene = new HBox(vBoxRecentProjects, new SeparatorNode(), vBoxStartMenu);
		introScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ENTER) {
				CustomList<String> recentProjectFullPaths = settings.getRecentProjects();
				if (!recentProjectFullPaths.isEmpty()) {
					LifecycleManager.startLoading(Project.readFromDisk(recentProjectFullPaths.getFirst()));
				} else {
					showCreateProjectScene();
				}
			}
		});
		VBox.setVgrow(introScene, Priority.ALWAYS);
	}
	private void showIntroScene() {
		this.setTitle("Welcome to Tagallery");
		this.setRoot(introScene);
		introScene.requestFocus();
	}
	public void removeProjectFromRecents(RecentProjectNode recentProjectNode, Project project) {
		CustomList<String> recentProjects = settings.getRecentProjects();
		recentProjects.remove(project.getProjectFileFullPath());
		vBoxRecentProjects.getChildren().remove(recentProjectNode);
		if (recentProjects.isEmpty()) {
			vBoxRecentProjects.setAlignment(Pos.CENTER);
			vBoxRecentProjects.getChildren().add(new TextNode("No Recent Projects"));
		}
	}
	public void refreshRecentProjectNodes() {
		CustomList<String> recentProjectFullPaths = settings.getRecentProjects();
		vBoxRecentProjects.getChildren().clear();
		if (!recentProjectFullPaths.isEmpty()) {
			for (String recentProjectFullPath : new CustomList<>(recentProjectFullPaths)) {
				File projectFile = new File(recentProjectFullPath);
				if (projectFile.exists()) {
					Project project = Project.readFromDisk(recentProjectFullPath);
					vBoxRecentProjects.getChildren().add(new RecentProjectNode(project));
				}
			}
		} else {
			vBoxRecentProjects.setAlignment(Pos.CENTER);
			vBoxRecentProjects.getChildren().add(new TextNode("No Recent Projects"));
		}
	}
	
	private EditNode edtProjectName1;
	private EditNode edtProjectDirectory1;
	private EditNode edtWorkingDirectory1;
	private void initCreateProjectScene() {
		TextNode lblProjectName = new TextNode("Project Name:");
		lblProjectName.setAlignment(Pos.CENTER_LEFT);
		edtProjectName1 = new EditNode("Project1");
		edtProjectName1.setPrefWidth(400);
		TextNode btnHelperProjectName = new TextNode("Browse", true, true, true, true);
		btnHelperProjectName.setVisible(false);
		HBox hBoxProjectName = new HBox(lblProjectName, edtProjectName1, btnHelperProjectName);
		hBoxProjectName.setSpacing(10);
		hBoxProjectName.setAlignment(Pos.CENTER);
		
		TextNode lblProjectDirectory = new TextNode("Project Directory:");
		lblProjectDirectory.setAlignment(Pos.CENTER_LEFT);
		edtProjectDirectory1 = new EditNode("");
		edtProjectDirectory1.setPrefWidth(400);
		TextNode btnProjectDirectory = new TextNode("Browse", true, true, true, true);
		btnProjectDirectory.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				String projectDirectory = FileUtil.directoryChooser(this.getScene());
				if (!projectDirectory.isEmpty()) {
					edtProjectDirectory1.setText(projectDirectory);
				}
			}
		});
		HBox hBoxProjectDirectory = new HBox(lblProjectDirectory, edtProjectDirectory1, btnProjectDirectory);
		hBoxProjectDirectory.setSpacing(10);
		hBoxProjectDirectory.setAlignment(Pos.CENTER);
		
		TextNode lblWorkingDirectory = new TextNode("Working Directory:");
		lblWorkingDirectory.setAlignment(Pos.CENTER_LEFT);
		edtWorkingDirectory1 = new EditNode("");
		edtWorkingDirectory1.setPrefWidth(400);
		TextNode btnWorkingDirectory = new TextNode("Browse", true, true, true, true);
		btnWorkingDirectory.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				edtWorkingDirectory1.setText(FileUtil.directoryChooser(this.getScene()));
			}
		});
		HBox hBoxWorkingDirectory = new HBox(lblWorkingDirectory, edtWorkingDirectory1, btnWorkingDirectory);
		hBoxWorkingDirectory.setSpacing(10);
		hBoxWorkingDirectory.setAlignment(Pos.CENTER);
		
		TextNode btnCreateProject = new TextNode("Create Project", true, true, true, true);
		btnCreateProject.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> createProject(edtProjectName1.getText(), edtProjectDirectory1.getText(), edtWorkingDirectory1.getText()));
		TextNode btnCancel = new TextNode("Cancel", true, true, true, true);
		btnCancel.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> showIntroScene());
		HBox hBoxCreateCancel = new HBox(btnCancel, btnCreateProject);
		
		double width = SizeUtil.getStringWidth(lblWorkingDirectory.getText());
		lblProjectName.setPrefWidth(width);
		lblProjectDirectory.setPrefWidth(width);
		lblWorkingDirectory.setPrefWidth(width);
		
		createProjectScene = new VBox(hBoxProjectName, hBoxProjectDirectory, hBoxWorkingDirectory, hBoxCreateCancel);
		createProjectScene.setPadding(new Insets(10));
		createProjectScene.setSpacing(5);
		createProjectScene.setAlignment(Pos.CENTER);
		createProjectScene.setFillWidth(false);
		createProjectScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ENTER) {
				createProject(edtProjectName1.getText(), edtProjectDirectory1.getText(), edtWorkingDirectory1.getText());
			} else if (event.getCode() == KeyCode.ESCAPE) {
				showIntroScene();
			}
		});
		VBox.setVgrow(createProjectScene, Priority.ALWAYS);
	}
	private void showCreateProjectScene() {
		edtProjectName1.setText("");
		edtProjectDirectory1.setText("");
		edtWorkingDirectory1.setText("");
		
		this.setTitle("Create a New Project");
		this.setRoot(createProjectScene);
	}
	private void createProject(String projectName, String projectDirectory, String workingDirectory) {
		if (!projectDirectory.endsWith(File.separator)) projectDirectory += File.separator;
		if (!projectName.endsWith(".json")) projectName += ".json";
		
		Project project = new Project(projectDirectory + projectName, workingDirectory);
		project.writeToDisk();
		
		settings.getRecentProjects().add(0, project.getProjectFileFullPath());
		settings.writeToDisk();
		LifecycleManager.startLoading(project);
	}
	
	private EditNode edtProjectName2;
	private EditNode edtProjectDirectory2;
	private EditNode edtWorkingDirectory2;
	private void initEditProjectScene() {
		TextNode lblProjectName = new TextNode("Project Name:");
		lblProjectName.setAlignment(Pos.CENTER_LEFT);
		
		edtProjectName2 = new EditNode("Project1");
		edtProjectName2.setPrefWidth(400);
		TextNode btnHelperProjectName = new TextNode("Browse", true, true, true, true);
		btnHelperProjectName.setVisible(false);
		HBox hBoxProjectName = new HBox(lblProjectName, edtProjectName2, btnHelperProjectName);
		hBoxProjectName.setSpacing(10);
		hBoxProjectName.setAlignment(Pos.CENTER);
		
		TextNode lblProjectDirectory = new TextNode("Project Directory:");
		lblProjectDirectory.setAlignment(Pos.CENTER_LEFT);
		
		edtProjectDirectory2 = new EditNode("");
		edtProjectDirectory2.setPrefWidth(400);
		TextNode btnProjectDirectory = new TextNode("Browse", true, true, true, true);
		btnProjectDirectory.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				String projectDirectory = FileUtil.directoryChooser(this.getScene());
				if (!projectDirectory.isEmpty()) {
					edtProjectDirectory2.setText(projectDirectory);
				}
			}
		});
		HBox hBoxProjectDirectory = new HBox(lblProjectDirectory, edtProjectDirectory2, btnProjectDirectory);
		hBoxProjectDirectory.setSpacing(10);
		hBoxProjectDirectory.setAlignment(Pos.CENTER);
		
		TextNode lblWorkingDirectory = new TextNode("Working Directory:");
		lblWorkingDirectory.setAlignment(Pos.CENTER_LEFT);
		edtWorkingDirectory2 = new EditNode("");
		edtWorkingDirectory2.setPrefWidth(400);
		TextNode btnWorkingDirectory = new TextNode("Browse", true, true, true, true);
		btnWorkingDirectory.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				edtWorkingDirectory2.setText(FileUtil.directoryChooser(this.getScene()));
			}
		});
		HBox hBoxWorkingDirectory = new HBox(lblWorkingDirectory, edtWorkingDirectory2, btnWorkingDirectory);
		hBoxWorkingDirectory.setSpacing(10);
		hBoxWorkingDirectory.setAlignment(Pos.CENTER);
		
		TextNode btnApplyChanges = new TextNode("Apply Changes", true, true, true, true);
		btnApplyChanges.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			editProject(edtProjectName2.getText(), edtProjectDirectory2.getText(), edtWorkingDirectory2.getText());
			refreshRecentProjectNodes();
			showIntroScene();
		});
		TextNode btnCancel = new TextNode("Cancel", true, true, true, true);
		btnCancel.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> showIntroScene());
		HBox hBoxCreateCancel = new HBox(btnCancel, btnApplyChanges);
		
		double width = SizeUtil.getStringWidth(lblWorkingDirectory.getText());
		lblProjectName.setPrefWidth(width);
		lblProjectDirectory.setPrefWidth(width);
		lblWorkingDirectory.setPrefWidth(width);
		
		editProjectScene = new VBox(hBoxProjectName, hBoxProjectDirectory, hBoxWorkingDirectory, hBoxCreateCancel);
		editProjectScene.setPadding(new Insets(10));
		editProjectScene.setSpacing(5);
		editProjectScene.setAlignment(Pos.CENTER);
		editProjectScene.setFillWidth(false);
		editProjectScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ENTER) {
				editProject(edtProjectName2.getText(), edtProjectDirectory2.getText(), edtWorkingDirectory2.getText());
				refreshRecentProjectNodes();
				showIntroScene();
			} else if (event.getCode() == KeyCode.ESCAPE) {
				showIntroScene();
			}
		});
		VBox.setVgrow(editProjectScene, Priority.ALWAYS);
	}
	public void showEditProjectScene(Project project) {
		edtProjectName2.setText(project.getProjectFileName());
		edtProjectDirectory2.setText(project.getProjectDirectory());
		edtWorkingDirectory2.setText(project.getWorkingDirectory());
		
		projectFullPathBeforeEdit = project.getProjectFileFullPath();
		
		this.setTitle("Edit Project");
		this.setRoot(editProjectScene);
	}
	private String projectFullPathBeforeEdit;
	private void editProject(String projectName, String projectDirectory, String workingDirectory) {
		if (!projectDirectory.endsWith(File.separator)) projectDirectory += File.separator;
		if (!projectName.endsWith(".json")) projectName += ".json";
		
		String projectFile = projectDirectory + projectName;
		Project project = new Project(projectFile, workingDirectory);
		
		SystemUtil.deleteFile(projectFullPathBeforeEdit);
		project.writeToDisk();
		
		settings.getRecentProjects().remove(projectFullPathBeforeEdit);
		settings.getRecentProjects().add(0, project.getProjectFileFullPath());
		settings.writeToDisk();
	}
	
	@Override
	public Object _show(String... args) {
		this.show();
		return null;
	}
}
