package control.reload;

import lifecycle.InstanceManager;
import user_interface.singleton.BaseNode;

import java.util.ArrayList;
import java.util.Arrays;

public class Reload {
    private ArrayList<BaseNode> queue;

    public Reload() {
        queue = new ArrayList<>();
    }
    public void init() {
        this.subscribe(InstanceManager.getMediaView()
                , Control.TARGET
        );
        this.subscribe(InstanceManager.getTagListViewL()
                , Control.INFO
                , Control.FILTER
        );
        this.subscribe(InstanceManager.getTagListViewR()
                , Control.INFO
                , Control.FILTER
                , Control.TARGET
                , Control.SELECT
        );
        this.subscribe(InstanceManager.getTileView()
                , Control.DATA
                , Control.FILTER
                , Control.SELECT
        );
        this.subscribe(InstanceManager.getTopMenu()
                , Control.TARGET
        );
    }
    private void subscribe(BaseNode node, Control... controls) {
        Arrays.asList(controls).forEach(control -> control.getSubscribers().add(node));
    }

    public void notifyChangeIn(Control... controls) {
        Arrays.asList(controls).forEach(control -> queue(control.getSubscribers()));
    }
    private void queue(ArrayList<BaseNode> nodes) {
        nodes.forEach(node -> {
            if (!queue.contains(node)) {
                queue.add(node);
            }
        });
    }
    public void doReload() {
        ArrayList<BaseNode> newQueue = new ArrayList<>(queue);

        queue.forEach(baseNode -> {
            if (baseNode.reload()) {
                newQueue.remove(baseNode);
            }
        });

        queue = newQueue;
    }

    public enum Control {
        DATA,
        INFO,
        FILTER,
        TARGET,
        SELECT,
        ;

        private ArrayList<BaseNode> subscribers;
        Control() {
            this.subscribers = new ArrayList<>();
        }
        public ArrayList<BaseNode> getSubscribers() {
            return this.subscribers;
        }
    }
}
