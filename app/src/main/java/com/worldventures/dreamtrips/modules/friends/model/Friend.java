package com.worldventures.dreamtrips.modules.friends.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.innahema.collections.query.functions.Converter;
import com.innahema.collections.query.functions.Predicate;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.util.Filterable;

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
        this.circles = TextUtils.join(", ", Queryable.from(circleIds).map((id) ->
                Queryable.from(circles).first(element ->
                        id.equals(element.getId())).getName()).toList());
    }

    public String getCircles() {
        return circles;
    }


}
