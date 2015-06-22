package com.worldventures.dreamtrips.modules.friends.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.util.Filterable;

import java.util.Arrays;

public class Friend extends User {
    String[] circle_ids;

    @SerializedName("mutual_friends")
    int mutualFriends;

    public String[] getCircleIds() {
        return circle_ids;
    }

    public int getMutualFriends() {
        return mutualFriends;
    }

}
