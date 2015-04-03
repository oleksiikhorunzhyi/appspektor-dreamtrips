package com.worldventures.dreamtrips.core.utils.events;

public class PhotoLikeEvent {

    protected boolean isLiked;
    protected String  id;

    public PhotoLikeEvent(String id, boolean isLiked) {
        this.id = id;
        this.isLiked = isLiked;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public String  getId() {
        return id;
    }
}
