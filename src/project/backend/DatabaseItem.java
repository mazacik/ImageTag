package project.backend;

import javafx.scene.image.Image;

import java.io.Serializable;
import java.util.List;

public class DatabaseItem implements Serializable {
    private String fullPath;
    private String simpleName;
    private String extension;
    private int index;
    private transient Image image;
    private List<String> tags;

    public String getExtension() {
        return this.extension;
    }

    public String getFullPath() {
        return this.fullPath;
    }

    public Image getImage() {
        return this.image;
    }

    public int getIndex() {
        return this.index;
    }

    public String getSimpleName() {
        return this.simpleName;
    }

    public List<String> getTags() {
        return this.tags;
    }

    public void setExtension(String Extension) {
        this.extension = Extension;
    }

    public void setFullPath(String FullPath) {
        this.fullPath = FullPath;
    }

    public void setImage(Image ImageIcon) {
        this.image = ImageIcon;
    }

    public void setIndex(int Index) {
        this.index = Index;
    }

    public void setSimpleName(String SimpleName) {
        this.simpleName = SimpleName;
    }

    public void setTags(List<String> Tags) {
        this.tags = Tags;
    }
}