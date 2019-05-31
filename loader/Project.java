package loader;

import system.Instances;
import system.JsonUtil;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Project implements Instances {
    private transient String projectFile;
    private ArrayList<String> sourceDirectoryList;
    private ArrayList<String> importDirectoryList;

    public Project(String projectFile, String dirSource) {
        this.projectFile = projectFile;

        sourceDirectoryList = new ArrayList<>();
        importDirectoryList = new ArrayList<>();

        sourceDirectoryList.add(dirSource);
    }
    public static Project readFromDisk(String projectFile) {
        Type typeToken = JsonUtil.TypeTokenEnum.PROJECT.getValue();
        return (Project) JsonUtil.read(typeToken, projectFile);
    }
    public void writeToDisk() {
        File projectDir = new File(projectFile).getParentFile();
        if (!projectDir.exists()) projectDir.mkdirs();

        Type typeToken = JsonUtil.TypeTokenEnum.PROJECT.getValue();
        JsonUtil.write(this, typeToken, projectFile);
    }
    public ArrayList<String> getSourceDirectoryList() {
        return sourceDirectoryList;
    }
    public ArrayList<String> getImportDirectoryList() {
        return importDirectoryList;
    }
}
