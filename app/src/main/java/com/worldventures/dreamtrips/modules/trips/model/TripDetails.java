package com.worldventures.dreamtrips.modules.trips.model;

import com.worldventures.dreamtrips.modules.common.model.BaseEntity;

import java.io.Serializable;
import java.util.List;

public class TripDetails implements Serializable {
    private List<ContentItem> content;

    public List<ContentItem> getContent() {
        return content;
    }

    public void setContent(List<ContentItem> content) {
        this.content = content;
    }
}
