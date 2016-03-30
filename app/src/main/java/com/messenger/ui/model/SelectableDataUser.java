package com.messenger.ui.model;

import com.messenger.entities.DataUser;

public class SelectableDataUser {
    private DataUser dataUser;
    private boolean selected;
    private boolean isSelectionEnabled;

    public SelectableDataUser() {
    }

    public SelectableDataUser(DataUser dataUser, boolean selected, boolean isSelectionEnabled) {
        this.selected = selected;
        this.dataUser = dataUser;
        this.isSelectionEnabled = isSelectionEnabled;
    }

    public DataUser getDataUser() {
        return dataUser;
    }

    public void setDataUser(DataUser dataUser) {
        this.dataUser = dataUser;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelectionEnabled() {
        return isSelectionEnabled;
    }
}
