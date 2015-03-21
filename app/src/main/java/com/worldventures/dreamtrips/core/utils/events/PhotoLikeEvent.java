package com.worldventures.dreamtrips.core.utils.events;

public class PhotoLikeEvent {

    boolean isLiked;
    int id;

    public PhotoLikeEvent(int id, boolean isLiked) {
        this.id = id;
        this.isLiked = isLiked;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public int getId() {
        return id;
    }
}
