package frontend.stage.primary;

import backend.misc.FileUtil;
import backend.project.Project;
import backend.project.ProjectUtil;
import backend.settings.Settings;
import frontend.UserInterface;
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
		this.setOpacity(0);
		
		Rectangle usableScreenBounds = DecoratorUtil.getUsableScreenBounds();
		double width = usableScreenBounds.getWidth() / 2;
		double height = usableScreenBounds.getHeight() / 2;
		
		introScene.getRoot().requestFocus();
		introScene.getProjectBox().refresh();
		
		this.setTitle("Welcome to " + FileUtil.APP_NAME);
		
		this.setScene(introScene);
		this.setMaximized(false);
		this.setWidth(width);
		this.setHeight(height);
		this.centerOnScreen();
		this.setOnCloseRequest(event -> Settings.write());
		
		if (!this.isShowing()) {
			this.show();
		}
		
		PauseTransition pt = new PauseTransition(new Duration(100));
		pt.setOnFinished(event -> this.setOpacity(1));
		pt.play();
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
		this.setOnCloseRequest(event -> Settings.write());
	}
	
	public void showMainScene() {
		this.setOpacity(0);
		
		this.setTitle(FileUtil.APP_NAME);
		this.setScene(mainScene);
		this.centerOnScreen();
		this.setMaximized(true);
		this.setOnCloseRequest(event -> {
			Main.THREADGROUP.interrupt();
			UserInterface.getCenterPane().getDisplayPane().disposeVideoPlayer();
			
			ProjectUtil.getCurrentProject().write();
			Settings.write();
			System.exit(0);
		});
		
		UserInterface.getToolbarPane().requestFocus();
		
		if (!this.isShowing()) {
			this.show();
		}
		
		PauseTransition pt1 = new PauseTransition(new Duration(100));
		pt1.setOnFinished(event -> this.setOpacity(1));
		pt1.play();
	}
	
	public void requestExit() {
		this.fireEvent(new WindowEvent(null, WindowEvent.WINDOW_CLOSE_REQUEST));
	}
	
	public IntroScene getIntroScene() {
		return introScene;
	}
	public MainScene getMainScene() {
		return mainScene;
	}
}
