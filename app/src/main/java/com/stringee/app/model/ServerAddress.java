package com.stringee.app.model;

import com.stringee.common.SocketAddress;

public class ServerAddress {
    private long id;
    private SocketAddress socketAddress;
    private boolean isSelected;

    public ServerAddress(SocketAddress socketAddress) {
        this.id = System.currentTimeMillis();
        this.socketAddress = socketAddress;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }

    public void setSocketAddress(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
