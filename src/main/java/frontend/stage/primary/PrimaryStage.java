package frontend.stage.primary;

import backend.list.BaseList;
import backend.misc.FileUtil;
import backend.misc.Project;
import backend.misc.Settings;
import frontend.decorator.DecoratorUtil;
import frontend.stage.primary.scene.IntroScene;
import frontend.stage.primary.scene.MainScene;
import frontend.stage.primary.scene.ProjectScene;
import javafx.animation.PauseTransition;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import main.Main;
import main.Root;

import java.awt.*;

public class PrimaryStage extends Stage {
	private final IntroScene introScene;
	private final ProjectScene projectScene;
	private final MainScene mainScene;
	
	public PrimaryStage() {
		this.getIcons().add(new Image("/logo-32px.png"));
		
		introScene = new IntroScene(this);
		projectScene = new ProjectScene(this);
		mainScene = new MainScene();
	}
	
	public void showIntroScene() {
		if (!this.isShowing()) {
			this.setOpacity(0);
		}
		
		Rectangle usableScreenBounds = DecoratorUtil.getUsableScreenBounds();
		double width = usableScreenBounds.getWidth() / 2;
		double height = usableScreenBounds.getHeight() / 2;
		
		introScene.getRoot().requestFocus();
		introScene.refreshIntroBox();
		
		this.setTitle("Welcome to " + FileUtil.APP_NAME);
		this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			switch (event.getCode()) {
				case ENTER:
					BaseList<Project> projects = FileUtil.getProjects();
					if (!projects.isEmpty()) {
						projects.sort(Project.getComparator());
						this.showMainScene(projects.getFirst());
					} else {
						this.showProjectScene();
					}
					break;
				default:
					break;
			}
		});
		
		this.setScene(introScene);
		this.setWidth(width);
		this.setHeight(height);
		this.centerOnScreen();
		this.setOnCloseRequest(event -> Settings.writeToDisk());
		
		if (!this.isShowing()) {
			this.show();
			PauseTransition pt = new PauseTransition(new Duration(100));
			pt.setOnFinished(event -> this.setOpacity(1));
			pt.play();
		}
	}
	
	public void showProjectScene() {
		showProjectScene(null);
	}
	public void showProjectScene(Project project) {
		Rectangle usableScreenBounds = DecoratorUtil.getUsableScreenBounds();
		double width = usableScreenBounds.getWidth() / 2;
		double height = usableScreenBounds.getHeight() / 2;
		
		projectScene.getRoot().requestFocus();
		
		if (project == null) {
			projectScene.refreshProjectNodes();
		} else {
			projectScene.refreshProjectNodes(project);
		}
		
		this.setTitle("Create a New Project");
		this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ENTER) {
				projectScene.tryFinish(this);
			} else if (event.getCode() == KeyCode.ESCAPE) {
				this.showIntroScene();
			}
		});
		
		this.setScene(projectScene);
		this.setWidth(width);
		this.setHeight(height);
		this.centerOnScreen();
		
		this.setOnCloseRequest(event -> Settings.writeToDisk());
	}
	
	public void showMainScene() {
		showMainScene(null);
	}
	public void showMainScene(Project project) {
		this.setOpacity(0);
		
		if (project == null) {
			Project.setFirstAsCurrent();
		} else {
			Project.setCurrent(project);
		}
		
		Root.startProjectDatabaseLoading();
		
		this.setTitle(FileUtil.APP_NAME);
		this.setScene(mainScene);
		this.centerOnScreen();
		this.setMaximized(true);
		this.setOnCloseRequest(event -> Main.exitApplication());
		
		Root.TOOLBAR_PANE.requestFocus();
		
		if (!this.isShowing()) {
			this.show();
			PauseTransition pt = new PauseTransition(new Duration(100));
			pt.setOnFinished(event -> this.setOpacity(1));
			pt.play();
		} else {
			this.setOpacity(1);
		}
	}
	
	public void requestExit() {
		this.fireEvent(new WindowEvent(null, WindowEvent.WINDOW_CLOSE_REQUEST));
	}
	
	public MainScene getMainScene() {
		return mainScene;
	}
}
