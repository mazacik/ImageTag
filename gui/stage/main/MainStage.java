package gui.stage.main;

import baseobject.CustomList;
import baseobject.Project;
import gui.component.simple.*;
import gui.decorator.ColorPreset;
import gui.decorator.ColorUtil;
import gui.decorator.Decorator;
import gui.decorator.SizeUtil;
import gui.main.center.MediaPaneControls;
import gui.main.center.VideoPlayer;
import gui.stage.StageManager;
import gui.stage.base.StageBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import main.InstanceCollector;
import main.LifecycleManager;
import tools.FileUtil;

import java.awt.*;
import java.io.File;

public class MainStage extends StageBase implements InstanceCollector {
	private HBox introScene;
	private VBox vBoxRecentProjects;
	
	private VBox createProjectScene;
	private EditNode edtProjectNameCPS;
	private EditNode edtProjectDirectoryCPS;
	private EditNode edtSourceDirectoryCPS;
	private TextNode errorNodeCPS;
	
	private VBox editProjectScene;
	private EditNode edtProjectNameEPS;
	private EditNode edtProjectDirectoryEPS;
	private EditNode edtSourceDirectoryEPS;
	private TextNode errorNodeEPS;
	private String projectFullPathBeforeEdit;
	
	private HBox mainScene;
	
	public MainStage() {
		super("", false, false, false);
	}
	
	//init
	public void initNormalStart() {
		this.setAlwaysOnTop(false);
		
		initIntroScene();
		initCreateProjectScene();
		initEditProjectScene();
		initMainScene();
		
		setStagePropertiesIntro();
		showIntroScene();
	}
	public void initQuickStart() {
		this.setAlwaysOnTop(false);
		
		initMainScene();
		
		this.show("");
		this.setTitle("Welcome to Tagallery");
	}
	private void initIntroScene() {
		TextNode applicationNameNode = new TextNode("Tagallery", false, false, false, true);
		applicationNameNode.setFont(new Font(48));
		applicationNameNode.setPadding(new Insets(-20, 0, 20, 0));
		
		TextNode btnNewProject = new TextNode("Create a New Project", true, false, true, true);
		btnNewProject.setMaxWidth(Double.MAX_VALUE);
		btnNewProject.addMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, this::showCreateProjectScene);
		
