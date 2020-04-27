package main;

import backend.misc.FileUtil;
import backend.misc.Project;
import backend.misc.Settings;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	public static final boolean DEBUG_AUTOLOAD_LATEST_PROJECT = false;
	public static final boolean DEBUG_UI_SCALING = true;
	public static final boolean DEBUG_USE_CACHE = true;
	
	public static final boolean DEBUG_FS_ALLOW_FILE_MOVE = true;
	public static final boolean DEBUG_FS_ALLOW_FILE_DELETE = true;
	
	public static void main(String[] args) {
		launch(args);
	}
	public void start(Stage stage) {
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-7s] %5$s%n");
		
		Settings.readFromDisk();
		
		if (!DEBUG_AUTOLOAD_LATEST_PROJECT || FileUtil.getProjectFiles().isEmpty()) {
			Root.PRIMARY_STAGE.showIntroScene();
		} else {
			Root.PRIMARY_STAGE.showMainScene();
		}
	}
	
	public static void exitApplication() {
		Root.THREADS.interrupt();
		Root.DISPLAY_PANE.disposeVideoPlayer();
		
		Project.getCurrent().writeToDisk();
		Settings.writeToDisk();
		System.exit(0);
	}
}
