package lifecycle;

import utils.FileUtil;
import database.loader.LoaderThread;
import database.loader.Project;
import javafx.application.Platform;
import user_interface.utils.SceneUtil;
import user_interface.singleton.center.VideoPlayer;

import java.io.File;

public abstract class LifeCycleManager {
    public static void initialize() {
        VideoPlayer.checkLibs();

        InstanceManager.createInstances();

        SceneUtil.createIntroScene();
        SceneUtil.stageLayoutIntro();
        SceneUtil.showIntroScene();

        Platform.runLater(() -> {
            SceneUtil.createProjectScene();
            SceneUtil.createMainScene();
        });
    }
    public static void startLoading(Project project) {
        String projectFilePath = project.getProjectFilePath();
        String projectDirectory = projectFilePath.substring(0, projectFilePath.lastIndexOf(File.separatorChar));
        String sourceDirectory = project.getSourceDirectoryList().get(0);

        FileUtil.initialize(projectDirectory, sourceDirectory);
        new LoaderThread().start();
        SceneUtil.showMainScene();
        SceneUtil.stageLayoutMain();
    }
}
