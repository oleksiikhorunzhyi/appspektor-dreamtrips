package com.worldventures.dreamtrips.api.friends.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import static com.worldventures.dreamtrips.api.friends.model.FriendRequestParams.HttpAction.CONFIRM;
import static com.worldventures.dreamtrips.api.friends.model.FriendRequestParams.HttpAction.REJECT;
import static com.worldventures.dreamtrips.api.friends.model.ImmutableFriendRequestParams.builder;
import static org.immutables.value.Value.Style.ImplementationVisibility.PACKAGE;

@Gson.TypeAdapters
@Value.Immutable
@Value.Style(visibility = PACKAGE)
public abstract class FriendRequestParams {

    @SerializedName("user_id")
    public abstract int userId();
    @Nullable
    @SerializedName("circle_id")
    public abstract String circleId();
    @SerializedName("action")
    public abstract HttpAction action();

    public static FriendRequestParams confirm(int userId, String circleId) {
        return builder().action(CONFIRM).userId(Integer.valueOf(userId)).circleId(circleId).build();
    }

    public static FriendRequestParams reject(int userId) {
        return builder().action(REJECT).userId(Integer.valueOf(userId)).build();
    }

    @Value.Check
    void confirmWithCircle() {
        if (action() == CONFIRM && circleId() == null) {
            throw new IllegalStateException("CircleId should be present to confirm friend");
        }
    }

    enum HttpAction {
        @SerializedName("CONFIRM")
        CONFIRM,
        @SerializedName("REJECT")
        REJECT
    }
}
