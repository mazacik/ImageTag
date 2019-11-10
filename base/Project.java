package application.base;

import application.util.JsonUtil;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;

public class Project {
	private transient static final Type typeToken = new TypeToken<Project>() {}.getType();
	private transient String projectFileFullPath;
	private String workingDirectory;
	
	public Project(String projectFileFullPath, String workingDirectory) {
		this.projectFileFullPath = projectFileFullPath;
		this.workingDirectory = workingDirectory;
	}
	
	public static Project readFromDisk(String projectFileFullPath) {
		Project project = (Project) JsonUtil.read(typeToken, projectFileFullPath);
		project.projectFileFullPath = projectFileFullPath;
		return project;
	}
	public void writeToDisk() {
		File projectDir = new File(projectFileFullPath).getParentFile();
		
		if (!projectDir.exists()) {
			projectDir.mkdirs();
		}
		
		JsonUtil.write(this, typeToken, projectFileFullPath);
	}
	
	public String getProjectFileName() {
		String projectNameWithExtension = projectFileFullPath.substring(projectFileFullPath.lastIndexOf(File.separatorChar) + 1);
		return projectNameWithExtension.substring(0, projectNameWithExtension.lastIndexOf('.'));
	}
	public String getProjectDirectory() {
		return projectFileFullPath.substring(0, projectFileFullPath.lastIndexOf(File.separatorChar) + 1);
	}
	public String getProjectFileFullPath() {
		return projectFileFullPath;
	}
	public String getWorkingDirectory() {
		return workingDirectory;
	}
}
