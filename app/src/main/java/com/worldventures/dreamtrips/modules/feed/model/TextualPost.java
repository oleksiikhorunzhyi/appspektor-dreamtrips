package com.worldventures.dreamtrips.modules.feed.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import java.util.ArrayList;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class TextualPost extends BaseFeedEntity {

    private String description;

    private List<FeedEntityHolder> attachments = new ArrayList<>();

    private Location location;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<FeedEntityHolder> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<FeedEntityHolder> attachments) {
        this.attachments = attachments;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String place() {
        return location != null ? location.getName() : "";
    }
}
