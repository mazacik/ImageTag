package control.reload;

import system.InstanceRepo;
import user_interface.singleton.BaseNode;

import java.util.ArrayList;
import java.util.Arrays;

public class Reload implements InstanceRepo {
    private ArrayList<BaseNode> queue;

    public Reload() {
        this.subscribe(fullView, Control.TARGET);
        this.subscribe(infoListViewL, Control.INFO, Control.FILTER);
        this.subscribe(infoListViewR, Control.INFO, Control.FILTER, Control.TARGET, Control.SELECT);
        this.subscribe(tileView, Control.DATA, Control.FILTER, Control.SELECT);
        this.subscribe(topMenu, Control.SELECT);

        queue = new ArrayList<>();
    }
    private void subscribe(BaseNode node, Control... controls) {
        Arrays.asList(controls).forEach(control -> control.getSubscribers().add(node));
    }

    public void notifyChangeIn(Control... controls) {
        Arrays.asList(controls).forEach(control -> this.queue(control.getSubscribers()));
    }
    private void queue(ArrayList<BaseNode> nodes) {
        nodes.forEach(node -> {
            if (!queue.contains(node)) {
                queue.add(node);
            }
        });
    }
    public void doReload() {
        queue.forEach(BaseNode::reload);
        queue.clear();
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
