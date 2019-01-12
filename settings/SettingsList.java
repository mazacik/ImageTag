package settings;

import java.util.ArrayList;

public class SettingsList extends ArrayList<SettingsBase> {
    public SettingsBase getObject(String id) {
        for (SettingsBase object : this) {
            if (object.getId().equals(id)) {
                return object;
            }
        }
        return null;
    }
    public Integer valueOf(String id) {
        for (SettingsBase object : this) {
            if (object.getId().equals(id)) {
                return object.getValue();
            }
        }
        return null;
    }
}
