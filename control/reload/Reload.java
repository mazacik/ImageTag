package control.reload;

import system.Instances;
import user_interface.singleton.BaseNode;

import java.util.ArrayList;
import java.util.Arrays;

public class Reload implements Instances {
    private ArrayList<BaseNode> queue;

    public Reload() {
        this.subscribe(mediaView
                , Control.TARGET
        );
        this.subscribe(tagListViewL
                , Control.INFO
                , Control.FILTER
        );
        this.subscribe(tagListViewR
                , Control.INFO
                , Control.FILTER
                , Control.TARGET
                , Control.SELECT
        );
        this.subscribe(tileView
                , Control.DATA
                , Control.FILTER
                , Control.SELECT
        );
        this.subscribe(topMenu
                , Control.TARGET
        );

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
