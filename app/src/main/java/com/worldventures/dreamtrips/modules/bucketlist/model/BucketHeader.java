package com.worldventures.dreamtrips.modules.bucketlist.model;

import com.worldventures.dreamtrips.modules.common.model.BaseEntity;

/**
 *  1 on 06.03.15.
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
