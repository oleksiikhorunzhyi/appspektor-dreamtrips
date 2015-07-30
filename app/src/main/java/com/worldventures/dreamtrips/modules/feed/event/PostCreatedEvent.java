package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.model.TextualPost;

public class PostCreatedEvent {

    TextualPost textualPost;

    public PostCreatedEvent(TextualPost textualPost) {
        this.textualPost = textualPost;
    }

    public TextualPost getTextualPost() {
        return textualPost;
    }
}
