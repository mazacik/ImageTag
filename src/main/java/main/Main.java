package main;

import javafx.application.Application;
import javafx.stage.Stage;
import server.misc.FileUtil;
import server.misc.Project;
import server.misc.Settings;

public class Main extends Application {
	public static final boolean DEBUG_MAIN_QUICKSTART = false;
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
		
		if (!DEBUG_MAIN_QUICKSTART || FileUtil.getProjectFiles().isEmpty()) {
			Root.PSC.showIntroStage();
		} else {
			Root.PSC.showMainStage();
		}
	}
	
	public static void exitApplication() {
		Root.THREADPOOL.interruptAll();
		
		Project.getCurrent().writeToDisk();
		Settings.writeToDisk();
		System.exit(0);
	}
}
