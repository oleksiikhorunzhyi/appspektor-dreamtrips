package com.worldventures.dreamtrips.api.bucketlist.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.Identifiable;
import com.worldventures.dreamtrips.api.messenger.model.response.ShortUserProfile;

public interface BucketItemWithAuthor extends BucketItem, Identifiable<Integer> {

    @SerializedName("user")
    ShortUserProfile author();

}
