package control;

import lifecycle.InstanceManager;
import user_interface.main.NodeBase;

import java.util.ArrayList;

public class Reload {
    private ArrayList<NodeBase> queue;

    public Reload() {
        queue = new ArrayList<>();

        this.subscribe(InstanceManager.getMediaPane()
                , Control.TARGET
        );
        this.subscribe(InstanceManager.getFilterPane()
                , Control.INFO
                , Control.FILTER
        );
        this.subscribe(InstanceManager.getSelectPane()
                , Control.INFO
                , Control.FILTER
                , Control.TARGET
                , Control.SELECT
        );
        this.subscribe(InstanceManager.getGalleryPane()
                , Control.DATA
                , Control.FILTER
                , Control.SELECT
        );
        this.subscribe(InstanceManager.getToolbarPane()
                , Control.TARGET
        );
    }

    public void flag(Control... controls) {
        for (Control control : controls) queue(control.getSubscribers());
    }
    public void doReload() {
        ArrayList<NodeBase> newQueue = new ArrayList<>(queue);

        for (NodeBase node : queue) if (node.reload()) newQueue.remove(node);

        queue = newQueue;
    }

    private void subscribe(NodeBase node, Control... controls) {
        for (Control control : controls) control.getSubscribers().add(node);
    }
    private void queue(ArrayList<NodeBase> nodes) {
        for (NodeBase node : nodes) if (!queue.contains(node)) queue.add(node);
    }

    public enum Control {
        DATA,
        INFO,
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
