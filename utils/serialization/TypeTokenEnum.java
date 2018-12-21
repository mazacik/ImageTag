package utils.serialization;

import com.google.gson.reflect.TypeToken;
import database.list.MainListData;
import settings.Settings;

import java.lang.reflect.Type;

public enum TypeTokenEnum {
    DATALIST(new TypeToken<MainListData>() {}.getType()),
    SETTINGS(new TypeToken<Settings>() {}.getType()),
    ;

    private Type value;

    TypeTokenEnum(Type value) {
        this.value = value;
    }

    public Type getValue() {
        return value;
    }
}
