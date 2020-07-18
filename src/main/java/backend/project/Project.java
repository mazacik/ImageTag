package backend.project;

import backend.entity.EntityList;
import backend.misc.FileUtil;
import backend.misc.GsonUtil;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import main.Main;

import java.io.File;
import java.lang.reflect.Type;

public class Project {
	@SerializedName("a") private long lastAccessMs;
	@SerializedName("s") private String directory;
	@SerializedName("e") private EntityList entityList;
	
	private transient String projectName;
	private transient String projectFile;
	
	public Project(String projectName, String directory) {
		this.projectName = projectName;
		this.projectFile = ProjectUtil.generateProjectFilePath(projectName);
		this.directory = directory;
	}
	
	private transient static final Type typeToken = new TypeToken<Project>() {}.getType();
	public static Project read(String projectFile) {
		Project project = GsonUtil.read(typeToken, projectFile);
		if (project != null) {
			project.projectName = FileUtil.getFileNameNoExtension(projectFile);
			project.projectFile = projectFile;
		}
		return project;
	}
	public boolean write() {
		this.lastAccessMs = System.currentTimeMillis();
		this.entityList = Main.ENTITYLIST;
		return GsonUtil.write(this, typeToken, projectFile);
	}
	
	public void updateProject(String projectNameNew, String directorySourceNew) {
		if (projectName.equals(projectNameNew)) {
			this.directory = directorySourceNew;
			this.write();
		} else {
			String projectFileNew = ProjectUtil.generateProjectFilePath(projectNameNew);
			if (!new File(projectFileNew).exists()) {
				FileUtil.moveFile(projectFile, projectFileNew);
				FileUtil.moveFile(FileUtil.getDirectoryCache(this.projectName), FileUtil.getDirectoryCache(projectNameNew));
				
				this.projectName = projectNameNew;
				this.projectFile = projectFileNew;
				this.directory = directorySourceNew;
			}
		}
	}
	
	public String getProjectName() {
		return projectName;
	}
	public String getProjectFile() {
		return projectFile;
	}
	
	public long getLastAccessMs() {
		return lastAccessMs;
	}
	public String getDirectory() {
		return directory;
	}
	public EntityList getEntityList() {
		return entityList;
	}
}
