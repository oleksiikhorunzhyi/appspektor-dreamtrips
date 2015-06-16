package com.worldventures.dreamtrips.modules.friends.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.util.Filterable;

public class Friend extends User implements Filterable {
    String[] group;

    @SerializedName("mutual_friends")
    int mutualFriends;

    public String[] getGroups() {
        return group;
    }

    public int getMutualFriends() {
        return mutualFriends;
    }

    @Override
    public boolean containsQuery(String query) {
        return containsIn(query, getFirstName()) ||
                containsIn(query, getLastName()) ||
                containsIn(query, getEmail()) ||
                containsIn(query, getLocation()) ||
                containsIn(query, getGroups());
    }

    private boolean containsIn(String query, String where) {
        return where != null && where.toLowerCase().contains(query.toLowerCase());
    }

    private boolean containsIn(String query, String[] array) {
        if (array != null) {
            for (String s : array) {
                if (s.contains(query)) return true;
            }
        }
        return false;
    }
}
