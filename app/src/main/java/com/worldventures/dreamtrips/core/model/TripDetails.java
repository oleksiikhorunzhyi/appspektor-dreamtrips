package com.worldventures.dreamtrips.core.model;

import java.util.List;

/**
 * Created by 1 on 23.01.15.
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
