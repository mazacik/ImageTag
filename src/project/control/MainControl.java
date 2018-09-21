package project.control;

public abstract class MainControl {
    private final static DataControl dataControl = new DataControl();
    private final static TagControl tagControl = new TagControl();

    private final static FilterControl filterControl = new FilterControl();
    private final static FocusControl focusControl = new FocusControl();
    private final static LogControl logControl = new LogControl();
    private final static ReloadControl reloadControl = new ReloadControl();
    private final static SelectionControl selectionControl = new SelectionControl();

    public static DataControl getDataControl() {
        return dataControl;
    }
    public static TagControl getTagControl() {
        return tagControl;
    }

    public static FilterControl getFilterControl() {
        return filterControl;
    }
    public static FocusControl getFocusControl() {
        return focusControl;
    }
    public static LogControl getLogControl() {
        return logControl;
    }
    public static ReloadControl getReloadControl() {
        return reloadControl;
    }
    public static SelectionControl getSelectionControl() {
        return selectionControl;
    }
}
