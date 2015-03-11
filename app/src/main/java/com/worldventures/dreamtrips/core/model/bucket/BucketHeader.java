package com.worldventures.dreamtrips.core.model.bucket;

import com.worldventures.dreamtrips.core.model.BaseEntity;

/**
 * Created by 1 on 06.03.15.
 */
public class BucketHeader extends BaseEntity {

    private int headerResource;

    public BucketHeader(int id, int headerResource) {
        this.id = id;
        this.headerResource = headerResource;
    }

    public int getHeaderResource() {
        return headerResource;
    }

    public void setHeaderResource(int headerResource) {
        this.headerResource = headerResource;
    }
}
