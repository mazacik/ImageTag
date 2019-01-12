package settings;

public class SettingsBase {
    private final String id;
    private final Integer minValue;
    private final Integer maxValue;
    private final Integer defValue;
    private Integer value;

    public SettingsBase(String id, Integer minValue, Integer maxValue, Integer defValue) {
        this.id = id;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.defValue = defValue;
        this.value = defValue;
    }
    public SettingsBase(String id, Integer minValue, Integer maxValue) {
        this(id, minValue, maxValue, minValue);
    }
    public SettingsBase(String id, Integer value) {
        this(id, value, value, value);
    }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SettingsBase)) return false;
        SettingsBase settingsBase = (SettingsBase) o;
        return this.id.equals(settingsBase.getId()) &&
                this.value.equals(settingsBase.getValue());
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
