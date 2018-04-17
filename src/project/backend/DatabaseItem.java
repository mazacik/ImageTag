package project.backend;

import javafx.scene.image.Image;

import java.util.List;

public class DatabaseItem {
    private String FullPath;
    private String SimpleName;
    private String Extension;
    private int Index;
    private Image Image;
    private List<String> Tags;

    public String getExtension() {
        return this.Extension;
    }

    public String getFullPath() {
        return this.FullPath;
    }

    public Image getImage() {
        return this.Image;
    }

    public int getIndex() {
        return this.Index;
    }

    public String getSimpleName() {
        return this.SimpleName;
    }

    public List<String> getTags() {
        return this.Tags;
    }

    public void setExtension(String Extension) {
        this.Extension = Extension;
    }

    public void setFullPath(String FullPath) {
        this.FullPath = FullPath;
    }

    public void setImage(Image ImageIcon) {
        this.Image = ImageIcon;
    }

    public void setIndex(int Index) {
        this.Index = Index;
    }

    public void setSimpleName(String SimpleName) {
        this.SimpleName = SimpleName;
    }

    public void setTags(List<String> Tags) {
        this.Tags = Tags;
    }
}