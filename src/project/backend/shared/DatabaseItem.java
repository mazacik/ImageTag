package project.backend.shared;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import project.frontend.shared.ColoredText;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Base component of every database. All information apart from the image of an item is stored in an instance of this class.
 */
public class DatabaseItem implements Serializable {
    private static long serialVersionUID = 1L;
    private String fullPath;
    private String simpleName;
    private String extension;
    private ArrayList<String> tags;
    private transient Image image;
    private transient ImageView imageView;
    private transient ColoredText coloredText;

    public String getExtension() {
        return this.extension;
    }

    public String getFullPath() {
        return this.fullPath;
    }

    public String getSimpleName() {
        return this.simpleName;
    }

    public ArrayList<String> getTags() {
        return this.tags;
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

    void setExtension(String Extension) {
        this.extension = Extension;
    }

    void setFullPath(String FullPath) {
        this.fullPath = FullPath;
    }

    void setSimpleName(String SimpleName) {
        this.simpleName = SimpleName;
    }

    void setTags(ArrayList<String> Tags) {
        this.tags = Tags;
    }

    void setImage(Image image) {
        this.image = image;
    }

    void setImageView(ImageView imageView) {
        this.imageView = (imageView != null) ? imageView : new ImageView();
        this.imageView.setOnMouseClicked(
            event -> Backend.getGalleryPane().imageViewClicked(this, event));
    }

    void setColoredText(ColoredText coloredText) {
        this.coloredText = coloredText;
    }
}
