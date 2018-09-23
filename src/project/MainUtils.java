package project;

import project.control.*;
import project.gui.CustomStage;
import project.gui.component.gallerypane.GalleryPane;
import project.gui.component.leftpane.LeftPane;
import project.gui.component.previewpane.PreviewPane;
import project.gui.component.rightpane.RightPane;
import project.gui.component.toppane.TopPane;

public interface MainUtils {
    DataControl dataControl = new DataControl();
    TagControl tagControl = new TagControl();

    FilterControl filterControl = new FilterControl();
    FocusControl focusControl = new FocusControl();
    LogControl logControl = new LogControl();
    ReloadControl reloadControl = new ReloadControl();
    SelectionControl selectionControl = new SelectionControl();

    TopPane topPane = new TopPane();
    LeftPane leftPane = new LeftPane();
    GalleryPane galleryPane = new GalleryPane();
    PreviewPane previewPane = new PreviewPane();
    RightPane rightPane = new RightPane();
    CustomStage customStage = new CustomStage();

    default void swapDisplayMode() {
        customStage.swapDisplayMode();
    }
    default boolean isPreviewFullscreen() {
        return customStage.isPreviewFullscreen();
    }
}
