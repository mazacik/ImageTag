package application.main;

import application.database.loader.LoaderThread;
import application.database.loader.Project;
import application.gui.panes.center.VideoPlayer;
import application.gui.scene.SceneUtil;
import application.misc.FileUtil;
import javafx.application.Platform;

import java.io.File;

public abstract class LifeCycle {
	private static Project project;
    
    public static void initialize() {
        VideoPlayer.checkLibs();
	
		Instances.createInstances();

        SceneUtil.createIntroScene();
        SceneUtil.stageLayoutIntro();
        SceneUtil.showIntroScene();

        Platform.runLater(() -> {
            SceneUtil.createProjectScene();
            SceneUtil.createMainScene();
        });
    }
    public static void startLoading(Project project) {
		LifeCycle.project = project;
        
        String projectFilePath = project.getProjectFilePath();
        String projectDirectory = projectFilePath.substring(0, projectFilePath.lastIndexOf(File.separatorChar));
		String sourceDirectory = project.getSourceDirectory();

        FileUtil.initialize(projectDirectory, sourceDirectory);
        new LoaderThread().start();
        SceneUtil.showMainScene();
        SceneUtil.stageLayoutMain();
		Instances.createInstancesEvents();
    }
	
	public static Project getProject() {
		return project;
	}
}
