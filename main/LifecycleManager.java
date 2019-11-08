package application.main;

import application.backend.loader.Loader;
import application.backend.loader.Project;
import application.backend.util.FileUtil;
import application.frontend.stage.StageManager;

import java.io.File;

public abstract class LifecycleManager implements InstanceCollector {
	public static void init() {
		setLoggerFormat();
		initInstances();
		showGUI();
	}
	
	private static void setLoggerFormat() {
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %2$s: %5$s%n");
	}
	private static void initInstances() {
		settings.readFromDisk();
		
		toolbarPane.init();     /* needs Settings */
		galleryPane.init();     /* needs Settings */
		mediaPane.init();       /* needs Settings, GalleryPane */
		filterPane.init();      /* needs Settings */
		selectPane.init();      /* needs Settings */
		
		filter.init();
		target.init();
		select.init();
		reload.init();          /* needs everything */
	}
	private static void showGUI() {
		StageManager.getIntroStage()._show();
	}
	
	public static void startLoading(Project project) {
		StageManager.getIntroStage().close();
		String projectFilePath = project.getProjectFileFullPath();
		String projectDirectory = projectFilePath.substring(0, projectFilePath.lastIndexOf(File.separatorChar));
		String sourceDirectory = project.getWorkingDirectory();
		
		FileUtil.init(projectDirectory, sourceDirectory);
		StageManager.getMainStage()._show();
		Loader.run();
	}
}
