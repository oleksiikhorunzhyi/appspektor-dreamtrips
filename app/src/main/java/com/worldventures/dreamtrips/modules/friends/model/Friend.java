package com.worldventures.dreamtrips.modules.friends.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.util.Filterable;

import java.util.Arrays;

public class Friend extends User {

    @SerializedName("circle_ids")
    String[] circleIds;

    @SerializedName("mutual_friends")
    int mutualFriends;

    public String[] getCircleIds() {
        return circleIds;
    }

    public int getMutualFriends() {
        return mutualFriends;
    }

}
