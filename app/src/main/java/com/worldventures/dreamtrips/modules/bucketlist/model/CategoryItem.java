package com.worldventures.dreamtrips.modules.bucketlist.model;

import com.worldventures.dreamtrips.modules.common.model.BaseEntity;

public class CategoryItem extends BaseEntity {

    private String name;

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
