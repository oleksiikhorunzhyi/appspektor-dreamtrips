package com.worldventures.dreamtrips.modules.friends.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.innahema.collections.query.functions.Converter;
import com.innahema.collections.query.functions.Predicate;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.util.Filterable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Friend extends User {

    @SerializedName("circle_ids")
    String[] circleIds;

    @SerializedName("mutual_friends")
    int mutualFriends;

    private transient String circles;

    public int getMutualFriends() {
        return mutualFriends;
    }

    public void setCircles(List<Circle> circles) {
        List<String> userCircles = new ArrayList<>();

        for (String s : circleIds) {
            for (Circle circle : circles) {
                if (circle.getId() != null && circle.getId().equals(s)) {
                    userCircles.add(circle.getName());
                    break;
                }
            }
        }

        this.circles = TextUtils.join(", ", userCircles);
    }

    public String getCircles() {
        return circles;
    }


}
