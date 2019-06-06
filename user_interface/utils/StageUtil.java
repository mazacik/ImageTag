package user_interface.utils;

import user_interface.factory.stage.FilterSettingsStage;
import user_interface.utils.enums.Stages;

public class StageUtil {
    //todo finish this
    private static FilterSettingsStage filterSettingsStage = new FilterSettingsStage();

    public static void show(Stages stage) {
        switch (stage) {
            case FILTER_SETTINGS:
                filterSettingsStage._show();
                break;
        }
    }
}
