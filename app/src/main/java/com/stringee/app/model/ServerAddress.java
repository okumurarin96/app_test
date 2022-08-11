package com.stringee.app.model;

import com.stringee.common.SocketAddress;

public class ServerAddress {
    private SocketAddress socketAddress;
    private boolean isSelected;

    public ServerAddress(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
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
