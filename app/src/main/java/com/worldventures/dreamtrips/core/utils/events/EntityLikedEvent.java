package com.worldventures.dreamtrips.core.utils.events;

public class EntityLikedEvent {

    protected boolean liked;
    protected String id;

    public EntityLikedEvent(String id, boolean liked) {
        this.id = id;
        this.liked = liked;
    }

    public boolean isLiked() {
        return liked;
    }

    public String getId() {
        return id;
    }
}