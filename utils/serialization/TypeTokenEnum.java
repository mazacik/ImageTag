package utils.serialization;

import com.google.gson.reflect.TypeToken;
import database.list.MainListData;
import settings.CoreSettings;
import settings.UserSettings;

import java.lang.reflect.Type;

public enum TypeTokenEnum {
    DATALIST(new TypeToken<MainListData>() {}.getType()),
    USERSETTINGS(new TypeToken<UserSettings>() {}.getType()),
    CORESETTINGS(new TypeToken<CoreSettings>() {}.getType()),
    ;

    private Type value;

    TypeTokenEnum(Type value) {
        this.value = value;
    }

    public Type getValue() {
        return value;
    }
}
