package backend.project;

import backend.entity.EntityList;
import backend.misc.FileUtil;
import backend.misc.GsonUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import main.Main;

import java.io.File;

public class Project {
	
	@JsonProperty("a") private long lastAccessMs;
	@JsonProperty("s") private String directory;
	@JsonProperty("e") private EntityList entityList;
	
	private transient String projectName;
	private transient String projectFile;
	
	public Project() {
	}
	
	public Project(String projectName, String directory) {
		this.projectName = projectName;
		this.projectFile = ProjectUtil.generateProjectFilePath(projectName);
		this.directory = directory;
	}
	
	public static Project read(String projectFile) {
		Project project = GsonUtil.read(Project.class, projectFile);
		if (project != null) {
			project.projectName = FileUtil.getFileNameNoExtension(projectFile);
			project.projectFile = projectFile;
		}
		return project;
	}
	public boolean write() {
		this.lastAccessMs = System.currentTimeMillis();
		this.entityList = Main.ENTITYLIST;
		return GsonUtil.write(this, projectFile);
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
