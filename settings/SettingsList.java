package settings;

import java.util.ArrayList;

public class SettingsList extends ArrayList<SettingObject> {
    public SettingObject getObject(String id) {
        for (SettingObject object : this) {
            if (object.getId().equals(id)) {
                return object;
            }
        }
        return null;
    }
    public Integer valueOf(String id) {
        for (SettingObject object : this) {
            if (object.getId().equals(id)) {
                return object.getValue();
            }
        }
        return null;
    }
}