		TextNode btnOpenProject = new TextNode("Open Project", true, false, true, true);
		btnOpenProject.setMaxWidth(Double.MAX_VALUE);
		btnOpenProject.addMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, () -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Project");
			fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
			File file = fileChooser.showOpenDialog(StageManager.getMainStage());
			if (file != null) {
				String projectFilePath = file.getAbsolutePath();
				settings.getRecentProjects().add(0, projectFilePath);
				Project project = Project.readFromDisk(projectFilePath);
				LifecycleManager.startDatabaseLoading(project);
			}
		});
		
		TextNode btnColorPreset = new TextNode("Color Preset: " + ColorUtil.getColorPreset().getDisplayName(), true, false, true, true);
		btnColorPreset.setMaxWidth(Double.MAX_VALUE);
		btnColorPreset.addMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, () -> {
			int presetIndex = ColorUtil.getColorPreset().ordinal();
			presetIndex++;
			if (presetIndex >= ColorPreset.values().length) presetIndex = 0;
			ColorUtil.setColorPreset(ColorPreset.values()[presetIndex]);
			btnColorPreset.setText("Color Preset: " + ColorUtil.getColorPreset().getDisplayName());
		});
		
		VBox vBoxStartMenu = new VBox();
		vBoxStartMenu.setAlignment(Pos.CENTER);
		vBoxStartMenu.setPadding(new Insets(-40, 0, 20, 0));
		HBox.setHgrow(vBoxStartMenu, Priority.ALWAYS);
		
		vBoxStartMenu.getChildren().add(new ImageView(getClass().getResource("/logo-1.png").toExternalForm()));
		vBoxStartMenu.getChildren().add(applicationNameNode);
		vBoxStartMenu.getChildren().add(btnNewProject);
		vBoxStartMenu.getChildren().add(btnOpenProject);
		vBoxStartMenu.getChildren().add(btnColorPreset);
		
		vBoxRecentProjects = new VBox();
		vBoxRecentProjects.setMinWidth(400);
		vBoxRecentProjects.setPadding(new Insets(5));
		this.refreshRecentProjectNodes();
		
		introScene = new HBox(vBoxRecentProjects, new BoxSeparatorNode(), vBoxStartMenu);
		introSceneOnKeyPress();
		VBox.setVgrow(introScene, Priority.ALWAYS);
	}
	private void initCreateProjectScene() {
		TextNode lblProjectName = new TextNode("Project Name:");
		lblProjectName.setAlignment(Pos.CENTER_LEFT);
		edtProjectNameCPS = new EditNode("Project1");
		edtProjectNameCPS.setPrefWidth(400);
		TextNode btnHelperProjectName = new TextNode("Browse", true, true, true, true);
		btnHelperProjectName.setVisible(false);
		HBox hBoxProjectName = new HBox(lblProjectName, edtProjectNameCPS, btnHelperProjectName);
		hBoxProjectName.setSpacing(10);
		hBoxProjectName.setAlignment(Pos.CENTER);
		
		TextNode lblProjectDirectory = new TextNode("Project Directory:");
		lblProjectDirectory.setAlignment(Pos.CENTER_LEFT);
		edtProjectDirectoryCPS = new EditNode("");
		edtProjectDirectoryCPS.setPrefWidth(400);
		TextNode btnProjectDirectory = new TextNode("Browse", true, true, true, true);
		btnProjectDirectory.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				String projectDirectory = FileUtil.directoryChooser(this.getScene());
				if (!projectDirectory.isEmpty()) {
					edtProjectDirectoryCPS.setText(projectDirectory);
				}
			}
		});
		HBox hBoxProjectDirectory = new HBox(lblProjectDirectory, edtProjectDirectoryCPS, btnProjectDirectory);
		hBoxProjectDirectory.setSpacing(10);
		hBoxProjectDirectory.setAlignment(Pos.CENTER);
		
		TextNode lblSourceDirectory = new TextNode("Source Directory:");
		lblSourceDirectory.setAlignment(Pos.CENTER_LEFT);
		edtSourceDirectoryCPS = new EditNode("");
		edtSourceDirectoryCPS.setPrefWidth(400);
		TextNode btnSourceDirectory = new TextNode("Browse", true, true, true, true);
		btnSourceDirectory.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				edtSourceDirectoryCPS.setText(FileUtil.directoryChooser(this.getScene()));
			}
		});
		HBox hBoxSourceDirectory = new HBox(lblSourceDirectory, edtSourceDirectoryCPS, btnSourceDirectory);
		hBoxSourceDirectory.setSpacing(10);
		hBoxSourceDirectory.setAlignment(Pos.CENTER);
		
		errorNodeCPS = new TextNode("");
		errorNodeCPS.setTextFill(ColorUtil.getColorNegative());
		
		TextNode btnCreateProject = new TextNode("Create Project", true, true, true, true);
		btnCreateProject.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (this.checkEditValidityCPS()) {
				this.createProject(edtProjectNameCPS.getText(), edtProjectDirectoryCPS.getText(), edtSourceDirectoryCPS.getText());
			}
		});
		TextNode btnCancel = new TextNode("Cancel", true, true, true, true);
		btnCancel.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> showIntroScene());
		HBox hBoxCreateCancel = new HBox(btnCancel, btnCreateProject);
		
		double width = SizeUtil.getStringWidth(lblSourceDirectory.getText());
		lblProjectName.setPrefWidth(width);
		lblProjectDirectory.setPrefWidth(width);
		lblSourceDirectory.setPrefWidth(width);
		
		createProjectScene = new VBox(hBoxProjectName, hBoxProjectDirectory, hBoxSourceDirectory, errorNodeCPS, hBoxCreateCancel);
		createProjectScene.setPadding(new Insets(10));
		createProjectScene.setSpacing(5);
		createProjectScene.setAlignment(Pos.CENTER);
		createProjectScene.setFillWidth(false);
		createProjectScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ENTER) {
				if (this.checkEditValidityCPS()) {
					createProject(edtProjectNameCPS.getText(), edtProjectDirectoryCPS.getText(), edtSourceDirectoryCPS.getText());
				}
			} else if (event.getCode() == KeyCode.ESCAPE) {
				showIntroScene();
			}
		});
		VBox.setVgrow(createProjectScene, Priority.ALWAYS);
	}
	private void initEditProjectScene() {
		TextNode lblProjectName = new TextNode("Project Name:");
		lblProjectName.setAlignment(Pos.CENTER_LEFT);
		
		edtProjectNameEPS = new EditNode("Project1");
		edtProjectNameEPS.setPrefWidth(400);
		TextNode btnHelperProjectName = new TextNode("Browse", true, true, true, true);
		btnHelperProjectName.setVisible(false);
		HBox hBoxProjectName = new HBox(lblProjectName, edtProjectNameEPS, btnHelperProjectName);
		hBoxProjectName.setSpacing(10);
		hBoxProjectName.setAlignment(Pos.CENTER);
		
		TextNode lblProjectDirectory = new TextNode("Project Directory:");
		lblProjectDirectory.setAlignment(Pos.CENTER_LEFT);
		
		edtProjectDirectoryEPS = new EditNode("");
		edtProjectDirectoryEPS.setPrefWidth(400);
		TextNode btnProjectDirectory = new TextNode("Browse", true, true, true, true);
		btnProjectDirectory.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				String projectDirectory = FileUtil.directoryChooser(this.getScene());
				if (!projectDirectory.isEmpty()) {
					edtProjectDirectoryEPS.setText(projectDirectory);
				}
			}
		});
		HBox hBoxProjectDirectory = new HBox(lblProjectDirectory, edtProjectDirectoryEPS, btnProjectDirectory);
		hBoxProjectDirectory.setSpacing(10);
		hBoxProjectDirectory.setAlignment(Pos.CENTER);
		
		TextNode lblSourceDirectory = new TextNode("Source Directory:");
		lblSourceDirectory.setAlignment(Pos.CENTER_LEFT);
		edtSourceDirectoryEPS = new EditNode("");
		edtSourceDirectoryEPS.setPrefWidth(400);
		TextNode btnSourceDirectory = new TextNode("Browse", true, true, true, true);
		btnSourceDirectory.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				edtSourceDirectoryEPS.setText(FileUtil.directoryChooser(this.getScene()));
			}
		});
		HBox hBoxSourceDirectory = new HBox(lblSourceDirectory, edtSourceDirectoryEPS, btnSourceDirectory);
		hBoxSourceDirectory.setSpacing(10);
		hBoxSourceDirectory.setAlignment(Pos.CENTER);
		
		errorNodeEPS = new TextNode("");
		errorNodeEPS.setTextFill(ColorUtil.getColorNegative());
		
		TextNode btnApplyChanges = new TextNode("Apply Changes", true, true, true, true);
		btnApplyChanges.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (this.checkEditValidityEPS()) {
				this.editProject(edtProjectNameEPS.getText(), edtProjectDirectoryEPS.getText(), edtSourceDirectoryEPS.getText());
				this.refreshRecentProjectNodes();
				this.showIntroScene();
			}
		});
		TextNode btnCancel = new TextNode("Cancel", true, true, true, true);
		btnCancel.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> showIntroScene());
		HBox hBoxCreateCancel = new HBox(btnCancel, btnApplyChanges);
		
		double width = SizeUtil.getStringWidth(lblSourceDirectory.getText());
		lblProjectName.setPrefWidth(width);
		lblProjectDirectory.setPrefWidth(width);
		lblSourceDirectory.setPrefWidth(width);
		
		editProjectScene = new VBox(hBoxProjectName, hBoxProjectDirectory, hBoxSourceDirectory, errorNodeEPS, hBoxCreateCancel);
		editProjectScene.setPadding(new Insets(10));
		editProjectScene.setSpacing(5);
		editProjectScene.setAlignment(Pos.CENTER);
		editProjectScene.setFillWidth(false);
		editProjectScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ENTER) {
				if (this.checkEditValidityEPS()) {
					this.editProject(edtProjectNameEPS.getText(), edtProjectDirectoryEPS.getText(), edtSourceDirectoryEPS.getText());
					this.refreshRecentProjectNodes();
					this.showIntroScene();
				}
			} else if (event.getCode() == KeyCode.ESCAPE) {
				showIntroScene();
			}
		});
		VBox.setVgrow(editProjectScene, Priority.ALWAYS);
	}
	private void initMainScene() {
		mainScene = new HBox(filterPane, galleryPane, selectPane);
	}
	
	//intro
	public void setStagePropertiesIntro() {
		Rectangle usableScreenBounds = SizeUtil.getUsableScreenBounds();
		double width = usableScreenBounds.getWidth() / 2;
		double height = usableScreenBounds.getHeight() / 2;
		this.setWidth(width);
		this.setHeight(height);
		this.setMinWidth(width);
		this.setMinHeight(height);
		this.setOnCloseRequest(event -> settings.writeToDisk());
	}
	public void showIntroScene() {
		this.show("");
		this.setTitle("Welcome to Tagallery");
		this.setRoot(introScene);
		this.centerOnScreen();
		
		errorNodeCPS.setText("");
		errorNodeEPS.setText("");
		
		introScene.requestFocus();
	}
	
	private void introSceneOnKeyPress() {
		introScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			switch (event.getCode()) {
				case ENTER:
					CustomList<String> recentProjectFullPaths = settings.getRecentProjects();
					if (!recentProjectFullPaths.isEmpty()) {
						LifecycleManager.startDatabaseLoading(Project.readFromDisk(recentProjectFullPaths.getFirst()));
					} else {
						showCreateProjectScene();
					}
					break;
				default:
					break;
			}
		});
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
	private void refreshRecentProjectNodes() {
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
	
	//create
	public void showCreateProjectScene() {
		edtProjectNameCPS.setText("");
		edtProjectDirectoryCPS.setText("");
		edtSourceDirectoryCPS.setText("");
		
		this.setTitle("Create a New Project");
		this.setRoot(createProjectScene);
	}
	private void createProject(String projectName, String projectDirectory, String sourceDirectory) {
		if (!projectName.endsWith(".json")) projectName += ".json";
		
		Project project = new Project(projectDirectory + File.separator + projectName, sourceDirectory);
		project.writeToDisk();
		
		settings.getRecentProjects().add(0, project.getProjectFileFullPath());
		settings.writeToDisk();
		LifecycleManager.startDatabaseLoading(project);
	}
	private boolean checkEditValidityCPS() {
		if (edtProjectNameCPS.getText().isEmpty()) {
			errorNodeCPS.setText("Project Name cannot be empty");
			return false;
		}
		if (edtProjectDirectoryCPS.getText().isEmpty()) {
			errorNodeCPS.setText("Project Directory cannot be empty");
			return false;
		}
		if (edtSourceDirectoryCPS.getText().isEmpty()) {
			errorNodeCPS.setText("Source Directory cannot be empty");
			return false;
		}
		
		File projectDirectory = new File(edtProjectDirectoryCPS.getText());
		File sourceDirectory = new File(edtSourceDirectoryCPS.getText());
		
		if (!projectDirectory.exists() || !projectDirectory.isDirectory()) {
			errorNodeCPS.setText("Project Directory invalid or not found");
			return false;
		}
		if (!sourceDirectory.exists() || !sourceDirectory.isDirectory()) {
			errorNodeCPS.setText("Project Directory invalid or not found");
			return false;
		}
		
		return true;
	}
	
	//edit
	public void showEditProjectScene(Project project) {
		edtProjectNameEPS.setText(project.getProjectFileName());
		edtProjectDirectoryEPS.setText(project.getProjectDirectory());
		edtSourceDirectoryEPS.setText(project.getSourceDirectory());
		
		projectFullPathBeforeEdit = project.getProjectFileFullPath();
		
		this.setTitle("Edit Project");
		this.setRoot(editProjectScene);
	}
	private void editProject(String projectName, String projectDirectory, String sourceDirectory) {
		if (!projectDirectory.endsWith(File.separator)) projectDirectory += File.separator;
		if (!projectName.endsWith(".json")) projectName += ".json";
		
		String projectFile = projectDirectory + projectName;
		Project project = new Project(projectFile, sourceDirectory);
		
		FileUtil.deleteFile(projectFullPathBeforeEdit);
		project.writeToDisk();
		
		settings.getRecentProjects().remove(projectFullPathBeforeEdit);
		settings.getRecentProjects().add(0, project.getProjectFileFullPath());
		settings.writeToDisk();
	}
	private boolean checkEditValidityEPS() {
		if (edtProjectNameEPS.getText().isEmpty()) {
			errorNodeEPS.setText("Project Name cannot be empty");
			return false;
		}
		if (edtProjectDirectoryEPS.getText().isEmpty()) {
			errorNodeEPS.setText("Project Directory cannot be empty");
			return false;
		}
		if (edtSourceDirectoryEPS.getText().isEmpty()) {
			errorNodeEPS.setText("Source Directory cannot be empty");
			return false;
		}
		
		File projectDirectory = new File(edtProjectDirectoryEPS.getText());
		File sourceDirectory = new File(edtSourceDirectoryEPS.getText());
		
		if (!projectDirectory.exists() || !projectDirectory.isDirectory()) {
			errorNodeEPS.setText("Project Directory invalid or not found");
			return false;
		}
		if (!sourceDirectory.exists() || !sourceDirectory.isDirectory()) {
			errorNodeEPS.setText("Project Directory invalid or not found");
			return false;
		}
		
		return true;
	}
	
	//main
	public void setStagePropertiesMain() {
		this.setMinWidth(100 + SizeUtil.getMinWidthSideLists() * 2 + settings.getTileSize());
		this.setMinHeight(100 + SizeUtil.getPrefHeightTopMenu() + settings.getTileSize());
		this.setWidth(SizeUtil.getUsableScreenWidth());
		this.setHeight(SizeUtil.getUsableScreenHeight());
		this.setOnCloseRequest(event -> LifecycleManager.exitApplication());
		
		this.setBorder(null);
		this.setAlwaysOnTop(false);
		this.getScene().widthProperty().addListener((observable, oldValue, newValue) -> SizeUtil.stageWidthChangeHandler());
		this.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			Node node = event.getPickResult().getIntersectedNode();
			if (node instanceof EditNode) {
				event.consume();
			} else {
				node.requestFocus();
			}
		});
		
		mainSceneOnKeyPress();
		mainSceneOnKeyRelease();
	}
	public void showMainScene() {
		this.setTitleBar(toolbarPane);
		this.setRoot(mainScene);
		
		Decorator.applyScrollbarStyle(galleryPane);
		Decorator.applyScrollbarStyle(filterPane.getScrollPane());
		Decorator.applyScrollbarStyle(selectPane.getScrollPane());
		
		mainScene.requestFocus();
		
		this.centerOnScreen();
	}
	
	public void swapViewMode() {
		ObservableList<Node> panes = mainScene.getChildren();
		
		if (panes.contains(mediaPane)) {
			MediaPaneControls controls = mediaPane.getControls();
			if (controls.isShowing()) controls.hide();
			
			VideoPlayer videoPlayer = mediaPane.getVideoPlayer();
			if (videoPlayer != null && videoPlayer.isPlaying()) videoPlayer.pause();
			
			panes.set(panes.indexOf(mediaPane), galleryPane);
			galleryPane.moveViewportToTarget();
			galleryPane.requestFocus();
		} else if (panes.contains(galleryPane)) {
			panes.set(panes.indexOf(galleryPane), mediaPane);
			mediaPane.requestFocus();
			mediaPane.fireEvent(new MouseEvent(MouseEvent.MOUSE_MOVED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, false, false, null));
		}
	}
	public boolean isFullView() {
		return mainScene.getChildren().contains(mediaPane);
	}
	
	private SimpleBooleanProperty shiftDown = new SimpleBooleanProperty(false);
	public SimpleBooleanProperty shiftDownProperty() {
		return shiftDown;
	}
	public boolean isShiftDown() {
		return shiftDownProperty().get();
	}
	
	private void mainSceneOnKeyPress() {
		mainScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (getScene().getFocusOwner() instanceof EditNode) {
				if (event.getCode() == KeyCode.ESCAPE) {
					getScene().getRoot().requestFocus();
				} else if (event.getCode() == KeyCode.SHIFT) {
					shiftDown.setValue(true);
					select.shiftSelectFrom(target.get());
				}
			} else {
				switch (event.getCode()) {
					case ESCAPE:
						if (isFullView()) swapViewMode();
						reload.doReload();
						break;
					case E:
						target.get().getGalleryTile().onGroupEffectClick();
						reload.doReload();
						break;
					case R:
						select.setRandom();
						reload.doReload();
						break;
					case G:
						select.setRandomFromEntityGroup();
						reload.doReload();
						break;
					case F:
						swapViewMode();
						reload.doReload();
						break;
					case SHIFT:
						shiftDown.setValue(true);
						select.shiftSelectFrom(target.get());
						break;
					case W:
					case A:
					case S:
					case D:
						target.move(event.getCode());
						
						if (event.isShiftDown()) select.shiftSelectTo(target.get());
						else if (event.isControlDown()) select.add(target.get());
						else select.set(target.get());
						
						reload.doReload();
						break;
				}
			}
		});
	}
	private void mainSceneOnKeyRelease() {
		mainScene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
			switch (event.getCode()) {
				case SHIFT:
					shiftDown.setValue(false);
					break;
				default:
					break;
			}
		});
	}
	
	public void setVisible(boolean value) {
		if (value) {
			this.setOpacity(1);
		} else {
			this.setOpacity(0);
		}
	}
	
	@Override
	public Object show(String... args) {
		super.show();
		return null;
	}
}
