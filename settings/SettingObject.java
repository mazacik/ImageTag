package settings;

public class SettingObject {
    private final String id;
    private final Integer minValue;
    private final Integer maxValue;
    private final Integer defValue;
    private Integer value;

    public SettingObject(String id, Integer minValue, Integer maxValue, Integer defValue) {
        this.id = id;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.defValue = defValue;
        this.value = defValue;
    }
    public SettingObject(String id, Integer minValue, Integer maxValue) {
        this(id, minValue, maxValue, minValue);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SettingObject)) return false;
        SettingObject settingObject = (SettingObject) o;
        return this.id.equals(settingObject.getId()) &&
                this.value.equals(settingObject.getValue());
    }

    public String getId() {
        return id;
    }
    public Integer getValue() {
        return value;
    }
    public void setValue(Integer value) {
        this.value = value;
    }
    public Integer getMinValue() {
        return minValue;
    }
    public Integer getMaxValue() {
        return maxValue;
    }
    public Integer getDefValue() {
        return defValue;
    }
}
