package com.stringee.app.model;

import com.stringee.messaging.Queue;

public class QueueObject {
    private Queue queue;
    private boolean isSelected;

    public QueueObject(Queue queue) {
        this.queue = queue;
    }

    public Queue getQueue() {
        return queue;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
