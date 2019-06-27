package userinterface.stage;

import database.object.TagObject;
import javafx.util.Pair;

public abstract class StageUtil {
	private static boolean init = false;
	
	private static StageError stageError;
	private static EditorTag stageEditorTag;
	private static EditorGroup stageEditorGroup;
	private static StageOkCancel stageOkCancel;
	private static StageSettings stageSettings;
	private static StageFilterOptions stageFilterOptions;
	
	public static Object showStageError(String... args) {
		if (!init) init();
		return stageError._show(args);
	}
	public static Pair<TagObject, Boolean> showStageEditorTag(String... args) {
		if (!init) init();
		return stageEditorTag._show(args);
	}
	public static String showStageEditorGroup(String... args) {
		if (!init) init();
		return stageEditorGroup._show(args);
	}
	public static boolean showStageOkCancel(String... args) {
		if (!init) init();
		return stageOkCancel._show(args);
	}
	public static Object showStageSettings(String... args) {
		if (!init) init();
		return stageSettings._show(args);
	}
	public static Object showStageFilterOptions(String... args) {
		if (!init) init();
		return stageFilterOptions._show(args);
	}
	
	private static void init() {
		init = true;
		stageError = new StageError();
		stageEditorTag = new EditorTag();
		stageEditorGroup = new EditorGroup();
		stageOkCancel = new StageOkCancel();
		stageSettings = new StageSettings();
		stageFilterOptions = new StageFilterOptions();
	}
}
