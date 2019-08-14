package application.main;

import application.database.loader.LoaderThread;
import application.database.loader.Project;
import application.gui.panes.center.VideoPlayer;
import application.gui.stage.Stages;
import application.misc.FileUtil;

import java.io.File;

public abstract class LifeCycle {
	public static void initialize() {
		VideoPlayer.checkLibs();
		Instances.createInstances();
		Stages.getIntroStage()._show();
	}
	public static void startLoading(Project project) {
		Stages.getIntroStage().close();
		Stages.getProjectStage().close();
		String projectFilePath = project.getProjectFilePath();
		String projectDirectory = projectFilePath.substring(0, projectFilePath.lastIndexOf(File.separatorChar));
		String sourceDirectory = project.getSourceDirectory();
		
		FileUtil.initialize(projectDirectory, sourceDirectory);
		new LoaderThread().start();
		Stages.getMainStage()._show();
	}
}
