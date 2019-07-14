package database.loader;

import utils.JsonUtil;

import java.io.File;
import java.lang.reflect.Type;

public class Project {
	private transient String projectFile;
	private String sourceDirectory;
	
	public Project(String projectFile, String sourceDirectory) {
		this.projectFile = projectFile;
		this.sourceDirectory = sourceDirectory;
	}
	public static Project readFromDisk(String projectFile) {
		Type typeToken = JsonUtil.TypeTokenEnum.PROJECT.getValue();
		Project project = (Project) JsonUtil.read(typeToken, projectFile);
		project.projectFile = projectFile;
		return project;
	}
	public void writeToDisk() {
		File projectDir = new File(projectFile).getParentFile();
		if (!projectDir.exists()) projectDir.mkdirs();
		
		Type typeToken = JsonUtil.TypeTokenEnum.PROJECT.getValue();
		JsonUtil.write(this, typeToken, projectFile);
	}
	
	public String getProjectFilePath() {
		return projectFile;
	}
	public String getSourceDirectory() {
		return sourceDirectory;
	}
}
