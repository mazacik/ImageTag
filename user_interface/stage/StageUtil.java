package user_interface.stage;

public abstract class StageUtil {
	private static boolean init = false;
	
	private static StageError stageError;
	private static EditorTag stageEditorTag;
	private static EditorGroup stageEditorGroup;
	private static StageOkCancel stageOkCancel;
	private static StageSettings stageSettings;
	private static StageFilterOptions stageFilterOptions;
	
	public static Object show(Stages stage, String... args) {
		if (!init) init();
		
		switch (stage) {
			case STAGE_ERROR:
				return stageError._show(args);
			case STAGE_FILTER_SETTINGS:
				return stageFilterOptions._show(args);
			case STAGE_GROUP_EDITOR:
				return stageEditorGroup._show(args);
			case STAGE_OK_CANCEL:
				return stageOkCancel._show(args);
			case STAGE_SETTINGS:
				return stageSettings._show(args);
			case STAGE_TAG_EDITOR:
				return stageEditorTag._show(args);
			default:
				return null;
		}
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
