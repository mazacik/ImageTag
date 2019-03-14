package settings;

public class SettingBase {
    private final SettingsEnum sn;
    private final Integer minValue;
    private final Integer maxValue;
    private final Integer defValue;
    private Integer value;

    public SettingBase(SettingsEnum sn, Integer minValue, Integer maxValue, Integer defValue) {
        this.sn = sn;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.defValue = defValue;
        this.value = defValue;
    }
    public SettingBase(SettingsEnum sn, Integer minValue, Integer maxValue) {
        this(sn, minValue, maxValue, minValue);
    }
    public SettingBase(SettingsEnum sn, Integer value) {
        this(sn, value, value, value);
    }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SettingBase)) return false;
        SettingBase settingBase = (SettingBase) o;
        return this.sn.equals(settingBase.getId()) &&
                this.value.equals(settingBase.getValue());
    }

    public SettingsEnum getId() {
        return sn;
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
