package project;

import project.control.filter.Filter;
import project.control.focus.Focus;
import project.control.logger.Logger;
import project.control.maintags.MainTags;
import project.control.reload.Reload;
import project.control.selection.Selection;
import project.database.object.DataCollection;
import project.database.object.TagCollection;
import project.gui.CustomStage;
import project.gui.component.gallerypane.GalleryPane;
import project.gui.component.leftpane.LeftPane;
import project.gui.component.previewpane.PreviewPane;
import project.gui.component.rightpane.RightPane;
import project.gui.component.toppane.TopPane;

public interface MainUtil {
    DataCollection mainData = new DataCollection();
    MainTags mainTags = new MainTags();

    Filter filter = new Filter();
    Focus focus = new Focus();
    Logger log = new Logger();
    Reload reload = new Reload();
    Selection selection = new Selection();

    TagCollection whitelist = filter.getWhitelist();
    TagCollection blacklist = filter.getBlacklist();

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
