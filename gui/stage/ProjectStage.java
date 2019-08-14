package application.gui.stage;

import application.database.loader.Project;
import application.gui.decorator.SizeUtil;
import application.gui.nodes.simple.EditNode;
import application.gui.nodes.simple.TextNode;
import application.main.Instances;
import application.main.LifeCycle;
import application.misc.FileUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ProjectStage extends StageBase {
	public ProjectStage() {
		super("New Project");
		
		TextNode lblProjectName = new TextNode("Project Name:");
		lblProjectName.setAlignment(Pos.CENTER_LEFT);
		EditNode edtProjectName = new EditNode("Project1");
		edtProjectName.setPrefWidth(400);
		TextNode btnProjectName = new TextNode("...", true, true, true, true);
		btnProjectName.setVisible(false);
		HBox hBoxProjectName = new HBox(lblProjectName, edtProjectName, btnProjectName);
		hBoxProjectName.setSpacing(10);
		hBoxProjectName.setAlignment(Pos.CENTER);
		
		TextNode lblProjectDirectory = new TextNode("Project Directory:");
		lblProjectDirectory.setAlignment(Pos.CENTER_LEFT);
		EditNode edtProjectDirectory = new EditNode("");
		edtProjectDirectory.setPrefWidth(400);
		TextNode btnProjectDirectory = new TextNode("...", true, true, true, true);    //	todo use an icon instead of "..."
		btnProjectDirectory.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				String projectDirectory = FileUtil.directoryChooser(this.getScene());
				if (!projectDirectory.isEmpty()) {
					edtProjectDirectory.setText(projectDirectory);
				}
			}
		});
		HBox hBoxProjectDirectory = new HBox(lblProjectDirectory, edtProjectDirectory, btnProjectDirectory);
		hBoxProjectDirectory.setSpacing(10);
		hBoxProjectDirectory.setAlignment(Pos.CENTER);
		
		TextNode lblWorkingDirectory = new TextNode("Working Directory:");
		lblWorkingDirectory.setAlignment(Pos.CENTER_LEFT);
		EditNode edtWorkingDirectory = new EditNode("");
		edtWorkingDirectory.setPrefWidth(400);
		TextNode btnWorkingDirectory = new TextNode("...", true, true, true, true);
		btnWorkingDirectory.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				edtWorkingDirectory.setText(FileUtil.directoryChooser(this.getScene()));
			}
		});
		HBox hBoxWorkingDirectory = new HBox(lblWorkingDirectory, edtWorkingDirectory, btnWorkingDirectory);
		hBoxWorkingDirectory.setSpacing(10);
		hBoxWorkingDirectory.setAlignment(Pos.CENTER);
		
		TextNode btnCreateProject = new TextNode("Create Project", true, true, true, true);
		btnCreateProject.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			//checkValues values, if invalid, show error
			createProject(edtProjectName.getText(), edtProjectDirectory.getText(), edtWorkingDirectory.getText());
		});
		TextNode btnCancel = new TextNode("Cancel", true, true, true, true);
		btnCancel.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			this.close();
			Stages.getIntroStage()._show();
		});
		HBox hBoxCreateCancel = new HBox(btnCancel, btnCreateProject);
		
		double width = SizeUtil.getStringWidth(lblWorkingDirectory.getText());
		lblProjectName.setPrefWidth(width);
		lblProjectDirectory.setPrefWidth(width);
		lblWorkingDirectory.setPrefWidth(width);
		
		VBox vBoxHelper = new VBox(hBoxProjectName, hBoxProjectDirectory, hBoxWorkingDirectory, hBoxCreateCancel);
		vBoxHelper.setPadding(new Insets(10));
		vBoxHelper.setSpacing(5);
		vBoxHelper.setAlignment(Pos.CENTER);
		vBoxHelper.setFillWidth(false);
		
		vBoxHelper.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				createProject(edtProjectName.getText(), edtProjectDirectory.getText(), edtWorkingDirectory.getText());
			} else if (event.getCode() == KeyCode.ESCAPE) {
				this.close();
				Stages.getIntroStage()._show();
			}
		});
		
		this.setAlwaysOnTop(false);
		
		setRoot(vBoxHelper);
	}
	
	private void createProject(String projectName, String projectDirectory, String workingDirectory) {
		String projectFile = projectDirectory + projectName + ".json";
		Project project = new Project(projectFile, workingDirectory);
		project.writeToDisk();
		Instances.getSettings().getRecentProjects().push(projectFile);
		Instances.getSettings().writeToDisk();
		LifeCycle.startLoading(project);
	}
	
	@Override
	public Object _show(String... args) {
		this.show();
		return null;
	}
}
