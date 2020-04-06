package ui.stage.primary;

import javafx.stage.WindowEvent;
import main.Root;
import misc.Project;
import ui.stage.primary.project.ProjectStage;

public class PrimaryStageController {
	public final IntroStage INTRO_STAGE = new IntroStage();
	public final ProjectStage PROJECT_STAGE = new ProjectStage();
	public final MainStage MAIN_STAGE = new MainStage();
	
	public void showIntroStage() {
		INTRO_STAGE.initNodes();
		
		INTRO_STAGE.show();
		PROJECT_STAGE.hide();
		MAIN_STAGE.hide();
	}
	public void showProjectStage() {
		PROJECT_STAGE.initNodes();
		
		INTRO_STAGE.hide();
		PROJECT_STAGE.show();
		MAIN_STAGE.hide();
	}
	public void showProjectStage(Project project) {
		PROJECT_STAGE.initNodes(project);
		
		INTRO_STAGE.hide();
		PROJECT_STAGE.show();
		MAIN_STAGE.hide();
	}
	public void showMainStage() {
		Project.setFirstAsCurrent();
		Root.startProjectDatabaseLoading();
		
		INTRO_STAGE.hide();
		PROJECT_STAGE.hide();
		MAIN_STAGE.show();
	}
	public void showMainStage(Project project) {
		Project.setCurrent(project);
		Root.startProjectDatabaseLoading();
		
		INTRO_STAGE.hide();
		PROJECT_STAGE.hide();
		MAIN_STAGE.show();
	}
	
	public void requestExit() {
		MAIN_STAGE.fireEvent(new WindowEvent(null, WindowEvent.WINDOW_CLOSE_REQUEST));
	}
}
