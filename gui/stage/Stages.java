package application.gui.stage;

public abstract class Stages {
	private static IntroStage introStage;
	private static ProjectStage projectStage;
	private static MainStage mainStage;
	
	private static ErrorStage errorStage;
	private static TagEditStage stageTagEditStage;
	private static GroupEditStage stageGroupEditStage;
	private static OkCancelStage okCancelStage;
	private static YesNoStage yesNoStage;
	private static FilterSettingsStage filterSettingsStage;
	
	public static IntroStage getIntroStage() {
		if (introStage == null) introStage = new IntroStage();
		return introStage;
	}
	public static ProjectStage getProjectStage() {
		if (projectStage == null) projectStage = new ProjectStage();
		return projectStage;
	}
	public static MainStage getMainStage() {
		if (mainStage == null) mainStage = new MainStage();
		return mainStage;
	}
	
	public static ErrorStage getErrorStage() {
		if (errorStage == null) errorStage = new ErrorStage();
		return errorStage;
	}
	public static TagEditStage getTagEditStage() {
		if (stageTagEditStage == null) stageTagEditStage = new TagEditStage();
		return stageTagEditStage;
	}
	public static GroupEditStage getGroupEditStage() {
		if (stageGroupEditStage == null) stageGroupEditStage = new GroupEditStage();
		return stageGroupEditStage;
	}
	public static OkCancelStage getOkCancelStage() {
		if (okCancelStage == null) okCancelStage = new OkCancelStage();
		return okCancelStage;
	}
	public static YesNoStage getYesNoStage() {
		if (yesNoStage == null) yesNoStage = new YesNoStage();
		return yesNoStage;
	}
	public static FilterSettingsStage getFilterSettingsStage() {
		if (filterSettingsStage == null) filterSettingsStage = new FilterSettingsStage();
		return filterSettingsStage;
	}
}
