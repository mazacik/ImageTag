package application.data.loader;

import application.misc.JsonUtil;

import java.io.File;
import java.lang.reflect.Type;

public class Project {
	private transient String projectFullPath;
	private String workingDirectory;
	
	public Project(String projectFullPath, String workingDirectory) {
		this.projectFullPath = projectFullPath;
		this.workingDirectory = workingDirectory;
	}
	
	public static Project readFromDisk(String projectFile) {
		Type typeToken = JsonUtil.TypeTokenEnum.PROJECT.getValue();
		Project project = (Project) JsonUtil.read(typeToken, projectFile);
		project.projectFullPath = projectFile;
		return project;
	}
	public void writeToDisk() {
		File projectDir = new File(projectFullPath).getParentFile();
		if (!projectDir.exists()) projectDir.mkdirs();
		
		Type typeToken = JsonUtil.TypeTokenEnum.PROJECT.getValue();
		JsonUtil.write(this, typeToken, projectFullPath);
	}
	
	public String getProjectName() {
		String projectNameWithExtension = projectFullPath.substring(projectFullPath.lastIndexOf(File.separatorChar) + 1);
		return projectNameWithExtension.substring(0, projectNameWithExtension.lastIndexOf('.'));
	}
	public String getProjectDirectory() {
		return projectFullPath.substring(0, projectFullPath.lastIndexOf(File.separatorChar) + 1);
	}
	public String getProjectFullPath() {
		return projectFullPath;
	}
	public String getWorkingDirectory() {
		return workingDirectory;
	}
}
