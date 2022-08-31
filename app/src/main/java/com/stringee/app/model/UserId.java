package com.stringee.app.model;

import com.stringee.messaging.User.Role;

public class UserId {
    private String id;
    private boolean isSelected;
    private Role role;

    public UserId(String name) {
        this.id = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
