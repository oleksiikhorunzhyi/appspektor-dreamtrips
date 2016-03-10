package com.messenger.ui.model;

import com.messenger.entities.DataUser;

import java.util.Collection;

public class UsersGroup {
    public final String groupName;
    public final Collection<DataUser> members;

    public UsersGroup(String groupName, Collection<DataUser> members) {
        this.groupName = groupName;
        this.members = members;
    }

    @Override
    public String toString() {
        return "UsersGroup{" +
                "groupName='" + groupName + '\'' +
                ", members=" + members +
                '}';
    }
}
