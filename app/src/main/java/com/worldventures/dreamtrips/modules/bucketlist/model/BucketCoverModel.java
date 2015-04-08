package com.worldventures.dreamtrips.modules.bucketlist.model;

import com.google.gson.annotations.SerializedName;

public class BucketCoverModel extends BucketBasePostItem {

    @SerializedName("cover_photo_id")
    private Integer coverId;

    public void setCoverId(Integer coverId) {
        this.coverId = coverId;
    }

    public int getCoverId() {
        return coverId;
    }
}
