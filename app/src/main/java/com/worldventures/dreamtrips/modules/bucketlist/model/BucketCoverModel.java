package com.worldventures.dreamtrips.modules.bucketlist.model;

import com.google.gson.annotations.SerializedName;

public class BucketCoverModel extends BucketBasePostItem {

    @SerializedName("cover_photo_id")
    private String coverId;

    public void setCoverId(String coverId) {
        this.coverId = coverId;
    }
}
