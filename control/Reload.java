package control;

import database.list.CustomList;
import main.InstanceManager;
import userinterface.main.NodeBase;

import java.util.ArrayList;

public class Reload {
	private ArrayList<NodeBase> queue;
	
	public Reload() {
		queue = new ArrayList<>();
		
		this.subscribe(InstanceManager.getMediaPane()
				, Control.TARGET
		);
		this.subscribe(InstanceManager.getFilterPane()
				, Control.TAG
				, Control.FILTER
		);
		this.subscribe(InstanceManager.getSelectPane()
				, Control.TAG
				, Control.FILTER
				, Control.TARGET
				, Control.SELECT
		);
		this.subscribe(InstanceManager.getGalleryPane()
				, Control.OBJ
				, Control.FILTER
		);
		this.subscribe(InstanceManager.getToolbarPane()
				, Control.TARGET
		);
	}
	
	public void doReload() {
		CustomList<NodeBase> reloaded = new CustomList<>();
		for (NodeBase node : queue) if (node.reload()) reloaded.add(node);
		queue.removeAll(reloaded);
	}
	
	public void notify(Control... controls) {
		for (Control control : controls) queue(control.getSubscribers());
	}
	private void queue(ArrayList<NodeBase> nodes) {
		for (NodeBase node : nodes) if (!queue.contains(node)) queue.add(node);
	}
	
	private void subscribe(NodeBase node, Control... controls) {
		for (Control control : controls) control.getSubscribers().add(node);
	}
	
	public enum Control {
		OBJ,
		TAG,
		FILTER,
		TARGET,
		SELECT,
		;
		
		private ArrayList<NodeBase> subscribers;
		Control() {
			this.subscribers = new ArrayList<>();
		}
		public ArrayList<NodeBase> getSubscribers() {
			return this.subscribers;
		}
	}
}
