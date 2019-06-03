package user_interface.factory.stage;

public class StageUtil {
    private static FilterSettingsStage filterSettingsStage = new FilterSettingsStage();

    public static void show(Stages stage) {
        switch (stage) {
            case FILTER_SETTINGS:
                filterSettingsStage._show();
                break;
        }
    }
}
