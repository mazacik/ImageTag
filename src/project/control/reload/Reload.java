package project.control.reload;

import project.MainUtil;
import project.gui.component.GUINode;

public class Reload implements MainUtil {
    private boolean _topPane;
    private boolean _leftPane;
    private boolean _galleryPane;
    private boolean _previewPane;
    private boolean _rightPane;

    public Reload() {
        _topPane = false;
        _leftPane = false;
        _galleryPane = false;
        _previewPane = false;
        _rightPane = false;
    }

    public void queue(boolean instant, GUINode... nodes) {
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
    public void queue(GUINode... items) {
        queue(false, items);
    }
    public void all(boolean sort) {
        if (sort) {
            mainData.sort();
            filter.sort();
            selection.sort();

            mainTags.sort();
            whitelist.sort();
            blacklist.sort();
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
