package baseobject;

import baseobject.entity.EntityList;
import baseobject.tag.TagList;
import com.google.gson.reflect.TypeToken;
import main.InstanceCollector;
import tools.FileUtil;
import tools.JsonUtil;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Comparator;

public class Project {
	private transient String projectName;
	private transient String projectFile;
	
	private long msLastAccess;
	private String directorySource;
	private EntityList entityList;
	private TagList tagList;
	
	public Project(String projectName, String directorySource) {
		this.projectName = projectName;
		this.projectFile = FileUtil.getDirectoryProject() + File.separator + projectName + ".json";
		this.directorySource = directorySource;
	}
	
	private transient static final Type typeToken = new TypeToken<Project>() {}.getType();
	public static Project readFromDisk(String projectFile) {
		Project project = (Project) JsonUtil.read(typeToken, projectFile);
		project.projectName = FileUtil.getFileNameNoExtension(projectFile);
		project.projectFile = projectFile;
		return project;
	}
	public void writeToDisk() {
		this.msLastAccess = System.currentTimeMillis();
		this.entityList = InstanceCollector.mainEntityList;
		this.tagList = InstanceCollector.mainTagList;
		JsonUtil.write(this, typeToken, projectFile);
	}
	
	public static Comparator<Project> getComparator() {
		return (o1, o2) -> (int) (o2.getMsLastAccess() - o1.getMsLastAccess());
	}
	
	public String getProjectName() {
		return projectName;
	}
	public String getProjectFile() {
		return projectFile;
	}
	
	public long getMsLastAccess() {
		return msLastAccess;
	}
	public String getDirectorySource() {
		return directorySource;
	}
	public EntityList getEntityList() {
		return entityList;
	}
	public TagList getTagList() {
		return tagList;
	}
	
	private static transient Project current;
	public static Project getCurrent() {
		return current;
	}
	public static void setCurrent(Project project) {
		Project.current = project;
	}
}
