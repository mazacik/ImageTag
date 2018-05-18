package project.backend;

import project.backend.common.Keybinds;
import project.backend.common.Settings;
import project.backend.database.Database;
import project.backend.database.DatabaseLoader;
import project.backend.listener.GalleryPaneListener;
import project.backend.listener.PreviewPaneListener;
import project.backend.listener.RightPaneListener;
import project.backend.listener.TopPaneListener;
import project.backend.singleton.GalleryPaneBack;
import project.backend.singleton.LeftPaneBack;
import project.backend.singleton.PreviewPaneBack;
import project.backend.singleton.RightPaneBack;
import project.frontend.common.DirectoryChooserWindow;

public abstract class Backend {

    public static void initialize() {
        /* singleton initialization */
        GalleryPaneListener.getInstance();
        PreviewPaneListener.getInstance();
        RightPaneListener.getInstace();
        TopPaneListener.getInstance();
        Keybinds.getInstance();

        new DatabaseLoader().start();
    }

    public static void reloadContent(boolean sortDatabase) {
        if (sortDatabase) Database.sort();
        LeftPaneBack.getInstance().reloadContent();
        RightPaneBack.getInstance().reloadContent();
        PreviewPaneBack.getInstance().draw();
        GalleryPaneBack.getInstance().reloadContent();
    }
}
