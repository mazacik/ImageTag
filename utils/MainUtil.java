package utils;

import control.filter.Filter;
import control.focus.Focus;
import control.logger.Logger;
import control.maintags.TagCollectionMain;
import control.reload.Reload;
import control.selection.Selection;
import database.object.DataCollection;
import database.object.TagCollection;
import gui.CustomStage;
import gui.component.gallerypane.GalleryPane;
import gui.component.leftpane.LeftPane;
import gui.component.previewpane.PreviewPane;
import gui.component.rightpane.RightPane;
import gui.component.toppane.TopPane;

public interface MainUtil {
    DataCollection mainData = new DataCollection();
    TagCollectionMain mainTags = new TagCollectionMain();

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
