package project.control;

import project.MainUtils;
import project.gui.component.GUINode;

public class ReloadControl implements MainUtils {
    private boolean _topPane;
    private boolean _leftPane;
    private boolean _galleryPane;
    private boolean _previewPane;
    private boolean _rightPane;

    public ReloadControl() {
        _topPane = false;
        _leftPane = false;
        _galleryPane = false;
        _previewPane = false;
        _rightPane = false;
    }

    public void reload(boolean instant, GUINode... nodes) {
        for (GUINode node : nodes) {
            switch (node) {
                case TOPPANE:
                    _topPane = true;
                    break;
                case LEFTPANE:
                    _leftPane = true;
                    break;
                case GALLERYPANE:
                    _galleryPane = true;
                    break;
                case PREVIEWPANE:
                    _previewPane = true;
                    break;
                case RIGHTPANE:
                    _rightPane = true;
                    break;
                default:
                    break;
            }
        }
        if (instant) doReload();
    }
    public void reload(GUINode... items) {
        reload(false, items);
    }
    public void reloadAll(boolean sort) {
        if (sort) {
            dataControl.getCollection().sort();
            filterControl.getCollection().sort();
            selectionControl.getCollection().sort();

            tagControl.getCollection().sort();
            filterControl.getWhitelist().sort();
            filterControl.getBlacklist().sort();
        }
        topPane.reload();
        leftPane.reload();
        galleryPane.reload();
        previewPane.reload();
        rightPane.reload();
    }
    public void doReload() {
        if (_topPane) {
            topPane.reload();
            _topPane = false;
        }
        if (_leftPane) {
            leftPane.reload();
            _leftPane = false;
        }
        if (_galleryPane) {
            galleryPane.reload();
            _galleryPane = false;
        }
        if (_previewPane) {
            previewPane.reload();
            _previewPane = false;
        }
        if (_rightPane) {
            rightPane.reload();
            _rightPane = false;
        }
    }
}
