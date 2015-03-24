package com.worldventures.dreamtrips.modules.trips.model;

import com.worldventures.dreamtrips.modules.common.model.BaseEntity;

import java.util.List;

/**
 *  1 on 23.01.15.
 */
public class TripDetails extends BaseEntity {
    private List<ContentItem> content;

    public List<ContentItem> getContent() {
        return content;
    }

    public void setContent(List<ContentItem> content) {
        this.content = content;
    }
}
