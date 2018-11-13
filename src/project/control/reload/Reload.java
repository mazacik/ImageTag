package project.control.reload;

import project.gui.component.GUINode;
import project.utils.MainUtil;

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

    public void queue(GUINode... nodes) {
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
    }
    public void queueAll() {
        _topPane = true;
        _leftPane = true;
        _galleryPane = true;
        _previewPane = true;
        _rightPane = true;
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
