package gui.stage;

import gui.stage.main.StageMain;
import gui.stage.template.*;
import gui.stage.template.tageditstage.TagEditStage;

public abstract class StageManager {
	private static StageMain stageMain;
	
	private static ErrorStage errorStage;
	private static TagEditStage stageTagEditStage;
	private static GroupEditStage stageGroupEditStage;
	private static OkCancelStage okCancelStage;
	private static YesNoStage yesNoStage;
	private static FilterSettingsStage filterSettingsStage;
	
	public static StageMain getStageMain() {
		if (stageMain == null) stageMain = new StageMain();
		return stageMain;
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
