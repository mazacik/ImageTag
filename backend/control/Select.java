package application.backend.control;

import application.backend.base.CustomList;
import application.backend.base.entity.Entity;
import application.backend.base.entity.EntityList;
import application.backend.base.tag.Tag;
import application.backend.base.tag.TagList;
import application.backend.util.JointGroupUtil;
import application.frontend.stage.StageManager;
import application.frontend.stage.template.YesNoCancelStage;
import application.main.InstanceCollector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class Select extends EntityList implements InstanceCollector {
	private Entity shiftStart = null;
	
	public Select() {
	
	}
	
	public void init() {
	
	}
	
	public boolean add(Entity entity) {
		if (entity == null) return false;
		int jointID = entity.getJointID();
		if (jointID != 0 && !galleryPane.getExpandedGroups().contains(jointID)) {
			if (super.addAll(JointGroupUtil.getJointObjects(entity))) {
				reload.requestTileEffect(JointGroupUtil.getJointObjects(entity));
				reload.notify(Reload.Control.SELECT);
				return true;
			}
		} else {
			if (super.add(entity)) {
				reload.requestTileEffect(entity);
				reload.notify(Reload.Control.SELECT);
				return true;
			}
		}
		return false;
	}
	public boolean addAll(Collection<? extends Entity> c) {
		if (c == null || c.isEmpty()) return false;
		
		ArrayList<Integer> expandedGroups = galleryPane.getExpandedGroups();
		
		for (Entity entity : c) {
			if (entity == null || this.contains(entity)) continue;
			
			int jointID = entity.getJointID();
			if (jointID == 0 || expandedGroups.contains(jointID)) {
				super.add(entity);
				reload.requestTileEffect(entity);
			} else {
				EntityList jointObject = JointGroupUtil.getJointObjects(entity);
				super.addAll(jointObject);
				reload.requestTileEffect(jointObject);
			}
		}
		
		reload.notify(Reload.Control.SELECT);
		return true;
	}
	public boolean remove(Entity entity) {
		if (entity == null) return false;
		
		int size = this.size();
		if (entity.getJointID() == 0 || galleryPane.getExpandedGroups().contains(entity.getJointID())) {
			reload.requestTileEffect(entity);
			super.remove(entity);
		} else {
			reload.requestTileEffect(JointGroupUtil.getJointObjects(entity));
			this.removeAll(JointGroupUtil.getJointObjects(entity));
		}
		
		if (size != this.size()) {
			reload.notify(Reload.Control.SELECT);
			return true;
		}
		return false;
	}
	public boolean removeAll(ArrayList<Entity> arrayList) {
		if (arrayList == null) return false;
		if (super.removeAll(arrayList)) {
			for (Entity entity : arrayList) reload.requestTileEffect(entity);
			reload.notify(Reload.Control.SELECT);
			return true;
		}
		return false;
	}
	
	public void set(Entity entity) {
		this.clear();
		this.add(entity);
	}
	public void setAll(ArrayList<Entity> entities) {
		this.clear();
		this.addAll(entities);
	}
	public void setRandom() {
		Entity entity = galleryPane.getDataObjectsOfTiles().getRandom();
		this.set(entity);
		target.set(entity);
	}
	public void setRandomFromJointGroup() {
		Entity entity = getRandom(JointGroupUtil.getJointObjects(target.get()));
		this.set(entity);
		target.set(entity);
	}
	public void clear() {
		reload.requestTileEffect(this);
		super.clear();
		reload.notify(Reload.Control.SELECT);
	}
	public void swapState(Entity entity) {
		if (super.contains(entity)) {
			this.remove(entity);
		} else {
			this.add(entity);
		}
	}
	
	public void jointObjectCreate() {
		CustomList<Integer> jointIDs = entityListMain.getJointIDs();
		int jointID;
		do jointID = new Random().nextInt();
		while (jointIDs.contains(jointID));
		
		YesNoCancelStage.Result result = StageManager.getYesNoCancelStage()._show("Merge tags? (" + this.size() + " items selected)");
		if (result == YesNoCancelStage.Result.YES) {
			TagList tagList = new TagList();
			for (Entity entity : this) {
				tagList.addAll(entity.getTagList());
			}
			for (Entity entity : this) {
				entity.setJointID(jointID);
				entity.setTagList(tagList);
			}
		} else if (result == YesNoCancelStage.Result.NO) {
			for (Entity entity : this) {
				entity.setJointID(jointID);
			}
		} else return;
		
		target.set(this.getFirst());
		reload.notify(Reload.Control.DATA, Reload.Control.TAGS);
	}
	public void jointObjectDiscard() {
		Entity entity = target.get();
		if (entity.getJointID() != 0) {
			ArrayList<Entity> jointObjects = JointGroupUtil.getJointObjects(entity);
			for (Entity jointObject : jointObjects) {
				jointObject.setJointID(0);
			}
		}
		reload.notify(Reload.Control.DATA, Reload.Control.TAGS);
	}
	public boolean isSelectJoint() {
		int jointID = target.get().getJointID();
		if (jointID == 0) return false;
		for (Entity entity : this) {
			if (entity.getJointID() != jointID) {
				return false;
			}
		}
		return true;
	}
	
	public void shiftSelectTo(Entity shiftCurrent) {
		CustomList<Entity> entities = galleryPane.getDataObjectsOfTiles();
		
		int indexFrom = entities.indexOf(shiftStart);
		int indexTo = entities.indexOf(shiftCurrent);
		
		int indexLower;
		int indexHigher;
		
		if (indexFrom > indexTo) {
			indexLower = indexTo;
			indexHigher = indexFrom;
		} else {
			indexLower = indexFrom;
			indexHigher = indexTo;
		}
		
		this.addAll(entities.subList(indexLower, indexHigher + 1));
	}
	
	public void addTagObject(Tag tag) {
		this.forEach(dataObject -> dataObject.getTagList().add(tag));
		reload.notify(Reload.Control.TAGS);
	}
	public void removeTagObject(Tag tag) {
		this.forEach(dataObject -> dataObject.getTagList().remove(tag));
		reload.notify(Reload.Control.TAGS);
	}
	
	public void setShiftStart(Entity shiftStart) {
		this.shiftStart = shiftStart;
	}
}
