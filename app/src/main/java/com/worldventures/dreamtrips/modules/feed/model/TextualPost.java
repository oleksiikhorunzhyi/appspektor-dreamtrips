package com.worldventures.dreamtrips.modules.feed.model;

import com.worldventures.dreamtrips.modules.common.model.BaseEntity;

public class TextualPost extends BaseEntity implements IFeedObject{

    private String description;

    public String getDescription() {
        return description;
    }

    @Override
    public String place() {
        return null;
    }
}
