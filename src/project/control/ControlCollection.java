package project.control;

public abstract class ControlCollection {
    public final DataControl dataControl = MainControl.getDataControl();
    public final TagControl tagControl = MainControl.getTagControl();

    public final FilterControl filterControl = MainControl.getFilterControl();
    public final FocusControl focusControl = MainControl.getFocusControl();
    public final LogControl logControl = MainControl.getLogControl();
    public final ReloadControl reloadControl = MainControl.getReloadControl();
    public final SelectionControl selectionControl = MainControl.getSelectionControl();
}
