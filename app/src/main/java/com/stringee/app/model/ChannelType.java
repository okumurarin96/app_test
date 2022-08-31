package com.stringee.app.model;

import com.stringee.messaging.Conversation;

public class ChannelType {
    private Conversation.ChannelType channelType;
    private boolean isSelected;

    public ChannelType(Conversation.ChannelType name) {
        this.channelType = name;
    }

    public Conversation.ChannelType getChannelType() {
        return channelType;
    }

    public void setChannelType(Conversation.ChannelType channelType) {
        this.channelType = channelType;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
