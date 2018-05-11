package project.backend.database;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import project.backend.components.GalleryPaneBack;
import project.frontend.shared.ColoredText;

import java.io.Serializable;
import java.util.ArrayList;

public class DatabaseItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private String simpleName;
    private String fullPath;
    private String extension;
    private ArrayList<String> tags;
    private transient Image image;
    private transient ImageView imageView;
    private transient ColoredText coloredText;

    public String getSimpleName() {
        return simpleName;
    }

    public String getFullPath() {
        return fullPath;
    }

    public String getExtension() {
        return extension;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public Image getImage() {
        return image;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public ColoredText getColoredText() {
        return coloredText;
    }

    public void setSimpleName(String SimpleName) {
        this.simpleName = SimpleName;
    }

    public void setFullPath(String FullPath) {
        this.fullPath = FullPath;
    }

    public void setExtension(String Extension) {
        this.extension = Extension;
    }

    public void setTags(ArrayList<String> Tags) {
        this.tags = Tags;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = (imageView != null) ? imageView : new ImageView();
        this.imageView.setOnMouseClicked(event -> GalleryPaneBack.imageViewClicked(this, event));
    }

    public void setColoredText(ColoredText coloredText) {
        this.coloredText = coloredText;
    }
}
