package com.worldventures.dreamtrips.modules.reptools.event;

public class StoryLikedEvent {

    public final String storyUrl;
    public final boolean isLiked;

    public StoryLikedEvent(String storyUrl, boolean isLiked) {
        this.storyUrl = storyUrl;
        this.isLiked = isLiked;
    }
}
