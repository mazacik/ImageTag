package user_interface.singleton.center;

import database.object.DataObject;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import system.CommonUtil;
import system.InstanceRepo;
import user_interface.factory.NodeFactory;
import user_interface.singleton.BaseNode;

public class FullView extends BorderPane implements BaseNode, InstanceRepo {
    private final ImageView imageView = new ImageView();
    private DataObject currentDataObject = null;
    private Image currentPreviewImage = null;

    public FullView() {
        imageView.setPreserveRatio(true);
        imageView.setSmooth(false);

        this.minWidthProperty().bind(tileView.widthProperty());
        this.minHeightProperty().bind(tileView.heightProperty());
        this.setBorder(NodeFactory.getBorder(0, 1, 0, 1));
        this.setCenter(imageView);
    }
    public void reload() {
        DataObject currentTarget = target.getCurrentTarget();
        if (CommonUtil.isFullView() && currentTarget != null) {
            if (currentDataObject == null || !currentDataObject.equals(currentTarget)) {
                String url = "file:" + settings.getCurrentDirectory() + "\\" + currentTarget.getName();
                currentPreviewImage = new Image(url, this.getMinWidth() - 2, this.getMinHeight(), true, true);
                currentDataObject = currentTarget;
            }
            imageView.setImage(currentPreviewImage);
        }
    }
}
