package frontend.stage.primary;

import backend.misc.FileUtil;
import backend.misc.Project;
import backend.misc.Settings;
import frontend.decorator.DecoratorUtil;
import frontend.stage.primary.scene.IntroScene;
import frontend.stage.primary.scene.MainScene;
import frontend.stage.primary.scene.ProjectScene;
import javafx.animation.PauseTransition;
import javafx.scene.image.Image;
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
		
		introScene = new IntroScene();
		projectScene = new ProjectScene();
		mainScene = new MainScene();
		
		initKeybinds();
	}
	
	private void initKeybinds() {
		this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (this.getScene() == introScene) {
				introScene.processKeyEvent(event);
			} else if (this.getScene() == projectScene) {
				projectScene.processKeyEvent(event);
			} else if (this.getScene() == mainScene) {
				mainScene.processKeyEvent(event);
			}
		});
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
		projectScene.getRoot().requestFocus();
		
		if (project == null) {
			projectScene.refreshProjectNodes();
		} else {
			projectScene.refreshProjectNodes(project);
		}
		
		this.setTitle("Create a New Project");
		this.setScene(projectScene);
		Rectangle usableScreenBounds = DecoratorUtil.getUsableScreenBounds();
		double width = usableScreenBounds.getWidth() / 2;
		double height = usableScreenBounds.getHeight() / 2;
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
