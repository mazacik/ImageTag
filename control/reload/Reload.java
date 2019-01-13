package control.reload;

import userinterface.node.BaseNode;
import utils.InstanceRepo;

import java.util.ArrayList;
import java.util.Arrays;

public class Reload implements InstanceRepo {
    private ArrayList<BaseNode> queue;

    public Reload() {
        this.subscribe(fullView, Reload.Control.TARGET);
        this.subscribe(infoListViewL, Reload.Control.INFO);
        this.subscribe(infoListViewR, Reload.Control.INFO, Reload.Control.SELECT, Reload.Control.TARGET);
        this.subscribe(tileView, Reload.Control.DATA, Reload.Control.FILTER);
        this.subscribe(topMenu, Reload.Control.SELECT);

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
