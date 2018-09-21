package project.control;

public abstract class Control {
    private static final FilterControl filterControl = new FilterControl();
    private static final FocusControl focusControl = new FocusControl();
    private static final LogControl logControl = new LogControl();
    private static final ReloadControl reloadControl = new ReloadControl();
    private static final SelectionControl selectionControl = new SelectionControl();

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
