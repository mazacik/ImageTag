package application.main;

import application.data.loader.LoaderThread;
import application.data.loader.Project;
import application.gui.stage.Stages;
import application.misc.FileUtil;

import java.io.File;

public abstract class AppLifeCycle {
	public static void init() {
		setSystemProperties();
		Instances.init();
		showGUI();
	}
	
	private static void setSystemProperties() {
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %2$s: %5$s%n");
	}
	private static void showGUI() {
		Stages.getIntroStage()._show();
	}
	
	public static void startLoading(Project project) {
		Stages.getIntroStage().close();
		String projectFilePath = project.getProjectFullPath();
		String projectDirectory = projectFilePath.substring(0, projectFilePath.lastIndexOf(File.separatorChar));
		String sourceDirectory = project.getWorkingDirectory();
		
		FileUtil.initialize(projectDirectory, sourceDirectory);
		new LoaderThread().start();
		Stages.getMainStage()._show();
	}
}
