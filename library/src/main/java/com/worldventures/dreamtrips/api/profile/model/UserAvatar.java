package com.worldventures.dreamtrips.api.profile.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.session.model.Avatar;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface UserAvatar {

    @SerializedName("id")
    int id();

    @SerializedName("avatar")
    Avatar avatar();

}
