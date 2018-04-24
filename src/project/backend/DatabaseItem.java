package project.backend;

import javafx.scene.image.ImageView;
import project.gui_components.ColoredText;

import java.io.Serializable;
import java.util.ArrayList;

public class DatabaseItem implements Serializable {
    private String fullPath;
    private String simpleName;
    private String extension;
    private int index;
    private transient ImageView imageView;
    private transient ColoredText coloredText;
    private ArrayList<String> tags;

    public String getExtension() {
        return this.extension;
    }

    public String getFullPath() {
        return this.fullPath;
    }

    public int getIndex() {
        return this.index;
    }

    public String getSimpleName() {
        return this.simpleName;
    }

    public ArrayList<String> getTags() {
        return this.tags;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public ColoredText getColoredText() {
        return coloredText;
    }

    public void setExtension(String Extension) {
        this.extension = Extension;
    }

    public void setFullPath(String FullPath) {
        this.fullPath = FullPath;
    }

    public void setIndex(int Index) {
        this.index = Index;
    }

    public void setSimpleName(String SimpleName) {
        this.simpleName = SimpleName;
    }

    public void setTags(ArrayList<String> Tags) {
        this.tags = Tags;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = (imageView != null) ? imageView : new ImageView();
        this.imageView.setOnMouseClicked(event -> {
            Database.setLastSelectedItem(this);
            if (!Database.getSelectedItems().contains(this))
                Database.addToSelection(this);
            else
                Database.removeIndexFromSelection(this);
        });
    }

    public void setColoredText(ColoredText coloredText) {
        this.coloredText = coloredText;
    }
}