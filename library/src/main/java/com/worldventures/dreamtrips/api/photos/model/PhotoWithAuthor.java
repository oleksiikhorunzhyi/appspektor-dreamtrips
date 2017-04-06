package com.worldventures.dreamtrips.api.photos.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.Identifiable;
import com.worldventures.dreamtrips.api.messenger.model.response.ShortUserProfile;

public interface PhotoWithAuthor extends Photo, Identifiable<Integer> {

    @SerializedName("user")
    ShortUserProfile author();

}
