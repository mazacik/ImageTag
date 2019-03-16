package user_interface.singleton.center;

import database.object.DataObject;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import settings.SettingsEnum;
import system.CommonUtil;
import system.InstanceRepo;
import user_interface.factory.util.ColorUtil;
import user_interface.singleton.BaseNode;

public class FullView extends Pane implements BaseNode, InstanceRepo {
    private final Canvas canvas = new Canvas();
    private DataObject currentDataObject = null;
    private Image currentPreviewImage = null;

    public FullView() {
        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());

        this.minWidthProperty().bind(tileView.widthProperty());
        this.setPrefHeight(coreSettings.valueOf(SettingsEnum.MAINSCENE_HEIGHT));
        this.setBorder(new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 1, 0, 1))));
        this.getChildren().add(canvas);
    }
    public Canvas getCanvas() {
        return canvas;
    }
    public void reload() {
        if (!CommonUtil.isFullView()) return;

        DataObject currentTarget = target.getCurrentTarget();
        if (currentTarget == null) return;
        if (currentDataObject == null || !currentDataObject.equals(currentTarget)) {
            String url = "file:" + coreSettings.getCurrentDirectory() + "\\" + target.getCurrentTarget().getName();
            currentPreviewImage = new Image(url);
            currentDataObject = currentTarget;
        }

        double imageWidth = currentPreviewImage.getWidth();
        double imageHeight = currentPreviewImage.getHeight();
        double maxWidth = canvas.getWidth() - 4;
        double maxHeight = canvas.getHeight();

        // scale image to fit width
        double resultWidth = maxWidth;
        double resultHeight = imageHeight * maxWidth / imageWidth;

        // if scaled image is too tall, scale to fit height instead
        if (resultHeight > maxHeight) {
            resultHeight = maxHeight;
            resultWidth = imageWidth * maxHeight / imageHeight;
        }

        double resultX = maxWidth / 2 - resultWidth / 2 + 2;
        double resultY = maxHeight / 2 - resultHeight / 2;

        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.clearRect(0, 0, this.getWidth(), this.getHeight());
        gc.drawImage(currentPreviewImage, resultX, resultY, resultWidth, resultHeight);
    }
}
